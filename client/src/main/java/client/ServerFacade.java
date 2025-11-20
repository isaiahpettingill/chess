package client;

import dto.*;
import models.Game;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gson.Gson;

public class ServerFacade {
    private final String baseurl;
    private final static HttpClient HTTP = HttpClient.newHttpClient();
    private final static Gson GSON = new Gson();

    public static void close() {
        HTTP.close();
    }

    private Optional<String> auth = Optional.empty();

    public String getAuth() {
        return auth != null && auth.isPresent() ? auth.get() : "";
    }

    public ServerFacade(String baseurl) {
        this.baseurl = baseurl;
    }

    private static URI makeUri(String base, String path) {
        try {
            return new URI(base + "/" + path);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ServerResponse<RegisterResponse> register(RegisterPayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "user"))
                .POST(BodyPublishers.ofString(GSON.toJson(payload)))
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GSON.fromJson(res.body(), RegisterResponse.class) : null;
        if (status == 200 && body != null) {
            auth = Optional.of(body.authToken());
        }
        return new ServerResponse<>(body, status);
    }

    public ServerResponse<LoginResponse> login(LoginPayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "session"))
                .POST(BodyPublishers.ofString(GSON.toJson(payload)))
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GSON.fromJson(res.body(), LoginResponse.class) : null;
        if (status == 200 && body != null) {
            auth = Optional.of(body.authToken());
        }
        return new ServerResponse<>(body, status);
    }

    public ServerResponse<Game> getGame(int gameId) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "single-game?gameId=" + String.valueOf(gameId)))
                .GET()
                .header("Authorization", getAuth())
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GSON.fromJson(res.body(), Game.class) : null;
        return new ServerResponse<>(body, status);
    }

    public ServerResponse<Void> logout() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "session"))
                .DELETE()
                .header("Authorization", getAuth())
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<Void> joinGame(JoinGamePayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .PUT(BodyPublishers.ofString(GSON.toJson(payload)))
                .header("Authorization", getAuth())
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<CreateGameResponse> createGame(CreateGamePayload payload)
            throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .POST(BodyPublishers.ofString(GSON.toJson(payload)))
                .header("Authorization", getAuth())
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GSON.fromJson(res.body(), CreateGameResponse.class) : null;
        return new ServerResponse<>(body, status);

    }

    public ServerResponse<Void> clearDb() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "db"))
                .DELETE()
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<ListGamesResponse> listGames() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .GET()
                .header("Authorization", getAuth())
                .build();
        var res = HTTP.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GSON.fromJson(res.body(), ListGamesResponse.class) : null;
        return new ServerResponse<>(body, status);
    }


    public WebSocketClient webSocketClient(Consumer<String> onMessage) {
        return new WebSocketClient(baseurl.replace("http", "ws") + "/ws", onMessage);
    }
}
