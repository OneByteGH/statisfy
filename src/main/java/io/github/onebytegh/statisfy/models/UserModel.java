package io.github.onebytegh.statisfy.models;

import lombok.Getter;

@Getter
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

    public UserModel(String id, String name, String spotifyId, String email, String country, String profilePic, int followerCount, boolean isActive, String uri, String productType, String accessToken, String refreshToken, String createdAt, String updatedAt, String lastLogin) {
        this.id = id;
        this.name = name;
        this.spotifyId = spotifyId;
        this.email = email;
        this.country = country;
        this.profilePic = profilePic;
        this.followerCount = followerCount;
        this.isActive = isActive;
        this.uri = uri;
        this.productType = productType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
    }
}
