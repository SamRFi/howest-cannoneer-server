package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.domain.Gamemode;
import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Request class is responsible for translating information that is part of the
 * request into Java.
 *
 * For every piece of information that you need from the request, you should provide a method here.
 * You can find information in:
 * - the request path: params.pathParameter("some-param-name")
 * - the query-string: params.queryParameter("some-param-name")
 * Both return a `RequestParameter`, which can contain a string or an integer in our case.
 * The actual data can be retrieved using `getInteger()` or `getString()`, respectively.
 * You can check if it is an integer (or not) using `isNumber()`.
 *
 * Finally, some requests have a body. If present, the body will always be in the json format.
 * You can acces this body using: `params.body().getJsonObject()`.
 *
 * **TIP:** Make sure that al your methods have a unique name. For instance, there is a request
 * that consists of more than one "player name". You cannot use the method `getPlayerName()` for both,
 * you will need a second one with a different name.
 */
public class Request {
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    private static final String ERR_MSG_LOG = "Unable to decipher the data in the body";
    private static final String ERR_MSG_EX = "Unable to decipher the data in the request body. See logs for details.";
    public static final String SPEC_PLAYER_ID = "playerId";
    public static final String SPEC_GAME_ID = "gameId";
    public static final String SPEC_GAMEMODE = "gamemode";
    public static final String SPEC_LOCATION = "location";

    private final RequestParameters params;

    public static Request from(RoutingContext ctx) {
        return new Request(ctx);
    }

    private Request(RoutingContext ctx) {
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }

    public int getPlayerId() {
        return params.pathParameter(SPEC_PLAYER_ID).getInteger();
    }

    public int getPlayerIdFromBody() {
        try {
            if (params.body().isJsonObject())
                return params.body().getJsonObject().getInteger(SPEC_PLAYER_ID);
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, ERR_MSG_LOG, ex);
            throw new MalformedRequestException(ERR_MSG_EX);
        }
    }

    public String getLocation() {
        try {
            if (params.body().isJsonObject())
                return params.body().getJsonObject().getString(SPEC_LOCATION);
            return params.body().get().toString();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, ERR_MSG_LOG, ex);
            throw new MalformedRequestException(ERR_MSG_EX);
        }
    }

    public Gamemode getGamemodeFromBody() {
        try {
            if (params.body().isJsonObject())
                return Gamemode.valueOf(params.body().getJsonObject().getString(SPEC_GAMEMODE).toUpperCase());
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, ERR_MSG_LOG, ex);
            throw new MalformedRequestException(ERR_MSG_EX);
        }
    }

    public int getGameId() {
        return params.pathParameter(SPEC_GAME_ID).getInteger();
    }

    public Gamemode getGamemode() {
        return Gamemode.valueOf(params.pathParameter(SPEC_GAMEMODE).getString().toUpperCase());
    }
}
