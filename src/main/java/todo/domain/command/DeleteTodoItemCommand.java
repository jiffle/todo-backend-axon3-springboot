package todo.domain.command;

import lombok.NonNull;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.concurrent.CountDownLatch;

@Value
public class DeleteTodoItemCommand {
    @TargetAggregateIdentifier
    @NonNull private final String userId;
    @NonNull private final String itemId;
    private CountDownLatch completionLatch;
}
