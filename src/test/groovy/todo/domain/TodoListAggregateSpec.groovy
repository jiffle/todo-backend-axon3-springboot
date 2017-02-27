package todo.domain

import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate
import org.axonframework.commandhandling.model.inspection.ModelInspector
import org.axonframework.eventhandling.EventBus
import spock.lang.*
import org.axonframework.test.aggregate.AggregateTestFixture
import todo.domain.command.*
import todo.domain.event.*
import todo.exception.ConflictException
import todo.exception.NotFoundException
import todo.helper.CompletionLatchFactory

import java.util.concurrent.CountDownLatch

import static java.util.Optional.empty
import static java.util.Optional.of

class TodoListAggregateSpec extends Specification {
    TodoListAggregate aggregate
    TodoListAggregate.Lifecycle lifecycle
    TodoItem expectTodo
    CountDownLatch latch
    def expectId = "abc123"
    def expectTitle = "do something"
    def expectCompleted = false
    def expectOrder = 2

    def setup() {
        expectTodo = TodoItem.builder().id( expectId).title(expectTitle).order( expectOrder).build();
        lifecycle = Mock()
        latch = Mock()
        aggregate = TodoListAggregate.createTodoListAggregate( lifecycle)
    }

    def "Add Item should emit a create event"() {
        when:
            aggregate.addItem( expectId, expectTitle, expectCompleted, expectOrder, latch)
        then:
            1 * lifecycle.apply({ TodoItemCreatedEvent e ->
                e.itemId == expectId &&
                e.title == expectTitle &&
                e.order == expectOrder &&
                e.completed == expectCompleted &&
                e.completionLatch == of(latch)}
            ) >> null
    }

    def "Add Item with existing item should throw exception"() {
        given:
        aggregate.handle( new TodoItemCreatedEvent( expectId, "Old Title", false, -1, empty()))
        when:
        aggregate.addItem( expectId, expectTitle, expectCompleted, expectOrder, latch)
        then:
            thrown( ConflictException)
    }

    def "Update Item with all optional fields filled should emit an update event"() {
        given:
            aggregate.handle( new TodoItemCreatedEvent( expectId, "Old Title", false, -1, empty()))
        when:
            aggregate.updateItem( expectId, of(expectTitle), of(expectCompleted), of(expectOrder), latch)
        then:
        1 * lifecycle.apply({ TodoItemUpdatedEvent e ->
            e.itemId == expectId &&
                    e.title == of(expectTitle) &&
                    e.order == of(expectOrder) &&
                    e.completed == of(expectCompleted) &&
                    e.completionLatch == of(latch)}
        ) >> null
    }

    def "Update Item with no optional fields filled should emit an update event"() {
        given:
            aggregate.handle( new TodoItemCreatedEvent( expectId, "Old Title", false, -1, empty()))
        when:
            aggregate.updateItem( expectId, empty(), empty(), empty(), latch)
        then:
        1 * lifecycle.apply({ TodoItemUpdatedEvent e ->
            e.itemId == expectId &&
                    e.title == empty() &&
                    e.order == empty() &&
                    e.completed == empty() &&
                    e.completionLatch == of(latch)}
        ) >> null
    }

    def "Update Item for missing item should throw not found exception"() {
        when:
        aggregate.updateItem( expectId, of(expectTitle), of(expectCompleted), of(expectOrder), latch)
        then:
        thrown( NotFoundException)
    }

    def "Delete Item should emit a delete event"() {
        given:
            aggregate.handle( new TodoItemCreatedEvent( expectId, "Old Title", false, -1, empty()))
        when:
            aggregate.deleteItem( expectId, latch)
        then:
        1 * lifecycle.apply({ TodoItemDeletedEvent e ->
            e.itemId == expectId &&
                    e.completionLatch == of(latch)}
        ) >> null
    }

    def "Delete Item for missing item should throw not found exception"() {
        when:
        aggregate.deleteItem( expectId, latch)
        then:
        thrown( NotFoundException)
    }

    def "Clear should emit a clear event"() {
        given:
            aggregate.handle( new TodoItemCreatedEvent( expectId, "Old Title", false, -1, empty()))
        when:
            aggregate.clear( latch)
        then:
        1 * lifecycle.apply({ TodoListClearedEvent e ->
            e.completionLatch == of(latch)}
        ) >> null
    }

    def "Handle create item event should add a todo item"() {
        when:
            aggregate.handle( new TodoItemCreatedEvent( expectId, expectTitle, expectCompleted, expectOrder, of( latch)))
        then:
        assertStandardItem( aggregate.getValue( expectId))
        1 * latch.countDown()
    }

    def "Handle update item event with all fields should update the todo item"() {
        given:
            aggregate.handle( new TodoItemCreatedEvent( expectId, "random title", true, -1, empty()))
        when:
            aggregate.handle( new TodoItemUpdatedEvent( expectId, of(expectTitle), of(expectCompleted), of(expectOrder), of( latch)))
        then:
        def result = aggregate.getValue( expectId)
        assertStandardItem( result)
        1 * latch.countDown()
    }

    def "Handle update item event with no fields should update the todo item"() {
        given:
        aggregate.handle( new TodoItemCreatedEvent( expectId, expectTitle, expectCompleted, expectOrder, empty()))
        when:
        aggregate.handle( new TodoItemUpdatedEvent( expectId, empty(), empty(), empty(), of( latch)))
        then:
        def result = aggregate.getValue( expectId)
        assertStandardItem( result)
        1 * latch.countDown()
    }

    def "Handle delete item event should delete the todo item"() {
        given:
        aggregate.handle( new TodoItemCreatedEvent( expectId, expectTitle, expectCompleted, expectOrder, empty()))
        when:
        aggregate.handle( new TodoItemDeletedEvent( expectId, of( latch)))
        then:
        def result = aggregate.getValue( expectId)
        result == null
        1 * latch.countDown()
    }

    def "Handle clear list event should empty the list"() {
        given:
        aggregate.handle( new TodoItemCreatedEvent( expectId, expectTitle, expectCompleted, expectOrder, empty()))
        when:
        aggregate.handle( new TodoListClearedEvent( of( latch)))
        then:
        aggregate.allValues().empty
        1 * latch.countDown()
    }

    void assertStandardItem( TodoItem result) {
        assert result != null
        assert result.id == expectId
        assert result.title == expectTitle
        assert result.completed == expectCompleted
        assert result.order == expectOrder
    }
}