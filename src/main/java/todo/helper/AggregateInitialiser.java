package todo.helper;

import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate;
import org.axonframework.commandhandling.model.inspection.ModelInspector;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import todo.domain.TodoListAggregate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AggregateInitialiser {
    private final AggregateModel<TodoListAggregate> aggregateModel;
    private final EventBus eventBus;

    @Autowired
    public AggregateInitialiser(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
        this.aggregateModel = ModelInspector.inspectAggregate( TodoListAggregate.class);

    }

    public TodoListAggregate initInstance(TodoListAggregate item) {
        if( item != null) {
            AnnotatedAggregate.initialize(item, aggregateModel, eventBus);
        }
        return item;
    }
}
