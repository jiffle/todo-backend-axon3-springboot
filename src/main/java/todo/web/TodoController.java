package todo.web;

import static java.util.Optional.of;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import todo.domain.TodoItem;
import todo.domain.command.ClearTodoListCommand;
import todo.domain.command.CreateTodoItemCommand;
import todo.domain.command.DeleteTodoItemCommand;
import todo.domain.command.UpdateTodoItemCommand;
import todo.facade.TodoFacadeService;
import todo.middleware.CompletionTracker;
import todo.query.TodoQueryService;
import todo.view.TodoItemView;
import todo.view.TodoItemViewFactory;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/todos")
public class TodoController {
    public static final String TODO_URL = "/{id}";
    private static final String USER_ID = "1";

    private final TodoFacadeService facadeService;
    private final CommandGateway commandGateway;
    private final TodoQueryService queryService;
    private final TodoItemViewFactory viewFactory;
    private final CompletionTracker completionTracker;

    @Autowired
    public TodoController( @NonNull TodoFacadeService facadeService, @NonNull CommandGateway commandGateway, @NonNull TodoQueryService queryService, @NonNull TodoItemViewFactory toDoItemViewFactory, CompletionTracker completionTracker) {
        this.facadeService = facadeService;
        this.commandGateway = commandGateway;
        this.queryService = queryService;
        this.viewFactory = toDoItemViewFactory;
        this.completionTracker = completionTracker;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<TodoItemView> index() {

        return viewFactory.buildList( facadeService.getList( USER_ID));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TodoItemView> create(@RequestBody @Valid TodoItemView todo) throws Throwable {
        String itemId = UUID.randomUUID().toString();

        todo.setCompleted(false);
        todo.setId( itemId);
        
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker(trackerId);
        try {
        	commandGateway.sendAndWait( new CreateTodoItemCommand( USER_ID, itemId, todo.getTitle(), todo.getCompleted(), todo.getOrder(), of( trackerId)),
        			1, TimeUnit.SECONDS);
			TodoItem item = future.get(1, TimeUnit.SECONDS);
			TodoItemView result = viewFactory.buildItem( item);
	        return new ResponseEntity<>( result, HttpStatus.CREATED);
		} catch (CommandExecutionException e) {
			if( e.getCause() != null) {
				throw e.getCause();
			}
			log.error( "Got CommandExecutionException with no underlying cause", e);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error( "Could not retrieve response to render output", e);
		}
        return new ResponseEntity<TodoItemView>( HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody Collection<TodoItemView> clear() {
        Collection<TodoItem> items = facadeService.deleteList( USER_ID);
        return viewFactory.buildList( items);
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.GET)
    public @ResponseBody
    TodoItemView show(@PathVariable String id) {
        return viewFactory.buildItem( queryService.queryListForItem(USER_ID, id));
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.PATCH)
    public ResponseEntity<TodoItemView> update(@PathVariable String id, @RequestBody TodoItemView todo) throws Throwable {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker(trackerId);
        try {
        	commandGateway.sendAndWait( new UpdateTodoItemCommand( USER_ID, id, todo.getTitle(), todo.getCompleted(), todo.getOrder(), of( trackerId)),
        			1, TimeUnit.SECONDS);
			TodoItem item = future.get(1, TimeUnit.SECONDS);
			TodoItemView result = viewFactory.buildItem( item);
	        return new ResponseEntity<>( result, HttpStatus.OK);
		} catch (CommandExecutionException e) {
			if( e.getCause() != null) {
				throw e.getCause();
			}
			log.error( "Got CommandExecutionException with no underlying cause", e);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error( "Could not retrieve response to render output", e);
		}
        return new ResponseEntity<TodoItemView>( HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable String id) throws Throwable {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker(trackerId);
        try {
        	commandGateway.sendAndWait( new DeleteTodoItemCommand( USER_ID, id, of( trackerId)),
        			1, TimeUnit.SECONDS);
			TodoItem item = future.get(1, TimeUnit.SECONDS);
	        return new ResponseEntity<>( "{}", HttpStatus.OK);
		} catch (CommandExecutionException e) {
			if( e.getCause() != null) {
				throw e.getCause();
			}
			log.error( "Got CommandExecutionException with no underlying cause", e);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error( "Could not retrieve response to render output", e);
		}
        return new ResponseEntity<String>( HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
