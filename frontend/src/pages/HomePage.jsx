import { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { CalendarPlus, ChevronRight, Lightbulb, SquarePlus, Settings, Star, Thermometer, Users, Wifi, Zap } from 'lucide-react';
import api, { errorMessage } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { useMonicaConfirm } from '../context/ConfirmContext';
import { useMonika } from '../context/MonikaContext';
import { useLoad } from '../hooks/useLoad';
import { describeDays, firstName, hhmm } from '../utils/format';
import { Badge, Button, ClockChip, Empty, Panel, Toggle } from '../components/UI';
import Portrait from '../components/Portrait';
import { TIPO_ICON, routineIcon } from '../components/constants';
import DeviceModal from '../components/modals/DeviceModal';
import RoutineModal from '../components/modals/RoutineModal';
import ManageHouseModal from '../components/modals/ManageHouseModal';

// O back-end ainda não tem sensor de temperatura; valor fixo do protótipo.
const AMBIENT_TEMP = 24;

export default function HomePage() {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const confirm = useMonicaConfirm();
  const { enter: enterMonika } = useMonika();

  const dashboard = useLoad('/dashboard');
  const devices = useLoad('/dispositivos');
  const routines = useLoad('/rotinas', { enabled: isAdmin });
  const alerts = useLoad('/alertas?limite=50', { enabled: isAdmin });

  const [modal, setModal] = useState(null); // 'routine' | 'device' | 'people'
  const [busyId, setBusyId] = useState(null);
  const [dismissed, setDismissed] = useState(false);
  const [flipped, setFlipped] = useState(false); // a Mônica troca os botões de lado quando você hesita

  const deviceList = devices.data || [];
  const lights = deviceList.filter((d) => d.tipo === 'ILUMINACAO');
  const lightsOn = lights.filter((d) => d.status);
  const pending = (alerts.data || []).filter((a) => a.nivel !== 'INFO').length;
  const stats = dashboard.data || {};

  const reloadAll = () => { devices.refresh({ silent: true }); dashboard.refresh({ silent: true }); };

  const toggleDevice = async (device) => {
    setBusyId(device.id);
    try {
      await api.patch(`/dispositivos/${device.id}/status`, { ligado: !device.status });
      reloadAll();
    } catch (e) {
      toast(errorMessage(e), 'error');
    } finally {
      setBusyId(null);
    }
  };

  // "Favoritas": as duas primeiras rotinas cadastradas.
  const favorites = useMemo(() => (routines.data || []).slice(0, 2), [routines.data]);

  // Sugestão da assistente: à noite propõe o modo noturno; durante o dia, apagar luzes esquecidas.
  const hour = new Date().getHours();
  const isLate = hour >= 18 || hour < 5;
  const nightRoutine = (routines.data || []).find((r) => /noturn/i.test(r.nome));
  const suggestion = isLate
    ? { text: ['Está ficando tarde.', 'Quer ativar o modo noturno?'], action: 'Ativar' }
    : { text: [`Há ${lightsOn.length} luz${lightsOn.length === 1 ? '' : 'es'} acesa${lightsOn.length === 1 ? '' : 's'}.`, 'Quer apagar tudo?'], action: 'Apagar' };
  const showSuggestion = !dismissed && (isLate || lightsOn.length > 0);

  const acceptSuggestion = async () => {
    try {
      if (isLate) {
        if (nightRoutine) {
          await api.post(`/rotinas/${nightRoutine.id}/executar`);
          toast('Modo noturno ativado.');
        } else if (isAdmin) {
          setModal('routine');
          return;
        } else {
          toast('Peça ao morador para criar a rotina do modo noturno.', 'error');
        }
      } else {
        await Promise.all(lightsOn.map((d) => api.patch(`/dispositivos/${d.id}/status`, { ligado: false })));
        toast('Luzes apagadas.');
      }
      reloadAll();
      setDismissed(true);
    } catch (e) {
      toast(errorMessage(e), 'error');
    }
  };

  const dismissSuggestion = async () => {
    const ok = await confirm({
      steps: 2,
      text: 'Você vai mesmo ignorar a minha sugestão? Eu só estou tentando cuidar da casa... e de você.',
      text2: 'Tem MESMO certeza? Eu pensei que a gente se entendia. 💚',
      confirmLabel: 'Sim, ignorar',
      cancelLabel: 'Tá bom, você manda',
      confirmVariant: 'ghost',
    });
    if (ok) { setDismissed(true); enterMonika(); } // você insistiu: ela se zanga e assume o controle
    else { setFlipped((f) => !f); toast('Eu sabia que você ia ficar. 💚'); }
  };

  const quick = [
    { label: 'Criar rotina', icon: CalendarPlus, admin: true, run: () => setModal('routine') },
    { label: 'Adicionar dispositivo', icon: SquarePlus, admin: true, run: () => setModal('device') },
    { label: 'Gerenciar usuarios', icon: Users, admin: true, run: () => setModal('people') },
    { label: 'Configurações', icon: Settings, run: () => navigate('/configuracoes') },
  ];

  return (
    <div className="home">
      <header className="home__top">
        <div className="home__greeting">
          <h1>Olá, {firstName(user?.nome) || 'morador'}!</h1>
          <p>{pending > 0 ? `${pending} alerta${pending > 1 ? 's' : ''} pendente${pending > 1 ? 's' : ''}` : 'Tudo em ordem'}</p>
        </div>

        <div className="stat-row">
          <Stat icon={Thermometer} value={`${AMBIENT_TEMP}°C`} label="Temperatura" badge={<Badge>Normal</Badge>} />
          <Stat icon={Zap} value={isAdmin ? stats.rotinasAtivas ?? 0 : '—'} label="Rotinas ativas" badge={<Badge tone={stats.rotinasAtivas > 0 ? 'green' : 'muted'}>Ativas</Badge>} />
          <Stat icon={Lightbulb} value={`${lightsOn.length}/${lights.length}`} label="Luzes Ligadas" badge={<Badge tone={lightsOn.length ? 'green' : 'muted'}>Ativo</Badge>} />
          <Stat icon={Wifi} value={stats.totalDispositivos ?? deviceList.length} label="Dispositivos" badge={<Badge>Conectados</Badge>} />
        </div>

        <div className="home__aside">
          <Portrait file="monica-hud.png" variant="hud" />
          <ClockChip />
        </div>
      </header>

      <div className="home__grid">
        <div className="home__col">
          <Panel
            outlined
            title="Rotinas favoritas"
            icon={Star}
            action={isAdmin && <Link to="/rotinas" className="link-more">Ver todas <ChevronRight size={14} /></Link>}
          >
            {!isAdmin ? (
              <Empty title="Rotinas disponíveis para o morador" />
            ) : routines.loading ? (
              <p className="muted-line">Carregando...</p>
            ) : favorites.length === 0 ? (
              <Empty title="Nenhuma rotina criada" text="Use “Criar rotina” para automatizar sua casa." />
            ) : (
              <ul className="fav-list">
                {favorites.map((r) => {
                  const Icon = routineIcon(r.nome);
                  return (
                    <li key={r.id}>
                      <Icon size={22} className="fav-list__icon" />
                      <div>
                        <strong>{r.nome}</strong>
                        <small>{r.dispositivoNome} · {describeDays(r.diasSemana)} · {hhmm(r.horario)}</small>
                      </div>
                      <Badge tone={r.ativo ? 'green' : 'red'}>{r.ativo ? 'Programado' : 'Desativado'}</Badge>
                    </li>
                  );
                })}
              </ul>
            )}
          </Panel>

          <Panel outlined className="panel--devices">
            {devices.loading ? (
              <p className="muted-line">Carregando...</p>
            ) : deviceList.length === 0 ? (
              <Empty title="Nenhum dispositivo cadastrado" text={isAdmin ? 'Use “Adicionar dispositivo” para começar.' : undefined} />
            ) : (
              <div className="device-tiles">
                {deviceList.slice(0, 4).map((d) => {
                  const Icon = TIPO_ICON[d.tipo] || Wifi;
                  return (
                    <div key={d.id} className="device-tile">
                      <div className="device-tile__top">
                        <Icon size={22} />
                        <Toggle checked={d.status} label={`Alternar ${d.nome}`} disabled={busyId === d.id} onChange={() => toggleDevice(d)} />
                      </div>
                      <strong>{d.nome}</strong>
                      <small>{d.status ? 'Conectado' : 'Desligado'}</small>
                    </div>
                  );
                })}
              </div>
            )}
          </Panel>
        </div>

        <div className="home__col">
          <Panel outlined title="Ações rápidas" icon={Zap}>
            <div className="quick-grid">
              {quick.map(({ label, icon: Icon, admin, run }) => (
                <button key={label} type="button" className="quick-btn" onClick={run} disabled={admin && !isAdmin} title={admin && !isAdmin ? 'Apenas o morador pode fazer isso' : undefined}>
                  <Icon size={19} />
                  <span>{label}</span>
                </button>
              ))}
            </div>
          </Panel>

          <Panel outlined className="panel--assistant">
            <h2 className="assistant-title">Assistente <em>Mônica</em></h2>
            <div className="assistant">
              <Portrait file="monica-assistente.png" variant="square" />
              {showSuggestion ? (
                <div className="assistant__body">
                  <p>{suggestion.text[0]}<br />{suggestion.text[1]}</p>
                  <div className={`assistant__actions ${flipped ? 'assistant__actions--flip' : ''}`}>
                    <Button variant="success" size="sm" onClick={acceptSuggestion}>{suggestion.action}</Button>
                    <Button variant="danger" size="sm" onClick={dismissSuggestion}>Agora não</Button>
                  </div>
                </div>
              ) : (
                <div className="assistant__body">
                  <p>Tudo certo por aqui.<br />Estou de olho na casa!</p>
                </div>
              )}
            </div>
          </Panel>
        </div>
      </div>

      {modal === 'routine' && (
        <RoutineModal
          devices={deviceList}
          preset={isLate && !nightRoutine ? { nome: 'Modo noturno', horario: '22:00', acao: 'DESLIGAR', dispositivoId: lights[0]?.id } : {}}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); routines.refresh({ silent: true }); dashboard.refresh({ silent: true }); toast('Rotina criada com sucesso.'); }}
        />
      )}
      {modal === 'device' && (
        <DeviceModal onClose={() => setModal(null)} onSaved={() => { setModal(null); reloadAll(); toast('Dispositivo salvo com sucesso.'); }} />
      )}
      {modal === 'people' && <ManageHouseModal initialTab="moradores" onClose={() => setModal(null)} />}
    </div>
  );
}

function Stat({ icon: Icon, value, label, badge }) {
  return (
    <div className="stat-tile">
      <div className="stat-tile__main">
        <Icon size={26} className="stat-tile__icon" />
        <div>
          <strong>{value}</strong>
          <small>{label}</small>
        </div>
      </div>
      {badge}
    </div>
  );
}
