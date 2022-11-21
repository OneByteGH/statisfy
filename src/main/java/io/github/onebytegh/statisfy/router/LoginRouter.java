package io.github.onebytegh.statisfy.router;

import com.mongodb.client.MongoCollection;
import io.github.onebytegh.statisfy.Statisfy;
import io.github.onebytegh.statisfy.models.SimpleResponseModel;
import io.github.onebytegh.statisfy.models.UserModel;
import io.javalin.http.Context;
import org.apache.hc.core5.http.ParseException;
import org.bson.Document;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class LoginRouter {
    /**
     * - Connect
     * - Disconnect
     *
     */
    public static void connect(Context ctx) {
        String code = ctx.queryParam("code");
        Statisfy.info(code);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(code)
                .setClientId(Statisfy.spotifyClientId)
                .setClientSecret(Statisfy.spotifySecret)
                .setRedirectUri(URI.create(Statisfy.redirectUri))
                .build();

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();

        try {
            final AuthorizationCodeCredentials authCodeCreds = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authCodeCreds.getAccessToken());
            spotifyApi.setRefreshToken(authCodeCreds.getRefreshToken());

            Statisfy.info("Received User Tokens: " + spotifyApi.getAccessToken() + " " + spotifyApi.getRefreshToken());
            Statisfy.info("Getting other user info");

            final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                    .build();

            final User user = getCurrentUsersProfileRequest.execute();

            Statisfy.info(user.toString());
            //region storing user data

            //generate a secure uuid
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            UUID uuid = UUID.nameUUIDFromBytes(bytes);

            String profilePic;
            if(user.getImages().length > 0) {
                profilePic = user.getImages()[0].getUrl();
            } else {
                profilePic = "";
            }

            String rn = String.valueOf(System.currentTimeMillis());

            UserModel dbUser = new UserModel(
                uuid.toString(),
                user.getDisplayName(),
                user.getId(),
                user.getEmail(),
                user.getCountry().getAlpha3(),
                profilePic,
                user.getFollowers().getTotal(),
                true,
                user.getExternalUrls().get("spotify"),
                user.getProduct().getType(),
                spotifyApi.getAccessToken(),
                spotifyApi.getRefreshToken(),
                rn,
                rn,
                rn);

            MongoCollection<UserModel> users = Statisfy.db.getCollection("users", UserModel.class);
            users.insertOne(dbUser);

            ctx.json(new SimpleResponseModel(false, "Successfully logged in!"));

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            Statisfy.error("Error in connect API \n" + e);
            ctx.json(new SimpleResponseModel(true, "We are currently having issues connecting with Spotify. This will be fixed soon, kindly let the devs know."));
        }
    }

    public static void deleteAcc(Context ctx) {
        if(ctx.header("Authorization") == null) {
            ctx.json(new SimpleResponseModel(true, "You are not logged in!"));
            return;
        }

        String id = ctx.header("Authorization");

        MongoCollection<Document> users = Statisfy.db.getCollection("users");
        Document user = users.find(eq("_id", id)).first();
        System.out.println(id);
        System.out.println(user);
        if(user != null) {
            users.updateOne(eq("_id", id), new Document("$set", new Document("isActive", false)));
            ctx.json(new SimpleResponseModel(false, "Successfully deleted account!"));
        } else {
            ctx.json(new SimpleResponseModel(true, "You are not logged in!"));
        }
    }

    public static void loginUrl(Context ctx) {
        ctx.redirect(getLoginUrl());
    }

    private static String getLoginUrl() {
        return "https://accounts.spotify.com/authorize?client_id=" + Statisfy.spotifyClientId +
            "&redirect_uri=" + Statisfy.redirectUri +
            "&scope=app-remote-control," +
                "playlist-read-private," +
                "playlist-read-collaborative," +
                "user-follow-read,user-top-read," +
                "user-read-recently-played," +
                "user-read-playback-position," +
                "user-read-email," +
                "user-read-private," +
                "user-library-read" +
            "&response_type=code";
    }
}
