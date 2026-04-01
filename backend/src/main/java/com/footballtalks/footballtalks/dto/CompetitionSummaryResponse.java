package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionSummaryResponse {
    private Long id;
    private String name;
    private String country;
    private String logoUrl;
}
