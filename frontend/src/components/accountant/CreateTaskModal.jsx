import { useState, useEffect } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function CreateTaskModal({ onClose, onCreated }) {
  const [operations, setOperations] = useState([]);
  const [form, setForm] = useState({
    operationId: "",
    documentType: "INVOICE",
    comment: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    // Получаем все операции, чтобы бухгалтер мог выбрать нужную
    api
      .get("/operations")
      .then((res) => {
        setOperations(res.data);
        if (res.data.length > 0) {
          setForm((prev) => ({ ...prev, operationId: res.data[0].id }));
        }
      })
      .catch(() => setError("Не удалось загрузить список операций"));
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.operationId) {
      setError("Выберите операцию");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const payload = {
        operationId: Number(form.operationId),
        documentType: form.documentType,
        comment: form.comment,
      };
      await api.post("/tasks", payload);
      onCreated();
      onClose();
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
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
      }}
    >
      <div className="card" style={{ width: "500px", margin: 0 }}>
        <h2>Создать задачу клиенту</h2>
        {error && <div className="auth-error">{error}</div>}

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "10px" }}
        >
          <label>Выберите операцию:</label>
          <select
            name="operationId"
            value={form.operationId}
            onChange={handleChange}
            style={{ padding: "8px" }}
            required
          >
            <option value="" disabled>
              Выберите операцию
            </option>
            {operations.map((op) => (
              <option key={op.id} value={op.id}>
                {op.title} ({op.organizationName})
              </option>
            ))}
          </select>

          <label>Какой документ нужен?</label>
          <select
            name="documentType"
            value={form.documentType}
            onChange={handleChange}
            style={{ padding: "8px" }}
          >
            <option value="INVOICE">Счет (INVOICE)</option>
            <option value="ACT">Акт (ACT)</option>
            <option value="CONTRACT">Договор (CONTRACT)</option>
            <option value="PAYMENT_CONFIRMATION">Подтверждение оплаты</option>
          </select>

          <label>Комментарий для клиента:</label>
          <textarea
            name="comment"
            value={form.comment}
            onChange={handleChange}
            placeholder="Например: Пожалуйста, пришлите акт выполненных работ"
            style={{
              padding: "8px",
              minHeight: "80px",
              borderRadius: "6px",
              border: "1px solid #d1d5db",
            }}
          />

          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                background: "#2563eb",
                color: "#fff",
                padding: "8px 16px",
                border: "none",
                borderRadius: "6px",
              }}
            >
              {loading ? "Создание..." : "Создать задачу"}
            </button>
            <button
              type="button"
              onClick={onClose}
              style={{
                background: "#e5e7eb",
                padding: "8px 16px",
                border: "none",
                borderRadius: "6px",
              }}
            >
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
