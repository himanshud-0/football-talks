package com.footballtalks.footballtalks.service;

import com.footballtalks.footballtalks.dto.UpdateProfileRequest;
import com.footballtalks.footballtalks.dto.UserProfileResponse;
import com.footballtalks.footballtalks.model.User;
import com.footballtalks.footballtalks.repository.CommentRepository;
import com.footballtalks.footballtalks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthService authService;

    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toProfileResponse(user);
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = authService.getCurrentUser();
        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = authService.getCurrentUser();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getFavoriteTeam() != null) {
            user.setFavoriteTeam(request.getFavoriteTeam());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    private UserProfileResponse toProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.resolveFullName());
        response.setBio(user.resolveBio());
        response.setFavoriteTeam(user.resolveFavoriteTeam());
        response.setProfileImageUrl(user.resolveProfileImageUrl());
        response.setCreatedAt(user.getCreatedAt());
        response.setPostsCount(user.getPosts().size());
        response.setCommentsCount(
                Math.toIntExact(commentRepository.countByUserId(user.getId()))
        );
        return response;
    }

}
