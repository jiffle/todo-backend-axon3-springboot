package todo.repo

import spock.lang.Specification
import todo.domain.TodoListAggregate
import todo.exception.NotFoundException
import todo.helper.AggregateInitialiser

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.that


class TodoListRepositorySpec extends Specification {

    TodoListRepository listRepository
    AggregateInitialiser aggregateInitialiser;
    String testId = "1"

    def setup() {
        aggregateInitialiser = Mock()
        listRepository = new TodoListRepository( aggregateInitialiser)
    }

    def "Load Instance should throw exception for a missing ID"() {
        when:
        def listAggregate = listRepository.loadInstance( testId)
        then:
        thrown( NotFoundException)
    }

    def "Load Instance should return existing instance for ID"() {
        given:
        def existing = listRepository.createInternal( testId)
        assert listRepository.getTodoListSize() == 1
        when:
        def listAggregate = listRepository.loadInstance( testId)
        then:
        listAggregate != null
        that listAggregate, sameInstance( existing)
        1 * aggregateInitialiser.initInstance(_ as TodoListAggregate) >> { TodoListAggregate agg -> agg }
        listRepository.getTodoListSize() == 1
        that listAggregate, sameInstance( listRepository.getTodoInstance( testId))
    }

    def "Load Or Create Instance should create list instance for new ID"() {
        given:
        assert listRepository.getTodoListSize() == 0
        when:
        def listAggregate = listRepository.loadOrCreateInstance( testId)
        then:
        listAggregate != null
        listAggregate.getId() == testId
        1 * aggregateInitialiser.initInstance(_) >> { TodoListAggregate agg -> agg }
        listRepository.getTodoListSize() == 1
        that listAggregate, sameInstance( listRepository.getTodoInstance( testId))
    }

    def "Load Or Create Instance should return existing list instance for ID"() {
        given:
        def existing = listRepository.createInternal( testId)
        assert listRepository.getTodoListSize() == 1
        when:
        def listAggregate = listRepository.loadOrCreateInstance( testId)
        then:
        listAggregate != null
        that listAggregate, sameInstance( existing)
        1 * aggregateInitialiser.initInstance(_) >> { TodoListAggregate agg -> agg }
        listRepository.getTodoListSize() == 1
        that listAggregate, sameInstance( listRepository.getTodoInstance( testId))
    }
}