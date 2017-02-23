/**
 * 
 */
package todo.repo;

import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate;
import org.axonframework.commandhandling.model.inspection.ModelInspector;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import todo.domain.TodoListAggregate;
import todo.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repository of To-do lists
 */
@Component
public class TodoListRepository {
private final AggregateModel<TodoListAggregate> aggregateModel;
private final EventBus eventBus;

private Map<String, TodoListAggregate> todoLists = new HashMap<>();

@Autowired
public TodoListRepository( EventBus eventBus) {
	super();
	this.eventBus = eventBus;
    this.aggregateModel = ModelInspector.inspectAggregate( TodoListAggregate.class);

    }

	private TodoListAggregate loadInternal(String aggregateId) {
		return todoLists.get( aggregateId);
/*
		if( result != null) {
		//	AnnotatedAggregate.initialize( result, aggregateModel, eventBus);
		}
		return Optional.ofNullable( result);
*/
	}

	public TodoListAggregate load( String aggregateId) {
		TodoListAggregate result = loadInternal( aggregateId);
/*
		return result.map( x -> initInstance( x)).
				orElseThrow( () -> new NotFoundException("No todo list found for user"));
*/
    if( result == null) {
        throw new NotFoundException("No todo list found for user");
        }
        return initInstance( result);
	}

	private TodoListAggregate createInstance( String aggregateId) {
		TodoListAggregate result = new TodoListAggregate();
		result.setId( aggregateId);
		todoLists.put( aggregateId, result);
        return result;
	}

	public TodoListAggregate loadOrCreateInstance( String aggregateId) {
        TodoListAggregate result = loadInternal(aggregateId);
        if( result == null) {
            result = createInstance(aggregateId);
        }
        return initInstance( result);
    }

    private TodoListAggregate initInstance( TodoListAggregate item) {
        if( item != null) {
            AnnotatedAggregate.initialize(item, aggregateModel, eventBus);
        }
        return item;
    }
}
