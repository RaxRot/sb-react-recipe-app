export const getUser = () => {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
};
export const setAuth = ({ token, user }) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(user));
};
export const clearAuth = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
};
export const isLoggedIn = () => !!localStorage.getItem("token");
export const isAdmin = () => getUser()?.role === "ROLE_ADMIN";
