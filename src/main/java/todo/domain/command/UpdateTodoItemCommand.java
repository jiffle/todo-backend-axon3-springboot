package todo.domain.command;

import java.util.Optional;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class UpdateTodoItemCommand {

    @TargetAggregateIdentifier
    @NonNull private final String userId;
    @NonNull private final String itemId;
	private String title;
	private Boolean completed;
	private Integer order;
	private Optional<String> trackerId;
}
