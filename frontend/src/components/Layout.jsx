import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Layout() {
  const { email, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <>
      <header className="layout-header">
        <h1>Курсовая: бухгалтерия</h1>
        <div>
          {email && <span style={{ marginRight: 12 }}>{email}</span>}
          <button onClick={handleLogout}>Выйти</button>
        </div>
      </header>
      <main className="layout-main">
        <Outlet />
      </main>
    </>
  );
}
