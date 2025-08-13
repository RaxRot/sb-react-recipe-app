import { useEffect, useState } from "react";
import api from "../lib/api";
import { Link } from "react-router-dom";
import Filters from "../components/Filters";

const badgeClass = (d) => {
    const base = "inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-semibold tracking-wide uppercase";
    switch (d) {
        case "EASY": return base + " bg-emerald-50 text-emerald-700 border border-emerald-200";
        case "MEDIUM": return base + " bg-amber-50 text-amber-700 border border-amber-200";
        case "HARD": return base + " bg-rose-50 text-rose-700 border border-rose-200";
        default: return base + " bg-neutral-100 text-neutral-700 border border-neutral-200";
    }
};

function CardSkeleton() {
    return (
        <div className="overflow-hidden rounded-2xl border bg-white animate-pulse">
            <div className="h-44 w-full bg-neutral-200" />
            <div className="p-4 space-y-3">
                <div className="h-4 w-20 bg-neutral-200 rounded" />
                <div className="h-4 w-3/4 bg-neutral-200 rounded" />
                <div className="h-4 w-1/2 bg-neutral-200 rounded" />
            </div>
        </div>
    );
}

export default function Home() {
    const [items, setItems] = useState([]);
    const [difficulty, setDifficulty] = useState("ALL");
    const [loading, setLoading] = useState(true);

    const load = async (diff) => {
        setLoading(true);
        try {
            const url =
                diff === "ALL"
                    ? "/api/public/recipes"
                    : `/api/public/recipes/difficulty/${diff}`;
            const r = await api.get(url);
            setItems(r.data);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(difficulty); }, [difficulty]);

    return (
        <>
            {/* HERO */}
            <div className="relative overflow-hidden rounded-3xl border bg-gradient-to-br from-neutral-900 via-neutral-800 to-black text-white mb-8">
                <div className="absolute inset-0 opacity-20"
                     style={{ backgroundImage: "radial-gradient(circle at 20% 20%, #fff 2px, transparent 2px)" }} />
                <div className="relative p-8 lg:p-12 flex flex-col lg:flex-row items-start lg:items-center gap-6">
                    <div className="flex-1">
                        <h1 className="text-4xl lg:text-5xl font-extrabold tracking-tight">Cook. Share. Inspire.</h1>
                        <p className="mt-2 text-neutral-300">
                            Explore community recipes, from easy weeknight dishes to weekend challenges.
                        </p>
                    </div>
                    <div className="bg-white text-neutral-900 rounded-2xl px-5 py-4 shadow-lg">
                        <div className="text-xs font-semibold text-neutral-500 mb-1 uppercase tracking-wide">
                            Filter by difficulty
                        </div>
                        <Filters value={difficulty} onChange={setDifficulty} />
                    </div>
                </div>
            </div>

            {/* CONTENT */}
            {loading ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {Array.from({ length: 6 }).map((_, i) => <CardSkeleton key={i} />)}
                </div>
            ) : items.length === 0 ? (
                <div className="text-center text-neutral-600">
                    No recipes found. Try another filter.
                </div>
            ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
                    {items.map((r) => (
                        <Link key={r.id} to={`/recipe/${r.id}`} className="group block">
                            <div className="overflow-hidden rounded-2xl border bg-white shadow-sm hover:shadow-xl transition-shadow">
                                <div className="relative">
                                    <img
                                        src={r.imageUrl}
                                        alt={r.title}
                                        className="h-48 w-full object-cover group-hover:scale-[1.03] transition-transform"
                                    />
                                    <div className="absolute left-3 top-3">
                                        <span className={badgeClass(r.difficulty)}>{r.difficulty}</span>
                                    </div>
                                </div>
                                <div className="p-4">
                                    <h3 className="text-lg font-semibold line-clamp-2">{r.title}</h3>
                                    <p className="mt-1 text-sm text-neutral-600 line-clamp-2">{r.description}</p>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>
            )}
        </>
    );
}
