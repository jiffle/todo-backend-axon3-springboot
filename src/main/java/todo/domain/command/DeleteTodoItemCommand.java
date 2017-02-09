package todo.domain.command;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class DeleteTodoItemCommand {
    @TargetAggregateIdentifier
    @NonNull private final String userId;
    @NonNull private final String itemId;
    private CountDownLatch completionLatch;
}
