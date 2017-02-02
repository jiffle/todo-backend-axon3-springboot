package todo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import todo.ToDoEventHandler;
import todo.domain.ToDoItem;
import todo.domain.command.CreateToDoItemCommand;
import todo.domain.command.DeleteToDoItemCommand;
import todo.domain.command.UpdateToDoItemCommand;
import todo.middleware.CompletionTracker;
import todo.persistance.TodoList;
import todo.query.TodoQueryService;
import todo.view.ToDoItemView;
import todo.view.ToDoItemViewFactory;

import java.util.Arrays;

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
    private CommandGateway commandGateway;
    @Mock
    private TodoQueryService queryService;
    @Mock
    private CompletionTracker completionTracker;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        ToDoItemViewFactory viewFactory = new ToDoItemViewFactory("http://test.host/todos");
        ToDoController toDoController = new ToDoController(commandGateway, queryService, viewFactory, completionTracker);

        mockMvc = MockMvcBuilders.standaloneSetup(toDoController).build();
    }

    @Test
    public void index_rendersViewOfAllTodos() throws Exception {
        ToDoItem todo1 = ToDoItem.builder().id("123abc").title( "do something").order( 1).build();
        ToDoItem todo2 = ToDoItem.builder().id("456def").title( "do something else").order( 2).build();

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

    @Test
    public void create_issuesACommandToCreateATodo() throws Exception {
        ToDoItem todo = ToDoItem.builder().title("do something").order( 1).build();

        ObjectMapper objectMapper = new ObjectMapper();
        String todoJson = objectMapper.writeValueAsString(todo);

        mockMvc.perform(post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(todoJson))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateToDoItemCommand> commandCaptor = ArgumentCaptor.forClass(CreateToDoItemCommand.class);
        verify(commandGateway).send(commandCaptor.capture());

        CreateToDoItemCommand command = commandCaptor.getValue();
        assertThat(command.getUserId(), is("1"));
        assertThat(command.getItemId(), is(not(nullValue())));
        assertThat(command.getTitle(), is("do something"));
        assertThat(command.getOrder(), is(1));
        assertThat(command.isCompleted(), is(false));
        assertThat(command.getTrackerId(), is(not(nullValue())));
    }

    @Test
    public void show_rendersViewOfASingleTodo() throws Exception {
        ToDoItem todo = ToDoItem.builder().id("123abc").title("do something").completed(true).order( 2).build();

        when( queryService.queryListForItem( "1", "123abc")).thenReturn(todo);

        mockMvc.perform(get("/todos/{id}", "123abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123abc"))
                .andExpect(jsonPath("$.title").value("do something"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.url").value("http://test.host/todos/123abc"))
                .andExpect(jsonPath("$.order").value(2));
    }

    @Test
    public void update_issuesACommandToUpdateATodo() throws Exception {
        String todoUpdateJson = "{\"completed\": \"true\"}";

        mockMvc.perform(patch("/todos/{id}", "123abc")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(todoUpdateJson))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdateToDoItemCommand> commandCaptor = ArgumentCaptor.forClass(UpdateToDoItemCommand.class);
        verify(commandGateway).send(commandCaptor.capture());

        UpdateToDoItemCommand command = commandCaptor.getValue();
        assertThat(command.getCompleted(), is(true));
    }

    @Test
    public void delete_issuesACommandToUpdateATodo() throws Exception {
        mockMvc.perform(delete("/todos/{id}", "123abc"))
                .andExpect(status().isOk());

        verify(commandGateway).send(any(DeleteToDoItemCommand.class));
    }
}