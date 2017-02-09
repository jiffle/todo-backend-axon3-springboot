package todo.domain.event;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class TodoItemCreatedEvent {
	@NonNull private final String itemId;
	@NonNull private String title;
	@NonNull private Boolean completed;
	@NonNull private Integer order;
	@NonNull private Optional<CountDownLatch> completionLatch;
}
