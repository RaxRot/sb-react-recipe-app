import { isAdmin, isLoggedIn } from "../lib/auth";
import { Navigate } from "react-router-dom";

export default function AdminOnly({ children }) {
    if (!isLoggedIn()) return <Navigate to="/login" replace />;
    if (!isAdmin()) return <Navigate to="/" replace />;
    return children;
}