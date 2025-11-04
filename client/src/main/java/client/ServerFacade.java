package client;

import dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import com.google.gson.Gson;

public class ServerFacade {
    private final String baseurl;
    final static HttpClient http = HttpClient.newHttpClient();
    final static Gson gson = new Gson();

    public static void close() {
        http.close();
    }

    private Optional<String> auth = Optional.empty();

    private String getAuth() {
        return auth != null && auth.isPresent() ? auth.get() : "";
    }

    public ServerFacade(String baseurl) {
        this.baseurl = baseurl;
    }

    public boolean loggedIn() {
        return auth.isPresent();
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
                .POST(BodyPublishers.ofString(gson.toJson(payload)))
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? gson.fromJson(res.body(), RegisterResponse.class) : null;
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
                .POST(BodyPublishers.ofString(gson.toJson(payload)))
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? gson.fromJson(res.body(), LoginResponse.class) : null;
        if (status == 200 && body != null) {
            auth = Optional.of(body.authToken());
        }
        return new ServerResponse<>(body, status);
    }

    public ServerResponse<Void> logout() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "session"))
                .DELETE()
                .header("Authorization", getAuth())
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<Void> joinGame(JoinGamePayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .PUT(BodyPublishers.ofString(gson.toJson(payload)))
                .header("Authorization", getAuth())
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<CreateGameResponse> createGame(CreateGamePayload payload)
            throws IOException, InterruptedException {
        if (payload == null) {
            return new ServerResponse<>(null, 400);
        }
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .POST(BodyPublishers.ofString(gson.toJson(payload)))
                .header("Authorization", getAuth())
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? gson.fromJson(res.body(), CreateGameResponse.class) : null;
        return new ServerResponse<>(body, status);

    }

    public ServerResponse<Void> clearDb() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "db"))
                .DELETE()
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ServerResponse<>(null, status);
    }

    public ServerResponse<ListGamesResponse> listGames() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "game"))
                .GET()
                .header("Authorization", getAuth())
                .build();
        var res = http.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? gson.fromJson(res.body(), ListGamesResponse.class) : null;
        return new ServerResponse<>(body, status);
    }
}
