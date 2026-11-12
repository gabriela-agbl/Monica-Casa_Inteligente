import { useCallback, useEffect, useRef, useState } from 'react';
import api, { errorMessage } from '../services/api';

/**
 * GET + estado de carregamento/erro.
 * `refresh({ silent: true })` recarrega sem piscar o spinner (útil depois de ligar/desligar algo).
 */
export function useLoad(url, { enabled = true } = {}) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(enabled);
  const [error, setError] = useState('');
  const alive = useRef(true);
  const sequence = useRef(0);

  const refresh = useCallback(async ({ silent = false } = {}) => {
    if (!enabled || !url) {
      setLoading(false);
      return null;
    }
    const id = ++sequence.current;
    if (!silent) setLoading(true);
    setError('');
    try {
      const response = await api.get(url);
      if (alive.current && id === sequence.current) setData(response.data);
      return response.data;
    } catch (e) {
      if (alive.current && id === sequence.current) setError(errorMessage(e));
      return null;
    } finally {
      if (alive.current && id === sequence.current) setLoading(false);
    }
  }, [url, enabled]);

  useEffect(() => {
    alive.current = true;
    refresh();
    return () => { alive.current = false; };
  }, [refresh]);

  return { data, setData, loading, error, refresh };
}
