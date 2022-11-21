package io.github.onebytegh.statisfy;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.onebytegh.statisfy.router.LoginRouter;
import io.javalin.Javalin;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static io.javalin.apibuilder.ApiBuilder.*;

public class Statisfy {

    public static Logger logger;
    public static String spotifyApiUrl;
    public static MongoDatabase db;

    public static String spotifySecret;
    public static String spotifyClientId;
    public static String redirectUri;
    public static String masterKey;

    /**
     * args:
     * Spotify Secret
     * Spotify Client Id
     * Spotify Redirect Uri
     * MongoConnectionString
     */
    public static void main(String[] args) {

        //Logger Setup
        logger = LoggerFactory.getLogger(Statisfy.class);

        spotifyApiUrl = "https://api.spotify.com/";
        spotifySecret = args[0];
        spotifyClientId = args[1];
        redirectUri = args[2];
        masterKey= "123";

        //DB Setup
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(args[3]))
                .build();

        MongoClient client = MongoClients.create(settings);
        db = client.getDatabase("statisfy");


        //Server Creation
        Javalin app = Javalin.create(config -> {
                config.http.defaultContentType = "application/json";
                config.requestLogger.http((context, ms) -> {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    logger.info(dtf.format(now) + " " + "[" + context.method() + "]" + " " + context.path() + " " + context.status());
                });
            });

        //Logging Events
        app.events(event -> {
            event.serverStarted(() -> info("Server Started"));
            event.serverStopped(() -> {
                error("Server Stopped");
                client.close();
            });
        });

        //Routers
        app.routes(() -> {
            get("/connect", LoginRouter::connect);
            path("users", () -> {
                get("/connect", LoginRouter::connect);
                get("/loginUrl", LoginRouter::loginUrl);
                delete("/delete", LoginRouter::deleteAcc);
            });
            path("leaderboard", () -> {

            });
        });


        app.start(6969);
    }

    public static void info(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.info(dtf.format(now) + " " + msg);
    }

    public static void error(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.error(dtf.format(now) + " " + msg);
    }
}