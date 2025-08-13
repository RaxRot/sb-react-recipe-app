import clsx from "clsx";

const DIFF = ["ALL", "EASY", "MEDIUM", "HARD"];

export default function Filters({ value, onChange }) {
    return (
        <div className="flex flex-wrap items-center gap-2">
            {DIFF.map((d) => (
                <button
                    key={d}
                    onClick={() => onChange(d)}
                    className={clsx(
                        "px-3 py-1 rounded-full border text-sm transition",
                        value === d
                            ? "bg-black text-white border-black shadow-sm"
                            : "bg-white hover:bg-neutral-100"
                    )}
                    aria-pressed={value === d}
                >
                    {d}
                </button>
            ))}
        </div>
    );
}
