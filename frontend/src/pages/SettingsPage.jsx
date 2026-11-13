import { useEffect, useState } from 'react';
import { ChevronDown, ChevronRight, House, Lock, UserRound } from 'lucide-react';
import api, { errorMessage } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { useLoad } from '../hooks/useLoad';
import { initials } from '../utils/format';
import { Button, Panel, Toggle } from '../components/UI';
import PageHead from '../components/PageHead';
import Portrait, { asset } from '../components/Portrait';
import ManageHouseModal from '../components/modals/ManageHouseModal';
import SecurityModal from '../components/modals/SecurityModal';

const KEY_NOTIFY = 'NOTIFICACOES_WHATSAPP';
const KEY_SMART = 'SUGESTOES_INTELIGENTES';

export default function SettingsPage() {
  const { user, isAdmin, setUser } = useAuth();
  const toast = useToast();
  const configs = useLoad('/admin/configuracoes', { enabled: isAdmin });

  const [values, setValues] = useState({});
  const [name, setName] = useState(user?.nome || '');
  const [savingName, setSavingName] = useState(false);
  const [modal, setModal] = useState(null); // 'house' | 'security'

  useEffect(() => {
    if (!configs.data) return;
    const map = {};
    configs.data.forEach((c) => { map[c.chave] = c.valor; });
    setValues(map);
  }, [configs.data]);

  const houseName = values.NOME_CASA || 'Casa Principal';
  const isOn = (key) => values[key] !== 'false';

  const setFlag = async (key, description, next) => {
    const previous = values[key];
    setValues((v) => ({ ...v, [key]: String(next) }));
    try {
      await api.put(`/admin/configuracoes/${key}`, { valor: String(next), descricao: description });
    } catch (e) {
      setValues((v) => ({ ...v, [key]: previous }));
      toast(errorMessage(e), 'error');
    }
  };

  const saveName = async () => {
    setSavingName(true);
    try {
      await api.put(`/admin/usuarios/${user.id}`, { nome: name.trim(), telefone: user.telefone, perfil: user.perfil });
      setUser({ ...user, nome: name.trim() });
      toast('Nome atualizado.');
    } catch (e) {
      toast(errorMessage(e), 'error');
    } finally {
      setSavingName(false);
    }
  };

  const nameChanged = isAdmin && name.trim() && name.trim() !== user?.nome;

  return (
    <div className="settings">
      <PageHead title="Configurações" subtitle="Personalize sua experiência com a Mônica" />

      <div className="settings__grid">
        <div className="settings__col">
          <Panel title="Minha conta" icon={UserRound} className="account">
            <div className="account__who">
              <span className="avatar avatar--lg" aria-hidden="true">
                <img src={asset('avatar.png')} alt="" onError={(e) => { e.currentTarget.style.display = 'none'; }} />
                <b>{initials(user?.nome)}</b>
              </span>
              <div>
                <strong>{user?.nome}</strong>
                <small>{user?.email}</small>
              </div>
            </div>
            <label className="plain-field">
              <span>Nome completo</span>
              <input value={name} onChange={(e) => setName(e.target.value)} readOnly={!isAdmin} maxLength={100} />
            </label>
            <label className="plain-field">
              <span>E-mail</span>
              <input value={user?.email || ''} readOnly />
            </label>
            {nameChanged && <Button size="sm" loading={savingName} onClick={saveName}>Salvar alterações</Button>}
          </Panel>

          <Panel title="Minha casa" icon={House} className="house">
            <div className="house__art">
              <Portrait file="casa.png" variant="house" alt="Ilustração da casa" />
            </div>
            <div className="house__name">
              <span>Minha Casa</span>
              <div className="select-like" aria-label="Casa selecionada">
                <span>{houseName}</span>
                <ChevronDown size={14} />
              </div>
            </div>
            {isAdmin && <Button variant="ghost" size="sm" className="house__manage" onClick={() => setModal('house')}>Gerenciar casa</Button>}
          </Panel>
        </div>

        <div className="settings__col">
          <Panel title={<>Assistente <em>Mônica</em></>} className="assistant-config">
            <p className="assistant-config__lead">Personalize como a Mônica se comunica</p>
            <div className="assistant-config__body">
              <div className="assistant-config__toggles">
                <label className="toggle-row">
                  <span>Notificações do assistente</span>
                  <Toggle
                    checked={isOn(KEY_NOTIFY)}
                    disabled={!isAdmin}
                    label="Notificações do assistente"
                    onChange={(v) => setFlag(KEY_NOTIFY, 'Envia alertas ao WhatsApp dos administradores via WAHA', v)}
                  />
                </label>
                <label className="toggle-row">
                  <span>Sugestões inteligentes</span>
                  <Toggle
                    checked={isOn(KEY_SMART)}
                    disabled={!isAdmin}
                    label="Sugestões inteligentes"
                    onChange={(v) => setFlag(KEY_SMART, 'Permite que a Mônica sugira rotinas e ações', v)}
                  />
                </label>
              </div>
              <Portrait file="monica-assistente.png" variant="square-lg" />
            </div>
            {!isAdmin && <p className="hint">Somente o morador pode alterar estas opções.</p>}
          </Panel>

          <Panel title="Segurança" icon={Lock} className="security">
            <button type="button" className="security__link" disabled={!isAdmin} onClick={() => setModal('security')}>
              <span>Gerencie suas opções de segurança</span>
              <ChevronRight size={16} />
            </button>
          </Panel>
        </div>
      </div>

      {modal === 'house' && <ManageHouseModal onClose={() => { setModal(null); configs.refresh({ silent: true }); }} />}
      {modal === 'security' && <SecurityModal onClose={() => setModal(null)} />}
    </div>
  );
}
