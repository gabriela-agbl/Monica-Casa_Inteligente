import { useEffect, useState } from 'react';
import { Pencil, Plus, Trash2 } from 'lucide-react';
import api, { errorMessage } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { useLoad } from '../../hooks/useLoad';
import { initials } from '../../utils/format';
import { Button, Empty, ErrorBox, Field, Loading, Modal, SelectField } from '../UI';

/** "Gerenciar casa": nome da casa + moradores/convidados (somente ADMIN). */
export default function ManageHouseModal({ onClose, initialTab = 'casa' }) {
  const [tab, setTab] = useState(initialTab);
  return (
    <Modal title="Gerenciar casa" onClose={onClose} wide>
      <div className="tabs" role="tablist">
        <button role="tab" aria-selected={tab === 'casa'} className={tab === 'casa' ? 'is-active' : ''} onClick={() => setTab('casa')}>Minha casa</button>
        <button role="tab" aria-selected={tab === 'moradores'} className={tab === 'moradores' ? 'is-active' : ''} onClick={() => setTab('moradores')}>Moradores e acessos</button>
      </div>
      {tab === 'casa' ? <HouseTab /> : <PeopleTab />}
    </Modal>
  );
}

function HouseTab() {
  const toast = useToast();
  const { data, loading, error } = useLoad('/admin/configuracoes/NOME_CASA');
  const [name, setName] = useState('');
  const [busy, setBusy] = useState(false);

  useEffect(() => { if (data) setName(data.valor || ''); }, [data]);

  const save = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await api.put('/admin/configuracoes/NOME_CASA', { valor: name, descricao: 'Nome exibido no painel e nas mensagens' });
      toast('Nome da casa atualizado.');
    } catch (err) {
      toast(errorMessage(err), 'error');
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <Loading />;
  return (
    <form onSubmit={save} className="form-stack">
      <ErrorBox>{error}</ErrorBox>
      <Field label="Nome da casa" value={name} onChange={(e) => setName(e.target.value)} required maxLength={100} />
      <p className="hint">Esse nome aparece no painel e nas mensagens enviadas pelo WhatsApp.</p>
      <div className="modal__actions"><Button type="submit" loading={busy}>Salvar nome</Button></div>
    </form>
  );
}

function PeopleTab() {
  const toast = useToast();
  const { data, loading, error, refresh } = useLoad('/admin/usuarios');
  const [editing, setEditing] = useState(null);

  const remove = async (u) => {
    if (!window.confirm(`Remover ${u.nome}?`)) return;
    try {
      await api.delete(`/admin/usuarios/${u.id}`);
      toast('Usuário removido.');
      refresh({ silent: true });
    } catch (e) {
      toast(errorMessage(e), 'error');
    }
  };

  if (editing) {
    return <UserForm item={editing} onCancel={() => setEditing(null)} onSaved={() => { setEditing(null); refresh({ silent: true }); toast('Usuário salvo.'); }} />;
  }

  return (
    <>
      <div className="people-head">
        <p className="hint">O morador (ADMIN) controla tudo; o convidado só aciona o que for liberado.</p>
        <Button size="sm" onClick={() => setEditing({})}><Plus size={15} /> Novo usuário</Button>
      </div>
      {loading ? <Loading /> : error ? <ErrorBox>{error}</ErrorBox> : !data?.length ? <Empty title="Nenhum usuário" /> : (
        <ul className="people-list">
          {data.map((u) => (
            <li key={u.id}>
              <span className="avatar">{initials(u.nome)}</span>
              <div>
                <strong>{u.nome}</strong>
                <small>{u.email} · {u.telefone}</small>
              </div>
              <span className={`role-pill ${u.perfil === 'ADMIN' ? 'is-admin' : ''}`}>{u.perfil === 'ADMIN' ? 'Morador' : 'Convidado'}</span>
              <button className="icon-btn" onClick={() => setEditing(u)} aria-label={`Editar ${u.nome}`}><Pencil size={16} /></button>
              <button className="icon-btn icon-btn--danger" onClick={() => remove(u)} aria-label={`Remover ${u.nome}`}><Trash2 size={16} /></button>
            </li>
          ))}
        </ul>
      )}
    </>
  );
}

function UserForm({ item, onCancel, onSaved }) {
  const [form, setForm] = useState({
    nome: item.nome || '', email: item.email || '', telefone: item.telefone || '', senha: '', perfil: item.perfil || 'COMUM',
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    setError('');
    try {
      if (item.id) {
        await api.put(`/admin/usuarios/${item.id}`, {
          nome: form.nome, telefone: form.telefone, perfil: form.perfil, ...(form.senha ? { senha: form.senha } : {}),
        });
      } else {
        await api.post('/admin/usuarios', form);
      }
      onSaved();
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} className="form-stack">
      <Field label="Nome" value={form.nome} onChange={set('nome')} required />
      {!item.id && <Field label="E-mail" type="email" value={form.email} onChange={set('email')} required />}
      <div className="form-row">
        <Field label="Telefone com DDD" value={form.telefone} onChange={set('telefone')} placeholder="11999999999" required />
        <SelectField label="Perfil" value={form.perfil} onChange={set('perfil')}>
          <option value="COMUM">Convidado</option>
          <option value="ADMIN">Morador (administrador)</option>
        </SelectField>
      </div>
      <Field label={item.id ? 'Nova senha (opcional)' : 'Senha (mínimo 6 caracteres)'} type="password" minLength={6} value={form.senha} onChange={set('senha')} required={!item.id} autoComplete="new-password" />
      <ErrorBox>{error}</ErrorBox>
      <div className="modal__actions">
        <Button variant="ghost" onClick={onCancel}>Voltar</Button>
        <Button type="submit" loading={busy}>Salvar</Button>
      </div>
    </form>
  );
}
