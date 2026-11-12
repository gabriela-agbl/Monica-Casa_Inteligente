import { ClockChip } from './UI';
import Portrait from './Portrait';

/**
 * Cabeçalho padrão das telas internas: título vermelho + subtítulo à esquerda,
 * relógio (e, opcionalmente, o retrato HUD da Mônica) à direita.
 */
export default function PageHead({ title, subtitle, portrait = false, children }) {
  return (
    <header className={`page-head ${portrait ? 'page-head--portrait' : ''}`}>
      <div className="page-head__text">
        <h1>{title}</h1>
        {subtitle && <p>{subtitle}</p>}
      </div>
      <div className="page-head__aside">
        {portrait && <Portrait file="monica-hud.png" variant="hud" />}
        <ClockChip />
        {children}
      </div>
    </header>
  );
}
