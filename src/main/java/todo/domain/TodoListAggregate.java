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
import todo.domain.event.ToDoItemCreatedEvent;
import todo.domain.event.ToDoItemDeletedEvent;
import todo.domain.event.ToDoListClearedEvent;
import todo.domain.event.TodoItemUpdatedEvent;
import todo.helper.ConflictException;
import todo.helper.ResourceNotFoundException;
import todo.middleware.CompletionTracker;

/** Aggregate root for the TodoList (singleton per session)
 */

@Slf4j
@AggregateRoot
public class TodoListAggregate {
	@AggregateIdentifier
	private String id;
	private CompletionTracker tracker;

	private Map<String, ToDoItem> todos;
	
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
		ToDoItem existing = todos.get( itemId);
		if( existing != null) {
			throw new ConflictException();
		}
		apply( new ToDoItemCreatedEvent( itemId, title, completed, order, trackerId));		
	}

	public void updateItem(String itemId, Optional<String> title, Optional<Boolean> completed, Optional<Integer> order, Optional<String> trackerId) {
		ToDoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new ResourceNotFoundException();
		}
		apply( new TodoItemUpdatedEvent( itemId, title, completed, order, trackerId));		
	}

	public void deleteItem(String itemId, Optional<String> trackerId) {
		ToDoItem existing = todos.get( itemId);
		if( existing == null) {
			throw new ResourceNotFoundException();
		}
		apply( new ToDoItemDeletedEvent( itemId, trackerId));		
	}
	
	public void clear( Optional<String> trackerId) {
		apply( new ToDoListClearedEvent( trackerId));		
	}
	
	public Collection<ToDoItem> allValues() {
		return Collections.unmodifiableCollection( todos.values());
	}
	
	public ToDoItem getValue(String itemId) {
		return todos.get( itemId);
	}
	
	@EventHandler
    public void handle( ToDoItemCreatedEvent event) {
		ToDoItem item = new ToDoItem.Builder()
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
		ToDoItem item = todos.get( event.getItemId());
		if( item != null) {
			event.getTitle().ifPresent( x -> item.setTitle( x));
			event.getCompleted().ifPresent( x -> item.setCompleted( x));
			event.getOrder().ifPresent( x -> item.setOrder( x));	
		} else {
			log.error( "Received update for non-existent todo item");			
		}
		
		event.getTrackerId().ifPresent( x -> tracker.getItemTracker().completeTracker( x, item));
    }
	
	@EventHandler
    public void handle( ToDoItemDeletedEvent event) {
		ToDoItem item = todos.remove( event.getItemId());
		if( item == null) {
			log.error( "Received deletion for non-existent todo item");			
		}
		
		event.getTrackerId().ifPresent( x -> tracker.getItemTracker().completeTracker( x, item));
    }
	
	@EventHandler
    public void handle( ToDoListClearedEvent event) {
		todos.clear();;
		
		event.getTrackerId().ifPresent( x -> tracker.getListTracker().completeTracker( x, todos.values()));
    }

}
