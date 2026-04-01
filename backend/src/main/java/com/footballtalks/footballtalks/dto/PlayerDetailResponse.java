package com.footballtalks.footballtalks.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlayerDetailResponse extends PlayerResponse {
    private List<PlayerSeasonStatResponse> statsHistory = new ArrayList<>();
    private List<TransferResponse> transferHistory = new ArrayList<>();
}
