package com.java.etlflow.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@Repository
public class MariaDbVectorStoreRepository implements VectorStoreRepository {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final JdbcTemplate jdbcTemplate;
    private final String tableName;
    private final String schemaName;

    public MariaDbVectorStoreRepository(
            JdbcTemplate jdbcTemplate,
            @Value("${spring.ai.vectorstore.mariadb.table-name:vector_store}") String tableName,
            @Value("${spring.ai.vectorstore.mariadb.schema-name:}") String schemaName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = quoteIdentifier(tableName);
        this.schemaName = schemaName.isBlank() ? "" : quoteIdentifier(schemaName) + ".";
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM " + schemaName + tableName);
    }

    private static String quoteIdentifier(String identifier) {
        if (!IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Invalid MariaDB identifier: " + identifier);
        }
        return "`" + identifier + "`";
    }
}
