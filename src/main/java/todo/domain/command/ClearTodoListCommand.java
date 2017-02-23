package todo.domain.command;

import lombok.NonNull;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.concurrent.CountDownLatch;

@Value
public class ClearTodoListCommand {
    @TargetAggregateIdentifier
    @NonNull private final String userId;
    private CountDownLatch completionLatch;
}
