package todo.view

import spock.lang.*
import todo.domain.ToDoItem

public class ToDoItemViewFactorySpec extends Specification {

    def toDoItemViewFactory

    def setup() {
        toDoItemViewFactory = new ToDoItemViewFactory("http://test.host/todos")
    }

    def "Creates A ToDo Item View"() {
        def todo = ToDoItem.builder().id("abc123").title("feed the dog").order(1).build()

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