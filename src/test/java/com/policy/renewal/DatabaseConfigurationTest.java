package com.policy.renewal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DatabaseConfigurationTest — verifies that the test environment uses an
 * in-memory H2 database and that the persistent file-based URL configured in
 * the main application.properties is correctly overridden by the test profile.
 *
 * <p>This test guards against the accidental use of the production
 * file-based database during automated test runs, which would pollute
 * real data or cause environment-dependent test failures.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
class DatabaseConfigurationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Test
    @DisplayName("Test environment uses in-memory H2 database (not file-based)")
    void testDatasourceUrlIsInMemory() {
        assertNotNull(datasourceUrl,
                "spring.datasource.url must be configured");

        assertTrue(datasourceUrl.startsWith("jdbc:h2:mem:"),
                "Tests must use an in-memory H2 URL. Actual URL was: " + datasourceUrl
                + " — check src/test/resources/application.properties");
    }

    @Test
    @DisplayName("Test datasource is healthy and H2 database is accessible")
    void testDatasourceIsHealthy() throws Exception {
        assertNotNull(dataSource, "DataSource bean must be present");

        try (Connection conn = dataSource.getConnection()) {
            assertFalse(conn.isClosed(), "Connection must be open");

            DatabaseMetaData meta = conn.getMetaData();
            String productName = meta.getDatabaseProductName();
            assertEquals("H2", productName,
                    "Database must be H2. Found: " + productName);
        }
    }

    @Test
    @DisplayName("POLICY table exists and is queryable in test database")
    void testPolicyTableExists() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM POLICY", Long.class);
        assertNotNull(count, "COUNT(*) on POLICY must return a non-null result");
        assertTrue(count >= 0, "Policy count must be zero or greater");
    }

    @Test
    @DisplayName("REMINDER_RECORD table exists and is queryable in test database")
    void testReminderRecordTableExists() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM REMINDER_RECORD", Long.class);
        assertNotNull(count, "COUNT(*) on REMINDER_RECORD must return a non-null result");
        assertTrue(count >= 0, "ReminderRecord count must be zero or greater");
    }

    @Test
    @DisplayName("Application context loads successfully with the test database configuration")
    void contextLoadsWithTestDatabaseConfig() {
        // If the context loaded (this test ran), the bean wiring is correct.
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
    }
}
