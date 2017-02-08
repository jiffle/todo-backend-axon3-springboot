package todo.query.impl;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import todo.domain.TodoItem;
import todo.query.TodoQueryService;
import todo.repo.TodoListRepository;

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
