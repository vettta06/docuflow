import { useState } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function DocumentDetailsModal({ doc, onClose, onUpdate }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [comment, setComment] = useState("");

  const changeStatus = async (newStatus) => {
    setLoading(true);
    setError("");
    try {
      await api.patch(`/documents/${doc.id}/status`, {
        status: newStatus,
        comment: comment || null,
      });
      onUpdate();
      onClose();
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async () => {
    try {
      const response = await api.get(`/documents/${doc.id}/file`, {
        responseType: "blob",
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", doc.fileName);
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
        <h2>Документ: {doc.fileName}</h2>
        {error && <div className="auth-error">{error}</div>}

        <p>
          <strong>Тип:</strong> {doc.documentType}
        </p>
        <p>
          <strong>Статус:</strong> {doc.status}
        </p>
        <p>
          <strong>Размер:</strong> {doc.fileSize} байт
        </p>

        {/* Поле для комментария */}
        <div style={{ marginTop: "16px" }}>
          <label
            style={{ display: "block", marginBottom: "4px", fontSize: "14px" }}
          >
            Комментарий (для клиента):
          </label>
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Например: Неверно указан ИНН, исправьте..."
            style={{
              width: "100%",
              padding: "8px",
              borderRadius: "6px",
              border: "1px solid #d1d5db",
              minHeight: "60px",
            }}
          />
        </div>

        <div
          style={{
            display: "flex",
            gap: "10px",
            marginTop: "20px",
            flexWrap: "wrap",
          }}
        >
          <button
            onClick={handleDownload}
            style={{
              background: "#6b7280",
              color: "#fff",
              border: "none",
              padding: "8px 12px",
              borderRadius: "6px",
            }}
          >
            Скачать файл
          </button>

          {doc.status === "UPLOADED" && (
            <>
              <button
                onClick={() => changeStatus("CONFIRMED")}
                disabled={loading}
                style={{
                  background: "#059669",
                  color: "#fff",
                  border: "none",
                  padding: "8px 12px",
                  borderRadius: "6px",
                }}
              >
                Подтвердить
              </button>
              <button
                onClick={() => changeStatus("NEEDS_REVIEW")}
                disabled={loading}
                style={{
                  background: "#d97706",
                  color: "#fff",
                  border: "none",
                  padding: "8px 12px",
                  borderRadius: "6px",
                }}
              >
                На доработку
              </button>
            </>
          )}

          <button
            onClick={onClose}
            style={{
              background: "#e5e7eb",
              border: "none",
              padding: "8px 12px",
              borderRadius: "6px",
            }}
          >
            Закрыть
          </button>
        </div>
      </div>
    </div>
  );
}
