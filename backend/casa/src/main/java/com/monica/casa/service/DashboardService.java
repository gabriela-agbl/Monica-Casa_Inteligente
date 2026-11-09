package com.monica.casa.service;

import com.monica.casa.dto.DashboardDTO;
import com.monica.casa.repository.AlertaRepository;
import com.monica.casa.repository.DispositivoRepository;
import com.monica.casa.repository.RotinaRepository;
import com.monica.casa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Números do painel central da casa. */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DispositivoRepository dispositivoRepository;
    private final RotinaRepository rotinaRepository;
    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DashboardDTO.Resumo resumo() {
        return new DashboardDTO.Resumo(
                dispositivoRepository.count(),
                dispositivoRepository.countByStatusTrue(),
                rotinaRepository.countByAtivoTrue(),
                alertaRepository.countByCriadoEmAfter(LocalDateTime.now().minusHours(24)),
                usuarioRepository.count());
    }
}
