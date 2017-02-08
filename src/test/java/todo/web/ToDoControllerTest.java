package todo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import todo.domain.TodoItem;
import todo.domain.command.CreateTodoItemCommand;
import todo.domain.command.DeleteTodoItemCommand;
import todo.domain.command.UpdateTodoItemCommand;
import todo.facade.TodoFacadeService;
import todo.middleware.CompletionTracker;
import todo.query.TodoQueryService;
import todo.view.TodoItemViewFactory;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context.xml")
public class ToDoControllerTest {
    private MockMvc mockMvc;
    @Mock
    private TodoFacadeService facadeService;
    @Mock
    private CommandGateway commandGateway;
    @Mock
    private TodoQueryService queryService;
    @Mock
    private CompletionTracker completionTracker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        TodoItemViewFactory viewFactory = new TodoItemViewFactory("http://test.host/todos");
        TodoController todoController = new TodoController( facadeService, commandGateway, queryService, viewFactory, completionTracker);

        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Ignore( "Covered by functional tests - needs rewrite / deletion")
    @Test
    public void index_rendersViewOfAllTodos() throws Exception {
        TodoItem todo1 = TodoItem.builder().id("123abc").title( "do something").order( 1).build();
        TodoItem todo2 = TodoItem.builder().id("456def").title( "do something else").order( 2).build();

        when( queryService.queryListForUser( any())).thenReturn(Arrays.asList(todo1, todo2));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123abc"))
                .andExpect(jsonPath("$[0].title").value("do something"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].url").value("http://test.host/todos/123abc"))
                .andExpect(jsonPath("$[0].order").value(1))
                .andExpect(jsonPath("$[1].id").value("456def"));
    }

    @Ignore( "Covered by functional tests - needs rewrite / deletion")
    @Test
    public void create_issuesACommandToCreateATodo() throws Exception {
        TodoItem todo = TodoItem.builder().title("do something").order( 1).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String todoJson = objectMapper.writeValueAsString(todo);

        mockMvc.perform(post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(todoJson))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateTodoItemCommand> commandCaptor = ArgumentCaptor.forClass(CreateTodoItemCommand.class);
        verify(commandGateway).send(commandCaptor.capture());

        CreateTodoItemCommand command = commandCaptor.getValue();
        assertThat(command.getUserId(), is("1"));
        assertThat(command.getItemId(), is(not(nullValue())));
        assertThat(command.getTitle(), is("do something"));
        assertThat(command.getOrder(), is(1));
        assertThat(command.isCompleted(), is(false));
        assertThat(command.getTrackerId(), is(not(nullValue())));
    }

    @Ignore( "Covered by functional tests - needs rewrite / deletion")
    @Test
    public void show_rendersViewOfASingleTodo() throws Exception {
        TodoItem todo = TodoItem.builder().id("123abc").title("do something").completed(true).order( 2).build();

        when( queryService.queryListForItem( "1", "123abc")).thenReturn( todo);

        mockMvc.perform(get("/todos/{id}", "123abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123abc"))
                .andExpect(jsonPath("$.title").value("do something"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.url").value("http://test.host/todos/123abc"))
                .andExpect(jsonPath("$.order").value(2));
    }

    @Ignore( "Covered by functional tests - needs rewrite / deletion")
    @Test
    public void update_issuesACommandToUpdateATodo() throws Exception {
        String todoUpdateJson = "{\"completed\": \"true\"}";

        mockMvc.perform(patch("/todos/{id}", "123abc")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(todoUpdateJson))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdateTodoItemCommand> commandCaptor = ArgumentCaptor.forClass(UpdateTodoItemCommand.class);
        verify(commandGateway).send(commandCaptor.capture());

        UpdateTodoItemCommand command = commandCaptor.getValue();
        assertThat(command.getCompleted(), is(true));
    }

    @Ignore( "Covered by functional tests - needs rewrite / deletion")
    @Test
    public void delete_issuesACommandToUpdateATodo() throws Exception {
        mockMvc.perform(delete("/todos/{id}", "123abc"))
                .andExpect(status().isOk());

        verify(commandGateway).send(any(DeleteTodoItemCommand.class));
    }
}