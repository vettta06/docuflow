import { useState } from "react";
import api from "../../api/axios";
import { extractErrorMessage } from "../../api/auth";

export default function UploadDocumentForm({
  operationId,
  onUploaded,
  onCancel,
}) {
  const [file, setFile] = useState(null);
  const [documentType, setDocumentType] = useState("INVOICE");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setError("Выберите файл");
      return;
    }
    setLoading(true);
    setError("");

    const formData = new FormData();
    formData.append("file", file);
    formData.append("operationId", operationId);
    formData.append("documentType", documentType);

    try {
      const { data } = await api.post("/documents", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      onUploaded(data);
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        marginTop: "10px",
        padding: "10px",
        background: "#f3f4f6",
        borderRadius: "6px",
      }}
    >
      <h4>Загрузить документ</h4>
      {error && (
        <div className="auth-error" style={{ fontSize: "12px" }}>
          {error}
        </div>
      )}
      <form
        onSubmit={handleSubmit}
        style={{ display: "flex", flexDirection: "column", gap: "8px" }}
      >
        <select
          value={documentType}
          onChange={(e) => setDocumentType(e.target.value)}
          style={{ padding: "6px" }}
        >
          <option value="INVOICE">Счет (INVOICE)</option>
          <option value="ACT">Акт (ACT)</option>
          <option value="CONTRACT">Договор (CONTRACT)</option>
        </select>
        <input type="file" onChange={(e) => setFile(e.target.files[0])} />
        <div style={{ display: "flex", gap: "8px" }}>
          <button
            type="submit"
            disabled={loading}
            style={{
              background: "#2563eb",
              color: "#fff",
              padding: "6px 12px",
              border: "none",
              borderRadius: "4px",
              fontSize: "12px",
            }}
          >
            {loading ? "Загрузка..." : "Загрузить"}
          </button>
          <button
            type="button"
            onClick={onCancel}
            style={{
              background: "#e5e7eb",
              padding: "6px 12px",
              border: "none",
              borderRadius: "4px",
              fontSize: "12px",
            }}
          >
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
}
