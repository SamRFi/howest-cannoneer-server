package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.CannoneerException;
import be.howest.ti.mars.web.WebServer;
import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import be.howest.ti.mars.web.exceptions.NotYetImplementedException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * In the MarsOpenApiBridge class you will create one handler-method per API operation.
 * The job of the "bridge" is to bridge between JSON (request and response) and Java (the controller).
 * <p>
 * For each API operation you should get the required data from the `Request` class.
 * The Request class will turn the HTTP request data into the desired Java types (int, String, Custom class,...)
 * This desired type is then passed to the controller.
 * The return value of the controller is turned to Json or another Web data type in the `Response` class.
 */
public class MarsOpenApiBridge {
    private static final Logger LOGGER = Logger.getLogger(MarsOpenApiBridge.class.getName());
    private static final String NOT_YET_IMPLEMENTED = "Not yet implemented";
    private final MarsController controller;
    private final MarsRtcBridge rtcBridge;

    public MarsOpenApiBridge(WebServer parent) {
        this.rtcBridge = parent.getRtcBridge();
        this.controller = parent.getController();
    }

    public Router buildRouter(RouterBuilder routerBuilder) {
        LOGGER.log(Level.INFO, "Installing cors handlers");
        routerBuilder.rootHandler(createCorsHandler());

        LOGGER.log(Level.INFO, "Installing failure handlers for all operations");
        routerBuilder.operations().forEach(op -> op.failureHandler(this::onFailedRequest));

        LOGGER.log(Level.INFO, "Installing handler for: getLeaderboard");
        routerBuilder.operation("getLeaderboard").handler(this::getLeaderboard);

        LOGGER.log(Level.INFO, "Installing handler for: getLeaderboardsPlayerOverview");
        routerBuilder.operation("getLeaderboardsPlayerOverview").handler(this::getLeaderboardsPlayerOverview);

        LOGGER.log(Level.INFO, "Installing handler for: getPlayer");
        routerBuilder.operation("getPlayer").handler(this::getPlayer);

        LOGGER.log(Level.INFO, "Installing handler for: getActiveGames");
        routerBuilder.operation("getActiveGames").handler(this::getActiveGames);

        LOGGER.log(Level.INFO, "Installing handler for: createGame");
        routerBuilder.operation("createGame").handler(this::createGame);

        LOGGER.log(Level.INFO, "Installing handler for: fireCanon");
        routerBuilder.operation("fireCanon").handler(this::fireCanon);

        LOGGER.log(Level.INFO, "Installing handler for: stopGame");
        routerBuilder.operation("stopGame").handler(this::stopGame);

        LOGGER.log(Level.INFO, "Installing handler for: getStoreItems");
        routerBuilder.operation("getStoreItems").handler(this::getStoreItems);

        LOGGER.log(Level.INFO, "Installing handler for: buyItem");
        routerBuilder.operation("buyItem").handler(this::buyItem);

        LOGGER.log(Level.INFO, "Installing handler for: getStoreItem");
        routerBuilder.operation("getStoreItem").handler(this::getStoreItem);

        LOGGER.log(Level.INFO, "Installing handler for: getBattlePass");
        routerBuilder.operation("getBattlePass").handler(this::getBattlePass);

        LOGGER.log(Level.INFO, "Installing handler for: claimBattlePassTier");
        routerBuilder.operation("claimBattlePassTier").handler(this::claimBattlePassTier);

        LOGGER.log(Level.INFO, "Installing handler for: getReservations");
        routerBuilder.operation("getReservations").handler(this::getReservations);

        LOGGER.log(Level.INFO, "Installing handler for: bookReservation");
        routerBuilder.operation("bookReservation").handler(this::bookReservation);

        LOGGER.log(Level.INFO, "All handlers are installed, creating router.");
        return routerBuilder.createRouter();
    }

    public void getLeaderboard(RoutingContext ctx) {
        List<LeaderboardPlayer> leaderboard = controller.getLeaderboard(Request.from(ctx).getGamemode());

        Response.sendLeaderboard(ctx, leaderboard);
    }

    public void getLeaderboardsPlayerOverview(RoutingContext ctx) {
        Map<String, Integer> leaderboardPlayerOverview = controller.getLeaderboardsPlayerOverview(Request.from(ctx).getPlayerId());

        Response.sendLeaderboardsPlayerOverview(ctx, leaderboardPlayerOverview);
    }

    public void getPlayer(RoutingContext ctx) {
        Player player = controller.getPlayer(Request.from(ctx).getPlayerId());
        Response.sendPlayer(ctx, player, controller.getLeaderboardsPlayerOverview(player.getId()));
    }

    private void getActiveGames(RoutingContext ctx) {
        Map<Integer, JsonObject> activeGames = controller.getActiveGames();
        Response.sendActiveGames(ctx, activeGames);
    }

    public void createGame(RoutingContext ctx) {
        int playerId = Request.from(ctx).getPlayerIdFromBody();
        String location = Request.from(ctx).getLocation();
        Gamemode gamemode = Request.from(ctx).getGamemodeFromBody();

        int id = controller.createGame(playerId, gamemode, location);

        Response.sendGameId(ctx, id);
    }

    public void fireCanon(RoutingContext ctx) {
        int playerId = Request.from(ctx).getPlayerIdFromBody();
        int gameId = Request.from(ctx).getGameId();
        controller.fireCanon(gameId, playerId);

        rtcBridge.sendGameChanged(gameId);

        Response.sendFiredCannon(ctx);
    }

    public void stopGame(RoutingContext ctx){
        int gameId = Request.from(ctx).getGameId();

        try {
            rtcBridge.sendGameEnded(gameId);
            controller.stopGame(gameId);
        } catch (ParseException e) {
            throw new CannoneerException("ParseException");
        }

        Response.sendGameStopped(ctx);
    }

    public void getStoreItems(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void buyItem(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void getStoreItem(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void getBattlePass(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void claimBattlePassTier(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void getReservations(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    public void bookReservation(RoutingContext ctx) {
        throw new NotYetImplementedException(NOT_YET_IMPLEMENTED);
    }

    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String quote = Objects.isNull(cause) ? "" + code : cause.getMessage();

        // Map custom runtime exceptions to a HTTP status code.
        LOGGER.log(Level.INFO, "Failed request", cause);
        if (cause instanceof IllegalArgumentException) {
            code = 400;
        } else if (cause instanceof MalformedRequestException) {
            code = 400;
        } else if (cause instanceof NoSuchElementException) {
            code = 404;
        } else {
            LOGGER.log(Level.WARNING, "Failed request", cause);
        }

        Response.sendFailure(ctx, code, quote);
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }
}