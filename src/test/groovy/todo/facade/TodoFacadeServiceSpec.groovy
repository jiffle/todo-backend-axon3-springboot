package todo.facade

import org.axonframework.commandhandling.gateway.CommandGateway
import spock.lang.Specification
import todo.domain.TodoItem
import todo.domain.command.ClearTodoListCommand
import todo.domain.command.CreateTodoItemCommand
import todo.domain.command.DeleteTodoItemCommand
import todo.domain.command.UpdateTodoItemCommand
import todo.exception.InternalServerErrorException
import todo.helper.CompletionLatchFactory
import todo.middleware.CompletionTracker
import todo.query.TodoQueryService

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

class TodoFacadeServiceSpec extends Specification {
    TodoFacadeService facadeService

    String userId = "1"
    TodoQueryService queryService
    CompletionLatchFactory latchFactory
    CountDownLatch latch
    CommandGateway commandGateway
    String expectId = "abc123"
    String expectTitle = "First Item"
    boolean expectCompleted = false
    Integer expectOrder = 10
    TodoItem expectItem = [ expectId, expectTitle, expectCompleted, expectOrder]
    TodoItem[] expectList = [["abc123", expectTitle, expectCompleted, expectOrder],
                             [ "def456", "second item", false, 20],
                             [ "ghi789", "third item", false, 30]]
    List<TodoItem> emptyList = []



    def setup() {
        queryService = Mock()
        commandGateway = Mock()
        latchFactory = Mock()
        latch = Mock()
        latchFactory.createInstance() >> latch
        facadeService = new TodoFacadeService( queryService, commandGateway, latchFactory)
    }

    def "Get List should return a list of items"() {
        when:
            def todos = facadeService.getList( userId)
        then:
            1 * queryService.queryListForUser( userId) >> expectList
            todos.size() == 3
            todos == expectList
    }

    def "Get Item should return item for valid ID"() {

        when:
        def todo = facadeService.getItem( userId, expectId)
        then:
        1 * queryService.queryListForItem( userId, expectId) >> expectItem
        todo == expectItem
    }

    def "Get Item should throw not found exception for invalid ID"() {

        when:
        def todo = facadeService.getItem( userId, expectId)
        then:
        1 * queryService.queryListForItem( userId, expectId) >> null
        todo == null
    }

    def "Create item should return the created item"() {

        when:
        def todo = facadeService.createItem( userId, expectId, expectTitle, expectCompleted, expectOrder)
        then:
        1 * commandGateway.sendAndWait({ CreateTodoItemCommand cmd -> cmd.userId == userId && cmd.itemId == expectId &&
                cmd.title == expectTitle && cmd.completed == expectCompleted && cmd.order == expectOrder}, _, _)
        1 * latch.await( _, _) >> true
        1 * queryService.queryListForItem( userId, expectId) >> expectItem
        todo == expectItem
    }

    def "Patch item should return the modified item"() {
        given:
        String updatedTitle = "Updated Title"
        Boolean updatedCompleted = true
        Integer updatedOrder = 99
        TodoItem updatedItem = [ expectId, updatedTitle, updatedCompleted, updatedOrder]
        when:
        def todo = facadeService.updateItem( userId, expectId, updatedTitle, updatedCompleted, updatedOrder)
        then:
        1 * commandGateway.sendAndWait({ UpdateTodoItemCommand cmd -> cmd.userId == userId && cmd.itemId == expectId &&
                cmd.title == updatedTitle && cmd.completed == updatedCompleted && cmd.order == updatedOrder}, _, _)
        1 * latch.await( _, _) >> true
        1 * queryService.queryListForItem( userId, expectId) >> updatedItem
        todo == updatedItem
    }

    def "Delete item should return an empty item"() {

        when:
        def todo = facadeService.deleteItem( userId, expectId)
        then:
        1 * commandGateway.sendAndWait({ DeleteTodoItemCommand cmd -> cmd.userId == userId && cmd.itemId == expectId}, _, _)
        1 * latch.await( _, _) >> true
        1 * queryService.queryListForItem( userId, expectId) >> expectItem
        todo == expectItem      // delete returns the deleted item
    }

    def "Delete List should return an empty list"() {

        when:
            def todos = facadeService.deleteList( userId)
        then:
            1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId == userId})
        1 * latch.await( _, _) >> true
        1 * queryService.queryListForUser( userId) >> emptyList
            todos.size() == 0
    }

    def "Delete List with timeout should throw exception"() {

        when:
        def todos = facadeService.deleteList( userId)
        then:
        1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId == userId})
        1 * latch.await( _, _) >> false
        thrown( InternalServerErrorException)
    }

    def "Delete item should return an empty list - version 2"() {

        when:
        def todos = facadeService.deleteList( userId)
        then:
        1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId == userId})
        1 * latch.await( _, _) >> true
        1 * queryService.queryListForUser( userId) >> emptyList
        todos.size() == 0
    }
}