import { useState } from 'react';
import api, { errorMessage } from '../../services/api';
import { hhmm, WEEK_DAYS } from '../../utils/format';
import { Button, ErrorBox, Field, Modal, SelectField } from '../UI';

/**
 * Criar/editar rotina. `preset` permite abrir já preenchido (ex.: "Criar rotina sugerida").
 */
export default function RoutineModal({ item = {}, preset = {}, devices = [], onClose, onSaved }) {
  const start = { ...preset, ...item };
  const [form, setForm] = useState({
    nome: start.nome || '',
    horario: hhmm(start.horario) || '07:00',
    diasSemana: start.diasSemana || 'SEG,TER,QUA,QUI,SEX,SAB,DOM',
    acao: start.acao || 'LIGAR',
    dispositivoId: start.dispositivoId || devices[0]?.id || '',
    ativo: start.ativo ?? true,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const chosen = form.diasSemana.split(',').filter(Boolean);

  const toggleDay = (day) => {
    const next = WEEK_DAYS.map(([v]) => v).filter((v) => (v === day ? !chosen.includes(v) : chosen.includes(v)));
    setForm({ ...form, diasSemana: next.join(',') });
  };

  const submit = async (e) => {
    e.preventDefault();
    if (!chosen.length) return setError('Escolha pelo menos um dia da semana.');
    setBusy(true);
    setError('');
    try {
      const payload = { ...form, dispositivoId: Number(form.dispositivoId) };
      if (item.id) await api.put(`/rotinas/${item.id}`, payload);
      else await api.post('/rotinas', payload);
      onSaved?.(!!item.id);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal title={item.id ? 'Editar rotina' : 'Criar rotina'} onClose={onClose}>
      <form onSubmit={submit} className="form-stack">
        <Field label="Nome da rotina" value={form.nome} onChange={(e) => setForm({ ...form, nome: e.target.value })} placeholder="Ex.: Modo noturno" required maxLength={100} />
        <div className="form-row">
          <Field label="Horário" type="time" value={form.horario} onChange={(e) => setForm({ ...form, horario: e.target.value })} required />
          <SelectField label="Ação" value={form.acao} onChange={(e) => setForm({ ...form, acao: e.target.value })}>
            <option value="LIGAR">Ligar</option>
            <option value="DESLIGAR">Desligar</option>
          </SelectField>
        </div>
        <SelectField label="Dispositivo" value={form.dispositivoId} onChange={(e) => setForm({ ...form, dispositivoId: e.target.value })} required>
          {devices.length === 0 && <option value="">Cadastre um dispositivo primeiro</option>}
          {devices.map((d) => <option key={d.id} value={d.id}>{d.nome}</option>)}
        </SelectField>
        <div className="field">
          <span className="field__label">Dias da semana</span>
          <div className="weekday-picker">
            {WEEK_DAYS.map(([v, l]) => (
              <button type="button" key={v} className={chosen.includes(v) ? 'is-selected' : ''} onClick={() => toggleDay(v)} aria-pressed={chosen.includes(v)}>{l}</button>
            ))}
          </div>
        </div>
        <ErrorBox>{error}</ErrorBox>
        <div className="modal__actions">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" loading={busy} disabled={!devices.length}>Salvar rotina</Button>
        </div>
      </form>
    </Modal>
  );
}
