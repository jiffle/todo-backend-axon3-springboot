package todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseWebException {

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}

}
