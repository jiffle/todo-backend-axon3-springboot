package todo.domain.event;

import java.util.Optional;

import lombok.NonNull;
import lombok.Value;

/** Update done as a single event to allow consistent state to be reported.
 * Ideally each property would have an individual event
 */
@Value
public class TodoItemUpdatedEvent {
	@NonNull private final String itemId;
	private Optional<String> title;
	private Optional<Boolean> completed;
	private Optional<Integer> order;
	private Optional<String> trackerId;
}
