import { useEffect, useState } from 'react';
import { ChevronDown, LoaderCircle, X } from 'lucide-react';
import { formatClock, formatDateChip } from '../utils/format';

/**
 * Componentes de interface compartilhados. As classes correspondentes estão em
 * `src/styles/ui.css` (tokens do Figma HiFi: vermelho #D61C23, verde #22C55E, azul HUD #0E93B0).
 */

/** Botão. `variant`: primary | danger | success | ghost · `size`: sm. Nunca envia formulário sem `type="submit"`. */
export function Button({ children, variant = 'primary', size, className = '', loading = false, type = 'button', disabled, ...props }) {
  const classes = ['btn', `btn--${variant}`, size && `btn--${size}`, className].filter(Boolean).join(' ');
  return (
    <button type={type} className={classes} disabled={loading || disabled} {...props}>
      {loading && <LoaderCircle size={16} className="spin" />}
      {children}
    </button>
  );
}

/** Cartão de seção com cabeçalho opcional (título + ícone teal + ação à direita). */
export function Panel({ title, icon: Icon, action, outlined = false, className = '', children }) {
  return (
    <section className={`panel ${outlined ? 'panel--outlined' : ''} ${className}`}>
      {(title || action) && (
        <div className="panel__head">
          <h2>{Icon && <Icon size={18} />}{title}</h2>
          {action}
        </div>
      )}
      {children}
    </section>
  );
}

/** Interruptor vermelho (desligado) / verde (ligado). `onChange` recebe o novo valor. */
export function Toggle({ checked, onChange, label, disabled = false }) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={!!checked}
      aria-label={label}
      disabled={disabled}
      className={`toggle ${checked ? 'toggle--on' : ''}`}
      onClick={() => onChange?.(!checked)}
    >
      <i />
    </button>
  );
}

/** Selo arredondado (Normal, Ativas, Programado...). `tone`: green | red | muted. */
export function Badge({ tone = 'green', children }) {
  return <span className={`badge ${tone === 'green' ? '' : `badge--${tone}`}`}>{children}</span>;
}

/** Relógio do cabeçalho ("17:34 / Sex 10 de Out"), atualizado a cada 15 s. */
export function ClockChip() {
  const [now, setNow] = useState(() => new Date());
  useEffect(() => {
    const id = setInterval(() => setNow(new Date()), 15000);
    return () => clearInterval(id);
  }, []);
  return (
    <div className="clock-chip" aria-label="Data e hora atuais">
      <strong>{formatClock(now)}</strong>
      <span>{formatDateChip(now)}</span>
    </div>
  );
}

/**
 * Campo de texto. `icon` (componente lucide) fica à esquerda e `right` (nó) à direita.
 * Sem `label`, o campo vira o "pílula escura" das telas de Login/Cadastro (use `aria-label`).
 */
export function Field({ label, icon: Icon, right, className = '', ...props }) {
  return (
    <label className={`field ${className}`}>
      {label && <span className="field__label">{label}</span>}
      <span className="field__control">
        {Icon && <Icon size={17} className="field__icon" />}
        <input {...props} />
        {right}
      </span>
    </label>
  );
}

export function SelectField({ label, children, className = '', ...props }) {
  return (
    <label className={`field ${className}`}>
      {label && <span className="field__label">{label}</span>}
      <span className="field__control">
        <select {...props}>{children}</select>
        <ChevronDown size={16} className="field__icon" />
      </span>
    </label>
  );
}

export function Modal({ title, onClose, children, wide = false }) {
  useEffect(() => {
    const onKey = (e) => e.key === 'Escape' && onClose?.();
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose?.()}>
      <div className={`modal ${wide ? 'modal--wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
        <div className="modal__head">
          <h2>{title}</h2>
          <button type="button" className="icon-btn" onClick={onClose} aria-label="Fechar"><X size={19} /></button>
        </div>
        {children}
      </div>
    </div>
  );
}

export function Empty({ title = 'Nada por aqui ainda', text }) {
  return (
    <div className="empty">
      <strong>{title}</strong>
      {text && <p>{text}</p>}
    </div>
  );
}

/** Caixa de erro; não renderiza nada quando não há mensagem. */
export function ErrorBox({ children }) {
  return children ? <div className="error-box" role="alert">{children}</div> : null;
}

export function Loading() {
  return <div className="loading"><LoaderCircle className="spin" /><span>Carregando...</span></div>;
}
