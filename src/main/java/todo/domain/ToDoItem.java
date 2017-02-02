package todo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder( builderClassName="Builder")
public class ToDoItem {
	@NonNull private String id;
	@NonNull private String title;
	private boolean completed;
	@NonNull private Integer order;
	
}
