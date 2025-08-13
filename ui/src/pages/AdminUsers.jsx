import { useEffect, useState } from "react";
import api from "../lib/api";
import toast from "react-hot-toast";

export default function AdminUsers() {
    const [items, setItems] = useState([]);

    const load = async () => {
        try {
            const r = await api.get("/api/admin/users");
            setItems(r.data);
        } catch (e) {
            toast.error(e.response?.data?.message || "Failed to load users");
        }
    };

    const del = async (id) => {
        if (!confirm("Delete this user?")) return;
        try {
            await api.delete(`/api/admin/users/${id}`);
            toast.success("User deleted");
            load();
        } catch (e) {
            toast.error(e.response?.data?.message || "Failed to delete");
        }
    };

    useEffect(() => { load(); }, []);

    return (
        <div>
            <h1 className="text-2xl font-bold mb-4">Users</h1>
            {items.length === 0 ? (
                <div className="text-neutral-600">No users.</div>
            ) : (
                <div className="overflow-x-auto rounded-xl border bg-white">
                    <table className="min-w-full text-sm">
                        <thead className="bg-neutral-50">
                        <tr>
                            <th className="px-4 py-2 text-left">ID</th>
                            <th className="px-4 py-2 text-left">Username</th>
                            <th className="px-4 py-2 text-left">Email</th>
                            <th className="px-4 py-2 text-left">Role</th>
                            <th className="px-4 py-2"></th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map(u => (
                            <tr key={u.id} className="border-t">
                                <td className="px-4 py-2">{u.id}</td>
                                <td className="px-4 py-2">{u.username}</td>
                                <td className="px-4 py-2">{u.email}</td>
                                <td className="px-4 py-2">{u.role}</td>
                                <td className="px-4 py-2 text-right">
                                    {u.role === "ROLE_USER" ? (
                                        <button onClick={() => del(u.id)}
                                                className="text-xs rounded-md border px-3 py-1 hover:bg-neutral-100">
                                            Delete
                                        </button>
                                    ) : (
                                        <span className="text-neutral-400 text-xs">protected</span>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
