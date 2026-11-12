import { NavLink, Link, Outlet } from 'react-router-dom';
import { Bell, ChevronRight, Clock3, House, LayoutGrid, LogOut, Settings } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { initials } from '../utils/format';
import Brand from '../components/Brand';
import { asset } from '../components/Portrait';

/** Ordem e ícones iguais à barra lateral do Figma. `admin`: só o morador enxerga. */
const LINKS = [
  { to: '/', label: 'Home', icon: House, end: true },
  { to: '/alertas', label: 'Alertas', icon: Bell, admin: true },
  { to: '/rotinas', label: 'Rotinas', icon: Clock3, admin: true },
  { to: '/dispositivos', label: 'Dispositivos', icon: LayoutGrid },
  { to: '/configuracoes', label: 'Configurações', icon: Settings },
];

export default function AppLayout() {
  const { user, isAdmin, logout } = useAuth();

  return (
    <div className="canvas">
      <div className="window">
        <aside className="sidebar">
          <Link to="/" aria-label="Mônica — início" className="sidebar__brand">
            <Brand size={46} />
          </Link>

          <nav className="sidebar__nav" aria-label="Navegação principal">
            {LINKS.filter((l) => !l.admin || isAdmin).map(({ to, label, icon: Icon, end }) => (
              <NavLink key={to} to={to} end={end} className={({ isActive }) => `nav-link ${isActive ? 'is-active' : ''}`}>
                <Icon size={17} />
                <span>{label}</span>
              </NavLink>
            ))}
          </nav>

          <div className="sidebar__bottom">
            <button type="button" className="nav-link nav-link--plain" onClick={logout}>
              <LogOut size={17} />
              <span>Sair</span>
            </button>
            <Link to="/configuracoes" className="profile-link" title={user?.nome}>
              <span className="avatar" aria-hidden="true">
                <img src={asset('avatar.png')} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                <b>{initials(user?.nome)}</b>
              </span>
              <span>Perfil</span>
              <ChevronRight size={16} />
            </Link>
          </div>
        </aside>

        <main className="main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
