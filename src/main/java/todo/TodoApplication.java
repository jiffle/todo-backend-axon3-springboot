package todo;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TodoApplication.class, args);
    }
    
    @Bean
    EventStorageEngine eventStorageEngine() {
    	return new InMemoryEventStorageEngine();
    }
}
