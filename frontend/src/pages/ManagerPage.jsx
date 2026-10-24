import { useEffect, useState } from "react";
import api from "../api/axios";
import Loader from "../components/Loader";
import CreateOrganizationModal from "../components/manager/CreateOrganizationModal";
import CreateAccountantModal from "../components/manager/CreateAccountantModal";
import AssignAccountantModal from "../components/manager/AssignAccountantModal";

export default function ManagerPage() {
  const [activeTab, setActiveTab] = useState("dashboard");
  const [dashboard, setDashboard] = useState(null);
  const [auditLogs, setAuditLogs] = useState([]);
  const [organizations, setOrganizations] = useState([]);
  const [accountants, setAccountants] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Состояния модалок
  const [showOrgModal, setShowOrgModal] = useState(false);
  const [showAccModal, setShowAccModal] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    setError("");
    try {
      const [dashRes, orgRes, accRes] = await Promise.all([
        api.get("/manager/dashboard"),
        api.get("/manager/organizations"),
        api.get("/manager/accountants"),
      ]);
      setDashboard(dashRes.data);
      setOrganizations(orgRes.data);
      setAccountants(accRes.data);
    } catch (err) {
      setError(
        err.response?.data?.message || "Ошибка загрузки данных менеджера",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === "dashboard") {
      fetchData();
    } else if (activeTab === "audit") {
      setLoading(true);
      api
        .get("/manager/audit")
        .then((res) => setAuditLogs(res.data))
        .catch((err) =>
          setError(err.response?.data?.message || "Ошибка загрузки истории"),
        )
        .finally(() => setLoading(false));
    }
  }, [activeTab]);

  if (loading && activeTab === "dashboard" && !dashboard) return <Loader />;
  if (error) return <div className="auth-error">{error}</div>;

  return (
    <div>
      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <button
          onClick={() => setActiveTab("dashboard")}
          style={{
            padding: "8px 16px",
            borderRadius: "6px",
            border: "none",
            fontWeight: "bold",
            background: activeTab === "dashboard" ? "#2563eb" : "#e5e7eb",
            color: activeTab === "dashboard" ? "#fff" : "#374151",
          }}
        >
          Управление и Дашборд
        </button>
        <button
          onClick={() => setActiveTab("audit")}
          style={{
            padding: "8px 16px",
            borderRadius: "6px",
            border: "none",
            fontWeight: "bold",
            background: activeTab === "audit" ? "#2563eb" : "#e5e7eb",
            color: activeTab === "audit" ? "#fff" : "#374151",
          }}
        >
          История (Audit Log)
        </button>
      </div>

      {activeTab === "dashboard" && dashboard && (
        <div>
          <div
            className="card"
            style={{
              display: "flex",
              gap: "40px",
              alignItems: "center",
              flexWrap: "wrap",
            }}
          >
            <div>
              <h2 style={{ margin: 0 }}>Дашборд</h2>
              <p style={{ margin: "8px 0 0 0" }}>
                Организаций: <strong>{dashboard.totalOrganizations}</strong> |
                Документов: <strong>{dashboard.totalDocuments}</strong>
              </p>
            </div>
            <div style={{ marginLeft: "auto", display: "flex", gap: "10px" }}>
              <button
                onClick={() => setShowOrgModal(true)}
                style={btnActionStyle}
              >
                + Организация
              </button>
              <button
                onClick={() => setShowAccModal(true)}
                style={btnActionStyle}
              >
                + Бухгалтер
              </button>
              <button
                onClick={() => setShowAssignModal(true)}
                style={btnActionStyle}
              >
                Назначить бухгалтера
              </button>
            </div>
          </div>

          <div className="card">
            <h2>Список организаций</h2>
            <ul className="list">
              {organizations.map((org) => (
                <li key={org.id}>
                  <strong>{org.name}</strong> — ИНН: {org.inn}
                </li>
              ))}
            </ul>
          </div>

          <div className="card">
            <h2>Список бухгалтеров</h2>
            <ul className="list">
              {accountants.map((acc) => (
                <li key={acc.id}>
                  <strong>{acc.fullName}</strong> ({acc.email}) — Клиентов:{" "}
                  {acc.clients?.length || 0}
                </li>
              ))}
            </ul>
          </div>

          <div className="card">
            <h2>Нагрузка на бухгалтеров</h2>
            {dashboard.accountants.length === 0 ? (
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
                  {dashboard.accountants.map((acc) => (
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
      )}

      {activeTab === "audit" && (
        <div className="card">
          <h2>Лента событий</h2>
          {auditLogs.length === 0 ? (
            <p>Событий пока нет.</p>
          ) : (
            <ul className="list">
              {auditLogs.map((log) => (
                <li
                  key={log.id}
                  style={{
                    padding: "12px 0",
                    borderBottom: "1px solid #eef0f2",
                  }}
                >
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <div>
                      <strong>{log.userName}</strong> — {log.action} <br />
                      <small style={{ color: "#6b7280" }}>
                        {log.entityType} (ID: {log.entityId})
                      </small>
                      {log.details && (
                        <div style={{ fontSize: "13px", marginTop: "4px" }}>
                          {log.details}
                        </div>
                      )}
                    </div>
                    <small style={{ color: "#6b7280" }}>
                      {new Date(log.createdAt).toLocaleString("ru-RU")}
                    </small>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}

      {showOrgModal && (
        <CreateOrganizationModal
          onClose={() => setShowOrgModal(false)}
          onCreated={fetchData}
        />
      )}
      {showAccModal && (
        <CreateAccountantModal
          onClose={() => setShowAccModal(false)}
          onCreated={fetchData}
        />
      )}
      {showAssignModal && (
        <AssignAccountantModal
          onClose={() => setShowAssignModal(false)}
          onAssigned={fetchData}
        />
      )}
    </div>
  );
}

const btnActionStyle = {
  background: "#2563eb",
  color: "#fff",
  border: "none",
  padding: "8px 14px",
  borderRadius: "6px",
  fontSize: "13px",
};
