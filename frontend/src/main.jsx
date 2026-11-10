import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import AppLayout from './layouts/AppLayout';
import { LoginPage, RegisterPage } from './pages/AuthPages';
import { AlertsPage, DashboardPage, DevicesPage, HistoryPage, RoutinesPage, SettingsPage, UsersPage } from './pages/Pages';
import { Loading } from './components/UI';
import './styles.css';
function Protected({children,admin=false}){const {user,loading,isAdmin}=useAuth();if(loading)return <Loading/>;if(!user)return <Navigate to="/login" replace/>;if(admin&&!isAdmin)return <Navigate to="/" replace/>;return children;}
function App(){return <AuthProvider><BrowserRouter><Routes><Route path="/login" element={<LoginPage/>}/><Route path="/cadastro" element={<RegisterPage/>}/><Route element={<Protected><AppLayout/></Protected>}><Route index element={<DashboardPage/>}/><Route path="dispositivos" element={<DevicesPage/>}/><Route path="rotinas" element={<Protected admin><RoutinesPage/></Protected>}/><Route path="alertas" element={<Protected admin><AlertsPage/></Protected>}/><Route path="historico" element={<Protected admin><HistoryPage/></Protected>}/><Route path="usuarios" element={<Protected admin><UsersPage/></Protected>}/><Route path="configuracoes" element={<Protected admin><SettingsPage/></Protected>}/></Route><Route path="*" element={<Navigate to="/" replace/>}/></Routes></BrowserRouter></AuthProvider>}
ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><App/></React.StrictMode>);
