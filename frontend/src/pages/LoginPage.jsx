import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  loginRequest,
  registerRequest,
  extractErrorMessage,
} from "../api/auth";
import { useAuth } from "../auth/AuthContext";
import { homePathForRole } from "../components/ProtectedRoute";

export default function LoginPage() {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({
    email: "",
    password: "",
    fullName: "",
    phone: "",
  });
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");
  const [loading, setLoading] = useState(false);

  const { saveAuth } = useAuth();
  const navigate = useNavigate();

  const isRegister = mode === "register";

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function switchMode() {
    setMode(isRegister ? "login" : "register");
    setError("");
    setInfo("");
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setInfo("");
    setLoading(true);
    try {
      if (isRegister) {
        await registerRequest({
          email: form.email,
          password: form.password,
          fullName: form.fullName,
          phone: form.phone || null,
        });
        setInfo("Регистрация успешна. Войдите в систему.");
        setMode("login");
      } else {
        const data = await loginRequest(form.email, form.password);
        saveAuth(data.token, data.role, data.email);
        navigate(homePathForRole(data.role), { replace: true });
      }
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>{isRegister ? "Регистрация" : "Вход"}</h2>

        {error && <div className="auth-error">{error}</div>}
        {info && <div className="auth-info">{info}</div>}

        <form onSubmit={handleSubmit}>
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={form.email}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Пароль"
            value={form.password}
            onChange={handleChange}
            required
            minLength={6}
          />
          {isRegister && (
            <>
              <input
                name="fullName"
                placeholder="ФИО"
                value={form.fullName}
                onChange={handleChange}
                required
              />
              <input
                name="phone"
                placeholder="Телефон (необязательно)"
                value={form.phone}
                onChange={handleChange}
              />
            </>
          )}
          <button type="submit" disabled={loading}>
            {loading
              ? "Подождите..."
              : isRegister
                ? "Зарегистрироваться"
                : "Войти"}
          </button>
        </form>

        <div className="auth-toggle">
          {isRegister ? "Уже есть аккаунт? " : "Нет аккаунта? "}
          <button type="button" onClick={switchMode}>
            {isRegister ? "Войти" : "Зарегистрироваться"}
          </button>
        </div>
      </div>
    </div>
  );
}
