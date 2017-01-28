package todo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ToDoItem {
    private String id;
    private String title;
    private boolean completed;
    private Integer order;

    @JsonCreator
    public ToDoItem(@JsonProperty("title") String title,
                    @JsonProperty("completed") boolean completed,
                    @JsonProperty("order") Integer order) {
        this.title = title;
        this.completed = completed;
        this.order = order;
    }
}
