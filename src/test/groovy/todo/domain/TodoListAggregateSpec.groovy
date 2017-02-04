package todo.domain

import spock.lang.*
import org.axonframework.test.aggregate.AggregateTestFixture
import todo.domain.command.*
import todo.domain.event.*

import static java.util.Optional.of

public class TodoListAggregateSpec extends Specification {
    def fixture
    def todo

    def setup() {
        fixture = new AggregateTestFixture(TodoListAggregate.class)
        todo = TodoItem.builder().id("abc123").title("do something").order( 2).build();
    }

    @Requires({ env['test.broken'] })
    def "Creating Todo Items Emits Created Events"() {
        def test = fixture.given()
		when:
			
			def validator = test.when( new CreateTodoItemCommand("1", "abc123", "do something", false, 2, of("id57")))
		then:
            validator.expectEvents( new TodoItemCreatedEvent("1", todo))

    }

    @Requires({ env['test.broken'] })
    def "Updating Todo Items Emits Updated Events"() {
        def updates = new TodoItem(null, true, null)

        def test = fixture.given(new TodoItemCreatedEvent("1", todo))
		when:
        	def validator = test.when(new UpdateTodoItemCommand("1", updates))
		then:
            validator.expectEvents(new TodoItemUpdatedEvent("1", updates))
    }

    @Requires({ env['test.broken'] })
    def "Deleting Todo Items Emits Deleted Events"() {
    	def test = fixture.given(new TodoItemCreatedEvent("1", todo))
		when:
	        def validator = test.when(new DeleteTodoItemCommand("1"))
		then:
			validator.expectEvents(new TodoItemDeletedEvent("1"))
    }
}