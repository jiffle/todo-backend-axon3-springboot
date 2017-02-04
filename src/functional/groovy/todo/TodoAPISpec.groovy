package todo

import org.apache.commons.collections.Bag
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

import spock.lang.*
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient

import todo.TodoApp

import static groovyx.net.http.ContentType.JSON
import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.that

//@ContextConfiguration

//@RunWith( SpringRunner.class)
//@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TodoApp)
//@WebIntegrationTest
//@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = [ TodoApp.class])
@ContextConfiguration( classes = [ TodoApp.class])
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Stepwise
class TodoAPISpec extends Specification {

   def baseEndpoint = System.getProperty("target.baseurl")

   def builder = new HTTPBuilder("$baseEndpoint")
   def client = new RESTClient("$baseEndpoint")

   def setup() {
	   client.handler.failure = { resp, data ->
		   println "Unexpected failure: ${ resp.statusLine}"
		   resp.data = data
		   return resp
	   }
   }

   def "Getting Todo List after startup should return empty set"() {
	   when: 'We get the root URL'
	   		def resp = client.get( path: 'todos', contentType: JSON)
   	   then: 'We should get an empty list'
		  resp.status == 200
		  resp.data.message == []
   }

    def "Creating Single Todo Item should succeed"() {
        given: 'We have a Todo item data definition'
        def itemTitle = "First Todo"
        def itemOrder = 10
        def todoPostBody = [title: itemTitle, order: itemOrder]
        def itemBase = baseEndpoint + "todos/"
        when: 'We post that data'
        def resp = client.post( path: 'todos',
                body: todoPostBody,
                contentType: JSON)
        then: 'We should get a 201 response and the data item returned'
        resp.status == 201
        with( resp.data) {
            def itemId = id
            title == itemTitle
            order == itemOrder
            url == itemBase + id
        }
    }

	@Unroll("Creating Todo Item should succeed with fields title: '#title', order: '#order'")
   def "Creating Todo Item should succeed"() {
	   given: 'We have a Todo item data definition'
    	   	def todoPostBody = [title: title, order: order]
	   when: 'We post that data'
			   def resp = client.post( path: 'todos',
					   body: todoPostBody,
					   contentType: JSON)
        then: 'We should get a 201 response and the data item returned'
		  resp.status == 201
		  resp.data.title == title
		  resp.data.order == order

		where:
			title         | order
			'Second Todo' | 20
			'Third Todo'  | 30
            'Fourth Todo' | 40

	}

    @Unroll("Creating Todo Item with missing fields should fail: '#title', order: '#order'")
    def "Creating Single Todo Item with missing fields should fail"() {
        given: 'We have a Todo item data definition'
        def todoPostBody = [title: title, order: order]
        when: 'We post that data'
        def resp = client.post( path: 'todos',
                body: todoPostBody,
                contentType: JSON)
        then: 'We should get a 400 response'
        resp.status == 400
        resp.data.error == "Bad Request"

        where:
        title      | order
        'Bad Todo' | null
        null       | 30
    }

    def "Getting Todo List after item addition should have added items"() {
        when: 'We get the root URL'
        def resp = client.get( path: 'todos', contentType: JSON)
        then: 'We should get unordered list with 3 items and correct data'
        resp.status == 200
        resp.data.size == 4
        that (resp.data*.order, containsInAnyOrder( 10, 20, 30, 40))
        that (resp.data*.title, containsInAnyOrder( "First Todo", "Second Todo", "Third Todo", "Fourth Todo"))
    }

    def "Getting single Todo Item should return that item"() {
        given: 'We find one item ID to query'
            def list = client.get( path: 'todos', contentType: JSON)
            def itemId = list.data[0].id
            def itemTitle = list.data[0].title
            def itemOrder = list.data[0].order
            def itemUrl = baseEndpoint + "todos/${itemId}"
        when: 'We get the item URL'
        def resp = client.get( path: "todos/$itemId", contentType: JSON)
        then: 'We should get a single item'
        resp.status == 200
        with( resp.data) {
            id == itemId
            title == itemTitle
            !completed
            order == itemOrder
            url == itemUrl
        }
    }

    def "Getting single Todo with bad id should give not found"() {
        when: 'We get the item URL'
        def resp = client.get( path: "todos/my-bad-id", contentType: JSON)
        then: 'We should get a 404'
        resp.status == 404
        resp.data.error == "Not Found"
    }

    def "Updating Todo title field should change just that field"() {
        given: 'We find one item ID to query'
        def list = client.get( path: 'todos', contentType: JSON)
        def itemId = list.data[0].id
        def itemTitle = list.data[0].title
        def itemOrder = list.data[0].order
        def itemUrl = baseEndpoint + "todos/${itemId}"
        def updatedTitle = "Updated Title"
        def patchBody = [title: updatedTitle]
        when: 'We patch the item'
        def resp = client.patch( path: "todos/$itemId",
                body: patchBody,
                contentType: JSON)
        then: 'We should have item with updated title'
        resp.status == 200
        with( resp.data) {
            id == itemId
            title == updatedTitle
            !completed
            order == itemOrder
            url == itemUrl
        }
    }

    def "Updating Todo order field should change just that field"() {
        given: 'We find one item ID to query'
        def list = client.get( path: 'todos', contentType: JSON)
        def itemId = list.data[0].id
        def itemTitle = list.data[0].title
        def itemOrder = list.data[0].order
        def itemUrl = baseEndpoint + "todos/${itemId}"
        def updatedOrder = 99
        def patchBody = [order: updatedOrder]
        when: 'We patch the item'
        def resp = client.patch( path: "todos/$itemId",
                body: patchBody,
                contentType: JSON)
        then: 'We should have item with updated order'
        resp.status == 200
        with( resp.data) {
            id == itemId
            title == itemTitle
            !completed
            order == updatedOrder
            url == itemUrl
        }
    }

    def "Updating Todo completion field should change just that field"() {
        given: 'We find one item ID to query'
        def list = client.get( path: 'todos', contentType: JSON)
        def itemId = list.data[0].id
        def itemTitle = list.data[0].title
        def itemOrder = list.data[0].order
        def itemUrl = baseEndpoint + "todos/${itemId}"
        def patchBody = [completed: true]
        when: 'We patch the item'
        def resp = client.patch( path: "todos/$itemId",
                body: patchBody,
                contentType: JSON)
        then: 'We have the item marked as completed'
        resp.status == 200
        with( resp.data) {
            id == itemId
            title == itemTitle
            completed
            order == itemOrder
            url == itemUrl
        }
    }

    def "Updating Todo item with bad id should give not found"() {
        given:
        def updatedOrder = 99
        def patchBody = [order: updatedOrder]
        when: 'We patch the item'
        def resp = client.patch( path: "todos/my-bad-id",
                body: patchBody,
                contentType: JSON)
        then: 'We should get 404'
        resp.status == 404
        resp.data.error == "Not Found"
    }

    def "Deleting single Todo Item should remove it"() {
        given: 'We find one item ID to query'
        def list = client.get( path: 'todos', contentType: JSON)
        def itemId = list.data[0].id
        def checkIds = list.data*.id
        checkIds.remove( itemId)
        when: 'We delete that item'
        def resp = client.delete( path: "todos/$itemId")
        then: 'We should have a list with only 3 items'
        resp.status == 200
        def newList = client.get( path: 'todos', contentType: JSON)
        newList.status == 200
        newList.data.size == 3
//      weird - should work, but doesn't
//        that( newList.data*.id, not( hasItem( itemId)))
//      worksaround
        that( newList.data*.id, containsInAnyOrder( checkIds.toArray()))
    }

    def "Deleting single Todo with bad id should give not found"() {
        when: 'We delete that item'
        def resp = client.delete( path: "todos/my-bad-id")
        then: 'We should get 404'
        resp.status == 404
        resp.data.error == "Not Found"
    }

    def "Clearing the List should leaves an empty list"() {
        when: 'We delete the entire list'
            def resp = client.delete( path: "todos")
        then: 'We should have an empty list'
        resp.status == 200
        resp.data.size == 0
        def newList = client.get( path: 'todos', contentType: JSON)
        newList.status == 200
        newList.data.size == 0
    }


}
