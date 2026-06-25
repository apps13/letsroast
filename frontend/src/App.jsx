import React, { useEffect, useMemo, useState } from "react";

async function api(path, options = {}) {
  const response = await fetch(path, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  let body = null;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (!response.ok) {
    const errorMessage = body?.error || response.statusText;
    throw new Error(errorMessage);
  }

  return body;
}

export default function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [currentUser, setCurrentUser] = useState(null);

  const [groupName, setGroupName] = useState("");
  const [groups, setGroups] = useState([]);
  const [selectedGroupId, setSelectedGroupId] = useState("");

  const [messageText, setMessageText] = useState("");
  const [messages, setMessages] = useState([]);

  const [busy, setBusy] = useState(false);
  const [status, setStatus] = useState("Sign in or register to continue.");
  const [error, setError] = useState("");

  const selectedGroup = useMemo(
    () => groups.find((g) => g.id === selectedGroupId) || null,
    [groups, selectedGroupId]
  );

  function clearError() {
    setError("");
  }

  function resetWorkspaceState() {
    setGroups([]);
    setSelectedGroupId("");
    setMessages([]);
    setGroupName("");
    setMessageText("");
  }

  useEffect(() => {
    async function restoreSession() {
      setBusy(true);
      try {
        const user = await api("/api/auth/me");
        setCurrentUser(user);
        setStatus(`Welcome back, ${user.username}.`);
      } catch {
        setCurrentUser(null);
      } finally {
        setBusy(false);
      }
    }

    restoreSession();
  }, []);

  useEffect(() => {
    if (!currentUser) {
      return;
    }

    refreshGroups();
  }, [currentUser]);

  async function handleRegister() {
    clearError();
    if (!username.trim()) {
      setError("Enter a username first.");
      return;
    }
    if (!password) {
      setError("Enter a password first.");
      return;
    }

    setBusy(true);
    try {
      const user = await api("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ username: username.trim(), password })
      });
      setCurrentUser(user);
      setStatus(`Account created. Signed in as ${user.username}.`);
      setUsername("");
      setPassword("");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleLogin() {
    clearError();
    if (!username.trim()) {
      setError("Enter a username first.");
      return;
    }
    if (!password) {
      setError("Enter a password first.");
      return;
    }

    setBusy(true);
    try {
      const user = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ username: username.trim(), password })
      });
      setCurrentUser(user);
      setStatus(`Signed in as ${user.username}.`);
      setUsername("");
      setPassword("");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleLogout() {
    clearError();
    setBusy(true);
    try {
      await api("/api/auth/logout", { method: "POST" });
      setCurrentUser(null);
      resetWorkspaceState();
      setStatus("Signed out.");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function refreshGroups() {
    clearError();
    setBusy(true);
    try {
      const myGroups = await api("/api/groups/mine");
      setGroups(myGroups);
      if (myGroups.length && !selectedGroupId) {
        setSelectedGroupId(myGroups[0].id);
      }
      if (!myGroups.length) {
        setSelectedGroupId("");
        setMessages([]);
      }
      setStatus(`Loaded ${myGroups.length} group${myGroups.length === 1 ? "" : "s"}.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleCreateGroup() {
    clearError();
    if (!currentUser?.id) {
      setError("Sign in before creating a group.");
      return;
    }
    if (!groupName.trim()) {
      setError("Enter a group name.");
      return;
    }

    setBusy(true);
    try {
      const created = await api("/api/groups", {
        method: "POST",
        body: JSON.stringify({ name: groupName.trim() })
      });
      setGroupName("");
      setStatus(`Group '${created.name}' created.`);
      await refreshGroups();
      setSelectedGroupId(created.id);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleJoinGroup() {
    clearError();
    if (!currentUser?.id) {
      setError("Sign in first.");
      return;
    }
    if (!selectedGroupId) {
      setError("Select a group to join.");
      return;
    }

    setBusy(true);
    try {
      await api(`/api/groups/${selectedGroupId}/join`, {
        method: "POST"
      });
      setStatus("You joined the selected group.");
      await refreshGroups();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleLoadMessages() {
    clearError();
    if (!currentUser?.id || !selectedGroupId) {
      setError("Select a group first.");
      return;
    }

    setBusy(true);
    try {
      const result = await api(`/api/groups/${selectedGroupId}/messages`);
      setMessages(result);
      setStatus(`Loaded ${result.length} message${result.length === 1 ? "" : "s"}.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handlePostMessage() {
    clearError();
    if (!currentUser?.id || !selectedGroupId) {
      setError("Select a group first.");
      return;
    }
    if (!messageText.trim()) {
      setError("Type a message before posting.");
      return;
    }

    setBusy(true);
    try {
      await api(`/api/groups/${selectedGroupId}/messages`, {
        method: "POST",
        body: JSON.stringify({ message: messageText.trim() })
      });
      setMessageText("");
      setStatus("Message posted.");
      await handleLoadMessages();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="page">
      <header className="hero">
        <div className="hero-top">
          <div className="hero-copy">
            <h1>LetsRoast</h1>
            <p>Spin up a group, drop in, and start the conversation.</p>
          </div>

          {currentUser ? (
            <div className="hero-user-controls">
              <p className="hero-user-meta">
                Signed in as <strong>{currentUser.username}</strong>
              </p>
              <button className="ghost" onClick={handleLogout} disabled={busy}>Sign Out</button>
            </div>
          ) : null}
        </div>
      </header>

      {!currentUser ? (
        <section className="auth-screen">
          <article className="card auth-card">
            <h2>Sign In</h2>
            <div className="row">
              <input
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Username (e.g. alice)"
                disabled={busy}
              />
            </div>
            <div className="row">
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Password"
                disabled={busy}
              />
            </div>
            <div className="row">
              <button onClick={handleLogin} disabled={busy}>Sign In</button>
              <button className="ghost" onClick={handleRegister} disabled={busy}>Register</button>
            </div>
          </article>

          <ul className="feature-list">
            <li className="feature-item">
              <span className="feature-icon">⚡</span>
              <div>
                <strong>Real-time groups</strong>
                <p>Messages land the moment they're posted.</p>
              </div>
            </li>
            <li className="feature-item">
              <span className="feature-icon">🔒</span>
              <div>
                <strong>Secure sessions</strong>
                <p>Cookie-based auth with hashed passwords.</p>
              </div>
            </li>
            <li className="feature-item">
              <span className="feature-icon">🚀</span>
              <div>
                <strong>No setup needed</strong>
                <p>Register, create a group, and start chatting.</p>
              </div>
            </li>
          </ul>
        </section>
      ) : (
        <section className="grid">
          <article className="card group-card">
            <h2>1) Group Arcade</h2>
            <div className="row">
              <input
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                placeholder="Group name"
                disabled={busy}
              />
              <button onClick={handleCreateGroup} disabled={busy}>Create Group</button>
              <button className="ghost" onClick={refreshGroups} disabled={busy}>Refresh</button>
            </div>

            <div className="row">
              <select
                value={selectedGroupId}
                onChange={(e) => setSelectedGroupId(e.target.value)}
                disabled={busy || groups.length === 0}
              >
                <option value="">Select group</option>
                {groups.map((group) => (
                  <option key={group.id} value={group.id}>
                    {group.name}
                  </option>
                ))}
              </select>
              <button onClick={handleJoinGroup} disabled={busy}>Join Group</button>
            </div>

            <p className="meta">
              Selected group: <strong>{selectedGroup ? selectedGroup.name : "none"}</strong>
            </p>
          </article>

          <article className="card chat-card">
            <h2>2) Chat Console</h2>
            <div className="row">
              <input
                value={messageText}
                onChange={(e) => setMessageText(e.target.value)}
                placeholder="Say something"
                disabled={busy}
              />
              <button onClick={handlePostMessage} disabled={busy}>Post</button>
              <button className="ghost" onClick={handleLoadMessages} disabled={busy}>Load</button>
            </div>

            <ul className="messages">
              {messages.length === 0 ? (
                <li className="empty">No messages yet.</li>
              ) : (
                messages.map((m) => (
                  <li key={m.id}>
                    <span className="pill">{m.username}</span>
                    <div>
                      <div className="text">{m.message}</div>
                      <small>{new Date(m.createdAt).toLocaleString()}</small>
                    </div>
                  </li>
                ))
              )}
            </ul>
          </article>
        </section>
      )}

      <footer className={`status-wrap${currentUser ? "" : " status-wrap-compact"}`}>
        {error ? <div className="status error">{error}</div> : <div className="status">{status}</div>}
      </footer>
    </div>
  );
}
