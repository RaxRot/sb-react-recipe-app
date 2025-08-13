import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../lib/api";
import toast from "react-hot-toast";
import { isLoggedIn, isAdmin, getUser } from "../lib/auth";

function Skeleton() {
    return (
        <div className="grid gap-6 lg:grid-cols-5 animate-pulse">
            <div className="lg:col-span-3 h-80 rounded-2xl bg-neutral-200" />
            <div className="lg:col-span-2 space-y-4">
                <div className="h-8 w-2/3 bg-neutral-200 rounded" />
                <div className="h-5 w-28 bg-neutral-200 rounded" />
                <div className="h-24 bg-neutral-200 rounded" />
                <div className="h-24 bg-neutral-200 rounded" />
            </div>
        </div>
    );
}

export default function RecipeDetails() {
    const { id } = useParams();
    const [recipe, setRecipe] = useState(null);
    const [comments, setComments] = useState([]);
    const [text, setText] = useState("");
    const [loading, setLoading] = useState(true);
    const [errorCode, setErrorCode] = useState(null);

    const me = getUser();
    const admin = isAdmin();

    const load = async () => {
        setLoading(true);
        setErrorCode(null);
        try {
            // грузим рецепт и комменты раздельно, чтобы падение одного не ломало всё
            const r1 = await api.get(`/api/public/recipes/${id}`);
            setRecipe(r1.data);
            try {
                const r2 = await api.get(`/api/comments/recipe/${id}`);
                setComments(r2.data);
            } catch {
                setComments([]); // комменты не критичны
            }
        } catch (e) {
            const status = e?.response?.status;
            setErrorCode(status || "ERR");
            if (status !== 404 && status !== 401 && status !== 403) {
                toast.error(e?.response?.data?.message || "Failed to load recipe");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id]);

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

    // загрузка
    if (loading) return <Skeleton />;

    // не найдено
    if (errorCode === 404)
        return (
            <div className="text-center space-y-2">
                <h1 className="text-2xl font-bold">Recipe not found</h1>
                <p className="text-neutral-600">The recipe you’re looking for doesn’t exist.</p>
                <Link to="/" className="inline-block mt-2 rounded-lg border px-4 py-2 hover:bg-neutral-100">
                    Back to list
                </Link>
            </div>
        );

    // другие ошибки
    if (errorCode && errorCode !== 404) {
        return (
            <div className="text-center space-y-2">
                <h1 className="text-2xl font-bold">Something went wrong</h1>
                <p className="text-neutral-600">Please try again later.</p>
                <button onClick={load} className="mt-2 rounded-lg border px-4 py-2 hover:bg-neutral-100">
                    Retry
                </button>
            </div>
        );
    }

    if (!recipe) return null; // на всякий случай

    return (
        <div className="grid gap-6 lg:grid-cols-5">
            <div className="lg:col-span-3">
                <img
                    src={recipe.imageUrl || recipe.image}
                    alt={recipe.title}
                    className="w-full rounded-2xl border object-cover"
                />
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
