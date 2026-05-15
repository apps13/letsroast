package com.letsroast.api;

import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final String SESSION_COOKIE = "letsroast_session";
	private static final Duration SESSION_DURATION = Duration.ofDays(7);

	private final UserService userService;
	private final AuthSessionService authSessionService;

	public AuthController(UserService userService, AuthSessionService authSessionService) {
		this.userService = userService;
		this.authSessionService = authSessionService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody AuthRequest request) {
		String validationError = validateAuthRequest(request);
		if (validationError != null) {
			return ResponseEntity.badRequest().body(Map.of("error", validationError));
		}

		User user = userService.registerUser(request.username().trim(), request.password());
		if (user == null) {
			return ResponseEntity.status(409).body(Map.of("error", "username already exists"));
		}

		String sessionId = authSessionService.createSession(user.getId());
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, buildSessionCookie(sessionId).toString())
				.body(user);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		String validationError = validateAuthRequest(request);
		if (validationError != null) {
			return ResponseEntity.badRequest().body(Map.of("error", validationError));
		}

		User user = userService.getUserByUsername(request.username().trim());
		if (!userService.verifyPassword(user, request.password())) {
			return ResponseEntity.status(401).body(Map.of("error", "invalid username or password"));
		}

		String sessionId = authSessionService.createSession(user.getId());
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, buildSessionCookie(sessionId).toString())
				.body(user);
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId) {
		User user = getAuthenticatedUser(sessionId);
		if (user == null) {
			return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
		}
		return ResponseEntity.ok(user);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId) {
		authSessionService.invalidateSession(sessionId);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearSessionCookie().toString())
				.body(Map.of("message", "logged out"));
	}

	private User getAuthenticatedUser(String sessionId) {
		String userId = authSessionService.getUserIdFromSession(sessionId);
		if (userId == null) {
			return null;
		}
		return userService.getUserById(userId);
	}

	private String validateAuthRequest(AuthRequest request) {
		if (request == null || request.username() == null || request.username().isBlank()) {
			return "username is required";
		}
		if (request.password() == null || request.password().isBlank()) {
			return "password is required";
		}
		if (request.password().length() < 6) {
			return "password must be at least 6 characters";
		}
		return null;
	}

	private ResponseCookie buildSessionCookie(String sessionId) {
		return ResponseCookie.from(SESSION_COOKIE, sessionId)
				.httpOnly(true)
				.secure(false)
				.sameSite("Lax")
				.path("/")
				.maxAge(SESSION_DURATION)
				.build();
	}

	private ResponseCookie clearSessionCookie() {
		return ResponseCookie.from(SESSION_COOKIE, "")
				.httpOnly(true)
				.secure(false)
				.sameSite("Lax")
				.path("/")
				.maxAge(Duration.ZERO)
				.build();
	}

	public record AuthRequest(String username, String password) {
	}
}

