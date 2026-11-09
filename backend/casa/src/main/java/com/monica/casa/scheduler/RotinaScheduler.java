package com.monica.casa.scheduler;

import com.monica.casa.model.Rotina;
import com.monica.casa.repository.RotinaRepository;
import com.monica.casa.service.RotinaService;
import com.monica.casa.util.DiasSemana;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Verificação automática de rotinas: roda no segundo 0 de todo minuto, procura as rotinas
 * ativas marcadas para aquele HH:mm e para o dia da semana atual, e as executa.
 * <p>
 * Intencionalmente NÃO é @Transactional: cada rotina roda na sua própria transação
 * (RotinaService.executar), então uma falha não impede as demais.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RotinaScheduler {

    private final RotinaRepository rotinaRepository;
    private final RotinaService rotinaService;

    @Value("${monica.timezone:America/Sao_Paulo}")
    private String timezone;

    @Scheduled(cron = "0 * * * * *", zone = "${monica.timezone:America/Sao_Paulo}")
    public void verificarRotinas() {
        LocalDateTime agora = LocalDateTime.now(ZoneId.of(timezone));
        LocalTime minuto = agora.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
        DayOfWeek hoje = agora.getDayOfWeek();

        List<Rotina> candidatas = rotinaRepository.findAtivasPorHorario(minuto);
        if (candidatas.isEmpty()) {
            return;
        }
        log.info("Scheduler {}: {} rotina(s) no horário", minuto, candidatas.size());

        for (Rotina rotina : candidatas) {
            if (!DiasSemana.contem(rotina.getDiasSemana(), hoje)) {
                continue;
            }
            try {
                rotinaService.executar(rotina.getId());
                log.info("Rotina '{}' executada", rotina.getNome());
            } catch (Exception e) {
                log.error("Falha ao executar a rotina '{}' (id={})", rotina.getNome(), rotina.getId(), e);
            }
        }
    }
}
