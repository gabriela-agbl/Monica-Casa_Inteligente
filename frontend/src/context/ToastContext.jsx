import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react';
import { CheckCircle2, CircleAlert } from 'lucide-react';
import { GLITCH_LINES, pick } from '../utils/monica';

const ToastContext = createContext(() => {});

export function ToastProvider({ children }) {
  const [items, setItems] = useState([]);
  const counter = useRef(0);

  const toast = useCallback((message, type = 'success') => {
    const id = ++counter.current;
    const glitch = type === 'error' ? pick(GLITCH_LINES) : null;
    setItems((list) => [...list, { id, message, type, glitch }]);
    setTimeout(() => setItems((list) => list.filter((i) => i.id !== id)), type === 'error' ? 5200 : 3800);
  }, []);

  const value = useMemo(() => toast, [toast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-stack" role="status" aria-live="polite">
        {items.map((t) => (
          <div key={t.id} className={`toast toast--${t.type}`}>
            {t.type === 'error' ? <CircleAlert size={18} /> : <CheckCircle2 size={18} />}
            <div className="toast__body">
              {t.glitch && <b className="glitch toast__monica" data-text={t.glitch}>{t.glitch}</b>}
              <span>{t.message}</span>
            </div>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export const useToast = () => useContext(ToastContext);
