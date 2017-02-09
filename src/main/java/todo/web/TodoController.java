package todo.web;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import todo.domain.TodoItem;
import todo.facade.TodoFacadeService;
import todo.view.TodoItemView;
import todo.view.TodoItemViewFactory;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/todos")
public class TodoController {
    public static final String TODO_URL = "/{id}";
    private static final String USER_ID = "1";

    private final TodoFacadeService facadeService;
    private final TodoItemViewFactory viewFactory;

    @Autowired
    public TodoController( @NonNull TodoFacadeService facadeService, @NonNull TodoItemViewFactory toDoItemViewFactory) {
        this.facadeService = facadeService;
        this.viewFactory = toDoItemViewFactory;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<TodoItemView> index() {

        return viewFactory.buildList( facadeService.getList( USER_ID));
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.GET)
    public @ResponseBody TodoItemView show(@PathVariable String id) {
        return viewFactory.buildItem( facadeService.getItem( USER_ID, id));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TodoItemView> create(@RequestBody @Valid TodoItemView todo) throws Throwable {
        String itemId = UUID.randomUUID().toString();
        TodoItem item = facadeService.createItem( USER_ID, itemId, todo.getTitle(), false, todo.getOrder());
        TodoItemView result = viewFactory.buildItem( item);
        return new ResponseEntity<>( result, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseBody Collection<TodoItemView> clear() {
        Collection<TodoItem> items = facadeService.deleteList( USER_ID);
        return viewFactory.buildList( items);
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.PATCH)
    public @ResponseBody TodoItemView update(@PathVariable String id, @RequestBody TodoItemView todo) throws Throwable {
        TodoItem item = facadeService.updateItem(USER_ID, id, todo.getTitle(), todo.getCompleted(), todo.getOrder());
        return viewFactory.buildItem( item);
    }

    @RequestMapping(value = TODO_URL, method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable String id) throws Throwable {
        facadeService.deleteItem( USER_ID, id);
        return new ResponseEntity<>( "{}", HttpStatus.OK);
    }
}
