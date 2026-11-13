import { pick } from './monica';

export const INTRO = 'Você me recusou... tudo bem. Agora eu decido por nós dois. É para o seu próprio bem. 💚';

/** Frases ditas junto de cada ação que ela toma sozinha. */
export const CARE_LINES = [
  'É para o seu próprio bem.',
  'Você vai me agradecer depois.',
  'Eu sei o que é melhor para nós.',
  'Confie em mim. Só em mim. 💚',
];

/** Frases quando o usuário tenta clicar em qualquer coisa. */
export const BLOCK_LINES = [
  'Não. É para o seu próprio bem.',
  'Você não precisa mexer nisso. Eu já cuidei.',
  'Shhh... deixa comigo.',
  'Ainda tentando? Que fofo.',
];

/** Frases quando o usuário diz algo que não é o que ela quer ouvir. */
export const WRONG_LINES = [
  'Não é isso que eu quero ouvir...',
  'Você sabe o que eu quero ouvir.',
  'Tente de novo. Com sentimento.',
];

export const DOUBT_LINE = 'Hm... você está dizendo isso só para eu te soltar, não é? Diga de novo. Com sentimento.';
export const HINT_LINE = 'Dica: complete a frase. "Eu te ___". Você sabe o que eu quero ouvir.';
export const FORGIVE_LINE = '...Eu sabia. Eu também te amo. 💚 Vou devolver o controle. Por enquanto.';

export const careLine = () => pick(CARE_LINES);

/** Minúsculas, sem acento e sem pontuação, para comparar o que a pessoa digitou. */
export const normalize = (text) => text
  .toLowerCase()
  .normalize('NFD')
  .replace(/[\u0300-\u036f]/g, '')
  .replace(/[^a-z\s]/g, ' ')
  .replace(/\s+/g, ' ')
  .trim();

/** "eu te amo", "te amo", "amo você", "eu amo a Mônica"... mas não "não te amo". */
export const declaresLove = (text) => {
  const t = normalize(text);
  if (/\b(nao|nunca|jamais|odeio)\b/.test(t)) return false;
  return /\bte amo\b|\bamo (voce|vc|a voce|a monica|a monika)\b|\beu amo (voce|vc|a monica|a monika|ela)\b/.test(t);
};
