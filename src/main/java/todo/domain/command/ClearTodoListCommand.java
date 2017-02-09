package todo.domain.command;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class ClearTodoListCommand {
    @TargetAggregateIdentifier
    @NonNull private final String userId;
    private CountDownLatch completionLatch;
}
