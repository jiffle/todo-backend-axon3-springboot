/**
 * 
 */
package todo.domain;

import org.axonframework.eventsourcing.AbstractAggregateFactory;
import org.axonframework.eventsourcing.DomainEventMessage;

/**
 * @author davidhamilton
 *
 */
@Deprecated
public class TodoListFactory {} /*extends AbstractAggregateFactory<TodoListAggregate> {

	protected TodoListFactory(Class<TodoListAggregate> aggregateBaseType) {
		super(aggregateBaseType);
	}

	@Override
	protected TodoListAggregate doCreateAggregate(String aggregateIdentifier, DomainEventMessage firstEvent) {
		return new TodoListAggregate( aggregateIdentifier);
	}

}
*/