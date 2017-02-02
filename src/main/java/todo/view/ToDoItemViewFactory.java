package todo.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import todo.domain.ToDoItem;
import todo.helper.ResourceNotFoundException;
import todo.web.ToDoController;

@Component
public class ToDoItemViewFactory {
    private String apiRoot;

    @Autowired
    public ToDoItemViewFactory(@Value("${api.root}") String apiRoot) {
        this.apiRoot = apiRoot;
    }

    private String todoUrlFor(String id) {
        return apiRoot + ToDoController.TODO_URL.replace("{id}", id);
    }
    
    public ToDoItemView buildItem( ToDoItem item) {
    	if( item == null) {
    		throw new ResourceNotFoundException( "Todo Item was not found");
    	}
    	return new ToDoItemView.Builder()
    			.todoItem(item)
    			.url( todoUrlFor( item.getId()))
    			.build();
    }
    
    public List< ToDoItemView> buildList( Collection< ToDoItem> list) {
    	return list.stream().map( this:: buildItem).collect( Collectors.toList());
    }
}
