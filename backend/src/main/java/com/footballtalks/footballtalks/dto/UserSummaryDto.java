package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String username;
    private String fullName;
    private String profileImageUrl;
    private String favoriteTeam;
}
