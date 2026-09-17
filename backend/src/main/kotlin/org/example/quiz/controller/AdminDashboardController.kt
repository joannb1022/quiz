package org.example.quiz.controller

import org.example.quiz.dto.UsersSummaryDto
import org.example.quiz.service.AdminDashboardService
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminDashboardController(
    private val adminDashboardService: AdminDashboardService
) {

    @GetMapping("/users-summary")
    @PreAuthorize("hasRole('ADMIN')")
    fun usersSummary(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): Page<UsersSummaryDto> {
        return adminDashboardService.getUsersSummary(page, size)
    }


}