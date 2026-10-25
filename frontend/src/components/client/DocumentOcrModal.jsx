import { useState, useEffect } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function DocumentOcrModal({
  documentId,
  fileName,
  onClose,
  onUpdated,
}) {
  const [form, setForm] = useState({
    number: "",
    date: "",
    sellerInn: "",
    amount: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get(`/documents/${documentId}/details`)
      .then((res) => {
        const data = res.data;
        setForm({
          number: data.number || "",
          date: data.date || "",
          sellerInn: data.sellerInn || "",
          amount: data.amount || "",
        });
      })
      .catch((err) => setError(extractErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [documentId]);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleConfirm = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = {
        number: form.number,
        date: form.date || null,
        sellerInn: form.sellerInn,
        amount: form.amount ? parseFloat(form.amount) : null,
      };
      await api.put(`/documents/${documentId}/confirm`, payload);
      onUpdated();
      onClose();
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleDownload = async () => {
    try {
      const response = await api.get(`/documents/${documentId}/file`, {
        responseType: "blob",
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", fileName || "document");
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError("Не удалось скачать файл");
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
        <h2>Данные документа: {fileName}</h2>
        {error && <div className="auth-error">{error}</div>}

        {loading ? (
          <p>Загрузка распознанных данных...</p>
        ) : (
          <form
            onSubmit={handleConfirm}
            style={{ display: "flex", flexDirection: "column", gap: "10px" }}
          >
            <label>Номер документа:</label>
            <input
              name="number"
              value={form.number}
              onChange={handleChange}
              placeholder="Например, 1234"
            />

            <label>Дата:</label>
            <input
              name="date"
              type="date"
              value={form.date}
              onChange={handleChange}
            />

            <label>ИНН Продавца:</label>
            <input
              name="sellerInn"
              value={form.sellerInn}
              onChange={handleChange}
              placeholder="10 или 12 цифр"
            />

            <label>Сумма:</label>
            <input
              name="amount"
              type="number"
              step="0.01"
              value={form.amount}
              onChange={handleChange}
              placeholder="0.00"
            />

            <div
              style={{
                display: "flex",
                gap: "10px",
                marginTop: "10px",
                flexWrap: "wrap",
              }}
            >
              <button
                type="submit"
                disabled={saving}
                style={{
                  background: "#059669",
                  color: "#fff",
                  padding: "8px 16px",
                  border: "none",
                  borderRadius: "6px",
                }}
              >
                {saving ? "Сохранение..." : "Подтвердить данные"}
              </button>
              <button
                type="button"
                onClick={handleDownload}
                style={{
                  background: "#6b7280",
                  color: "#fff",
                  padding: "8px 16px",
                  border: "none",
                  borderRadius: "6px",
                }}
              >
                Скачать файл
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
                Закрыть
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
