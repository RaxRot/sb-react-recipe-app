import { useForm } from "react-hook-form";
import api from "../lib/api";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

export default function NewRecipe() {
    const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm();
    const navigate = useNavigate();

    const onSubmit = async (vals) => {
        try {
            const data = {
                title: vals.title,
                description: vals.description,
                ingredients: vals.ingredients,
                difficulty: vals.difficulty,
                imageUrl: null, // по контракту
            };

            const form = new FormData();
            form.append("data", JSON.stringify(data));
            form.append("file", vals.file[0]);

            const r = await api.post("/api/recipes", form, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            toast.success("Recipe created");
            reset();
            navigate(`/recipe/${r.data.id}`);
        } catch (e) {
            toast.error(e.response?.data?.message || "Failed to create recipe");
        }
    };

    return (
        <div className="mx-auto max-w-2xl space-y-4">
            <h1 className="text-2xl font-bold">Create a new recipe</h1>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
                <input {...register("title", { required: "Title is required", maxLength: { value: 100, message: "Max 100" } })}
                       placeholder="Title" className="w-full rounded-lg border px-3 py-2" />
                {errors.title && <p className="text-sm text-red-600">{errors.title.message}</p>}

                <textarea {...register("description", { required: "Description is required", maxLength: { value: 2000, message: "Max 2000" } })}
                          placeholder="Description" rows={5}
                          className="w-full rounded-lg border px-3 py-2" />
                {errors.description && <p className="text-sm text-red-600">{errors.description.message}</p>}

                <textarea {...register("ingredients", { required: "Ingredients are required", maxLength: { value: 2000, message: "Max 2000" } })}
                          placeholder="Ingredients (one per line or separated by ;)"
                          rows={4} className="w-full rounded-lg border px-3 py-2" />
                {errors.ingredients && <p className="text-sm text-red-600">{errors.ingredients.message}</p>}

                <select {...register("difficulty", { required: "Difficulty is required" })}
                        className="w-full rounded-lg border px-3 py-2">
                    <option value="">Select difficulty</option>
                    <option value="EASY">EASY</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HARD">HARD</option>
                </select>
                {errors.difficulty && <p className="text-sm text-red-600">{errors.difficulty.message}</p>}

                <input type="file" accept="image/*" {...register("file", { required: "Image is required" })}
                       className="w-full rounded-lg border px-3 py-2" />
                {errors.file && <p className="text-sm text-red-600">{errors.file.message}</p>}

                <button disabled={isSubmitting}
                        className="w-full rounded-lg bg-black py-2 text-white disabled:opacity-50">
                    {isSubmitting ? "Please wait..." : "Publish"}
                </button>
            </form>
        </div>
    );
}
