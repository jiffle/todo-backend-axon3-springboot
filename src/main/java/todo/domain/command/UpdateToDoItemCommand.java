package todo.domain.command;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;
import todo.domain.ToDoItem;

@Value
public class UpdateToDoItemCommand {

    @TargetAggregateIdentifier
    @NonNull private final String todoId;
    @NonNull private final ToDoItem todoUpdates;

}
