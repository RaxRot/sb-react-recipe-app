import { Link, useLocation, useNavigate } from "react-router-dom";
import { clearAuth, getUser, isAdmin, isLoggedIn } from "../lib/auth";

export default function Navbar() {
    const logged = isLoggedIn();
    const admin = isAdmin();
    const user = getUser();
    const navigate = useNavigate();
    const { pathname } = useLocation();

    const item = (to, label) => (
        <Link to={to}
              className={`px-3 py-1.5 rounded-xl text-sm transition ${
                  pathname === to ? "bg-black text-white shadow-sm" : "border hover:bg-neutral-100"
              }`}>
            {label}
        </Link>
    );

    return (
        <header className="sticky top-0 z-10 border-b bg-white/70 backdrop-blur supports-[backdrop-filter]:bg-white/60">
            <div className="mx-auto max-w-5xl px-4 h-14 flex items-center justify-between">
                <Link to="/" className="text-xl font-extrabold tracking-tight">🍳 RecipeHub</Link>
                <nav className="flex items-center gap-2">
                    {item("/", "Browse")}
                    {logged && item("/new", "Create")}
                    {admin && item("/admin/users", "Users")}
                    {!logged ? (
                        <>
                            {item("/login", "Sign in")}
                            <Link to="/register"
                                  className="px-3 py-1.5 rounded-xl text-sm bg-black text-white hover:opacity-90">
                                Sign up
                            </Link>
                        </>
                    ) : (
                        <button
                            onClick={() => { clearAuth(); navigate("/"); }}
                            className="px-3 py-1.5 rounded-xl text-sm border hover:bg-neutral-100"
                            title="Logout">
                            Logout ({user?.username})
                        </button>
                    )}
                </nav>
            </div>
        </header>
    );
}
