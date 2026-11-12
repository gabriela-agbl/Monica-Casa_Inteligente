import { useMemo, useState } from 'react';
import { Pencil, Play, Plus, Trash2 } from 'lucide-react';
import api, { errorMessage } from '../services/api';
import { useToast } from '../context/ToastContext';
import { useLoad } from '../hooks/useLoad';
import { describeDays, hhmm } from '../utils/format';
import { Button, Empty, ErrorBox, Loading, Panel, Toggle } from '../components/UI';
import PageHead from '../components/PageHead';
import Portrait from '../components/Portrait';
import RoutineModal from '../components/modals/RoutineModal';
import { routineIcon } from '../components/constants';

export default function RoutinesPage() {
  const toast = useToast();
  const routines = useLoad('/rotinas');
  const devices = useLoad('/dispositivos');
  const [modal, setModal] = useState(null); // { item?, preset? }

  const list = routines.data || [];
  const summary = useMemo(() => {
    const active = list.filter((r) => r.ativo).length;
    return { total: list.length, active, inactive: list.length - active };
  }, [list]);

  const act = async (fn, okMessage) => {
    try {
      await fn();
      if (okMessage) toast(okMessage);
      routines.refresh({ silent: true });
    } catch (e) {
      toast(errorMessage(e), 'error');
    }
  };

  const toggle = (r) => act(() => api.patch(`/rotinas/${r.id}/ativo`, { ativo: !r.ativo }), r.ativo ? 'Rotina desativada.' : 'Rotina ativada.');
  const execute = (r) => window.confirm(`Executar "${r.nome}" agora?`) && act(() => api.post(`/rotinas/${r.id}/executar`), 'Rotina executada.');
  const remove = (r) => window.confirm(`Excluir a rotina "${r.nome}"?`) && act(() => api.delete(`/rotinas/${r.id}`), 'Rotina excluída.');

  // Sugestão fixa do protótipo: preparar o ambiente para o filme das 22h.
  const tv = (devices.data || []).find((d) => /tv|televis/i.test(d.nome));
  const suggested = {
    nome: 'Hora do filme',
    horario: '22:00',
    acao: 'LIGAR',
    dispositivoId: (tv || (devices.data || [])[0])?.id,
  };

  return (
    <div className="routines">
      <PageHead title="Minhas rotinas" subtitle="Automatize sua casa inteligente">
        <Button className="routines__create" onClick={() => setModal({})}><Plus size={15} /> Criar rotina</Button>
      </PageHead>

      <div className="routines__grid">
        <Panel className="routines__list">
          {routines.loading ? (
            <Loading />
          ) : routines.error ? (
            <ErrorBox>{routines.error}</ErrorBox>
          ) : list.length === 0 ? (
            <Empty title="Nenhuma rotina criada" text="Crie uma automação para sua casa agir sozinha nos dias e horários que você escolher." />
          ) : (
            <ul>
              {list.map((r) => {
                const Icon = routineIcon(r.nome);
                return (
                  <li key={r.id} className="routine-row">
                    <Icon size={24} className="routine-row__icon" />
                    <div className="routine-row__info">
                      <strong>{r.nome}</strong>
                      <small>{describeDays(r.diasSemana)} - {hhmm(r.horario)}</small>
                    </div>
                    <div className="row-actions">
                      <button type="button" className="icon-btn" onClick={() => execute(r)} aria-label={`Executar ${r.nome} agora`} title="Executar agora"><Play size={15} /></button>
                      <button type="button" className="icon-btn" onClick={() => setModal({ item: r })} aria-label={`Editar ${r.nome}`} title="Editar"><Pencil size={15} /></button>
                      <button type="button" className="icon-btn icon-btn--danger" onClick={() => remove(r)} aria-label={`Excluir ${r.nome}`} title="Excluir"><Trash2 size={15} /></button>
                    </div>
                    <Toggle checked={r.ativo} label={`${r.ativo ? 'Desativar' : 'Ativar'} ${r.nome}`} onChange={() => toggle(r)} />
                  </li>
                );
              })}
            </ul>
          )}
        </Panel>

        <div className="routines__side">
          <Panel className="recommend">
            <h2 className="recommend__title">Recomendação da <em>Mônica</em></h2>
            <div className="recommend__body">
              <Portrait file="monica-feliz.png" variant="square" />
              <p>Percebi que você costuma assistir a um filme por volta das 22h, não é? Posso preparar o ambiente para você: diminuir as luzes e já deixar a TV pronta sempre que der essa hora!</p>
            </div>
            <Button variant="success" className="recommend__cta" onClick={() => setModal({ preset: suggested })}>Criar rotina sugerida</Button>
          </Panel>

          <Panel title="Resumo rotinas" className="summary">
            <dl className="summary__grid">
              <div><dd>{summary.total}</dd><dt>total</dt></div>
              <div><dd>{summary.active}</dd><dt>Ativas</dt></div>
              <div><dd>{summary.inactive}</dd><dt>Inativas</dt></div>
            </dl>
          </Panel>
        </div>
      </div>

      {modal && (
        <RoutineModal
          item={modal.item}
          preset={modal.preset}
          devices={devices.data || []}
          onClose={() => setModal(null)}
          onSaved={(edited) => { setModal(null); routines.refresh({ silent: true }); toast(edited ? 'Rotina atualizada.' : 'Rotina criada com sucesso.'); }}
        />
      )}
    </div>
  );
}
