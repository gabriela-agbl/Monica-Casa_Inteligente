import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import api from '../services/api';
const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); const [loading, setLoading] = useState(true);
  const logout = () => { localStorage.removeItem('monica.auth'); setUser(null); };
  const login = async (email, password) => {
    const raw = `${email.trim().toLowerCase()}:${password}`;
    const bytes = new TextEncoder().encode(raw);
    const token = btoa(Array.from(bytes, (byte) => String.fromCharCode(byte)).join(''));
    localStorage.setItem('monica.auth', token);
    try { const { data } = await api.get('/auth/me'); setUser(data); return data; }
    catch (e) { logout(); throw e; }
  };
  const register = async (payload) => api.post('/auth/register', payload);
  useEffect(() => { const onUnauthorized = () => logout(); window.addEventListener('monica:unauthorized', onUnauthorized);
    if (localStorage.getItem('monica.auth')) api.get('/auth/me').then(({ data }) => setUser(data)).catch(logout).finally(() => setLoading(false)); else setLoading(false);
    return () => window.removeEventListener('monica:unauthorized', onUnauthorized);
  }, []);
  const value = useMemo(() => ({ user, loading, login, logout, register, isAdmin: user?.perfil === 'ADMIN' }), [user, loading]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
