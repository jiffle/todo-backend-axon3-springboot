package todo.domain

import spock.lang.*
import org.axonframework.test.Fixtures
import org.axonframework.test.GivenWhenThenTestFixture
import org.axonframework.test.ResultValidator
import todo.domain.command.*
import todo.domain.event.*


public class ToDoItemAggregateSpec extends Specification {
    def fixture;
    def todo;

    def setup() {
        fixture = Fixtures.newGivenWhenThenFixture(ToDoItemAggregate.class);
        todo = new ToDoItem("do something", false, 2);
    }

    def "Creating Todo Items Emits Created Events"() {
        def test = fixture.given()
		when:
			def validator = test.when(new CreateToDoItemCommand("1", todo))
		then:
            validator.expectEvents(new ToDoItemCreatedEvent("1", todo));

    }

    def "Updating Todo Items Emits Updated Events"() {
        def updates = new ToDoItem(null, true, null);

        def test = fixture.given(new ToDoItemCreatedEvent("1", todo))
		when:
        	def validator = test.when(new UpdateToDoItemCommand("1", updates))
		then:
            validator.expectEvents(new TodoItemUpdatedEvent("1", updates));
    }

    def "Deleting Todo Items Emits Deleted Events"() {
    	def test = fixture.given(new ToDoItemCreatedEvent("1", todo))
		when:
	        def validator = test.when(new DeleteToDoItemCommand("1"))
		then:
			validator.expectEvents(new ToDoItemDeletedEvent("1"));
    }
}