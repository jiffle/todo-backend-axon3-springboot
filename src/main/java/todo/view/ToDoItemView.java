package todo.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import todo.domain.ToDoItem;

import javax.validation.constraints.NotNull;

@Data
@Builder( builderClassName="Builder")
@NoArgsConstructor
@AllArgsConstructor
public class ToDoItemView {
	private String id;
@NotNull private String title;
	private Boolean completed;
@NotNull private Integer order;
	private String url;
	
    /*    @JsonCreator
    public ToDoItem(@JsonProperty("title") String title,
                    @JsonProperty("completed") boolean completed,
                    @JsonProperty("order") Integer order) {
        this.title = title;
        this.completed = completed;
        this.order = order;
    }
*/        

	public static class Builder {
		public Builder todoItem( ToDoItem item) {
			this.id = item.getId();
			this.title = item.getTitle();
			this.completed = item.isCompleted();
			this.order = item.getOrder();
			return this;
		}
	}
}
