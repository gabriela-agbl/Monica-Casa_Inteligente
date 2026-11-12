/**
 * O back-end ainda não mede consumo. Esta função ESTIMA os kWh a partir do histórico de
 * acionamentos (tempo ligado × potência típica por tipo de dispositivo).
 */
const KW_BY_TYPE = {
  ILUMINACAO: 0.01,
  CLIMATIZACAO: 1.2,
  IRRIGACAO: 0.75,
  SEGURANCA: 0.05,
  OUTRO: 0.1,
};

export function estimateKwh(devices = [], history = [], from, to = new Date()) {
  const fromMs = from.getTime();
  const toMs = to.getTime();
  let total = 0;

  devices.forEach((device) => {
    const events = history
      .filter((h) => h.dispositivoId === device.id && h.criadoEm)
      .map((h) => ({ t: new Date(h.criadoEm).getTime(), on: !!h.ligado }))
      .sort((a, b) => a.t - b.t);

    const before = events.filter((e) => e.t < fromMs);
    const inside = events.filter((e) => e.t >= fromMs && e.t <= toMs);

    let on;
    if (before.length) on = before[before.length - 1].on;
    else if (inside.length) on = !inside[0].on;
    else on = !!device.status;

    let cursor = fromMs;
    let ms = 0;
    inside.forEach((e) => {
      if (on) ms += e.t - cursor;
      cursor = e.t;
      on = e.on;
    });
    if (on) ms += toMs - cursor;

    total += (ms / 3600000) * (KW_BY_TYPE[device.tipo] ?? KW_BY_TYPE.OUTRO);
  });

  return total;
}

export const startOfToday = () => {
  const d = new Date();
  d.setHours(0, 0, 0, 0);
  return d;
};

export const startOfMonth = () => {
  const d = new Date();
  d.setDate(1);
  d.setHours(0, 0, 0, 0);
  return d;
};
