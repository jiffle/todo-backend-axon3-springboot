/**
 * 
 */
package todo.repo;

import java.util.HashMap;
import java.util.Map;

import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate;
import org.axonframework.commandhandling.model.inspection.ModelInspector;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import todo.domain.TodoListAggregate;
import todo.helper.ResourceNotFoundException;
import todo.middleware.CompletionTracker;

/** Repository of Todo lists 
 */
@Component
public class TodoListRepository {
private final CompletionTracker tracker;
private final AggregateModel<TodoListAggregate> aggregateModel;
private final EventBus eventBus;

@Autowired
public TodoListRepository(CompletionTracker tracker, EventBus eventBus) {
	super();
	this.tracker = tracker;
	this.eventBus = eventBus;
    this.aggregateModel = ModelInspector.inspectAggregate( TodoListAggregate.class);	
    
    }

private Map<String, TodoListAggregate> todoLists = new HashMap<>();
	
	public TodoListAggregate load( String aggregateId) throws ResourceNotFoundException {
		TodoListAggregate result = todoLists.get( aggregateId);
		if( result == null) {
			throw new ResourceNotFoundException( "No todo list found for user");
		}
		AnnotatedAggregate.initialize( result, aggregateModel, eventBus);
		return result;
	}

	public TodoListAggregate createInstance( String aggregateId) {
		TodoListAggregate result = new TodoListAggregate();
		result.setId( aggregateId);
		result.setTracker( tracker);
		AnnotatedAggregate.initialize( result, aggregateModel, eventBus);
		todoLists.put( aggregateId, result);
        return result;
	}

	public TodoListAggregate loadOrCreateInstance( String aggregateId) {
		TodoListAggregate result;
		try {
			result = load( aggregateId);
		} catch (ResourceNotFoundException e) {
			result = createInstance( aggregateId);
		}
		return result;
	}	
}
