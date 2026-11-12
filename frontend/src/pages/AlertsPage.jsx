import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { Bell, ChevronRight, Clock3, Cog, Lightbulb, ListFilter, Monitor, ShieldCheck, Wifi } from 'lucide-react';
import { useLoad } from '../hooks/useLoad';
import { timeAgo } from '../utils/format';
import { Empty, ErrorBox, Loading, Panel } from '../components/UI';
import PageHead from '../components/PageHead';

const FILTERS = [
  { id: 'TODOS', label: 'Todos', icon: ListFilter },
  { id: 'SISTEMA', label: 'Sistema', icon: Cog },
  { id: 'DISPOSITIVO', label: 'Dispositivo', icon: Monitor },
  { id: 'SEGURANCA', label: 'Segurança', icon: ShieldCheck },
  { id: 'ROTINAS', label: 'Rotinas', icon: Clock3 },
];

const CATEGORY_ICON = { SISTEMA: Wifi, DISPOSITIVO: Lightbulb, SEGURANCA: ShieldCheck, ROTINAS: Clock3 };

/** O back-end só guarda título, mensagem e nível; a categoria do Figma é deduzida do texto. */
export function categoryOf(alert) {
  const text = `${alert.titulo} ${alert.mensagem}`.toLowerCase();
  if (/rotina/.test(text)) return 'ROTINAS';
  if (/segur|movimento|alarme|invas/.test(text)) return 'SEGURANCA';
  if (/dispositivo|luz|l[aâ]mpada|ligad|desligad|tomada|ar-condicionado/.test(text)) return 'DISPOSITIVO';
  return 'SISTEMA';
}

const DOT_BY_LEVEL = { INFO: 'ok', ALERTA: 'warn', CRITICO: 'bad' };

export default function AlertsPage() {
  const alerts = useLoad('/alertas?limite=100');
  const history = useLoad('/dispositivos/historico?limite=4');
  const [filter, setFilter] = useState('TODOS');

  const list = alerts.data || [];
  const visible = useMemo(() => list.filter((a) => filter === 'TODOS' || categoryOf(a) === filter), [list, filter]);

  // "Resolvidos" = avisos informativos; "Pendentes" = alertas e críticos.
  const pending = list.filter((a) => a.nivel !== 'INFO').length;
  const resolved = list.length - pending;

  return (
    <div className="alerts">
      <PageHead title="Alertas" subtitle="Saiba tudo que acontece na sua casa" portrait />

      <div className="chips" role="tablist" aria-label="Filtrar alertas">
        {FILTERS.map(({ id, label, icon: Icon }) => (
          <button key={id} type="button" role="tab" aria-selected={filter === id} className={`chip ${filter === id ? 'is-active' : ''}`} onClick={() => setFilter(id)}>
            <Icon size={14} />{label}
          </button>
        ))}
      </div>

      <div className="alerts__grid">
        <Panel className="alerts__list">
          {alerts.loading ? (
            <Loading />
          ) : alerts.error ? (
            <ErrorBox>{alerts.error}</ErrorBox>
          ) : visible.length === 0 ? (
            <Empty title="Nenhum alerta por aqui" text="Quando algo acontecer na sua casa, você verá aqui." />
          ) : (
            <ul>
              {visible.map((a) => {
                const Icon = CATEGORY_ICON[categoryOf(a)] || Bell;
                return (
                  <li key={a.id} className="alert-row">
                    <span className="alert-row__icon"><Icon size={20} /></span>
                    <div className="alert-row__text">
                      <strong>{a.titulo}</strong>
                      <small>{a.mensagem}</small>
                    </div>
                    <span className="alert-row__time">
                      {timeAgo(a.criadoEm)}
                      <i className={`dot dot--${DOT_BY_LEVEL[a.nivel] || 'ok'}`} title={a.nivel} />
                    </span>
                  </li>
                );
              })}
            </ul>
          )}
        </Panel>

        <div className="alerts__side">
          <Panel title="Últimas Atividades" action={<Link to="/historico" className="link-more">Ver Histórico <ChevronRight size={14} /></Link>} className="activities">
            {history.loading ? (
              <Loading />
            ) : !history.data?.length ? (
              <Empty title="Sem atividades recentes" />
            ) : (
              <ul>
                {history.data.slice(0, 3).map((h) => (
                  <li key={h.id}>
                    <span>{h.dispositivoNome} {h.ligado ? 'ligado' : 'desligado'}</span>
                    <small>{timeAgo(h.criadoEm)}</small>
                  </li>
                ))}
              </ul>
            )}
          </Panel>

          <Panel title="Resumo Alertas" outlined className="summary">
            <dl className="summary__grid">
              <div><dd>{list.length}</dd><dt>Total</dt></div>
              <div><dd>{resolved}</dd><dt>Resolvidos</dt></div>
              <div><dd>{pending}</dd><dt>Pendentes</dt></div>
            </dl>
          </Panel>
        </div>
      </div>
    </div>
  );
}
