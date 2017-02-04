Todo Backend Axon
=========================
An implementation of [Todo Backend](http://www.todobackend.com/) with [Axon Framework 3](http://www.axonframework.org/) + [Spring Boot](http://projects.spring.io/spring-boot/)

Forked from Ryan Oglesby's [Todo-backend-axon project](https://github.com/ryanoglesby08/todo-backend-axon), converted to Gradle,  and rewritten for Axon3, using Spock + RestClient tests.

Using the new Event and Command handlers of Axon Framework 3, it uses a non-persisting Repository implementation to materialise the Todo List.

It has complete functional tests for the API in the `todo.TodoAPISpec` class

Things still To Do (ironically!):

* Refactor controller code into a Façade Service
* Integrate with Axon Test Framework, as the repository implementation is incompatible with the default Axon fixtures

