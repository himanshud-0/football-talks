package com.footballtalks.footballtalks.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String fullName;
    private String bio;
    private String favoriteTeam;
    private String profileImageUrl;
}
