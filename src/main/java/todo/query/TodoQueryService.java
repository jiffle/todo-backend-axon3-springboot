package todo.query;

import java.util.Collection;

import org.springframework.stereotype.Service;

import todo.domain.TodoItem;
import todo.repo.TodoListRepository;

@Service
public class TodoQueryService {
private final TodoListRepository repository;	
	
	public TodoQueryService(TodoListRepository repository) {
		this.repository = repository;
	}
	
	public Collection<TodoItem> queryListForUser(String userId) {
		return repository.loadOrCreateInstance( userId).allValues();
	}

	public TodoItem queryListForItem(String userId, String itemId) {
		return repository.load( userId).getValue( itemId);
	}

	
}
