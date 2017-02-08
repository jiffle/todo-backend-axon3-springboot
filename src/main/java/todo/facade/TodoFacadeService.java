package todo.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import todo.domain.TodoItem;
import todo.query.TodoQueryService;
import todo.view.TodoItemView;

import java.util.Collection;
import java.util.List;

/** Provides a service to abstract the complexities away from the controller class, and a facade pattern onto the backend services
 */
@Service
public class TodoFacadeService {
    private final TodoQueryService queryService;

    @Autowired
    public TodoFacadeService(TodoQueryService queryService) {
        this.queryService = queryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<TodoItem> getTodoList(String userId) {
        return queryService.queryListForUser( userId);
    }

}
