package com.monica.casa.controller;

import com.monica.casa.dto.DashboardDTO;
import com.monica.casa.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.monica.casa.util.AuthUtil;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDTO.Resumo resumo(Authentication auth) {
        return dashboardService.resumo(AuthUtil.isAdmin(auth));
    }
}
