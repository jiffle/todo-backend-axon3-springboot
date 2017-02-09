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
import todo.exception.InternalServerErrorException;
import todo.helper.CompletionLatchFactory;
import todo.middleware.CompletionTracker;
import todo.query.TodoQueryService;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.*;

import static java.util.Optional.of;

/** Provides a service to abstract the complexities away from the controller class, and a facade pattern onto the backend services
 */
@Slf4j
@Service
public class TodoFacadeService {
    private final TodoQueryService queryService;
    private final CommandGateway commandGateway;
    private CompletionLatchFactory latchFactory;

    @Autowired
    public TodoFacadeService(TodoQueryService queryService, CommandGateway commandGateway, CompletionLatchFactory latchFactory) {
        this.queryService = queryService;
        this.commandGateway = commandGateway;
        this.latchFactory = latchFactory;
    }

    public Collection<TodoItem> getList( String userId) {
        return queryService.queryListForUser( userId);
    }

    public TodoItem getItem( String userId, String itemId) {
        return queryService.queryListForItem(userId, itemId);
    }

    public TodoItem createItem( String userId, String itemId, String title, boolean completed, Integer order) throws Throwable {
        CountDownLatch latch = latchFactory.createInstance();
        try {
            commandGateway.sendAndWait( new CreateTodoItemCommand( userId, itemId, title, completed, order, latch),
                    1, TimeUnit.SECONDS);
            if( latch.await( 1, TimeUnit.SECONDS)) {
                return getItem( userId, itemId);
            }
        } catch (CommandExecutionException e) {
            if( e.getCause() != null) {
                throw e.getCause();
            }
            log.error( "Got CommandExecutionException with no underlying cause", e);
        } catch (InterruptedException e) {
            log.error( "Interrupted waiting for response to complete");
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public TodoItem updateItem( String userId, String itemId, String title, Boolean completed, Integer order) {
        CountDownLatch latch = latchFactory.createInstance();
        try {
            commandGateway.sendAndWait( new UpdateTodoItemCommand( userId, itemId, title, completed, order, latch),
                    1, TimeUnit.SECONDS);
            if( latch.await( 1, TimeUnit.SECONDS)) {
                return getItem( userId, itemId);
            }
        } catch (InterruptedException e) {
            log.error( "Interrupted waiting for response to complete");
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public TodoItem deleteItem( String userId, String itemId) throws Throwable {
        CountDownLatch latch = latchFactory.createInstance();
        try {
            commandGateway.sendAndWait( new DeleteTodoItemCommand( userId, itemId, latch),
                    1, TimeUnit.SECONDS);
            if( latch.await( 1, TimeUnit.SECONDS)) {
                return getItem( userId, itemId);
            }
        } catch (InterruptedException e) {
            log.error( "Interrupted waiting for response to complete");
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

    public Collection<TodoItem> deleteList(String userId) {
        CountDownLatch latch = latchFactory.createInstance();
        commandGateway.send(new ClearTodoListCommand( userId, latch));
        try {
            if( latch.await( 1, TimeUnit.SECONDS)) {
                return getList( userId);
            }
        } catch (InterruptedException e) {
            log.error( "Interrupted waiting for response to complete");
        }
        throw new InternalServerErrorException( "Timeout waiting for action to be processed");
    }

}