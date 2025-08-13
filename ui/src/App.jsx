import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import RecipeDetails from "./pages/RecipeDetails";
import Login from "./pages/Login";
import Register from "./pages/Register";
import NewRecipe from "./pages/NewRecipe";
import AdminUsers from "./pages/AdminUsers";
import Protected from "./components/Protected";
import AdminOnly from "./components/AdminOnly";

export default function App() {
    return (
        <BrowserRouter>
            <div className="min-h-dvh bg-neutral-50 text-neutral-900">
                <Navbar />
                <main className="mx-auto max-w-5xl px-4 py-8">
                    <Routes>
                        {/* public */}
                        <Route path="/" element={<Home />} />
                        <Route path="/recipe/:id" element={<RecipeDetails />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />

                        {/* protected */}
                        <Route path="/new" element={<Protected><NewRecipe /></Protected>} />

                        {/* admin */}
                        <Route path="/admin/users" element={<AdminOnly><AdminUsers /></AdminOnly>} />

                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </main>
                <Toaster position="top-right" />
            </div>
        </BrowserRouter>
    );
}
