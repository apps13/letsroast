package com.letsroast.service;

import com.letsroast.model.UserSession;
import com.letsroast.repository.UserSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Profile("postgres")
public class PostgresAuthSessionService implements AuthSessionService {
	private static final long SESSION_DAYS = 7;

	private final UserSessionRepository sessionRepository;

	public PostgresAuthSessionService(UserSessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

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
		sessionRepository.save(session);
		return sessionId;
	}

	@Override
	public String getUserIdFromSession(String sessionId) {
		if (sessionId == null || sessionId.isBlank()) {
			return null;
		}

		UserSession session = sessionRepository.findById(sessionId).orElse(null);
		if (session == null) {
			return null;
		}

		if (session.getExpiresAt().isBefore(Instant.now())) {
			sessionRepository.deleteById(sessionId);
			return null;
		}

		return session.getUserId();
	}

	@Override
	public void invalidateSession(String sessionId) {
		if (sessionId == null || sessionId.isBlank()) {
			return;
		}
		sessionRepository.deleteById(sessionId);
	}
}

