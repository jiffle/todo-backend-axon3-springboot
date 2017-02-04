package todo.view

import spock.lang.*
import todo.domain.TodoItem

public class ToDoItemViewFactorySpec extends Specification {

    def toDoItemViewFactory
    def baseUrl = "http://test.host/todos"

    def setup() {
        toDoItemViewFactory = new TodoItemViewFactory( baseUrl)
    }

    def "Creates a Todo View Item"() {
        def itemId = "abc123"
        def todo = TodoItem.builder().id( itemId).title("feed the dog").order(1).build()
        def checkUrl = baseUrl
		when:
        	def todoItemView = toDoItemViewFactory.buildItem(todo)

		then:
        	todoItemView.id == itemId
			todoItemView.title == "feed the dog"
        	!todoItemView.completed
			todoItemView.order == 1
        	todoItemView.url == "${checkUrl}/${itemId}"
    }

    def "Creates a List of ToDo View Items"() {
        def todo1 = TodoItem.builder().id("abc123").title("feed the dog").order(1).build()
        def todo2 = TodoItem.builder().id("def456").title("something else").order(2).build()
        def checkUrl = baseUrl
        when:
        def todoViews = toDoItemViewFactory.buildList([todo1, todo2])

        then:
        with( todoViews[0]) {
            id == "abc123"
            title == "feed the dog"
            !completed
            order == 1
            url == "${checkUrl}/${id}"
        }
        with( todoViews[1]) {
            id == "def456"
            title == "something else"
            !completed
            order == 2
            url == "${checkUrl}/${id}"
        }
    }
}