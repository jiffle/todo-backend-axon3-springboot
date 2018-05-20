package todo

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.that

@ContextConfiguration(classes = [TodoApplication.class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Stepwise
class TodoAPISpec extends Specification {

    def baseEndpoint = System.getProperty("target.baseurl")

    def builder = new HTTPBuilder("$baseEndpoint")
    def client = new RESTClient("$baseEndpoint")

    def setup() {
        client.handler.failure = { resp, data ->
            println "Unexpected failure: ${resp.statusLine}"
            resp.data = data
            return resp
        }
    }

    def "Getting Todo List after startup should return empty set"() {
        when: 'we request the list of todos'
            def resp = client.get(path: 'todos', contentType: JSON)
        then: 'response should be an empty list'
            resp.status == 200
            resp.data.message == []
    }

    def "Creating Single Todo Item with minimal data should succeed"() {
        given: 'a valid single todo definition'
            def itemTitle = "First Todo"
            def todoPostBody = [title: itemTitle]
            def itemBase = baseEndpoint + "todos/"
        when: 'we post that single item'
            def resp = client.post(path: 'todos',
                    body: todoPostBody,
                    contentType: JSON)
        then: 'response should be a created and the data item returned'
            resp.status == 201
            with(resp.data) {
                def itemId = id
                title == itemTitle
                url == itemBase + id
            }
    }

    @Unroll("Creating Todo Item should succeed with fields title: '#completed', order: '#order'")
    def "Creating Todo Item should succeed"() {
        given: 'a valid todo item definition'
            def itemTitle = "First Todo"
            def todoPostBody = [title: title, completed: completed, order: order]
        when: 'we post that item'
            def resp = client.post(path: 'todos',
                    body: todoPostBody,
                    contentType: JSON)
        then: 'response should be created and the data item returned'
            resp.status == 201
            resp.data.title == title
            resp.data.completed == completed
            resp.data.order == order

        where:
            title         | completed | order
            'Second Todo' | true      | 20
            'Third Todo'  | false     | 30
            'Fourth Todo' | true      | null
    }

    def "Creating Single Todo Item with missing tile should fail"() {
        given: 'a todo item data definition with a missing title'
            def todoPostBody = [title: null, order: 99]
        when: 'we post that item'
            def resp = client.post(path: 'todos',
                    body: todoPostBody,
                    contentType: JSON)
        then: 'response should be bad request error'
            resp.status == 400
            resp.data.error == "Bad Request"
    }

    def "Getting Todo List after item addition should have added items"() {
        when: 'we request the list of todos'
            def resp = client.get(path: 'todos', contentType: JSON)
        then: 'response should contain an unordered 4 item list and correct data'
            resp.status == 200
            resp.data.size == 4
            that(resp.data*.order, containsInAnyOrder(null, 20, 30, null))
            that(resp.data*.completed, containsInAnyOrder(false, true, false, true))
            that(resp.data*.title, containsInAnyOrder("First Todo", "Second Todo", "Third Todo", "Fourth Todo"))
    }

    def "Getting single Todo Item should return that item"() {
        given: 'the id for an existing todo'
            def list = client.get(path: 'todos', contentType: JSON)
            def expected = list.data[0]
            def itemUrl = baseEndpoint + "todos/${expected.id}"
        when: 'we request that todo'
            def resp = client.get(path: "todos/${expected.id}", contentType: JSON)
        then: 'response should succeed and return the single requested item'
            resp.status == 200
            with(resp.data) {
                id == expected.id
                title == expected.title
                completed == expected.completed
                order == expected.order
                url == itemUrl
            }
    }

    def "Getting single Todo with bad id should give not found"() {
        when: 'we request a todo that does not exist'
            def resp = client.get(path: "todos/my-bad-id", contentType: JSON)
        then: 'response should be not found'
            resp.status == 404
            resp.data.error == "Not Found"
    }

    def "Updating Todo title field should change just that field"() {
        given: 'an existing todo with a modified title'
            def list = client.get(path: 'todos', contentType: JSON)
            def expected = list.data[0]
            def itemUrl = baseEndpoint + "todos/${expected.id}"
            def updatedTitle = "Updated Title"
            def patchBody = [title: updatedTitle]
        when: 'we patch the todo'
            def resp = client.patch(path: "todos/${expected.id}",
                    body: patchBody,
                    contentType: JSON)
        then: 'response should contain the updated title'
            resp.status == 200
            with(resp.data) {
                id == expected.id
                title == updatedTitle
            }
        and: 'the other fields should remain unchanged'
            with(resp.data) {
                completed == expected.completed
                order == expected.order
                url == itemUrl
            }
    }

    def "Updating Todo order field should change just that field"() {
        given: 'an existing todo with a modified order value'
            def list = client.get(path: 'todos', contentType: JSON)
            def expected = list.data[0]
            def itemUrl = baseEndpoint + "todos/${expected.id}"
            def updatedOrder = 99
            def patchBody = [order: updatedOrder]
        when: 'we patch the todo'
            def resp = client.patch(path: "todos/${expected.id}",
                    body: patchBody,
                    contentType: JSON)
        then: 'response should contain the updated order'
            resp.status == 200
            with(resp.data) {
                id == expected.id
                order == updatedOrder
            }
        and: 'the other fields should remain unchanged'
            with(resp.data) {
                title == expected.title
                completed == expected.completed
                url == itemUrl
            }
    }

    def "Updating Todo completion field should change just that field"() {
        given: 'an existing todo with a modified completed field'
            def list = client.get(path: 'todos', contentType: JSON)
            def expected = list.data[0]
            def itemUrl = baseEndpoint + "todos/${expected.id}"
            def patchBody = [completed: true]
        when: 'we patch the todo'
            def resp = client.patch(path: "todos/${expected.id}",
                    body: patchBody,
                    contentType: JSON)
        then: 'response should contain the item marked as completed'
            resp.status == 200
            with(resp.data) {
                id == expected.id
                completed == true
            }
        and: 'the other fields should remain unchanged'
            with(resp.data) {
                title == expected.title
                order == expected.order
                url == itemUrl
            }
    }

    def "Updating Todo item with bad id should give not found"() {
        given: 'a todo with an id that does not match any existing item'
            def updatedOrder = 99
            def patchBody = [order: updatedOrder]
        when: 'we patch the missing todo'
            def resp = client.patch(path: "todos/my-bad-id",
                    body: patchBody,
                    contentType: JSON)
        then: 'response should be not found'
            resp.status == 404
            resp.data.error == "Not Found"
    }

    def "Deleting single Todo Item should remove it"() {
        given: 'the id for an existing todo'
            def list = client.get(path: 'todos', contentType: JSON)
            def itemId = list.data[0].id
            def checkIds = list.data*.id
            checkIds.remove(itemId)
        when: 'we delete the todo'
            def resp = client.delete(path: "todos/$itemId")
        then: 'response should be success'
            resp.status == 200
        and: 'the new list of todos should only contain 3 items'
            def newList = client.get(path: 'todos', contentType: JSON)
            newList.status == 200
            newList.data.size == 3
//      weird - should work, but doesn't
//        that( newList.data*.id, not( hasItem( itemId)))
//      workaround
            that(newList.data*.id, containsInAnyOrder(checkIds.toArray()))
    }

    def "Deleting single Todo with bad id should give not found"() {
        when: 'we delete a missing todo'
            def resp = client.delete(path: "todos/my-bad-id")
        then: 'response should be not found'
            resp.status == 404
            resp.data.error == "Not Found"
    }

    def "Clearing the List should leaves an empty list"() {
        when: 'we delete the entire list'
            def resp = client.delete(path: "todos")
        then: 'the new list of todos should be empty'
            resp.status == 200
            resp.data.size == 0
            def newList = client.get(path: 'todos', contentType: JSON)
            newList.status == 200
            newList.data.size == 0
    }


}
