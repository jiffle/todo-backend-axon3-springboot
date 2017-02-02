package todo.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateRoot;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import todo.domain.command.CreateToDoItemCommand;
import todo.domain.command.DeleteToDoItemCommand;
import todo.domain.command.UpdateToDoItemCommand;
import todo.domain.event.ToDoItemCreatedEvent;
import todo.domain.event.ToDoItemDeletedEvent;
import todo.domain.event.TodoItemUpdatedEvent;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

//@AggregateRoot
//@Component
@Deprecated
public class ToDoItemAggregate {} /* {
    @AggregateIdentifier
    private String id;

    public ToDoItemAggregate() {
    }

    @CommandHandler
    public ToDoItemAggregate(CreateToDoItemCommand command) {
        apply(new ToDoItemCreatedEvent(command.getTodoId(), command.getTodo()));
    }

    @CommandHandler
    public void update(UpdateToDoItemCommand command) {
        apply(new TodoItemUpdatedEvent(id, command.getTodoUpdates()));
    }

    @CommandHandler
    public void delete(DeleteToDoItemCommand command) {
        apply(new ToDoItemDeletedEvent(id));
    }

    @EventHandler
    public void on(ToDoItemCreatedEvent event) {
        this.id = event.getTodoId();
    }
}
*/