package todo.domain

import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate
import org.axonframework.commandhandling.model.inspection.ModelInspector
import org.axonframework.eventhandling.EventBus
import spock.lang.*
import org.axonframework.test.aggregate.AggregateTestFixture
import todo.domain.command.*
import todo.domain.event.*
import todo.helper.CompletionLatchFactory

import java.util.concurrent.CountDownLatch

import static java.util.Optional.of

class TodoListAggregateSpec extends Specification {
    TodoListAggregate aggregate
    TodoItem expectTodo
    def lifecycleSpy
    def aggregateModel
    EventBus eventBus
    CountDownLatch latch
    def expectId = "abc123"
    def expectTitle = "do something"
    def expectCompleted = false
    def expectOrder = 2

    def setup() {
        expectTodo = TodoItem.builder().id("abc123").title("do something").order( 2).build();
        lifecycleSpy = GroovySpy( AggregateLifecycle.class, global: true)
        eventBus = Mock()
        latch = Mock()
        aggregate = new TodoListAggregate()
        aggregateModel = ModelInspector.inspectAggregate( TodoListAggregate.class)
        AnnotatedAggregate.initialize( aggregate, aggregateModel, eventBus)
    }

    @Requires({ env['test.newtests'] })
    def "Add Item should succeed an invoke event generation"() {
        when:
            aggregate.addItem( expectId, expectTitle, expectCompleted, expectOrder, latch)
        then:
            1 * lifecycleSpy.apply( _)
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