package todo.facade

import org.axonframework.commandhandling.gateway.CommandGateway
import spock.lang.Specification
import todo.domain.TodoItem
import todo.domain.command.ClearTodoListCommand
import todo.middleware.CompletionTracker
import todo.query.TodoQueryService

import javax.annotation.processing.Completion
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionService


class TodoFacadeServiceSpec extends Specification {
    TodoFacadeService facadeService

    String userId = "1"
    TodoQueryService queryService
    CompletionTracker completionTracker
    CommandGateway commandGateway
    TodoItem[] checkList = [[ "abc123", "first item", false, 10],
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
            1 * queryService.queryListForUser(_) >> checkList
            todos.size() == 3
            todos == checkList
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
}