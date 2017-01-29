package todo.view;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import todo.domain.ToDoItem;

@RequiredArgsConstructor
public class ToDoItemView {
	@Delegate
	@NonNull private final ToDoItem todo;
	@NonNull private final String todoUrl;

    public String getUrl() {
        return todoUrl;
    }

}
