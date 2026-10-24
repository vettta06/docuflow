import { useState, useEffect } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function CreateOperationForm({ onCreated, onCancel }) {
  const [types, setTypes] = useState([]);
  const [form, setForm] = useState({
    title: "",
    typeId: "",
    totalAmount: "",
    operationDate: new Date().toISOString().split("T")[0],
  });
  const [loading, setLoading] = useState(false);
  const [loadingTypes, setLoadingTypes] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/operation-types")
      .then((res) => {
        setTypes(res.data);
        if (res.data.length > 0) {
          setForm((prev) => ({ ...prev, typeId: res.data[0].id }));
        }
      })
      .catch((err) => {
        setError("Не удалось загрузить список типов операций");
      })
      .finally(() => {
        setLoadingTypes(false);
      });
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.typeId) {
      setError("Выберите тип операции");
      return;
    }
    setLoading(true);
    setError("");
    try {
      const payload = {
        title: form.title,
        typeId: Number(form.typeId),
        totalAmount: parseFloat(form.totalAmount),
        operationDate: form.operationDate,
      };
      const { data } = await api.post("/operations", payload);
      onCreated(data);
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  if (loadingTypes) return <p>Загрузка типов...</p>;

  return (
    <div
      className="card"
      style={{ backgroundColor: "#f9fafb", border: "1px solid #e5e7eb" }}
    >
      <h3>Новая операция</h3>
      {error && <div className="auth-error">{error}</div>}
      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", flexDirection: "column", gap: "10px" }}
      >
        <input
          name="title"
          placeholder="Название операции"
          value={form.title}
          onChange={handleChange}
          required
        />

        <select
          name="typeId"
          value={form.typeId}
          onChange={handleChange}
          style={{ padding: "8px" }}
          required
        >
          <option value="" disabled>
            Выберите тип
          </option>
          {types.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name}
            </option>
          ))}
        </select>

        <input
          name="totalAmount"
          type="number"
          step="0.01"
          placeholder="Сумма"
          value={form.totalAmount}
          onChange={handleChange}
          required
        />
        <input
          name="operationDate"
          type="date"
          value={form.operationDate}
          onChange={handleChange}
          required
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
            {loading ? "Создание..." : "Создать"}
          </button>
          <button
            type="button"
            onClick={onCancel}
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
  );
}
