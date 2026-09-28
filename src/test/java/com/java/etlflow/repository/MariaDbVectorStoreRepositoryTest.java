package com.java.etlflow.repository;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MariaDbVectorStoreRepositoryTest {
    @Test
    void deletesAllRowsFromConfiguredVectorStoreTable() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        MariaDbVectorStoreRepository repository =
                new MariaDbVectorStoreRepository(jdbcTemplate, "custom_vectors", "springai");

        repository.deleteAll();

        verify(jdbcTemplate).update("DELETE FROM `springai`.`custom_vectors`");
    }

    @Test
    void rejectsUnsafeTableNames() {
        assertThrows(IllegalArgumentException.class,
                () -> new MariaDbVectorStoreRepository(mock(JdbcTemplate.class), "vectors; DROP TABLE users", ""));
    }
}
