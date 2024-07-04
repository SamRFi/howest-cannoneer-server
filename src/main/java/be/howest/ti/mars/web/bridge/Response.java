package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.domain.LeaderboardPlayer;
import be.howest.ti.mars.logic.domain.Player;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Map;

/**
 * The Response class is responsible for translating the result of the controller into
 * JSON responses with an appropriate HTTP code.
 */
public class Response {

    private Response() { }

    public static void sendPlayer(RoutingContext ctx, Player player, Map<String, Integer> ranking) {
        JsonObject response = new JsonObject().put("player", JsonObject.mapFrom(player));
        response.getJsonObject("player").put("ranking", ranking);
        sendOkJsonResponse(ctx, response);
    }

    public static void sendActiveGames(RoutingContext ctx, Map<Integer, JsonObject> activeGames) {
        JsonObject json = JsonObject.mapFrom(activeGames);
        sendOkJsonResponse(ctx, json);
    }

    public static void sendGameId(RoutingContext ctx, int id) {
        sendOkJsonResponse(ctx, new JsonObject()
                .put("gameId", id));
    }

    public static void sendFiredCannon(RoutingContext ctx) {
        sendEmptyResponse(ctx, 204);
    }

    public static void sendGameStopped(RoutingContext ctx) {
        sendEmptyResponse(ctx, 204);
    }

    public static void sendLeaderboard(RoutingContext ctx, List<LeaderboardPlayer> leaderboard) {
        JsonObject json = new JsonObject();

        json.put("leaderboard", leaderboard);

        sendOkJsonResponse(ctx, json);
    }

    public static void sendLeaderboardsPlayerOverview(RoutingContext ctx, Map<String, Integer> leaderboardPlayerMap) {
        sendOkJsonResponse(ctx, JsonObject.mapFrom(leaderboardPlayerMap));
    }

    private static void sendOkJsonResponse(RoutingContext ctx, JsonObject response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendEmptyResponse(RoutingContext ctx, int statusCode) {
        ctx.response()
                .setStatusCode(statusCode)
                .end();
    }

    private static void sendJsonResponse(RoutingContext ctx, int statusCode, Object response) {
        ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(Json.encodePrettily(response));
    }

    public static void sendFailure(RoutingContext ctx, int code, String quote) {
        sendJsonResponse(ctx, code, new JsonObject()
                .put("failure", code)
                .put("cause", quote));
    }
}
