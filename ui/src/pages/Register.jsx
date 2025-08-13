import { useForm } from "react-hook-form";
import api from "../lib/api";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

export default function Register() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const navigate = useNavigate();

    const onSubmit = async (data) => {
        try {
            await api.post("/api/public/auth/signup", data);
            toast.success("Account created. Please sign in.");
            navigate("/login");
        } catch (e) {
            toast.error(e.response?.data?.message || "Registration failed");
        }
    };

    return (
        <div className="mx-auto max-w-md space-y-4">
            <h1 className="text-2xl font-bold">Create account</h1>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
                <input {...register("username", { required: "Username is required", minLength: { value: 3, message: "Min 3 chars" } })}
                       placeholder="Username" className="w-full rounded-lg border px-3 py-2" />
                {errors.username && <p className="text-sm text-red-600">{errors.username.message}</p>}

                <input {...register("email", { required: "Email is required", pattern: { value: /\S+@\S+\.\S+/, message: "Invalid email" } })}
                       type="email" placeholder="Email" className="w-full rounded-lg border px-3 py-2" />
                {errors.email && <p className="text-sm text-red-600">{errors.email.message}</p>}

                <input {...register("password", { required: "Password is required", minLength: { value: 5, message: "Min 5 chars" } })}
                       type="password" placeholder="Password" className="w-full rounded-lg border px-3 py-2" />
                {errors.password && <p className="text-sm text-red-600">{errors.password.message}</p>}

                <button disabled={isSubmitting}
                        className="w-full rounded-lg bg-black py-2 text-white disabled:opacity-50">
                    {isSubmitting ? "Please wait..." : "Sign up"}
                </button>
            </form>
        </div>
    );
}
