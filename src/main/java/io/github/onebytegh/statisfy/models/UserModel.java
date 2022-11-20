package io.github.onebytegh.statisfy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public final class UserModel {
    private final String id;
    private final String name;
    private final String spotifyId;
    private final String email;
    private final String country;
    private final String profilePic;
    private final int followerCount;
    private final boolean isActive;
    private final String uri;
    private final String productType;
    private final String accessToken;
    private final String refreshToken;
    private final String createdAt;
    private final String updatedAt;
    private final String lastLogin;
}
