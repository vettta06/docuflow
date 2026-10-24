import { useEffect, useState } from "react";
import api from "../api/axios";
import Loader from "../components/Loader";

export default function AccountantPage() {
  const [queue, setQueue] = useState({ stats: null, documents: [] });
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([api.get("/accountant/queue"), api.get("/tasks")])
      .then(([queueRes, tasksRes]) => {
        setQueue(queueRes.data);
        setTasks(tasksRes.data);
      })
      .catch((err) => {
        setError(err.response?.data?.message || "Ошибка загрузки данных");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) return <Loader />;
  if (error) return <div className="auth-error">{error}</div>;

  return (
    <div>
      <div className="card">
        <h2>Рабочая очередь</h2>
        {queue.stats && (
          <div style={{ marginBottom: "16px", display: "flex", gap: "20px" }}>
            <div>
              <strong>Новые:</strong> {queue.stats.newDocuments}
            </div>
            <div>
              <strong>На проверке:</strong> {queue.stats.needsReview}
            </div>
            <div>
              <strong>Ждут клиента:</strong> {queue.stats.waitingClient}
            </div>
            <div>
              <strong>Подтверждено:</strong> {queue.stats.confirmed}
            </div>
          </div>
        )}

        {queue.documents.length === 0 ? (
          <p>Очередь пуста.</p>
        ) : (
          <ul className="list">
            {queue.documents.map((doc) => (
              <li key={doc.id}>
                <strong>{doc.fileName}</strong> ({doc.documentType}) <br />
                <small>
                  Статус: {doc.status} | Размер: {doc.fileSize} байт
                </small>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="card">
        <h2>Задачи</h2>
        {tasks.length === 0 ? (
          <p>Задач нет.</p>
        ) : (
          <ul className="list">
            {tasks.map((task) => (
              <li key={task.id}>
                <strong>{task.documentType}</strong> —{" "}
                {task.comment || "Без комментария"} <br />
                <small>Статус: {task.status}</small>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
