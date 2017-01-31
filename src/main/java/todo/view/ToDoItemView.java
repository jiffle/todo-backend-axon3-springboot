package todo.view;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import todo.domain.ToDoItem;

@Data
public class ToDoItemView {
	private String id;
	private String title;
	private Boolean completed;
	private Integer order;
	private String url;

	public ToDoItemView(ToDoItem item) {
		this( item, null);
	}

	public ToDoItemView(ToDoItem item, String urlFor) {
		this.id = item.getId();
		this.title = item.getTitle();
		this.completed = item.isCompleted();
		this.order = item.getOrder();
		this.url = urlFor;
	}
	
    /*    @JsonCreator
    public ToDoItem(@JsonProperty("title") String title,
                    @JsonProperty("completed") boolean completed,
                    @JsonProperty("order") Integer order) {
        this.title = title;
        this.completed = completed;
        this.order = order;
    }
*/        
    
}
