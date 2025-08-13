import axios from "axios";
import toast from "react-hot-toast";

const api = axios.create({
    baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

api.interceptors.response.use(
    (res) => res,
    (err) => {
        const status = err?.response?.status;
        if (status === 401 || status === 403) {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            toast.error("Please sign in to continue");
            // hard redirect, чтобы гарантированно уйти на /login
            window.location.href = "/login";
        }
        return Promise.reject(err);
    }
);

export default api;
