package todo.domain;

import static java.util.Optional.ofNullable;

import org.axonframework.commandhandling.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import todo.domain.command.ClearTodoListCommand;
import todo.domain.command.CreateTodoItemCommand;
import todo.domain.command.DeleteTodoItemCommand;
import todo.domain.command.UpdateTodoItemCommand;
import todo.repo.TodoListRepository;

@Component
public class TodoItemCommandHandler {
	private TodoListRepository repository;
	
	@Autowired
	public TodoItemCommandHandler( TodoListRepository repository) {
	    this.repository = repository;
	}
	
	@CommandHandler
	public void create( CreateTodoItemCommand cmd) {
		TodoListAggregate todoListAggregate = repository.loadOrCreateInstance( cmd.getUserId());
		todoListAggregate.addItem( cmd.getItemId(), cmd.getTitle(), cmd.isCompleted(), cmd.getOrder(), cmd.getTrackerId());
	}
	
	@CommandHandler
	public void update( UpdateTodoItemCommand cmd) {
		TodoListAggregate todoListAggregate = repository.loadOrCreateInstance( cmd.getUserId());
		todoListAggregate.updateItem( cmd.getItemId(), ofNullable( cmd.getTitle()), ofNullable( cmd.getCompleted()), ofNullable( cmd.getOrder()), cmd.getTrackerId());
	}
	
	@CommandHandler
	public void delete( DeleteTodoItemCommand cmd) {
		TodoListAggregate todoListAggregate = repository.loadOrCreateInstance( cmd.getUserId());
		todoListAggregate.deleteItem( cmd.getItemId(), cmd.getTrackerId());	    	
	}
	
	@CommandHandler
	public void clear( ClearTodoListCommand cmd) {
		TodoListAggregate todoListAggregate = repository.loadOrCreateInstance( cmd.getUserId());
		todoListAggregate.clear( cmd.getTrackerId());	    	
	}
}
