package todo
import org.springframework.web.context.request.async.DeferredResult
import spock.lang.*
import todo.domain.ToDoItem
import todo.domain.event.*
import todo.persistance.TodoList
import todo.view.*

/**
 */
class ToDoEventHandlerSpec extends Specification {
    static final String TODO_ID = "123abc"
    def todoList
    def toDoEventHandler

    def setup() {
        todoList = new TodoList()
        def toDoItemViewFactory = new ToDoItemViewFactory("http://test.host/todos")

        toDoEventHandler = new ToDoEventHandler(todoList, toDoItemViewFactory)
    }

    def "Receiving A Created Event Adds A Todo"() {
        def createTodo = new ToDoItem("do something", false, 10)
        def createdEvent = new ToDoItemCreatedEvent(TODO_ID, createTodo)

        def result = linkResultAndTodo(TODO_ID)
        
      when:
        toDoEventHandler.handle(createdEvent)

      then:
        def fetchedTodo = todoList.get(TODO_ID)
        fetchedTodo.getId() == TODO_ID
        fetchedTodo.getTitle() == "do something"
        
        assertThatResultIsFinished(result, TODO_ID)
    }

    def "Receiving An Updated Event Can Update A Todos Title Only"() {
        def todo = addTodoToList(TODO_ID)

        def todoUpdates = new ToDoItem("do something else", null, null)
        def updatedEvent = new TodoItemUpdatedEvent(TODO_ID, todoUpdates)

        def result = linkResultAndTodo(TODO_ID)
        
      when:
        toDoEventHandler.handle(updatedEvent)

      then:
        def updatedTodo = todoList.get(TODO_ID)
        updatedTodo.getTitle() == "do something else"
        updatedTodo.isCompleted() == todo.isCompleted()
        updatedTodo.getOrder() == todo.getOrder()

        assertThatResultIsFinished(result, TODO_ID)
    }

    def "Receiving An Updated Event Can Update A Todos Completed Status Only"() {
        def todo = addTodoToList(TODO_ID)

        def todoUpdates = new ToDoItem(null, true, null)
        def updatedEvent = new TodoItemUpdatedEvent(TODO_ID, todoUpdates)

        def result = linkResultAndTodo(TODO_ID)
        
      when:
        toDoEventHandler.handle(updatedEvent)

      then:
        def updatedTodo = todoList.get(TODO_ID)
        updatedTodo.getTitle() == todo.getTitle()
        updatedTodo.isCompleted()
        updatedTodo.getOrder() == todo.getOrder()

        assertThatResultIsFinished(result, TODO_ID)
    }

    def "Receiving An Updated Event Can Update A Todos Order Only"() {
        def todo = addTodoToList(TODO_ID)
        int newOrder = todo.getOrder() + 1

        def todoUpdates = new ToDoItem(null, null, newOrder)
        def updatedEvent = new TodoItemUpdatedEvent(TODO_ID, todoUpdates)

        def result = linkResultAndTodo(TODO_ID)
        
      when:
        toDoEventHandler.handle(updatedEvent)

      then:
        def updatedTodo = todoList.get(TODO_ID)
        updatedTodo.getTitle() == todo.getTitle()
        updatedTodo.isCompleted() == todo.isCompleted()
        updatedTodo.getOrder() == newOrder

        assertThatResultIsFinished(result, TODO_ID)
    }

    def "Receiving A Deleted Event Removes A Todo"() {
        addTodoToList(TODO_ID)

        def anotherTodo = new ToDoItem("another task", false, 2)
        anotherTodo.setId("789ghi")
        todoList.add(anotherTodo)

        def deletedEvent = new ToDoItemDeletedEvent(TODO_ID)

        def result = linkResultAndTodo(TODO_ID)
        
      when:
        toDoEventHandler.handle(deletedEvent);

      then:
        todoList.all().size() == 1
        todoList.get(TODO_ID) == null

        assertThatResultIsFinished(result, TODO_ID)
    }

    private ToDoItem addTodoToList(String todoId) {
        ToDoItem todo = new ToDoItem("do something", false, 1)
        todo.setId(todoId)
        todoList.add(todo)
        return todo
    }

    private DeferredResult<ToDoItemView> linkResultAndTodo(String todoId) {
        DeferredResult<ToDoItemView> result = new DeferredResult<ToDoItemView>()
        toDoEventHandler.linkResultWithEvent(todoId, result)
        return result
    }

    private void assertThatResultIsFinished(DeferredResult<ToDoItemView> result, String todoId) {
        assert result.hasResult()
        ToDoItemView todoView = (ToDoItemView) result.getResult()
        assert todoView.getId() == todoId
    }

}