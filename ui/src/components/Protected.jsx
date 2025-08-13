import { isLoggedIn } from "../lib/auth";
import { Navigate } from "react-router-dom";

export default function Protected({ children }) {
    if (!isLoggedIn()) return <Navigate to="/login" replace />;
    return children;
}
