package todo.persistance;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import todo.domain.ToDoItem;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TodoList {
    private List<ToDoItem> todos;

    public TodoList() {
        todos = new ArrayList<ToDoItem>();
    }

    public List<ToDoItem> all() {
        return todos;
    }

    public ToDoItem get(String id) {
        for (ToDoItem todo : todos) {
            if(todo.getId().equals(id)) {
                return todo;
            }
        }

        return null;
    }

    public void add(ToDoItem todo) {
        todos.add(todo);
    }

    public void clear() {
        todos.clear();
    }

    public ToDoItem remove(String id) {
        ToDoItem todo = get(id);
        todos.remove(todo);
        return todo;
    }
}
