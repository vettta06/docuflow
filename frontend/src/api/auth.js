import api from "./axios";

export async function loginRequest(email, password) {
  const { data } = await api.post("/auth/login", { email, password });
  return data;
}

export async function registerRequest({ email, password, fullName, phone }) {
  const { data } = await api.post("/auth/register", {
    email,
    password,
    fullName,
    phone,
  });
  return data;
}

export function extractErrorMessage(err) {
  const data = err.response?.data;
  if (data?.fields) {
    const first = Object.values(data.fields)[0];
    if (first) return first;
  }
  return data?.message || "Не удалось выполнить запрос. Попробуйте ещё раз.";
}
