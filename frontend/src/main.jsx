import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { MonicaConfirmProvider } from './context/ConfirmContext';
import { MonikaProvider } from './context/MonikaContext';
import AppLayout from './layouts/AppLayout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import RoutinesPage from './pages/RoutinesPage';
import AlertsPage from './pages/AlertsPage';
import DevicesPage from './pages/DevicesPage';
import SettingsPage from './pages/SettingsPage';
import HistoryPage from './pages/HistoryPage';
import { Loading } from './components/UI';
import './styles.css';

/** Exige login; com `admin`, exige também o perfil de morador (ADMIN). */
function Protected({ children, admin = false }) {
  const { user, loading, isAdmin } = useAuth();
  if (loading) return <div className="canvas"><Loading /></div>;
  if (!user) return <Navigate to="/login" replace />;
  if (admin && !isAdmin) return <Navigate to="/" replace />;
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <MonicaConfirmProvider>
        <AuthProvider>
          <MonikaProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/cadastro" element={<RegisterPage />} />
            <Route element={<Protected><AppLayout /></Protected>}>
              <Route index element={<HomePage />} />
              <Route path="dispositivos" element={<DevicesPage />} />
              <Route path="configuracoes" element={<SettingsPage />} />
              <Route path="rotinas" element={<Protected admin><RoutinesPage /></Protected>} />
              <Route path="alertas" element={<Protected admin><AlertsPage /></Protected>} />
              <Route path="historico" element={<Protected admin><HistoryPage /></Protected>} />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
          </MonikaProvider>
        </AuthProvider>
        </MonicaConfirmProvider>
      </ToastProvider>
    </BrowserRouter>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><App /></React.StrictMode>);
