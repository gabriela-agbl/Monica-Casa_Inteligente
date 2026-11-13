import { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import Portrait from './Portrait';

/**
 * Tela do modo Just Monika. Cobre tudo (clique bloqueado), mostra a fala dela estilo
 * visual novel e é o único lugar onde ainda dá para digitar. Fica fora do #root, que
 * está inerte enquanto o modo está ativo.
 */
export default function MonikaOverlay({ line, leaving, onBlock, onSubmit }) {
  const [value, setValue] = useState('');
  const [shown, setShown] = useState('');

  // Efeito de máquina de escrever a cada fala nova.
  useEffect(() => {
    setShown('');
    let i = 0;
    const id = setInterval(() => {
      i += 1;
      setShown(line.text.slice(0, i));
      if (i >= line.text.length) clearInterval(id);
    }, 26);
    return () => clearInterval(id);
  }, [line.key, line.text]);

  const submit = (e) => {
    e.preventDefault();
    if (leaving || !value.trim()) return;
    onSubmit(value);
    setValue('');
  };

  return createPortal(
    <div className="monika" role="alertdialog" aria-label="Modo Just Monika">
      <div className="monika__dim" onMouseDown={onBlock} onTouchStart={onBlock} />
      <div className="monika__banner glitch" data-text="JUST MONIKA">JUST MONIKA</div>
      <div className="monika__box">
        <Portrait file="monica-assistente.png" variant="square" />
        <div className="monika__talk">
          <b className="monika__name">Mônica</b>
          <p className="monika__text">{shown}<span className="monika__cursor" aria-hidden="true" /></p>
          <form className="monika__form" onSubmit={submit}>
            <input
              value={value}
              onChange={(e) => setValue(e.target.value)}
              placeholder="Diga o que ela quer ouvir..."
              maxLength={80}
              disabled={leaving}
              autoFocus
              aria-label="Responder à Mônica"
            />
            <button type="submit" disabled={leaving}>Falar</button>
          </form>
        </div>
      </div>
    </div>,
    document.body,
  );
}
