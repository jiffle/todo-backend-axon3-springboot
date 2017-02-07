package todo.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import todo.domain.TodoItem;
import todo.helper.NotFoundException;
import todo.web.TodoController;

@Component
public class TodoItemViewFactory {
    private String apiRoot;

    @Autowired
    public TodoItemViewFactory(@Value("${api.root}") String apiRoot) {
        this.apiRoot = apiRoot;
    }

    private String todoUrlFor(String id) {
        return apiRoot + TodoController.TODO_URL.replace("{id}", id);
    }
    
    public TodoItemView buildItem(TodoItem item) {
    	if( item == null) {
    		throw new NotFoundException( "Todo Item was not found");
    	}
    	return new TodoItemView.Builder()
    			.todoItem(item)
    			.url( todoUrlFor( item.getId()))
    			.build();
    }
    
    public List<TodoItemView> buildList(Collection<TodoItem> list) {
    	return list.stream().map( this:: buildItem).collect( Collectors.toList());
    }
}
