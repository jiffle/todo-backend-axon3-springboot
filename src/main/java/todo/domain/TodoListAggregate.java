package todo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateRoot;
import org.axonframework.eventhandling.EventHandler;
import todo.domain.event.TodoItemCreatedEvent;
import todo.domain.event.TodoItemDeletedEvent;
import todo.domain.event.TodoItemUpdatedEvent;
import todo.domain.event.TodoListClearedEvent;
import todo.exception.ConflictException;
import todo.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static java.util.Optional.of;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/** Aggregate root for the TodoList (singleton per session)
 */

@Slf4j
@AggregateRoot
public class TodoListAggregate {
	@AggregateIdentifier
	@Getter @Setter private String id;

	private Map<String, TodoItem> todos;
	
	public TodoListAggregate() {
		todos = Collections.synchronizedMap( new HashMap<>());
	}

	public void addItem(String itemId, String title, Boolean completed, Integer order, CountDownLatch completionLatch) {
		TodoItem existing = todos.get( itemId);
		if( existing != null) {
			throw new ConflictException();
		}
		boolean isCompleted = completed != null ? completed.booleanValue() : false;
		apply( new TodoItemCreatedEvent( itemId, title, isCompleted, order, of(completionLatch)));
	}

	public void updateItem(String itemId, Optional<String> title, Optional<Boolean> completed, Optional<Integer> order, CountDownLatch completionLatch) {
		TodoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new NotFoundException();
		}
		apply( new TodoItemUpdatedEvent( itemId, title, completed, order, of(completionLatch)));
	}

	public void deleteItem(String itemId, CountDownLatch completionLatch) {
		TodoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new NotFoundException();
		}
		apply( new TodoItemDeletedEvent( itemId, of(completionLatch)));
	}
	
	public void clear( CountDownLatch completionLatch) {
		apply( new TodoListClearedEvent( of(completionLatch)));
	}
	
	public Collection<TodoItem> allValues() {
		return Collections.unmodifiableCollection( todos.values());
	}
	
	public TodoItem getValue(String itemId) {
        return todos.get( itemId);
	}
	
	@EventHandler
    public void handle( TodoItemCreatedEvent event) {
		TodoItem item = TodoItem.builder()
				.id( event.getItemId())
				.title( event.getTitle())
				.completed( event.isCompleted())
				.order( event.getOrder())
				.build();
		todos.put( event.getItemId(), item);

		event.getCompletionLatch().ifPresent( CountDownLatch::countDown);
    }
	
	@EventHandler
    public void handle( TodoItemUpdatedEvent event) {
		TodoItem item = todos.get( event.getItemId());
		if( item != null) {
			event.getTitle().ifPresent( item::setTitle);
			event.getCompleted().ifPresent( item::setCompleted);
			event.getOrder().ifPresent( item::setOrder);
		} else {
			log.error( "Received update for non-existent todo item");			
		}

		event.getCompletionLatch().ifPresent( CountDownLatch::countDown);
    }
	
	@EventHandler
    public void handle( TodoItemDeletedEvent event) {
		TodoItem item = todos.remove( event.getItemId());
		if( item == null) {
			log.error( "Received deletion for non-existent todo item");			
		}

		event.getCompletionLatch().ifPresent( CountDownLatch::countDown);
    }
	
	@EventHandler
    public void handle( TodoListClearedEvent event) {
		todos.clear();

		event.getCompletionLatch().ifPresent( CountDownLatch::countDown);
    }

}
