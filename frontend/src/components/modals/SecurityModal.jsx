import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { errorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { Button, Modal } from '../UI';

const STATE_LABEL = {
  connecting: 'Iniciando...',
  scan_qr_code: 'Aguardando QR Code',
  stopped: 'Parada',
  failed: 'Falhou',
};

/** "Segurança": integração com o WhatsApp (WAHA) e atalho para o histórico. */
export default function SecurityModal({ onClose }) {
  const toast = useToast();
  const [wa, setWa] = useState(null);
  const [busy, setBusy] = useState('');

  // Consulta o status a cada 3s; quando a sessão pede o QR (scan_qr_code), busca a imagem sozinho.
  useEffect(() => {
    let alive = true;
    const tick = async () => {
      try {
        const { data } = await api.get('/admin/whatsapp/status');
        if (!alive) return;
        if (data.state === 'scan_qr_code') {
          try {
            const qrRes = await api.get('/admin/whatsapp/qrcode');
            if (alive) setWa({ ...data, base64: qrRes.data.base64 });
            return;
          } catch { /* mantém só o status */ }
        }
        setWa(data);
      } catch {
        if (alive) setWa({ state: 'indisponível' });
      }
    };
    tick();
    const id = setInterval(tick, 3000);
    return () => { alive = false; clearInterval(id); };
  }, []);

  const run = async (key, fn, okMessage) => {
    setBusy(key);
    try {
      const result = await fn();
      if (result) setWa(result);
      if (okMessage) toast(okMessage);
    } catch (e) {
      toast(errorMessage(e), 'error');
    } finally {
      setBusy('');
    }
  };

  const state = wa?.state || wa?.instance?.state || 'verificando...';
  const connected = state === 'open';
  const qr = wa?.base64 || wa?.qrcode?.base64;

  return (
    <Modal title="Segurança" onClose={onClose}>
      <div className="security-row">
        <div>
          <strong>Conexão com o WhatsApp</strong>
          <small>Alertas de segurança e rotinas chegam direto no seu celular.</small>
        </div>
        <span className={`status-dot ${connected ? 'is-ok' : 'is-off'}`}>{connected ? 'Conectado' : STATE_LABEL[state] || state}</span>
      </div>
      <div className="security-actions">
        <Button variant="ghost" size="sm" loading={busy === 'create'} onClick={() => run('create', async () => (await api.post('/admin/whatsapp/instancia')).data, 'Sessão da WAHA garantida.')}>Iniciar sessão</Button>
        <Button variant="ghost" size="sm" loading={busy === 'qr'} onClick={() => run('qr', async () => (await api.get('/admin/whatsapp/qrcode')).data, 'QR Code atualizado.')}>Obter QR Code</Button>
        <Button size="sm" loading={busy === 'test'} onClick={() => run('test', async () => { await api.post('/admin/whatsapp/teste'); return null; }, 'Alerta de teste enviado.')}>Enviar teste</Button>
      </div>
      {qr && (
        <div className="qr-box">
          <img src={qr.startsWith('data:') ? qr : `data:image/png;base64,${qr}`} alt="QR Code para conectar o WhatsApp" />
          <small>Escaneie com o WhatsApp da casa.</small>
        </div>
      )}
      <div className="security-row">
        <div>
          <strong>Histórico de acionamentos</strong>
          <small>Veja quem ligou ou desligou cada dispositivo.</small>
        </div>
        <Link className="btn btn--ghost btn--sm" to="/historico" onClick={onClose}>Abrir histórico</Link>
      </div>
    </Modal>
  );
}
