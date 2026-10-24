import { useState, useEffect } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function AssignAccountantModal({ onClose, onAssigned }) {
  const [accountants, setAccountants] = useState([]);
  const [organizations, setOrganizations] = useState([]);
  const [form, setForm] = useState({ accountantId: "", organizationId: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([
      api.get("/manager/accountants"),
      api.get("/manager/organizations"),
    ])
      .then(([accRes, orgRes]) => {
        setAccountants(accRes.data);
        setOrganizations(orgRes.data);
        if (accRes.data.length > 0)
          setForm((prev) => ({ ...prev, accountantId: accRes.data[0].id }));
        if (orgRes.data.length > 0)
          setForm((prev) => ({ ...prev, organizationId: orgRes.data[0].id }));
      })
      .catch(() => setError("Не удалось загрузить данные для назначения"));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.accountantId || !form.organizationId) return;
    setLoading(true);
    setError("");
    try {
      await api.post("/manager/assignments", {
        accountantId: Number(form.accountantId),
        organizationId: Number(form.organizationId),
      });
      onAssigned();
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
        <h2>Назначить бухгалтера</h2>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleSubmit} style={formStyle}>
          <label>Бухгалтер:</label>
          <select
            value={form.accountantId}
            onChange={(e) => setForm({ ...form, accountantId: e.target.value })}
            required
          >
            {accountants.map((acc) => (
              <option key={acc.id} value={acc.id}>
                {acc.fullName} ({acc.email})
              </option>
            ))}
          </select>

          <label>Организация (Клиент):</label>
          <select
            value={form.organizationId}
            onChange={(e) =>
              setForm({ ...form, organizationId: e.target.value })
            }
            required
          >
            {organizations.map((org) => (
              <option key={org.id} value={org.id}>
                {org.name}
              </option>
            ))}
          </select>

          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            <button type="submit" disabled={loading} style={btnPrimaryStyle}>
              {loading ? "Назначение..." : "Назначить"}
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
