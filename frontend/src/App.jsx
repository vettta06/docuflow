import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthContext";
import ProtectedRoute, { homePathForRole } from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import ClientPage from "./pages/ClientPage";
import AccountantPage from "./pages/AccountantPage";
import ManagerPage from "./pages/ManagerPage";
import "./App.css";

function AppRoutes() {
  const { token, role } = useAuth();

  return (
    <Routes>
      <Route
        path="/login"
        element={
          token ? (
            <Navigate to={homePathForRole(role)} replace />
          ) : (
            <LoginPage />
          )
        }
      />

      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route
          path="/client"
          element={
            <ProtectedRoute allowedRole="CLIENT">
              <ClientPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/accountant"
          element={
            <ProtectedRoute allowedRole="ACCOUNTANT">
              <AccountantPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/manager"
          element={
            <ProtectedRoute allowedRole="MANAGER">
              <ManagerPage />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route
        path="*"
        element={
          <Navigate to={token ? homePathForRole(role) : "/login"} replace />
        }
      />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}
