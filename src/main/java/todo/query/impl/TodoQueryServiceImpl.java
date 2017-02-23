package todo.query.impl;

import org.springframework.stereotype.Service;
import todo.domain.TodoItem;
import todo.query.TodoQueryService;
import todo.repo.TodoListRepository;

import java.util.Collection;

@Service
public class TodoQueryServiceImpl implements TodoQueryService {
private final TodoListRepository repository;	
	
	public TodoQueryServiceImpl(TodoListRepository repository) {
		this.repository = repository;
	}
	
	@Override public Collection<TodoItem> queryListForUser(String userId) {
		return repository.loadOrCreateInstance( userId).allValues();
	}

	@Override public TodoItem queryListForItem(String userId, String itemId) {
		return repository.load( userId).getValue( itemId);
	}

	
}
