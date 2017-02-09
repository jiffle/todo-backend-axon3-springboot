package todo.helper;

import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/** Factory to allow Completion Latches to be injected for testing
 */
@Service
public class CompletionLatchFactory {
    public CountDownLatch createInstance() {
        return new CountDownLatch( 1);
    }
}
