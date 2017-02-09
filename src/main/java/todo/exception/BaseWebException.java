package todo.exception;

/** Base app exception to allow general handling
 */
@SuppressWarnings("serial")
public class BaseWebException extends RuntimeException {
    public BaseWebException() {
        super();
    }

    public BaseWebException(String message) {
        super(message);
    }
}
