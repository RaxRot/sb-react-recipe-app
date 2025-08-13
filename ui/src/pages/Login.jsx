import { useForm } from "react-hook-form";
import api from "../lib/api";
import toast from "react-hot-toast";
import { setAuth } from "../lib/auth";
import { useNavigate } from "react-router-dom";

export default function Login() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const navigate = useNavigate();

    const onSubmit = async (data) => {
        try {
            const r = await api.post("/api/public/auth/signin", data);
            setAuth({
                token: r.data.jwtToken,
                user: { username: r.data.username, role: r.data.role },
            });
            toast.success("Welcome back!");
            navigate("/");
        } catch (e) {
            toast.error(e.response?.data?.message || "Invalid credentials");
        }
    };

    return (
        <div className="mx-auto max-w-md space-y-4">
            <h1 className="text-2xl font-bold">Sign in</h1>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
                <input {...register("username", { required: "Username is required" })}
                       placeholder="Username" className="w-full rounded-lg border px-3 py-2" />
                {errors.username && <p className="text-sm text-red-600">{errors.username.message}</p>}
                <input {...register("password", { required: "Password is required" })}
                       type="password" placeholder="Password" className="w-full rounded-lg border px-3 py-2" />
                {errors.password && <p className="text-sm text-red-600">{errors.password.message}</p>}
                <button disabled={isSubmitting}
                        className="w-full rounded-lg bg-black py-2 text-white disabled:opacity-50">
                    {isSubmitting ? "Please wait..." : "Sign in"}
                </button>
            </form>
        </div>
    );
}
