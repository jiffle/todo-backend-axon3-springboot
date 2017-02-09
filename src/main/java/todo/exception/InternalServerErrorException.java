package todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends BaseWebException {

	public InternalServerErrorException() {
		super();
	}

	public InternalServerErrorException(String message) {
		super(message);
	}

}
