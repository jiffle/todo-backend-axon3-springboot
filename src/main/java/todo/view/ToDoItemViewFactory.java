package todo.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import todo.domain.ToDoItem;
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
    	return new ToDoItemView( item, todoUrlFor( item.getId()));
    }
    
    public List< ToDoItemView> buildList( Collection< ToDoItem> list) {
    	return list.stream().map( this:: buildItem).collect( Collectors.toList());
    }
}
