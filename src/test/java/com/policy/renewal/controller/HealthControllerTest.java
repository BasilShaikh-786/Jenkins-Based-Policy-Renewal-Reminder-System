package com.policy.renewal.controller;

import com.policy.renewal.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HealthControllerTest — verifies container health check endpoint behavior under healthy and degraded conditions.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(HealthController.class)
@Import({SecurityConfig.class, AuthController.class})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    @Test
    @DisplayName("GET /health without authentication returns 200 OK with status UP when database is reachable")
    void testHealthEndpointReturnsUp() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isValid(anyInt())).thenReturn(true);
        when(dataSource.getConnection()).thenReturn(mockConnection);

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("UP"))
                .andExpect(jsonPath("$.application").value("policy-renewal-reminder-system"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /actuator/health is aliased and returns 200 OK with status UP")
    void testActuatorHealthAliasReturnsUp() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isValid(anyInt())).thenReturn(true);
        when(dataSource.getConnection()).thenReturn(mockConnection);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("UP"));
    }

    @Test
    @DisplayName("GET /health returns 503 SERVICE_UNAVAILABLE when database connection fails")
    void testHealthEndpointReturnsDownWhenDatabaseFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("H2 database file is locked or unavailable"));

        mockMvc.perform(get("/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("DOWN"));
    }
}
