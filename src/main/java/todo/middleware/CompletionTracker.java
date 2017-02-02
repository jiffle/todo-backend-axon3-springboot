package todo.middleware;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import todo.domain.ToDoItem;

/** Tracks aggregate updates that need to be rendered as responses
 */
@Component
@Slf4j
@Getter
public class CompletionTracker {
	private CompletableStatus< ToDoItem> itemTracker = new CompletableStatus<>();
	private CompletableStatus< Collection<ToDoItem>> listTracker = new CompletableStatus<>();
		
	public class CompletableStatus<T> {
		private Map<String, CompletableFuture<T>> trackers = new HashMap<>();
		public void addTracker( String id, CompletableFuture<T> future) {
			CompletableFuture<T> old = trackers.put( id, future);
			if (old != null) {
				log.warn( "Found existing Completion Tracker for when adding new one for id={}", id);
			}
		}
		public boolean completeTracker( String id, T data) {
			CompletableFuture<T> future = trackers.get( id);
			if( future == null) {
				log.warn( "Could not find Completion Tracker for id={}", id);
			}
			return future.complete( data);
		}
	}
}
