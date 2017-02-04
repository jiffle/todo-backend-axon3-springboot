package todo.query;

import java.util.Collection;

import org.springframework.stereotype.Service;

import todo.domain.ToDoItem;
import todo.repo.TodoListRepository;

@Service
public class TodoQueryService {
private final TodoListRepository repository;	
	
	public TodoQueryService(TodoListRepository repository) {
		this.repository = repository;
	}
	
	public Collection<ToDoItem> queryListForUser( String userId) {		
		return repository.loadOrCreateInstance( userId).allValues();
	}

	public ToDoItem queryListForItem( String userId, String itemId) {
		return repository.load( userId).getValue( itemId);
	}

	
}
