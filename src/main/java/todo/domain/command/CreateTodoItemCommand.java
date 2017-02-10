package todo.domain.command;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class CreateTodoItemCommand {

    @TargetAggregateIdentifier
    @NonNull private final String userId;
    @NonNull private final String itemId;
	@NonNull private String title;
	private Boolean completed;
	private Integer order;
	private CountDownLatch completionLatch;
}
