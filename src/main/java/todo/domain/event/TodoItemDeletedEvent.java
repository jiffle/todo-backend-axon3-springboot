package todo.domain.event;

import java.util.Optional;

import lombok.NonNull;
import lombok.Value;

@Value
public class TodoItemDeletedEvent {
	@NonNull private final String itemId;
	private Optional<String> trackerId;
}
