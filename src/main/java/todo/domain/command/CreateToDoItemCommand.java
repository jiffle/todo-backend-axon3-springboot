package todo.domain.command;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;
import todo.view.ToDoItemView;

@Value
public class CreateToDoItemCommand {

    @TargetAggregateIdentifier
    @NonNull private final String todoId;
    @NonNull private final ToDoItemView todo;

}
