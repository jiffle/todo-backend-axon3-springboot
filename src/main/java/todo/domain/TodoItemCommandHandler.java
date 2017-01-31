package todo.domain;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import todo.domain.command.CreateToDoItemCommand;
import todo.domain.command.DeleteToDoItemCommand;
import todo.domain.command.UpdateToDoItemCommand;
import todo.domain.event.ToDoItemCreatedEvent;
import todo.domain.event.ToDoItemDeletedEvent;
import todo.domain.event.TodoItemUpdatedEvent;
import todo.persistance.TodoList;

@Component
public class TodoItemCommandHandler {
	    private TodoList repository;
	    private EventBus eventBus;

	    @Autowired
	    public TodoItemCommandHandler( TodoList repository, EventBus eventBus) {
	        this.repository = repository;
	        this.eventBus = eventBus;
	    }
	    
	    @CommandHandler
	    public void create(CreateToDoItemCommand command) {
	    	
	        apply(new ToDoItemCreatedEvent( command.getTodoId(), command.getTodo().getTitle(), command.getTodo().isCompleted(), command.getTodo().getOrder()));
	        
	        repository.add( new ToDoItem( command.getTodoId(), command.getTodo().));
	        
	    }

	    @CommandHandler
	    public void update(UpdateToDoItemCommand command) {
	        apply(new TodoItemUpdatedEvent( command.getTodoId(), command.getTodoUpdates()));
	    }

	    @CommandHandler
	    public void delete(DeleteToDoItemCommand command) {
	        apply(new ToDoItemDeletedEvent( command.getTodoId()));
	    }

}
