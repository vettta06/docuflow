import { useEffect, useState } from "react";
import api from "../api/axios";
import Loader from "../components/Loader";

export default function ManagerPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/manager/dashboard")
      .then((res) => setData(res.data))
      .catch((err) =>
        setError(err.response?.data?.message || "Ошибка загрузки данных"),
      )
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loader />;
  if (error) return <div className="auth-error">{error}</div>;
  if (!data) return null;

  return (
    <div>
      <div className="card" style={{ display: "flex", gap: "40px" }}>
        <h2>Дашборд</h2>
        <div>
          <strong>Всего организаций:</strong> {data.totalOrganizations}
        </div>
        <div>
          <strong>Всего документов:</strong> {data.totalDocuments}
        </div>
      </div>

      <div className="card">
        <h2>Нагрузка на бухгалтеров</h2>
        {data.accountants.length === 0 ? (
          <p>Нет данных.</p>
        ) : (
          <table
            style={{
              width: "100%",
              textAlign: "left",
              borderCollapse: "collapse",
            }}
          >
            <thead>
              <tr style={{ borderBottom: "2px solid #eef0f2" }}>
                <th style={{ padding: "8px" }}>Бухгалтер</th>
                <th>Активные операции</th>
                <th>Ждут клиента</th>
                <th>Подтверждено</th>
              </tr>
            </thead>
            <tbody>
              {data.accountants.map((acc) => (
                <tr
                  key={acc.accountantId}
                  style={{ borderBottom: "1px solid #eef0f2" }}
                >
                  <td style={{ padding: "8px" }}>{acc.accountantName}</td>
                  <td>{acc.activeOperations}</td>
                  <td>{acc.waitingClient}</td>
                  <td>{acc.confirmedDocs}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
