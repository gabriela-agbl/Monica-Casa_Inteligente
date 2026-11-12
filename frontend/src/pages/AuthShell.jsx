import Brand from '../components/Brand';
import Portrait from '../components/Portrait';
import { useToast } from '../context/ToastContext';
import { SOCIAL } from '../components/SocialIcons';

/**
 * Estrutura comum de Login e Cadastro: cartão do formulário + cartão da Mônica com balão de fala.
 * `imageSide` define de que lado fica a imagem (Login: direita · Cadastro: esquerda).
 */
export default function AuthShell({ imageSide = 'right', bubble, children }) {
  return (
    <div className="canvas canvas--auth">
      <div className={`auth-window auth-window--image-${imageSide}`}>
        <section className="auth-card">{children}</section>
        <aside className="auth-visual">
          <Portrait file="monica-hero.png" variant="hero" />
          <div className="speech-bubble">{bubble}</div>
        </aside>
      </div>
    </div>
  );
}

export function AuthHeader({ title }) {
  return (
    <>
      <Brand className="auth-brand" size={46} />
      <h1 className="auth-title">{title}</h1>
    </>
  );
}

/** Divisor "OU" + botões sociais (sem back-end: apenas avisam que ainda não estão disponíveis). */
export function SocialRow() {
  const toast = useToast();
  return (
    <>
      <div className="or-divider"><span>OU</span></div>
      <div className="social-row">
        {SOCIAL.map(({ id, label, Icon }) => (
          <button
            key={id}
            type="button"
            className="social-btn"
            aria-label={`Continuar com ${label}`}
            onClick={() => toast(`Login com ${label} ainda não está disponível. Use e-mail e senha.`, 'error')}
          >
            <Icon size={26} />
          </button>
        ))}
      </div>
    </>
  );
}
