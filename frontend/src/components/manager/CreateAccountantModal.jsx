import { useState } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function CreateAccountantModal({ onClose, onCreated }) {
  const [form, setForm] = useState({
    email: "",
    password: "",
    fullName: "",
    phone: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await api.post("/manager/accountants", form);
      onCreated();
      onClose();
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={modalOverlayStyle}>
      <div className="card" style={{ width: "400px", margin: 0 }}>
        <h2>Новый бухгалтер</h2>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleSubmit} style={formStyle}>
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
            placeholder="Пароль (от 8 символов)"
            value={form.password}
            onChange={handleChange}
            required
            minLength={8}
          />
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

          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            <button type="submit" disabled={loading} style={btnPrimaryStyle}>
              {loading ? "Создание..." : "Создать"}
            </button>
            <button type="button" onClick={onClose} style={btnSecondaryStyle}>
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const modalOverlayStyle = {
  position: "fixed",
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: "rgba(0,0,0,0.5)",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  zIndex: 1000,
};
const formStyle = { display: "flex", flexDirection: "column", gap: "10px" };
const btnPrimaryStyle = {
  background: "#2563eb",
  color: "#fff",
  padding: "8px 16px",
  border: "none",
  borderRadius: "6px",
};
const btnSecondaryStyle = {
  background: "#e5e7eb",
  padding: "8px 16px",
  border: "none",
  borderRadius: "6px",
};
