package todo.facade

import spock.lang.Specification
import todo.domain.TodoItem
import todo.query.TodoQueryService


class TodoFacadeServiceSpec extends Specification {
    TodoFacadeService facadeService

    String userId = "1"
    TodoQueryService queryService

    def setup() {
        queryService = Mock()
        facadeService = new TodoFacadeService( queryService)
    }


    def "GetAll should return a list of items"() {
        given:
            TodoItem[] checkList = [[ "abc123", "first item", false, 10],
                                    [ "def456", "second item", false, 20],
                                    [ "ghi789", "third item", false, 30]]
        when:
            def todos = facadeService.getTodoList( userId)
        then:
            queryService.queryListForUser(_) >> checkList
            todos.size() == 3
            todos == checkList
    }

}