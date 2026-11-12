export const WEEK_DAYS = [
  ['SEG', 'Seg'], ['TER', 'Ter'], ['QUA', 'Qua'], ['QUI', 'Qui'],
  ['SEX', 'Sex'], ['SAB', 'Sáb'], ['DOM', 'Dom'],
];

const WEEKDAY_SHORT = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
const MONTH_SHORT = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

const pad = (n) => String(n).padStart(2, '0');

/** "17:34" */
export const formatClock = (d = new Date()) => `${pad(d.getHours())}:${pad(d.getMinutes())}`;

/** "Sex 10 de Out" */
export const formatDateChip = (d = new Date()) => `${WEEKDAY_SHORT[d.getDay()]} ${d.getDate()} de ${MONTH_SHORT[d.getMonth()]}`;

/** "Todos os dias", "Seg a Sex", "Fins de semana" ou "Seg, Qua, Sex". */
export function describeDays(diasSemana = '') {
  const set = new Set(diasSemana.split(',').map((s) => s.trim().toUpperCase()).filter(Boolean));
  if (set.size === 7) return 'Todos os dias';
  const weekdays = ['SEG', 'TER', 'QUA', 'QUI', 'SEX'];
  if (set.size === 5 && weekdays.every((d) => set.has(d))) return 'Seg a Sex';
  if (set.size === 2 && set.has('SAB') && set.has('DOM')) return 'Fins de semana';
  return WEEK_DAYS.filter(([v]) => set.has(v)).map(([, l]) => l).join(', ') || 'Sem dias definidos';
}

/** "Há 5 min", "Há 2 h", "Há 3 d". */
export function timeAgo(iso) {
  if (!iso) return '—';
  const minutes = Math.max(0, Math.round((Date.now() - new Date(iso).getTime()) / 60000));
  if (minutes < 1) return 'Agora';
  if (minutes < 60) return `Há ${minutes} min`;
  const hours = Math.round(minutes / 60);
  if (hours < 24) return `Há ${hours} h`;
  return `Há ${Math.round(hours / 24)} d`;
}

export const hhmm = (time) => String(time || '').slice(0, 5);

export const firstName = (name = '') => name.trim().split(/\s+/)[0] || '';

export const initials = (name = '') => (name.trim()[0] || 'M').toUpperCase();
