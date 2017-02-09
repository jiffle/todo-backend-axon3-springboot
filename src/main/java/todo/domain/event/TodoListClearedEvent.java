package todo.domain.event;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import lombok.NonNull;
import lombok.Value;

@Value
public class TodoListClearedEvent {
	@NonNull private Optional<CountDownLatch> completionLatch;
}
