import { useState } from 'react';
import api, { errorMessage } from '../../services/api';
import { Button, ErrorBox, Field, Modal, SelectField } from '../UI';
import { TIPO_LABEL } from '../constants';

export default function DeviceModal({ item = {}, onClose, onSaved }) {
  const [form, setForm] = useState({
    nome: item.nome || '',
    tipo: item.tipo || 'ILUMINACAO',
    comodo: item.comodo || '',
    acessoConvidado: item.acessoConvidado ?? false,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError('');
    try {
      if (item.id) await api.put(`/dispositivos/${item.id}`, form);
      else await api.post('/dispositivos', form);
      onSaved?.(!!item.id);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal title={item.id ? 'Editar dispositivo' : 'Novo dispositivo'} onClose={onClose}>
      <form onSubmit={submit} className="form-stack">
        <Field label="Nome do dispositivo" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} placeholder="Ex.: Luz da sala" required maxLength={100} />
        <div className="form-row">
          <SelectField label="Tipo" value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}>
            {Object.entries(TIPO_LABEL).map(([v, l]) => <option key={v} value={v}>{l}</option>)}
          </SelectField>
          <Field label="Cômodo" value={form.comodo} onChange={(e) => setForm({ ...form, comodo: e.target.value })} placeholder="Ex.: Sala" maxLength={50} />
        </div>
        <label className="check-field">
          <input type="checkbox" checked={form.acessoConvidado} onChange={(e) => setForm({ ...form, acessoConvidado: e.target.checked })} />
          Permitir que convidados controlem este dispositivo
        </label>
        <ErrorBox>{error}</ErrorBox>
        <div className="modal__actions">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" loading={busy}>Salvar dispositivo</Button>
        </div>
      </form>
    </Modal>
  );
}
