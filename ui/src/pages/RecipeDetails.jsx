import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../lib/api";
import toast from "react-hot-toast";
import { isLoggedIn, isAdmin, getUser } from "../lib/auth";

export default function RecipeDetails() {
    const { id } = useParams();
    const [recipe, setRecipe] = useState(null);
    const [comments, setComments] = useState([]);
    const [text, setText] = useState("");

    const me = getUser();
    const admin = isAdmin();

    const load = async () => {
        const [r1, r2] = await Promise.all([
            api.get(`/api/public/recipes/${id}`),
            api.get(`/api/comments/recipe/${id}`),
        ]);
        setRecipe(r1.data);
        setComments(r2.data);
    };

    useEffect(() => { load(); }, [id]);

    const submitComment = async (e) => {
        e.preventDefault();
        if (!text.trim()) return;
        try {
            await api.post(`/api/comments/recipe/${id}`, { comment: text });
            setText("");
            await load();
            toast.success("Comment added");
        } catch (e) {
            toast.error(e.response?.data?.message || "Failed to comment");
        }
    };

    const removeComment = async (cid) => {
        try {
            await api.delete(`/api/comments/${cid}`);
            await load();
            toast.success("Comment removed");
        } catch (e) {
            toast.error(e.response?.data?.message || "Failed to remove");
        }
    };

    if (!recipe) return null;

    return (
        <div className="grid gap-6 lg:grid-cols-5">
            <div className="lg:col-span-3">
                <img src={recipe.imageUrl} alt={recipe.title} className="w-full rounded-2xl border object-cover" />
            </div>
            <div className="lg:col-span-2 space-y-4">
                <h1 className="text-3xl font-bold">{recipe.title}</h1>
                <span className="inline-block rounded-full border px-3 py-1 text-xs">{recipe.difficulty}</span>
                <div>
                    <h2 className="font-semibold mb-2">Ingredients</h2>
                    <p className="whitespace-pre-line text-neutral-700">{recipe.ingredients}</p>
                </div>
                <div>
                    <h2 className="font-semibold mb-2">Description</h2>
                    <p className="whitespace-pre-line text-neutral-700">{recipe.description}</p>
                </div>
            </div>

            <div className="lg:col-span-5">
                <h2 className="text-xl font-semibold mb-3">Comments</h2>
                <ul className="space-y-3 mb-4">
                    {comments.map((c) => {
                        const mine = me && c.authorUsername === me.username;
                        return (
                            <li key={c.id} className="rounded-xl border bg-white p-3 flex justify-between items-start">
                                <div>
                                    <div className="text-sm text-neutral-500">@{c.authorUsername}</div>
                                    <div>{c.comment}</div>
                                </div>
                                {(admin || mine) && (
                                    <button
                                        onClick={() => removeComment(c.id)}
                                        className="text-xs rounded-md border px-2 py-1 hover:bg-neutral-100"
                                        title="Delete comment">
                                        Delete
                                    </button>
                                )}
                            </li>
                        );
                    })}
                </ul>

                {isLoggedIn() ? (
                    <form onSubmit={submitComment} className="flex gap-2">
                        <input
                            value={text}
                            onChange={(e) => setText(e.target.value)}
                            placeholder="Write a comment..."
                            className="flex-1 rounded-lg border px-3 py-2"
                            maxLength={100}
                            required
                        />
                        <button className="rounded-lg bg-black px-4 py-2 text-white">Send</button>
                    </form>
                ) : (
                    <div className="text-sm text-neutral-600">Sign in to write a comment.</div>
                )}
            </div>
        </div>
    );
}
