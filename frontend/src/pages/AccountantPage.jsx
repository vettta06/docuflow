import { useEffect, useState } from "react";
import api from "../api/axios";
import Loader from "../components/Loader";
import DocumentDetailsModal from "../components/accountant/DocumentDetailsModal";
import CreateTaskModal from "../components/accountant/CreateTaskModal";

export default function AccountantPage() {
  const [queue, setQueue] = useState({ stats: null, documents: [] });
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [showTaskModal, setShowTaskModal] = useState(false);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [queueRes, tasksRes] = await Promise.all([
        api.get("/accountant/queue"),
        api.get("/tasks"),
      ]);
      setQueue(queueRes.data);
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

  if (loading && !queue.stats) return <Loader />;
  if (error) return <div className="auth-error">{error}</div>;

  const filteredDocs = queue.documents.filter((doc) => {
    const matchesSearch = doc.fileName
      .toLowerCase()
      .includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === "ALL" || doc.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div>
      <div className="card">
        <h2>Рабочая очередь</h2>

        {queue.stats && (
          <div
            style={{
              marginBottom: "20px",
              display: "flex",
              gap: "16px",
              flexWrap: "wrap",
            }}
          >
            <span
              style={{
                padding: "6px 12px",
                background: "#e0f2fe",
                color: "#0369a1",
                borderRadius: "20px",
                fontSize: "14px",
              }}
            >
              Новые: <strong>{queue.stats.newDocuments}</strong>
            </span>
            <span
              style={{
                padding: "6px 12px",
                background: "#fef08a",
                color: "#854d0e",
                borderRadius: "20px",
                fontSize: "14px",
              }}
            >
              На проверке: <strong>{queue.stats.needsReview}</strong>
            </span>
            <span
              style={{
                padding: "6px 12px",
                background: "#ffedd5",
                color: "#9a3412",
                borderRadius: "20px",
                fontSize: "14px",
              }}
            >
              Ждут клиента: <strong>{queue.stats.waitingClient}</strong>
            </span>
            <span
              style={{
                padding: "6px 12px",
                background: "#dcfce7",
                color: "#166534",
                borderRadius: "20px",
                fontSize: "14px",
              }}
            >
              Подтверждено: <strong>{queue.stats.confirmed}</strong>
            </span>
          </div>
        )}

        <div style={{ display: "flex", gap: "10px", marginBottom: "16px" }}>
          <input
            type="text"
            placeholder="Поиск по имени файла..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              padding: "8px",
              borderRadius: "6px",
              border: "1px solid #d1d5db",
              flex: 1,
            }}
          />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{
              padding: "8px",
              borderRadius: "6px",
              border: "1px solid #d1d5db",
            }}
          >
            <option value="ALL">Все статусы</option>
            <option value="UPLOADED">Загружен (UPLOADED)</option>
            <option value="CONFIRMED">Подтвержден (CONFIRMED)</option>
            <option value="NEEDS_REVIEW">На доработке (NEEDS_REVIEW)</option>
          </select>
        </div>

        {filteredDocs.length === 0 ? (
          <p>По вашему запросу ничего не найдено.</p>
        ) : (
          <ul className="list">
            {filteredDocs.map((doc) => (
              <li key={doc.id}>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                  }}
                >
                  <div>
                    <strong>{doc.fileName}</strong>{" "}
                    <span style={{ color: "#6b7280", fontSize: "12px" }}>
                      ({doc.documentType})
                    </span>
                    <br />
                    <small style={{ color: "#6b7280" }}>
                      Размер: {doc.fileSize} байт
                    </small>
                  </div>
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "10px",
                    }}
                  >
                    <span
                      style={{
                        padding: "4px 8px",
                        borderRadius: "4px",
                        fontSize: "12px",
                        fontWeight: "bold",
                        background:
                          doc.status === "CONFIRMED"
                            ? "#dcfce7"
                            : doc.status === "NEEDS_REVIEW"
                              ? "#fef08a"
                              : "#e5e7eb",
                        color:
                          doc.status === "CONFIRMED"
                            ? "#166534"
                            : doc.status === "NEEDS_REVIEW"
                              ? "#854d0e"
                              : "#374151",
                      }}
                    >
                      {doc.status}
                    </span>
                    <button
                      onClick={() => setSelectedDoc(doc)}
                      style={{
                        background: "#2563eb",
                        color: "#fff",
                        border: "none",
                        padding: "6px 12px",
                        borderRadius: "4px",
                        fontSize: "12px",
                      }}
                    >
                      Детали
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="card">
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "16px",
          }}
        >
          <h2 style={{ margin: 0 }}>Задачи</h2>
          <button
            onClick={() => setShowTaskModal(true)}
            style={{
              background: "#2563eb",
              color: "#fff",
              border: "none",
              padding: "8px 16px",
              borderRadius: "6px",
            }}
          >
            + Создать задачу
          </button>
        </div>

        {tasks.length === 0 ? (
          <p>Задач нет.</p>
        ) : (
          <ul className="list">
            {tasks.map((task) => (
              <li key={task.id}>
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <div>
                    <strong>{task.documentType}</strong>
                    <div style={{ fontSize: "14px", marginTop: "4px" }}>
                      {task.comment || "Без комментария"}
                    </div>
                  </div>
                  <span style={{ fontSize: "12px", color: "#6b7280" }}>
                    {task.status}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      {selectedDoc && (
        <DocumentDetailsModal
          doc={selectedDoc}
          onClose={() => setSelectedDoc(null)}
          onUpdate={fetchData}
        />
      )}

      {showTaskModal && (
        <CreateTaskModal
          onClose={() => setShowTaskModal(false)}
          onCreated={fetchData}
        />
      )}
    </div>
  );
}
