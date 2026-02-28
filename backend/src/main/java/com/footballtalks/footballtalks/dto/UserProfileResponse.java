package com.footballtalks.footballtalks.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String favoriteTeam;
    private String profileImageUrl;

    private LocalDateTime createdAt;
    private int postsCount;
    private int commentsCount;
}
