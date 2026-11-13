import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react';
import { Button, Modal } from '../components/UI';
import Portrait from '../components/Portrait';

const ConfirmContext = createContext(async () => true);

/**
 * "Tem certeza?" da Mônica. Uso:
 *   const confirm = useMonicaConfirm();
 *   if (await confirm({ text: 'Remover X?', confirmLabel: 'Remover' })) { ... }
 *
 * Opções: title, text, text2 (fala da 2ª etapa), steps (1 ou 2), confirmLabel, cancelLabel,
 * confirmVariant. De propósito, a ordem dos botões é sorteada e inverte a cada etapa.
 */
export function MonicaConfirmProvider({ children }) {
  const [ask, setAsk] = useState(null);
  const pending = useRef(null);

  const open = useCallback((state) => {
    pending.current = state;
    setAsk(state);
  }, []);

  const confirm = useCallback((opts = {}) => new Promise((resolve) => {
    pending.current?.resolve(false); // um pedido novo cancela o anterior
    open({ opts, stage: 0, flip: Math.random() < 0.5, resolve });
  }), [open]);

  const finish = (value) => {
    ask.resolve(value);
    pending.current = null;
    setAsk(null);
  };

  const yes = () => {
    const steps = ask.opts.steps ?? 1;
    if (ask.stage + 1 < steps) open({ ...ask, stage: ask.stage + 1, flip: !ask.flip });
    else finish(true);
  };

  const value = useMemo(() => confirm, [confirm]);
  const o = ask?.opts || {};
  const second = ask?.stage > 0;
  const body = second
    ? o.text2 || 'Tem MESMO certeza? Eu só queria cuidar de você... 💚'
    : o.text || 'Tem certeza? Eu não queria que você se arrependesse.';

  const yesBtn = <Button key="yes" variant={o.confirmVariant || 'danger'} onClick={yes}>{o.confirmLabel || 'Sim, tenho certeza'}</Button>;
  const noBtn = <Button key="no" variant="success" onClick={() => finish(false)}>{o.cancelLabel || 'Voltar'}</Button>;

  return (
    <ConfirmContext.Provider value={value}>
      {children}
      {ask && (
        <Modal title={second ? 'Tem MESMO certeza?' : o.title || 'Tem certeza?'} onClose={() => finish(false)}>
          <div className="monica-confirm">
            <Portrait file="monica-assistente.png" variant="square" />
            <p className={second ? 'glitch' : ''} data-text={body}>{body}</p>
          </div>
          <div className="modal__actions">{ask.flip ? [noBtn, yesBtn] : [yesBtn, noBtn]}</div>
        </Modal>
      )}
    </ConfirmContext.Provider>
  );
}

export const useMonicaConfirm = () => useContext(ConfirmContext);
