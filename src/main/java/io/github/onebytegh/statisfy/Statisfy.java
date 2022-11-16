package io.github.onebytegh.statisfy;

import io.github.onebytegh.statisfy.database.DB;
import io.github.onebytegh.statisfy.database.DBUtil;
import io.github.onebytegh.statisfy.misc.DiscordWebhook;
import io.github.onebytegh.statisfy.router.LoginRouter;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statisfy {

    public static Logger logger;
    public static String spotifyApiUrl;
    public static DB db;

    public static String spotifySecret;
    public static String spotifyClientId;
    public static String redirectUri;
    public static DiscordWebhook statusWebhook;
    public static DiscordWebhook errorWebhook;
    public static DiscordWebhook infoWebhook;

    /**
     * args:
     * Spotify Secret
     * Spotify Client Id
     * Status Webhook Url
     * Error Webhook Url
     * Info Webhook Url
     * DB URL
     * DBUSER:DBPASS
     */
    public static void main(String[] args) {

        //Logger Setup
        logger = LoggerFactory.getLogger(Statisfy.class);

        //DB Setup
        try {
            db = new DB(args[7], args[8], args[9]);
        } catch (SQLException e) {
            Statisfy.error("Error while connecting to db\n" + e);
        } finally {
            Statisfy.info("Connected to DB");
        }

        try {
            DBUtil.setupDB();
        } catch (SQLException e) {
            Statisfy.error("Error while setting db up\n" + e);
        } finally {
            Statisfy.info("DB Setup Done");
        }

        spotifyApiUrl = "https://api.spotify.com/";
        spotifySecret = args[0];
        spotifyClientId = args[1];
        redirectUri = args[2];
        statusWebhook = new DiscordWebhook(args[3]);
        errorWebhook = new DiscordWebhook(args[4]);
        infoWebhook = new DiscordWebhook(args[5]);

        //Server Creation
        Javalin app = Javalin.create(config -> {
                config.http.defaultContentType = "application/json";
                config.requestLogger.http((context, ms) -> {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    logger.info(dtf.format(now) + " " + "[" + context.method() + "]" + " " + context.path() + " " + context.status());

                    if (context.status().getCode() == 200) {
                        infoWebhook.setContent(dtf.format(now) + " [" + context.method() + "]" + " " + context.path() + " " + context.status());
                        infoWebhook.execute();
                    } else {
                        errorWebhook.setContent(dtf.format(now) + " [" + context.method() + "]" + " " + context.path() + " " + context.status());
                        errorWebhook.execute();
                    }
                });
            });

        //Logging Events
        app.events(event -> {
            event.serverStarted(() -> {
                statusWebhook.setContent(":green_circle: Server started!");
                statusWebhook.execute();

                info("Server Started");
            });
            event.serverStopped(() -> {
                statusWebhook.setContent(":red_circle: Server stopped!");
                statusWebhook.execute();

                error("Server Stopped");
            });
        });

        //Routers
        app.get("/connect", LoginRouter::connect);
        app.get("/loginUrl", LoginRouter::loginUrl);


        app.start(6969);
    }

    public static void info(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.info(dtf.format(now) + " " + msg);
        infoWebhook.setContent(dtf.format(now) + " " + msg);

        try {
            infoWebhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void error(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.error(dtf.format(now) + " " + msg);
        errorWebhook.setContent(dtf.format(now) + " " + msg);
        try {
            errorWebhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}