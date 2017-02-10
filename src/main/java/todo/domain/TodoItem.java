package todo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder( builderClassName="Builder")
public class TodoItem {
	@NonNull private String id;
	@NonNull private String title;
	private boolean completed;
	private Integer order;
	
}
