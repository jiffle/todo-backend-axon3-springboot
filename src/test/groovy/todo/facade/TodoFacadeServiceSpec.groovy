package todo.facade

import org.axonframework.commandhandling.gateway.CommandGateway
import spock.lang.Specification
import todo.domain.TodoItem
import todo.domain.command.ClearTodoListCommand
import todo.helper.InternalServerErrorException
import todo.middleware.CompletionTracker
import todo.query.TodoQueryService

import java.util.concurrent.CompletableFuture

class TodoFacadeServiceSpec extends Specification {
    TodoFacadeService facadeService

    String userId = "1"
    TodoQueryService queryService
    CompletionTracker completionTracker
    CommandGateway commandGateway
    String expectId = "abc123"
    TodoItem expectItem = [ expectId, "first item", false, 10]
    TodoItem[] expectList = [["abc123", "first item", false, 10],
                             [ "def456", "second item", false, 20],
                             [ "ghi789", "third item", false, 30]]
    List<TodoItem> emptyList = []



    def setup() {
        queryService = Mock()
        commandGateway = Mock()
        completionTracker = Mock()
        facadeService = new TodoFacadeService( queryService, commandGateway, completionTracker)
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


    def "Delete List should return an empty list"() {
        given:
            CompletionTracker.CompletableStatus status = Spy( CompletionTracker.CompletableStatus, constructorArgs: [])
            CompletableFuture<Collection<TodoItem>> future = new CompletableFuture()
            status.addTracker(_) >> future
            future.complete( emptyList)
        when:
            def todos = facadeService.deleteList( userId)
        then:
            1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId })
            1 * completionTracker.getListTracker() >> status
            todos.size() == 0
    }

    def "Delete List with timeout should throw exception"() {
        given:
        CompletionTracker.CompletableStatus status = Mock()
        CompletableFuture<Collection<TodoItem>> future = new CompletableFuture()
        when:
        def todos = facadeService.deleteList( userId)
        then:
        1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId })
        1 * completionTracker.getListTracker() >> status
        1 * status.addTracker(_) >> future
        thrown( InternalServerErrorException)
    }

    def "Delete List should return an empty list - version 2"() {
        given:
        CompletionTracker.CompletableStatus status = Mock()
        CompletableFuture<Collection<TodoItem>> future = new CompletableFuture()
        future.complete( emptyList)
        when:
        def todos = facadeService.deleteList( userId)
        then:
        1 * commandGateway.send({ ClearTodoListCommand cmd -> cmd.userId })
        1 * completionTracker.getListTracker() >> status
        1 * status.addTracker(_) >> future
        todos.size() == 0
    }

}