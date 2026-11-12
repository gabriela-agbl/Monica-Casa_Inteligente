import { Link } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { useLoad } from '../hooks/useLoad';
import { Badge, Empty, ErrorBox, Loading, Panel } from '../components/UI';
import PageHead from '../components/PageHead';

const ORIGEM = { MANUAL: 'Manual', ROTINA: 'Rotina', AUTOMATICO: 'Automático' };

/** Histórico completo de acionamentos (aberto por "Ver Histórico" em Alertas). */
export default function HistoryPage() {
  const history = useLoad('/dispositivos/historico?limite=100');

  return (
    <div className="history">
      <PageHead title="Histórico" subtitle="Quem ligou ou desligou cada dispositivo">
        <Link to="/alertas" className="link-more link-more--back"><ArrowLeft size={14} /> Voltar aos alertas</Link>
      </PageHead>

      <Panel>
        {history.loading ? (
          <Loading />
        ) : history.error ? (
          <ErrorBox>{history.error}</ErrorBox>
        ) : !history.data?.length ? (
          <Empty title="Histórico vazio" text="Os acionamentos dos dispositivos serão registrados aqui." />
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr><th>Dispositivo</th><th>Ação</th><th>Origem</th><th>Responsável</th><th>Data e hora</th></tr>
              </thead>
              <tbody>
                {history.data.map((h) => (
                  <tr key={h.id}>
                    <td><strong>{h.dispositivoNome}</strong></td>
                    <td><Badge tone={h.ligado ? 'green' : 'red'}>{h.ligado ? 'Ligado' : 'Desligado'}</Badge></td>
                    <td>{ORIGEM[h.origem] || h.origem}</td>
                    <td>{h.acionadoPor || '—'}</td>
                    <td>{h.criadoEm ? new Date(h.criadoEm).toLocaleString('pt-BR') : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Panel>
    </div>
  );
}
