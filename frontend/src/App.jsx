import React, { useMemo, useState } from "react";

async function api(path, options = {}) {
  const response = await fetch(path, {
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
  const [currentUser, setCurrentUser] = useState(null);

  const [groupName, setGroupName] = useState("");
  const [groups, setGroups] = useState([]);
  const [selectedGroupId, setSelectedGroupId] = useState("");

  const [messageText, setMessageText] = useState("");
  const [messages, setMessages] = useState([]);

  const [busy, setBusy] = useState(false);
  const [status, setStatus] = useState("Create a user to get started.");
  const [error, setError] = useState("");

  const selectedGroup = useMemo(
    () => groups.find((g) => g.id === selectedGroupId) || null,
    [groups, selectedGroupId]
  );

  function clearError() {
    setError("");
  }

  async function handleCreateUser() {
    clearError();
    if (!username.trim()) {
      setError("Enter a username first.");
      return;
    }

    setBusy(true);
    try {
      const user = await api("/api/users", {
        method: "POST",
        body: JSON.stringify({ username: username.trim() })
      });
      setCurrentUser(user);
      setStatus(`Signed in as ${user.username}.`);
      setUsername("");
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
      const allGroups = await api("/api/groups");
      setGroups(allGroups);
      if (allGroups.length && !selectedGroupId) {
        setSelectedGroupId(allGroups[0].id);
      }
      setStatus(`Loaded ${allGroups.length} group(s).`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleCreateGroup() {
    clearError();
    if (!currentUser?.id) {
      setError("Create a user before creating a group.");
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
        body: JSON.stringify({ name: groupName.trim(), createdBy: currentUser.id })
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
      setError("Create a user first.");
      return;
    }
    if (!selectedGroupId) {
      setError("Select a group to join.");
      return;
    }

    setBusy(true);
    try {
      await api(`/api/groups/${selectedGroupId}/join`, {
        method: "POST",
        body: JSON.stringify({ userId: currentUser.id })
      });
      setStatus("You joined the selected group.");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleLoadMessages() {
    clearError();
    if (!currentUser?.id || !selectedGroupId) {
      setError("Select a user and group first.");
      return;
    }

    setBusy(true);
    try {
      const result = await api(
        `/api/groups/${selectedGroupId}/messages?userId=${encodeURIComponent(currentUser.id)}`
      );
      setMessages(result);
      setStatus(`Loaded ${result.length} message(s).`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handlePostMessage() {
    clearError();
    if (!currentUser?.id || !selectedGroupId) {
      setError("Select a user and group first.");
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
        body: JSON.stringify({ userId: currentUser.id, message: messageText.trim() })
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
        <h1>LetsRoast</h1>
        <p>A simple chat app where users can create and join groups, and post messages.</p>
      </header>

      <section className="grid">
        <article className="card">
          <h2>1) User Deck</h2>
          <div className="row">
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username (e.g. alice)"
              disabled={busy}
            />
            <button onClick={handleCreateUser} disabled={busy}>Create User</button>
          </div>
          <p className="meta">
            Current user: <strong>{currentUser ? `${currentUser.username} (${currentUser.id.slice(0, 8)})` : "none"}</strong>
          </p>
        </article>

        <article className="card">
          <h2>2) Group Arcade</h2>
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
                  {group.name} ({group.id.slice(0, 8)})
                </option>
              ))}
            </select>
            <button onClick={handleJoinGroup} disabled={busy}>Join Group</button>
          </div>

          <p className="meta">
            Selected group: <strong>{selectedGroup ? `${selectedGroup.name} (${selectedGroup.id.slice(0, 8)})` : "none"}</strong>
          </p>
        </article>

        <article className="card chat-card">
          <h2>3) Chat Console</h2>
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
                  <span className="pill">{m.userId.slice(0, 8)}</span>
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

      <footer className="status-wrap">
        {error ? <div className="status error">{error}</div> : <div className="status">{status}</div>}
      </footer>
    </div>
  );
}

