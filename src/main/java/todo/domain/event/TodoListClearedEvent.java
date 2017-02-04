package todo.domain.event;

import java.util.Optional;

import lombok.NonNull;
import lombok.Value;

@Value
public class TodoListClearedEvent {
	private Optional<String> trackerId;
}
