import { AirVent, Clock3, Droplets, Lightbulb, Moon, Shield, Smartphone, Sun } from 'lucide-react';

export const TIPO_LABEL = {
  ILUMINACAO: 'Iluminação',
  CLIMATIZACAO: 'Climatização',
  IRRIGACAO: 'Irrigação',
  SEGURANCA: 'Segurança',
  OUTRO: 'Outro',
};

export const TIPO_ICON = {
  ILUMINACAO: Lightbulb,
  CLIMATIZACAO: AirVent,
  IRRIGACAO: Droplets,
  SEGURANCA: Shield,
  OUTRO: Smartphone,
};

/** Ícone da rotina conforme o nome (modo noturno, bom dia...). */
export const routineIcon = (name = '') => {
  const n = name.toLowerCase();
  if (/noturn|dormir|boa noite/.test(n)) return Moon;
  if (/dia|despert|acord|manh/.test(n)) return Sun;
  return Clock3;
};
