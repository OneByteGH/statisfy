package io.github.onebytegh.statisfy;

import io.github.onebytegh.statisfy.misc.DiscordWebhook;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statisfy {

    public static Logger logger;
    public static DiscordWebhook statusWebhook;
    public static DiscordWebhook errorWebhook;
    public static DiscordWebhook infoWebhook;


    /**
     * args:
     * 0. IDK
     * 1. Status Webhook Url
     * 2. Error Webhook Url
     * 3. Info Webhook Url
     * 4. DB URL
     * 5. DBUSER:DBPASS
     */
    public static void main(String[] args) {
        //Logger Setup
        logger = LoggerFactory.getLogger(Statisfy.class);

        statusWebhook = new DiscordWebhook(args[1]);
        errorWebhook = new DiscordWebhook(args[2]);
        infoWebhook = new DiscordWebhook(args[3]);

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


        app.start(6969);
    }

    public static void info(String msg) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.info(dtf.format(now) + " " + msg);
        infoWebhook.setContent(dtf.format(now) + " " + msg);
        infoWebhook.execute();
    }

    public static void error(String msg) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        logger.error(dtf.format(now) + " " + msg);
        errorWebhook.setContent(dtf.format(now) + " " + msg);
        errorWebhook.execute();
    }
}