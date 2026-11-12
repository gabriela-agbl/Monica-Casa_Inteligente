import { useState } from 'react';
import { Sparkles } from 'lucide-react';

/**
 * Slot de imagem da assistente Mônica.
 *
 * As ilustrações NÃO fazem parte do código: exporte-as do seu Figma (PNG) e salve em
 * `frontend/public/assets/` com os nomes abaixo. Se o arquivo não existir, o componente
 * mostra o quadro "HUD" sem imagem, e o layout continua idêntico.
 *
 *   monica-hero.png        → Login e Cadastro (imagem grande)
 *   monica-hud.png         → Home, Alertas, Dispositivos (retrato com moldura HUD)
 *   monica-assistente.png  → cartão "Assistente Mônica" (com headset/prancheta)
 *   monica-feliz.png       → Recomendação da Mônica (Rotinas) e assistente em Dispositivos
 *   (Configurações reaproveita monica-assistente.png)
 *   casa.png               → ilustração em "Minha casa" (Configurações)
 *   avatar.png             → avatar do perfil (opcional)
 */
export const asset = (file) => `${import.meta.env.BASE_URL}assets/${file}`;

export default function Portrait({ file = 'monica-hud.png', variant = 'hud', alt = 'Assistente Mônica', className = '' }) {
  const [failed, setFailed] = useState(false);
  return (
    <div className={`portrait portrait--${variant} ${className}`}>
      {failed ? (
        <div className="portrait__fallback" role="img" aria-label={alt}>
          <Sparkles size={variant === 'hero' ? 34 : 20} />
          {variant === 'hero' && <span>Mônica</span>}
          {import.meta.env.DEV && variant === 'hero' && <small>adicione public/assets/{file}</small>}
        </div>
      ) : (
        <img src={asset(file)} alt={alt} draggable="false" onError={() => setFailed(true)} />
      )}
    </div>
  );
}
