import { useMemo, useState } from 'react';
import { ChevronDown, Pencil, Plus, Search, Trash2, Zap } from 'lucide-react';
import api, { errorMessage } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { useLoad } from '../hooks/useLoad';
import { estimateKwh, startOfMonth, startOfToday } from '../utils/energy';
import { Button, Empty, ErrorBox, Loading, Panel, Toggle } from '../components/UI';
import PageHead from '../components/PageHead';
import Portrait from '../components/Portrait';
import DeviceModal from '../components/modals/DeviceModal';
import { TIPO_ICON, TIPO_LABEL } from '../components/constants';

const MAIN_ROOMS = ['Sala', 'Quarto', 'Cozinha'];
const same = (a, b) => (a || '').trim().toLowerCase() === b.toLowerCase();

export default function DevicesPage() {
  const { isAdmin } = useAuth();
  const toast = useToast();
  const devices = useLoad('/dispositivos');
  const history = useLoad('/dispositivos/historico?limite=200', { enabled: isAdmin });

  const [search, setSearch] = useState('');
  const [room, setRoom] = useState('TODAS');
  const [menuOpen, setMenuOpen] = useState(false);
  const [modal, setModal] = useState(null); // { item? }
  const [busyId, setBusyId] = useState(null);
  const [dismissed, setDismissed] = useState(false);

  const list = devices.data || [];
  const online = list.filter((d) => d.status).length;
  const otherRooms = useMemo(
    () => [...new Set(list.map((d) => (d.comodo || '').trim()).filter((c) => c && !MAIN_ROOMS.some((m) => same(c, m))))],
    [list],
  );

  const visible = list.filter((d) => {
    const matchesRoom = room === 'TODAS' || same(d.comodo, room);
    const matchesText = `${d.nome} ${d.comodo || ''}`.toLowerCase().includes(search.trim().toLowerCase());
    return matchesRoom && matchesText;
  });

  const reload = () => devices.refresh({ silent: true });

  const toggle = async (d) => {
    setBusyId(d.id);
    try {
      await api.patch(`/dispositivos/${d.id}/status`, { ligado: !d.status });
      reload();
      history.refresh({ silent: true });
    } catch (e) {
      toast(errorMessage(e), 'error');
    } finally {
      setBusyId(null);
    }
  };

  const remove = async (d) => {
    if (!window.confirm(`Remover o dispositivo "${d.nome}"?`)) return;
    try {
      await api.delete(`/dispositivos/${d.id}`);
      toast('Dispositivo removido.');
      reload();
    } catch (e) {
      toast(errorMessage(e), 'error');
    }
  };

  // Consumo estimado a partir do histórico (só o morador tem acesso ao histórico).
  const energy = useMemo(() => {
    if (!isAdmin || !history.data) return null;
    return {
      today: estimateKwh(list, history.data, startOfToday()),
      month: estimateKwh(list, history.data, startOfMonth()),
    };
  }, [isAdmin, list, history.data]);

  // Assistente: aponta a luz que está ligada há mais tempo.
  const forgotten = useMemo(() => {
    const lights = list.filter((d) => d.tipo === 'ILUMINACAO' && d.status);
    if (!lights.length) return null;
    const since = (d) => {
      const lastOn = (history.data || []).find((h) => h.dispositivoId === d.id && h.ligado); // histórico vem do mais recente
      return lastOn ? new Date(lastOn.criadoEm).getTime() : Date.now();
    };
    const oldest = lights.sort((a, b) => since(a) - since(b))[0];
    const hours = Math.floor((Date.now() - since(oldest)) / 3600000);
    return { device: oldest, hours };
  }, [list, history.data]);

  return (
    <div className="devices">
      <PageHead title="Meus dispositivos" subtitle="Gerencie tudo conectado na sua casa" portrait>
        {isAdmin && <Button className="devices__new" onClick={() => setModal({})}><Plus size={15} /> Novo Dispositivo</Button>}
      </PageHead>

      <label className="searchbar">
        <Search size={16} />
        <input type="search" placeholder="Buscar Dispositivos" aria-label="Buscar dispositivos" value={search} onChange={(e) => setSearch(e.target.value)} />
      </label>

      <div className="count-row">
        <div className="count-tile"><Zap size={18} /><div><strong>{list.length}</strong><small>Dispositivos no total</small></div></div>
        <div className="count-tile"><i className="dot dot--ok dot--lg" /><div><strong>{online}</strong><small>Online</small></div></div>
        <div className="count-tile"><i className="dot dot--bad dot--lg" /><div><strong>{list.length - online}</strong><small>Offline</small></div></div>
      </div>

      <div className="chips" role="tablist" aria-label="Filtrar por cômodo">
        <button type="button" className={`chip ${room === 'TODAS' ? 'is-active' : ''}`} onClick={() => { setRoom('TODAS'); setMenuOpen(false); }}>Todas</button>
        {MAIN_ROOMS.map((r) => (
          <button key={r} type="button" className={`chip ${room === r ? 'is-active' : ''}`} onClick={() => { setRoom(r); setMenuOpen(false); }}>{r}</button>
        ))}
        <div className="chip-menu">
          <button type="button" className={`chip ${otherRooms.includes(room) ? 'is-active' : ''}`} aria-haspopup="listbox" aria-expanded={menuOpen} onClick={() => setMenuOpen(!menuOpen)}>
            Outro <ChevronDown size={14} />
          </button>
          {menuOpen && (
            <ul className="chip-menu__list" role="listbox">
              {otherRooms.length === 0 && <li className="chip-menu__empty">Nenhum outro cômodo</li>}
              {otherRooms.map((c) => (
                <li key={c}>
                  <button type="button" role="option" aria-selected="false" onClick={() => { setRoom(c); setMenuOpen(false); }}>{c}</button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {devices.loading ? (
        <Loading />
      ) : devices.error ? (
        <ErrorBox>{devices.error}</ErrorBox>
      ) : visible.length === 0 ? (
        <Panel><Empty title="Nenhum dispositivo encontrado" text={isAdmin ? 'Ajuste a busca ou cadastre um novo dispositivo.' : 'O morador ainda não liberou dispositivos para você.'} /></Panel>
      ) : (
        <div className="device-cards">
          {visible.map((d) => {
            const Icon = TIPO_ICON[d.tipo] || Zap;
            return (
              <article key={d.id} className={`device-card ${d.status ? 'is-on' : ''}`}>
                <div className="device-card__top">
                  <Icon size={24} />
                  <span className="device-card__state">{d.status ? 'ON' : 'OFF'}</span>
                  <Toggle checked={d.status} label={`${d.status ? 'Desligar' : 'Ligar'} ${d.nome}`} disabled={busyId === d.id} onChange={() => toggle(d)} />
                </div>
                <strong>{d.nome}</strong>
                <small>{d.comodo || TIPO_LABEL[d.tipo]}</small>
                {isAdmin && (
                  <div className="device-card__actions">
                    <button type="button" className="icon-btn" onClick={() => setModal({ item: d })} aria-label={`Editar ${d.nome}`}><Pencil size={14} /></button>
                    <button type="button" className="icon-btn icon-btn--danger" onClick={() => remove(d)} aria-label={`Remover ${d.nome}`}><Trash2 size={14} /></button>
                  </div>
                )}
              </article>
            );
          })}
        </div>
      )}

      <div className="devices__bottom">
        <Panel title="Consumo de energia" icon={Zap} outlined className="energy">
          <div className="energy__tiles">
            <div className="energy__tile energy__tile--today">
              <strong>{energy ? `${energy.today.toFixed(2)} kWh` : '—'}</strong>
              <small>Hoje</small>
            </div>
            <div className="energy__tile energy__tile--month">
              <strong>{energy ? `${energy.month.toFixed(energy.month >= 100 ? 0 : 1)} kWh` : '—'}</strong>
              <small>Mensal</small>
            </div>
          </div>
          <p className="energy__note">{energy ? 'Estimativa baseada no histórico de acionamentos.' : 'Disponível para o morador.'}</p>
        </Panel>

        <Panel outlined className="panel--assistant">
          <h2 className="assistant-title">Assistente <em>Mônica</em></h2>
          <div className="assistant">
            <Portrait file="monica-feliz.png" variant="square" />
            {forgotten && !dismissed && isAdmin ? (
              <div className="assistant__body">
                <p>
                  {forgotten.hours >= 1
                    ? <>A {forgotten.device.nome.toLowerCase()} está ligada há {forgotten.hours} {forgotten.hours === 1 ? 'hora' : 'horas'}, quer desligar?</>
                    : <>A {forgotten.device.nome.toLowerCase()} está ligada, quer desligar?</>}
                </p>
                <div className="assistant__actions">
                  <Button variant="success" size="sm" onClick={async () => { await toggle(forgotten.device); setDismissed(true); }}>Desligar</Button>
                  <Button variant="danger" size="sm" onClick={() => setDismissed(true)}>Agora não</Button>
                </div>
              </div>
            ) : (
              <div className="assistant__body"><p>Nenhuma luz esquecida acesa. Tudo sob controle!</p></div>
            )}
          </div>
        </Panel>
      </div>

      {modal && (
        <DeviceModal
          item={modal.item}
          onClose={() => setModal(null)}
          onSaved={(edited) => { setModal(null); reload(); toast(edited ? 'Dispositivo atualizado.' : 'Dispositivo cadastrado.'); }}
        />
      )}
    </div>
  );
}
