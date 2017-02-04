package todo.domain.command;

import java.util.Optional;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.NonNull;
import lombok.Value;

@Value
public class ClearTodoListCommand {
    @TargetAggregateIdentifier
    @NonNull private final String userId;
	private Optional<String> trackerId;
}