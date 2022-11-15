package io.github.onebytegh.statisfy.router;

import io.github.onebytegh.statisfy.Statisfy;
import io.github.onebytegh.statisfy.http.HTTPClient;
import io.javalin.http.Context;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class LoginRouter {
    /**
     * - Connect
     * - Disconnect
     *
     */
    public void connect(Context ctx) {
        HttpClient client = HTTPClient.getClient();


        HttpRequest authReq = HTTPClient.getRequest("authorize","GET", "", null);



    }


    private String getLoginUrl() {
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
