package todo.view

import spock.lang.*
import todo.domain.ToDoItem

public class ToDoItemViewFactorySpec extends Specification {

    def toDoItemViewFactory

    def setup() {
        toDoItemViewFactory = new ToDoItemViewFactory("http://test.host/todos")
    }

    def "Creates A ToDo Item View"() {
        def todo = new ToDoItem("feed the dog", false, 1)
        todo.setId("abc123")

		when:
        	def toDoItemView = toDoItemViewFactory.build(todo)

		then:
        	toDoItemView.getId() == "abc123"
			toDoItemView.getTitle() == "feed the dog"
        	!toDoItemView.isCompleted()
			toDoItemView.getOrder() == 1
        	toDoItemView.getUrl() == "http://test.host/todos/abc123"
    }
}