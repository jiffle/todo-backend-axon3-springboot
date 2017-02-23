package todo.domain.command;

import lombok.NonNull;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.concurrent.CountDownLatch;

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
