package todo.middleware;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import todo.domain.TodoItem;

/** Tracks aggregate updates that need to be rendered as responses
 */
@Component
@Slf4j
@Getter
public class CompletionTracker {
	private CompletableStatus<TodoItem> itemTracker = new CompletableStatus<>();
	private CompletableStatus< Collection<TodoItem>> listTracker = new CompletableStatus<>();
		
	public static class CompletableStatus<T> {
		private Map<String, CompletableFuture<T>> trackers = new HashMap<>();
		public CompletableFuture<T> addTracker( String id) {
			CompletableFuture<T> result = new CompletableFuture<>();
			CompletableFuture<T> old = trackers.put( id, result);
			if (old != null) {
				log.warn( "Found existing Completion Tracker for when adding new one for id={}", id);
			}
			return result;
		}
		public boolean completeTracker( String id, T data) {
			CompletableFuture<T> future = trackers.get( id);
			if( future == null) {
				log.warn( "Could not find Completion Tracker for id={}", id);
				return false;
			}
			return future.complete( data);
		}
	}
}
