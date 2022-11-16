package io.github.onebytegh.statisfy.router;

import io.github.onebytegh.statisfy.Statisfy;
import io.github.onebytegh.statisfy.database.SPConstants;
import io.github.onebytegh.statisfy.http.HTTPClient;
import io.javalin.http.Context;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginRouter {
    /**
     * - Connect
     * - Disconnect
     *
     */
    public static void connect(Context ctx) {
        HttpClient client = HTTPClient.getClient();

        String accessToken = ctx.pathParam("code");
        Statisfy.info(accessToken);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(accessToken)
                .build();

        try {
            final AuthorizationCodeCredentials authCodeCreds = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authCodeCreds.getAccessToken());
            spotifyApi.setRefreshToken(authCodeCreds.getRefreshToken());

            try(PreparedStatement ps = Statisfy.db.createStatement(SPConstants.createUser)){
                int result = Statisfy.db.update(ps);
                if(result == 0) {
                    Statisfy.info("0");
                } else {
                    Statisfy.info("1");
                }
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            Statisfy.error("Error in connect API \n" + e);
        } catch (SQLException e) {
            Statisfy.error("SQL Error while creating user \n" + e);
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
