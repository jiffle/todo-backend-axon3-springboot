package todo.domain.event;

import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ToDoItemCreatedEvent {
	@NonNull private final String itemId;
	@NonNull private String title;
	@NonNull private Boolean completed;
	@NonNull private Integer order;
	private Optional<String> trackerId;
}
