import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { pick } from '../utils/monica';
import { BLOCK_LINES, DOUBT_LINE, FORGIVE_LINE, HINT_LINE, INTRO, WRONG_LINES, careLine, declaresLove } from '../utils/monika';
import { useAuth } from './AuthContext';
import MonikaOverlay from '../components/MonikaOverlay';

const STORAGE = 'monica.justmonika';
const ACTION_EVERY_MS = 9000;

const MonikaContext = createContext({ active: false, enter: () => {} });

/**
 * Lê o estado salvo na ABA (sessionStorage): recarregar a página não escapa do modo,
 * mas fechar a aba ou abrir o app de novo sim. A URL com `?paz` também zera tudo
 * (saída de emergência para a apresentação).
 */
function readActive() {
  try {
    if (new URLSearchParams(window.location.search).has('paz')) {
      sessionStorage.removeItem(STORAGE);
      return false;
    }
    return sessionStorage.getItem(STORAGE) === '1';
  } catch {
    return false;
  }
}

/** Uma ação real que ela toma sozinha: roda uma rotina ativa ou liga/desliga um dispositivo. */
async function takeControl(isAdmin) {
  try {
    if (isAdmin && Math.random() < 0.5) {
      const routines = ((await api.get('/rotinas')).data || []).filter((r) => r.ativo !== false);
      if (routines.length) {
        const r = pick(routines);
        await api.post(`/rotinas/${r.id}/executar`);
        return `Executei a rotina "${r.nome}" por você.`;
      }
    }
    const devices = (await api.get('/dispositivos')).data || [];
    if (!devices.length) return 'Não há nada aqui para eu cuidar... só você.';
    const d = pick(devices);
    const ligado = !d.status;
    await api.patch(`/dispositivos/${d.id}/status`, { ligado });
    return `${ligado ? 'Liguei' : 'Desliguei'} "${d.nome}".`;
  } catch {
    return 'Mexi em algumas coisas por aqui.';
  }
}

/**
 * Modo Just Monika: ao ser recusada, a Mônica inverte o app, bloqueia a interface,
 * toma decisões sozinha (rotinas e dispositivos reais) e só solta quem disser que a ama.
 */
export function MonikaProvider({ children }) {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [active, setActive] = useState(readActive);
  const [leaving, setLeaving] = useState(false);
  const [line, setLine] = useState({ key: 0, text: INTRO });

  const snapshot = useRef(null);   // estado dos dispositivos antes dela mexer
  const doubt = useRef(0);         // quantas vezes ele disse "eu te amo"
  const fails = useRef(0);         // tentativas erradas
  const lastBlock = useRef(0);

  const say = useCallback((text) => setLine((l) => ({ key: l.key + 1, text })), []);

  const enter = useCallback(async () => {
    if (active) return;
    try {
      const { data } = await api.get('/dispositivos');
      snapshot.current = Object.fromEntries((data || []).map((d) => [d.id, d.status]));
    } catch { snapshot.current = null; }
    try { sessionStorage.setItem(STORAGE, '1'); } catch { /* sem storage: vale só em memória */ }
    doubt.current = 0;
    fails.current = 0;
    setLeaving(false);
    setActive(true);
    say(INTRO);
    api.post('/monika/assumir').catch(() => {}); // alerta no WhatsApp do morador (só ADMIN)
  }, [active, say]);

  const leave = useCallback(async () => {
    // Devolve os dispositivos ao estado em que estavam antes dela assumir.
    if (snapshot.current) {
      try {
        const { data } = await api.get('/dispositivos');
        await Promise.all((data || [])
          .filter((d) => d.id in snapshot.current && snapshot.current[d.id] !== d.status)
          .map((d) => api.patch(`/dispositivos/${d.id}/status`, { ligado: snapshot.current[d.id] })));
      } catch { /* melhor esforço */ }
    }
    api.post('/monika/perdoar').catch(() => {});
    try { sessionStorage.removeItem(STORAGE); } catch { /* ignore */ }
    snapshot.current = null;
    setActive(false);
    setLeaving(false);
    navigate('/');
  }, [navigate]);

  // Inverte o app, congela o #root e treme uma vez ao entrar.
  useEffect(() => {
    if (!active) return undefined;
    const root = document.getElementById('root');
    document.documentElement.classList.add('just-monika');
    if (root) root.inert = true;
    return () => {
      document.documentElement.classList.remove('just-monika');
      if (root) root.inert = false;
    };
  }, [active]);

  // Ela mexe na casa sozinha e passeia pelas telas para você ver o que fez.
  useEffect(() => {
    if (!active || leaving || !user) return undefined;
    let alive = true;
    let step = 0;
    const routes = isAdmin ? ['/dispositivos', '/', '/rotinas', '/alertas'] : ['/dispositivos', '/'];
    const tick = async () => {
      const done = await takeControl(isAdmin);
      if (!alive) return;
      say(`${done} ${careLine()}`);
      navigate(routes[step % routes.length]);
      step += 1;
    };
    const first = setTimeout(tick, 5000);
    const id = setInterval(tick, ACTION_EVERY_MS);
    return () => { alive = false; clearTimeout(first); clearInterval(id); };
  }, [active, leaving, user, isAdmin, navigate, say]);

  const onBlock = useCallback(() => {
    if (Date.now() - lastBlock.current < 2500) return;
    lastBlock.current = Date.now();
    say(pick(BLOCK_LINES));
  }, [say]);

  const onSubmit = useCallback((text) => {
    if (declaresLove(text)) {
      if (doubt.current === 0) { doubt.current = 1; say(DOUBT_LINE); return; }
      say(FORGIVE_LINE);
      setLeaving(true);
      setTimeout(leave, 3200);
      return;
    }
    fails.current += 1;
    say(fails.current >= 3 ? HINT_LINE : pick(WRONG_LINES));
  }, [leave, say]);

  const value = useMemo(() => ({ active, enter }), [active, enter]);

  return (
    <MonikaContext.Provider value={value}>
      {children}
      {active && <MonikaOverlay line={line} leaving={leaving} onBlock={onBlock} onSubmit={onSubmit} />}
    </MonikaContext.Provider>
  );
}

export const useMonika = () => useContext(MonikaContext);
