/**
 * 
 */
package todo.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import todo.domain.TodoListAggregate;
import todo.exception.NotFoundException;
import todo.helper.AggregateInitialiser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/** Repository of To-do lists
 */
@Slf4j
@Component
public class TodoListRepository {

private Map<String, TodoListAggregate> todoLists = Collections.synchronizedMap(new HashMap<>());
private AggregateInitialiser aggregateInitialiser;

    @Autowired
public TodoListRepository( AggregateInitialiser aggregateInitialiser) {
	this.aggregateInitialiser = aggregateInitialiser;
    }

	private Optional<TodoListAggregate> loadInternal(String aggregateId) {
        return ofNullable( todoLists.get( aggregateId));
	}

	public TodoListAggregate loadInstance(String aggregateId) {
        return loadInternal( aggregateId)
                .map( aggregateInitialiser::initInstance)
                .orElseThrow( () -> new NotFoundException ("No todo list found for user"));
	}

	private TodoListAggregate createInternal(String aggregateId) {
		TodoListAggregate result = new TodoListAggregate();
		result.setId( aggregateId);
		todoLists.put( aggregateId, result);
        return result;
	}

	public TodoListAggregate loadOrCreateInstance( String aggregateId) {
        return aggregateInitialiser.initInstance(
                loadInternal(aggregateId)
                        .orElseGet(() -> createInternal(aggregateId)));
    }

    // test method
    protected int getTodoListSize() {
    return todoLists.size();
    }
    // test method
    protected TodoListAggregate getTodoInstance( String id) {
        return todoLists.get( id);
    }

}
