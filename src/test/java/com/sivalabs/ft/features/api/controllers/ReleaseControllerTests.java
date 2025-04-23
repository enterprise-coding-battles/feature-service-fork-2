package com.sivalabs.ft.features.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.ft.features.AbstractIT;
import com.sivalabs.ft.features.WithMockOAuth2User;
import com.sivalabs.ft.features.api.dtos.ReleaseDto;
import com.sivalabs.ft.features.domain.ReleaseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ReleaseControllerTests extends AbstractIT {

    @Test
    void shouldGetReleasesByProductCode() {
        var result = mvc.get().uri("/api/releases?productCode={code}", "goland");
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.size()")
                .asNumber()
                .isEqualTo(2);
    }

    @Test
    void shouldGetReleaseByCode() {
        String code = "IJ-2023.3.8";
        var result = mvc.get().uri("/api/releases/{code}", code);
        assertThat(result).hasStatusOk().bodyJson().convertTo(ReleaseDto.class).satisfies(dto -> {
            assertThat(dto.code()).isEqualTo(code);
        });
    }

    @Test
    void shouldReturn404WhenReleaseNotFound() {
        var result = mvc.get().uri("/api/releases/{code}", "INVALID_CODE");
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldCreateNewRelease() {
        var payload =
                """
            {
                "productCode": "goland",
                "code": "goland-2025.1",
                "description": "GoLand 2025.1"
            }
            """;

        var result = mvc.post()
                .uri("/api/releases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        assertThat(result).hasStatus(HttpStatus.CREATED);
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldUpdateRelease() {
        var payload =
                """
            {
                "description": "Updated description",
                "status": "RELEASED",
                "releasedAt": "2023-12-01T10:00:00Z"
            }
            """;

        var result = mvc.put()
                .uri("/api/releases/{code}", "IJ-2023.3.8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        assertThat(result).hasStatusOk();

        // Verify the update
        var updatedRelease = mvc.get().uri("/api/releases/{code}", "IJ-2023.3.8");
        assertThat(updatedRelease)
                .hasStatusOk()
                .bodyJson()
                .convertTo(ReleaseDto.class)
                .satisfies(dto -> {
                    assertThat(dto.description()).isEqualTo("Updated description");
                    assertThat(dto.status()).isEqualTo(ReleaseStatus.RELEASED);
                    assertThat(dto.releasedAt()).isNotNull();
                });
    }

    @Test
    @WithMockOAuth2User(username = "user")
    void shouldDeleteRelease() {
        var result = mvc.delete().uri("/api/releases/{code}", "RI-2024.2.6");
        assertThat(result).hasStatusOk();

        // Verify deletion
        var getResult = mvc.get().uri("/api/releases/{code}", "RI-2024.2.6");
        assertThat(getResult).hasStatus(HttpStatus.NOT_FOUND);
    }
}
