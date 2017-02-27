package todo.domain.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Value
@RequiredArgsConstructor
public class TodoItemCreatedEvent {
	@NonNull private final String itemId;
	@NonNull private String title;
	private boolean completed;
	private Integer order;
	@NonNull private Optional<CountDownLatch> completionLatch;
}
