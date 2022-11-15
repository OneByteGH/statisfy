package io.github.onebytegh.statisfy.http;

import io.github.onebytegh.statisfy.Statisfy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;

public class HTTPClient {
    public static HttpClient getClient() {
        return HttpClient.newHttpClient();
    }

    public static HttpRequest getRequest(String url, String method, String body, HashMap<String, String> headers) {
        HttpRequest.Builder req = HttpRequest
                .newBuilder()
                .uri(URI.create(Statisfy.spotifyApiUrl + " " + url))
                .method(method, HttpRequest.BodyPublishers.ofString(body));

        if(headers != null || !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                req.header(key, headers.get(key));
            }
        }
        return req.build();
    }
}
