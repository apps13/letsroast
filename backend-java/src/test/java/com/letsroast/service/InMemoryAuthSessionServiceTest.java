package com.letsroast.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAuthSessionServiceTest {

    private InMemoryAuthSessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new InMemoryAuthSessionService();
    }

    @Test
    void createSession_returnsNonBlankSessionId() {
        String sessionId = sessionService.createSession("user-1");

        assertThat(sessionId).isNotBlank();
    }

    @Test
    void createSession_twoSessionsForSameUser_returnsDifferentIds() {
        String first = sessionService.createSession("user-1");
        String second = sessionService.createSession("user-1");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void getUserIdFromSession_validSession_returnsUserId() {
        String sessionId = sessionService.createSession("user-42");

        String userId = sessionService.getUserIdFromSession(sessionId);

        assertThat(userId).isEqualTo("user-42");
    }

    @Test
    void getUserIdFromSession_unknownSessionId_returnsNull() {
        assertThat(sessionService.getUserIdFromSession("random-id")).isNull();
    }

    @Test
    void getUserIdFromSession_nullSessionId_returnsNull() {
        assertThat(sessionService.getUserIdFromSession(null)).isNull();
    }

    @Test
    void getUserIdFromSession_blankSessionId_returnsNull() {
        assertThat(sessionService.getUserIdFromSession("   ")).isNull();
    }

    @Test
    void invalidateSession_sessionIsNoLongerValid() {
        String sessionId = sessionService.createSession("user-1");

        sessionService.invalidateSession(sessionId);

        assertThat(sessionService.getUserIdFromSession(sessionId)).isNull();
    }

    @Test
    void invalidateSession_nullOrBlank_doesNotThrow() {
        sessionService.invalidateSession(null);
        sessionService.invalidateSession("  ");
        // No exception = pass
    }
}

