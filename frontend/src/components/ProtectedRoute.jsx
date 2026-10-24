import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function homePathForRole(role) {
  switch (role) {
    case "CLIENT":
      return "/client";
    case "ACCOUNTANT":
      return "/accountant";
    case "MANAGER":
      return "/manager";
    default:
      return "/login";
  }
}

export default function ProtectedRoute({ allowedRole, children }) {
  const { token, role } = useAuth();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRole && role !== allowedRole) {
    return <Navigate to={homePathForRole(role)} replace />;
  }

  return children;
}
