package todo.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateRoot;
import org.axonframework.eventhandling.EventHandler;

import lombok.extern.slf4j.Slf4j;
import todo.domain.event.TodoItemCreatedEvent;
import todo.domain.event.TodoItemDeletedEvent;
import todo.domain.event.TodoListClearedEvent;
import todo.domain.event.TodoItemUpdatedEvent;
import todo.helper.ConflictException;
import todo.helper.NotFoundException;
import todo.middleware.CompletionTracker;

/** Aggregate root for the TodoList (singleton per session)
 */

@Slf4j
@AggregateRoot
public class TodoListAggregate {
	@AggregateIdentifier
	private String id;
	private CompletionTracker tracker;

	private Map<String, TodoItem> todos;
	
	public TodoListAggregate() {
		todos = new HashMap<>();
	}
	
	public void setId( String id) {
		this.id = id;
	}
	
	public void setTracker( CompletionTracker tracker) {
		this.tracker = tracker;
	}

	public void addItem(String itemId, String title, boolean completed, Integer order, Optional<String> trackerId) {
		TodoItem existing = todos.get( itemId);
		if( existing != null) {
			throw new ConflictException();
		}
		apply( new TodoItemCreatedEvent( itemId, title, completed, order, trackerId));
	}

	public void updateItem(String itemId, Optional<String> title, Optional<Boolean> completed, Optional<Integer> order, Optional<String> trackerId) {
		TodoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new NotFoundException();
		}
		apply( new TodoItemUpdatedEvent( itemId, title, completed, order, trackerId));		
	}

	public void deleteItem(String itemId, Optional<String> trackerId) {
		TodoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new NotFoundException();
		}
		apply( new TodoItemDeletedEvent( itemId, trackerId));
	}
	
	public void clear( Optional<String> trackerId) {
		apply( new TodoListClearedEvent( trackerId));
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
				.completed( event.getCompleted())
				.order( event.getOrder())
				.build();
		todos.put( event.getItemId(), item);

		event.getTrackerId().ifPresent( x -> tracker.getItemTracker().completeTracker( x, item));
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
		
		event.getTrackerId().ifPresent( x -> tracker.getItemTracker().completeTracker( x, item));
    }
	
	@EventHandler
    public void handle( TodoItemDeletedEvent event) {
		TodoItem item = todos.remove( event.getItemId());
		if( item == null) {
			log.error( "Received deletion for non-existent todo item");			
		}
		
		event.getTrackerId().ifPresent( x -> tracker.getItemTracker().completeTracker( x, item));
    }
	
	@EventHandler
    public void handle( TodoListClearedEvent event) {
		todos.clear();
		
		event.getTrackerId().ifPresent( x -> tracker.getListTracker().completeTracker( x, todos.values()));
    }

}
