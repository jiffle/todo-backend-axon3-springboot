package todo.domain

import spock.lang.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.*
import todo.domain.command.*
import todo.domain.event.*

import static java.util.Optional.of

public class TodoListAggregateSpec extends Specification {
    def fixture
    def todo

    def setup() {
        fixture = new AggregateTestFixture(TodoListAggregate.class)
        todo = ToDoItem.builder().id("abc123").title("do something").order( 2).build();
    }

    def "Creating Todo Items Emits Created Events"() {
        def test = fixture.given()
		when:
			
			def validator = test.when( new CreateToDoItemCommand("1", "abc123", "do something", false, 2, of("id57")))
		then:
            validator.expectEvents( new ToDoItemCreatedEvent("1", todo))

    }

    def "Updating Todo Items Emits Updated Events"() {
        def updates = new ToDoItem(null, true, null)

        def test = fixture.given(new ToDoItemCreatedEvent("1", todo))
		when:
        	def validator = test.when(new UpdateToDoItemCommand("1", updates))
		then:
            validator.expectEvents(new TodoItemUpdatedEvent("1", updates))
    }

    def "Deleting Todo Items Emits Deleted Events"() {
    	def test = fixture.given(new ToDoItemCreatedEvent("1", todo))
		when:
	        def validator = test.when(new DeleteToDoItemCommand("1"))
		then:
			validator.expectEvents(new ToDoItemDeletedEvent("1"))
    }
}