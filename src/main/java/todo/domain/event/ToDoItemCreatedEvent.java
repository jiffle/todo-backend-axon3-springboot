package todo.domain.event;

import lombok.NonNull;
import lombok.Value;
import todo.domain.ToDoItem;

@Value
public class ToDoItemCreatedEvent {
	@NonNull private final String todoId;
	@NonNull private final ToDoItem todo;
}
