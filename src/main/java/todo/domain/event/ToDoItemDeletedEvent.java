package todo.domain.event;

import lombok.NonNull;
import lombok.Value;

@Value
public class ToDoItemDeletedEvent {
	@NonNull private final String todoId;

}
