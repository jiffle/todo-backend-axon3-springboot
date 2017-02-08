package todo.facade;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import todo.domain.TodoItem;
import todo.domain.command.ClearTodoListCommand;
import todo.domain.command.CreateTodoItemCommand;
import todo.domain.command.DeleteTodoItemCommand;
import todo.domain.command.UpdateTodoItemCommand;
import todo.helper.InternalServerErrorException;
import todo.middleware.CompletionTracker;
import todo.query.TodoQueryService;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Optional.of;

/** Provides a service to abstract the complexities away from the controller class, and a facade pattern onto the backend services
 */
@Slf4j
@Service
public class TodoFacadeService {
    private final TodoQueryService queryService;
    private final CommandGateway commandGateway;
    private final CompletionTracker completionTracker;

    @Autowired
    public TodoFacadeService(TodoQueryService queryService, CommandGateway commandGateway, CompletionTracker completionTracker) {
        this.queryService = queryService;
        this.commandGateway = commandGateway;
        this.completionTracker = completionTracker;
    }

    public Collection<TodoItem> getList( String userId) {
        return queryService.queryListForUser( userId);
    }

    public TodoItem getItem( String userId, String itemId) {
        return queryService.queryListForItem(userId, itemId);
    }

    public TodoItem createItem( String userId, String itemId, String title, boolean completed, Integer order) throws Throwable {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker( trackerId);
        try {
            commandGateway.sendAndWait( new CreateTodoItemCommand( userId, itemId, title, completed, order, of( trackerId)),
                    1, TimeUnit.SECONDS);
            return future.get(1, TimeUnit.SECONDS);
        } catch (CommandExecutionException e) {
            if( e.getCause() != null) {
                throw e.getCause();
            }
            log.error( "Got CommandExecutionException with no underlying cause", e);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error( "Could not retrieve response to render output", e);
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public TodoItem updateItem( String userId, String itemId, String title, Boolean completed, Integer order) {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker(trackerId);
        try {
            commandGateway.sendAndWait( new UpdateTodoItemCommand( userId, itemId, title, completed, order, of( trackerId)),
                    1, TimeUnit.SECONDS);
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error( "Could not retrieve response to render output", e);
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public TodoItem deleteItem( String userId, String itemId) throws Throwable {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<TodoItem> future = completionTracker.getItemTracker().addTracker(trackerId);
        try {
            commandGateway.sendAndWait( new DeleteTodoItemCommand( userId, itemId, of( trackerId)),
                    1, TimeUnit.SECONDS);
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error( "Could not retrieve response to render output", e);
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public Collection<TodoItem> deleteList(String userId) {
        String trackerId = UUID.randomUUID().toString();
        CompletableFuture<Collection<TodoItem>> future = completionTracker.getListTracker().addTracker( trackerId);
        commandGateway.send(new ClearTodoListCommand( userId, of(trackerId)));
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Could not retrieve response to render output", e);
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

}