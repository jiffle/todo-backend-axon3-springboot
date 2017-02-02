package todo.domain.command;

import java.util.Optional;
import java.util.concurrent.Callable;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class CreateToDoItemCommand {

    @TargetAggregateIdentifier
    @NonNull private final String userId;
    @NonNull private final String itemId;
	@NonNull private String title;
	private boolean completed;
	@NonNull private Integer order;
	private Optional<String> trackerId;
}
