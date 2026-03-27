package src.testcontainer;


import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
public class TestContainer {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16")
                    .withLabel("service", "test-postgres");

    @Test
    void shouldStartContainer() {
        System.out.println("Container started: " + postgres.isRunning());
    }
}
