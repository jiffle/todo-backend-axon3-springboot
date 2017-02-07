package todo.query;

import todo.domain.TodoItem;

import java.util.Collection;

/** Service for querying to do lists and items */
public interface TodoQueryService {
    Collection<TodoItem> queryListForUser(String userId);

    TodoItem queryListForItem(String userId, String itemId);
}
