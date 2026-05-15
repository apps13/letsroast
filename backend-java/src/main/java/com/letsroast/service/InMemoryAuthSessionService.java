package com.letsroast.service;

import com.letsroast.model.UserSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("default")
public class InMemoryAuthSessionService implements AuthSessionService {
	private static final long SESSION_DAYS = 7;

	private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

	@Override
	public String createSession(String userId) {
		String sessionId = UUID.randomUUID().toString();
		Instant now = Instant.now();
		UserSession session = new UserSession(
				sessionId,
				userId,
				now,
				now.plus(SESSION_DAYS, ChronoUnit.DAYS)
		);
		sessions.put(sessionId, session);
		return sessionId;
	}

	@Override
	public String getUserIdFromSession(String sessionId) {
		if (sessionId == null || sessionId.isBlank()) {
			return null;
		}

		UserSession session = sessions.get(sessionId);
		if (session == null) {
			return null;
		}

		if (session.getExpiresAt().isBefore(Instant.now())) {
			sessions.remove(sessionId);
			return null;
		}

		return session.getUserId();
	}

	@Override
	public void invalidateSession(String sessionId) {
		if (sessionId == null || sessionId.isBlank()) {
			return;
		}
		sessions.remove(sessionId);
	}
}

