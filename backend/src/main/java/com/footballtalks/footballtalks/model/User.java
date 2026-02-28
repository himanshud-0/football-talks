package com.footballtalks.footballtalks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public static final String DEFAULT_BIO = "Passionate football fan.";
    public static final String DEFAULT_FAVORITE_TEAM = "Football Club";
    private static final String DEFAULT_DISPLAY_NAME = "Football Fan";
    private static final String DEFAULT_AVATAR_BACKGROUND = "0f172a";
    private static final String DEFAULT_AVATAR_COLOR = "ffffff";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(length = 500)
    private String bio;

    @Column(length = 100)
    private String favoriteTeam;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    public String resolveFullName() {
        String cleanFullName = cleanProfileValue(fullName, true);
        if (cleanFullName != null) {
            return cleanFullName;
        }

        String cleanUsername = cleanProfileValue(username, true);
        if (cleanUsername != null) {
            return cleanUsername;
        }

        return DEFAULT_DISPLAY_NAME;
    }

    public String resolveBio() {
        String cleanBio = cleanProfileValue(bio, false);
        return cleanBio != null ? cleanBio : DEFAULT_BIO;
    }

    public String resolveFavoriteTeam() {
        String cleanFavoriteTeam = cleanProfileValue(favoriteTeam, false);
        return cleanFavoriteTeam != null ? cleanFavoriteTeam : DEFAULT_FAVORITE_TEAM;
    }

    public String resolveProfileImageUrl() {
        String cleanProfileImageUrl = cleanProfileValue(profileImageUrl, false);
        if (cleanProfileImageUrl != null) {
            return cleanProfileImageUrl;
        }

        String encodedName = URLEncoder.encode(resolveFullName(), StandardCharsets.UTF_8);
        return "https://ui-avatars.com/api/?name=" + encodedName
                + "&background=" + DEFAULT_AVATAR_BACKGROUND
                + "&color=" + DEFAULT_AVATAR_COLOR
                + "&bold=true";
    }

    @PrePersist
    @PreUpdate
    private void applyProfileDefaults() {
        fullName = resolveFullName();
        bio = resolveBio();
        favoriteTeam = resolveFavoriteTeam();
        profileImageUrl = resolveProfileImageUrl();

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
    }

    private String cleanProfileValue(String value, boolean treatUnderscoreAsSpace) {
        if (value == null) {
            return null;
        }

        String cleaned = treatUnderscoreAsSpace ? value.replace('_', ' ') : value;
        cleaned = cleaned.trim().replaceAll("\\s+", " ");

        return cleaned.isEmpty() ? null : cleaned;
    }
}
