import React, { useState, useEffect } from "react";
import "./App.css";

const API_BASE_URL = "http://localhost:8080";

function App() {
  const [step, setStep] = useState("login");
  const [loginForm, setLoginForm] = useState({ username: "", password: "" });
  const [loginError, setLoginError] = useState("");
  const [teamCount, setTeamCount] = useState(2);
  const [teamNames, setTeamNames] = useState(["Team 1", "Team 2"]);
  const [teams, setTeams] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loadingCategories, setLoadingCategories] = useState(false);
  const [activeQuestion, setActiveQuestion] = useState(null);
  const [isLoadingQuestion, setIsLoadingQuestion] = useState(false);
  const [answerForm, setAnswerForm] = useState({
    teamName: "",
    answer: "",
  });
  const [answerResult, setAnswerResult] = useState(null);
  const [isSubmittingAnswer, setIsSubmittingAnswer] = useState(false);

  // Keep teamNames array aligned with teamCount
  useEffect(() => {
    setTeamNames((prev) => {
      const next = [];
      for (let i = 0; i < teamCount; i++) {
        next.push(prev[i] || `Team ${i + 1}`);
      }
      return next;
    });
  }, [teamCount]);

  const handleLoginChange = (e) => {
    const { name, value } = e.target;
    setLoginForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setLoginError("");
    try {
      const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginForm),
      });
      const data = await res.json();
      if (res.ok && data.success) {
        setStep("teams");
      } else {
        setLoginError(data.message || "Login failed");
      }
    } catch (err) {
      console.error(err);
      setLoginError("Unable to reach server. Is the backend running?");
    }
  };

  const handleTeamNameChange = (index, value) => {
    setTeamNames((prev) => {
      const copy = [...prev];
      copy[index] = value;
      return copy;
    });
  };

  const fetchCategories = async () => {
    setLoadingCategories(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/game/categories`);
      const data = await res.json();
      setCategories(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingCategories(false);
    }
  };

  const handleStartGame = async (e) => {
    e.preventDefault();
    const trimmedNames = teamNames.map((n) => n.trim()).filter((n) => n.length);
    if (!trimmedNames.length) {
      alert("Please provide at least one team name.");
      return;
    }
    try {
      const res = await fetch(`${API_BASE_URL}/api/game/start`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ teamNames: trimmedNames }),
      });
      const data = await res.json();
      setTeams(data.teams || []);
      await fetchCategories();
      setStep("game");
      setActiveQuestion(null);
      setAnswerResult(null);
    } catch (err) {
      console.error(err);
      alert("Could not start game. Check backend server.");
    }
  };

  const handleGetQuestion = async (category) => {
    setIsLoadingQuestion(true);
    setAnswerResult(null);
    setAnswerForm({
      teamName: teams[0]?.name || "",
      answer: "",
    });
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/game/random-question?category=${encodeURIComponent(
          category
        )}`
      );
      if (res.status === 404) {
        setActiveQuestion(null);
        alert("No questions remaining for this category.");
        return;
      }
      const data = await res.json();
      setActiveQuestion(data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch question.");
    } finally {
      setIsLoadingQuestion(false);
    }
  };

  const handleAnswerSubmit = async (e) => {
    e.preventDefault();
    if (!activeQuestion) return;
    if (!answerForm.teamName) {
      alert("Please choose a team answering.");
      return;
    }
    if (!answerForm.answer.trim()) {
      alert("Please type an answer.");
      return;
    }
    setIsSubmittingAnswer(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/game/answer`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          questionId: activeQuestion.id,
          teamName: answerForm.teamName,
          answer: answerForm.answer,
        }),
      });
      const data = await res.json();
      setAnswerResult({
        correct: data.correct,
        correctAnswer: data.correctAnswer,
      });
      setTeams(data.teams || []);
    } catch (err) {
      console.error(err);
      alert("Failed to submit answer.");
    } finally {
      setIsSubmittingAnswer(false);
    }
  };

  const resetToSetup = () => {
    setStep("teams");
    setActiveQuestion(null);
    setAnswerResult(null);
    setIsLoadingQuestion(false);
  };

  const renderLogin = () => (
    <div className="page page-centered">
      <div className="card login-card fade-in">
        <div className="app-title">Career Jeopardy</div>
        <p className="subtitle">Teacher Login</p>
        <form onSubmit={handleLoginSubmit} className="form">
          <label className="field">
            <span>Username</span>
            <input
              type="text"
              name="username"
              value={loginForm.username}
              onChange={handleLoginChange}
              placeholder="teacher"
              autoComplete="off"
            />
          </label>
          <label className="field">
            <span>Password</span>
            <input
              type="password"
            name="password"
              value={loginForm.password}
              onChange={handleLoginChange}
              placeholder="password"
            />
          </label>
          {loginError && <div className="error-banner">{loginError}</div>}
          <button type="submit" className="btn primary-btn">
            Log In
          </button>
          <p className="hint">
            Hint: <code>teacher</code> / <code>password</code>
          </p>
        </form>
      </div>
    </div>
  );

  const renderTeamSetup = () => (
    <div className="page page-centered">
      <div className="card setup-card fade-in">
        <div className="app-title">Set Up Your Game</div>
        <p className="subtitle">
          Choose how many teams will play and give them fun names.
        </p>
        <form onSubmit={handleStartGame} className="form">
          <label className="field">
            <span>Number of teams</span>
            <select
              value={teamCount}
              onChange={(e) => setTeamCount(parseInt(e.target.value, 10))}
            >
              {[1, 2, 3, 4, 5, 6].map((n) => (
                <option key={n} value={n}>
                  {n}
                </option>
              ))}
            </select>
          </label>

          <div className="team-list">
            {Array.from({ length: teamCount }).map((_, idx) => (
              <label className="field" key={idx}>
                <span>Team {idx + 1} name</span>
                <input
                  type="text"
                  value={teamNames[idx] || ""}
                  onChange={(e) => handleTeamNameChange(idx, e.target.value)}
                  placeholder={`Team ${idx + 1}`}
                />
              </label>
            ))}
          </div>

          <button type="submit" className="btn primary-btn">
            Start Game
          </button>
        </form>
      </div>
    </div>
  );

  const renderScoreboard = () => (
    <div className="scoreboard">
      {teams.map((team) => (
        <div className="score-card" key={team.name}>
          <div className="score-name">{team.name}</div>
          <div className="score-points">{team.score}</div>
        </div>
      ))}
    </div>
  );

  const renderGameBoard = () => (
    <div className="page game-page fade-in">
      <div className="top-bar">
        <div>
          <div className="app-title small">Career Jeopardy</div>
          <div className="subtitle small">
            5 points for correct · -1 point for wrong
          </div>
        </div>
        <button className="btn subtle-btn" onClick={resetToSetup}>
          ⟵ Back to setup
        </button>
      </div>

      {renderScoreboard()}

      <div className="game-layout">
        {/* Left: categories */}
        <div className="categories-panel">
          <h3 className="panel-title">Categories</h3>
          {loadingCategories ? (
            <p className="muted">Loading categories…</p>
          ) : (
            <div className="categories-grid">
              {categories.map((cat) => (
                <button
                  key={cat}
                  className={
                    "category-tile" +
                    (activeQuestion && activeQuestion.category === cat
                      ? " category-tile-active"
                      : "")
                  }
                  onClick={() => handleGetQuestion(cat)}
                >
                  <span className="category-label">{cat}</span>
                  <span className="category-cta">Get question</span>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right: question + answer */}
        <div className="question-panel">
          <h3 className="panel-title">Question</h3>
          <div className="card question-card">
            {isLoadingQuestion && (
              <div className="question-placeholder">
                <div className="spinner" />
                <span>Fetching a question…</span>
              </div>
            )}

            {!isLoadingQuestion && !activeQuestion && (
              <div className="question-placeholder">
                <span>Select a category on the left to begin.</span>
              </div>
            )}

            {!isLoadingQuestion && activeQuestion && (
              <>
                <div className="question-header">
                  <span className="badge">{activeQuestion.category}</span>
                  <span className="question-label">Question</span>
                </div>
                <div className="question-text">{activeQuestion.text}</div>

                <form onSubmit={handleAnswerSubmit} className="answer-form">
                  <div className="answer-row">
                    <label className="field compact">
                      <span>Answering team</span>
                      <select
                        value={answerForm.teamName}
                        onChange={(e) =>
                          setAnswerForm((prev) => ({
                            ...prev,
                            teamName: e.target.value,
                          }))
                        }
                      >
                        <option value="">-- choose team --</option>
                        {teams.map((t) => (
                          <option key={t.name} value={t.name}>
                            {t.name}
                          </option>
                        ))}
                      </select>
                    </label>

                    <label className="field fill compact">
                      <span>Your answer</span>
                      <input
                        type="text"
                        value={answerForm.answer}
                        onChange={(e) =>
                          setAnswerForm((prev) => ({
                            ...prev,
                            answer: e.target.value,
                          }))
                        }
                        placeholder="Type your answer here…"
                      />
                    </label>

                    <button
                      type="submit"
                      className="btn primary-btn compact"
                      disabled={isSubmittingAnswer}
                    >
                      {isSubmittingAnswer ? "Checking…" : "Submit"}
                    </button>
                  </div>
                </form>

                {answerResult && (
                  <>
                    <div
                      className={
                        "result-banner " +
                        (answerResult.correct ? "correct" : "incorrect")
                      }
                    >
                      {answerResult.correct ? (
                        <span>✅ Correct! +5 points</span>
                      ) : (
                        <span>
                          ❌ Not quite. The expected answer was:{" "}
                          <strong>{answerResult.correctAnswer}</strong> (-1
                          point)
                        </span>
                      )}
                    </div>

                    <button
                      type="button"
                      className="btn next-question-btn"
                      onClick={() =>
                        handleGetQuestion(activeQuestion.category)
                      }
                    >
                      Next {activeQuestion.category} question
                    </button>
                  </>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="app-shell">
      {step === "login" && renderLogin()}
      {step === "teams" && renderTeamSetup()}
      {step === "game" && renderGameBoard()}
    </div>
  );
}

export default App;
