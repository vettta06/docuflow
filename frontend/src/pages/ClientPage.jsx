import { useEffect, useState } from "react";
import api from "../api/axios";
import Loader from "../components/Loader";
import CreateOperationForm from "../components/client/CreateOperationForm";
import UploadDocumentForm from "../components/client/UploadDocumentForm";

export default function ClientPage() {
  const [operations, setOperations] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [expandedOpId, setExpandedOpId] = useState(null);
  const [uploadingForOpId, setUploadingForOpId] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [opsRes, tasksRes] = await Promise.all([
        api.get("/operations"),
        api.get("/tasks"),
      ]);
      setOperations(opsRes.data);
      setTasks(tasksRes.data);
    } catch (err) {
      setError(err.response?.data?.message || "Ошибка загрузки данных");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleOperationCreated = () => {
    setShowCreateForm(false);
    fetchData();
  };

  const handleDocumentUploaded = () => {
    setUploadingForOpId(null);
    fetchData();
  };

  if (loading && operations.length === 0) return <Loader />;
  if (error) return <div className="auth-error">{error}</div>;

  return (
    <div>
      <div className="card">
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "16px",
          }}
        >
          <h2 style={{ margin: 0 }}>Мои операции</h2>
          {!showCreateForm && (
            <button
              onClick={() => setShowCreateForm(true)}
              style={{
                background: "#2563eb",
                color: "#fff",
                border: "none",
                padding: "8px 16px",
                borderRadius: "6px",
              }}
            >
              + Создать операцию
            </button>
          )}
        </div>

        {showCreateForm && (
          <CreateOperationForm
            onCreated={handleOperationCreated}
            onCancel={() => setShowCreateForm(false)}
          />
        )}

        {operations.length === 0 ? (
          <p>Операций пока нет.</p>
        ) : (
          <ul className="list" style={{ marginTop: "16px" }}>
            {operations.map((op) => (
              <li
                key={op.id}
                style={{ padding: "12px 0", borderBottom: "1px solid #eef0f2" }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    cursor: "pointer",
                  }}
                  onClick={() =>
                    setExpandedOpId(expandedOpId === op.id ? null : op.id)
                  }
                >
                  <div>
                    <strong>{op.title}</strong> ({op.type?.name}) <br />
                    <small style={{ color: "#6b7280" }}>
                      Дата: {op.operationDate} | Статус: {op.status}
                    </small>
                  </div>
                  <div style={{ textAlign: "right" }}>
                    <div style={{ fontWeight: "bold" }}>{op.totalAmount} ₽</div>
                    <small style={{ color: "#2563eb" }}>
                      {expandedOpId === op.id ? "Скрыть" : "Подробнее"}
                    </small>
                  </div>
                </div>

                {expandedOpId === op.id && (
                  <div
                    style={{
                      marginTop: "12px",
                      padding: "12px",
                      background: "#f9fafb",
                      borderRadius: "6px",
                    }}
                  >
                    <h4 style={{ margin: "0 0 8px 0" }}>
                      Комплектность документов:
                    </h4>
                    <ul style={{ paddingLeft: "20px", fontSize: "14px" }}>
                      {op.requiredDocuments?.map((doc, idx) => (
                        <li
                          key={idx}
                          style={{
                            color: doc.provided ? "#166534" : "#991b1b",
                          }}
                        >
                          {doc.documentType}{" "}
                          {doc.mandatory ? "(обязательный)" : "(опциональный)"}{" "}
                          — {doc.provided ? "✓ Загружен" : "✗ Отсутствует"}
                        </li>
                      ))}
                    </ul>

                    {uploadingForOpId === op.id ? (
                      <UploadDocumentForm
                        operationId={op.id}
                        onUploaded={handleDocumentUploaded}
                        onCancel={() => setUploadingForOpId(null)}
                      />
                    ) : (
                      <button
                        onClick={() => setUploadingForOpId(op.id)}
                        style={{
                          marginTop: "10px",
                          background: "#059669",
                          color: "#fff",
                          border: "none",
                          padding: "6px 12px",
                          borderRadius: "4px",
                          fontSize: "12px",
                        }}
                      >
                        Прикрепить документ
                      </button>
                    )}
                  </div>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="card">
        <h2>Задачи от бухгалтера</h2>
        {tasks.length === 0 ? (
          <p>Задач пока нет.</p>
        ) : (
          <ul className="list">
            {tasks.map((task) => (
              <li key={task.id} style={{ padding: "12px 0" }}>
                <strong>{task.documentType}</strong>
                <div style={{ fontSize: "14px", marginTop: "4px" }}>
                  {task.comment || "Без комментария"}
                </div>
                <small style={{ color: "#6b7280" }}>
                  Статус: {task.status}
                </small>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
