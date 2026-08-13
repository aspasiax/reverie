package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.statistics.OverviewResponse;
import io.github.aspasiax.reverie.service.IStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the overview of the whole application.
 *
 * <p>
 * The statistics of a single user live under that user instead, because
 * they describe a person rather than the installation, and are readable
 * by anyone signed in.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Statistics",
        description = "Operations for reading the state of the application."
)
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    /**
     * Returns a summary of the whole application.
     *
     * @return what exists and how much has happened
     */
    @Operation(
            summary = "Get the application overview",
            description = "Returns how many accounts, films and genres exist, how much activity has been recorded, and what leads each count."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overview retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('STATISTICS_READ')")
    public ResponseEntity<OverviewResponse> getOverview() {
        return ResponseEntity.ok(statisticsService.getOverview());
    }
}