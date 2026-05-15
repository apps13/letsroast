package com.letsroast.service;

public interface AuthSessionService {
	String createSession(String userId);
	String getUserIdFromSession(String sessionId);
	void invalidateSession(String sessionId);
}

