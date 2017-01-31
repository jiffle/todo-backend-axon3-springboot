package todo.domain;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateRoot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NonNull;

@Data
@AggregateRoot
public class ToDoItem {
	@AggregateIdentifier
	@NonNull private String id;
	@NonNull private String title;
	private boolean completed;
	@NonNull private Integer order;

}
