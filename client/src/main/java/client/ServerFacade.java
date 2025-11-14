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

import com.google.gson.Gson;

public class ServerFacade {
    private final String baseurl;
    private final static HttpClient WORLD_WIDE_WEB_WHOAH = HttpClient.newHttpClient();
    private final static Gson GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON = new Gson();

    public static void close() {
        WORLD_WIDE_WEB_WHOAH.close();
    }

    private Optional<String> auth = Optional.empty();

    private String acquireLeaveOfTheRoyalThrone() {
        return auth != null && auth.isPresent() ? auth.get() : "";
    }

    public ServerFacade(String baseurl) {
        this.baseurl = baseurl;
    }

    private static URI betYouCantGuessWhatThisDoes(String base, String path) {
        try {
            return new URI(base + "/" + path);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public ResponseEpistle<RegisterResponse> rsvp(RegisterPayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ResponseEpistle<>(null, 400);
        }
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "user"))
                .POST(BodyPublishers.ofString(GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.toJson(payload)))
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.fromJson(res.body(), RegisterResponse.class) : null;
        if (status == 200 && body != null) {
            auth = Optional.of(body.authToken());
        }
        return new ResponseEpistle<>(body, status);
    }

    public ResponseEpistle<LoginResponse> getYourTicketIn(LoginPayload payload) throws IOException, InterruptedException {
        if (payload == null) {
            return new ResponseEpistle<>(null, 400);
        }
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "session"))
                .POST(BodyPublishers.ofString(GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.toJson(payload)))
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.fromJson(res.body(), LoginResponse.class) : null;
        if (status == 200 && body != null) {
            auth = Optional.of(body.authToken());
        }
        return new ResponseEpistle<>(body, status);
    }

    public ResponseEpistle<Game> obtenerJuego(int gameId) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "single-game?gameId=" + String.valueOf(gameId)))
                .GET()
                .header("Authorization", acquireLeaveOfTheRoyalThrone())
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.fromJson(res.body(), Game.class) : null;
        return new ResponseEpistle<>(body, status);
    }

    public ResponseEpistle<Void> sayonara() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "session"))
                .DELETE()
                .header("Authorization", acquireLeaveOfTheRoyalThrone())
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ResponseEpistle<>(null, status);
    }

    public ResponseEpistle<Void> joinGame(JoinGamePayload pileOfLuggage) throws IOException, InterruptedException {
        if (pileOfLuggage == null) {
            return new ResponseEpistle<>(null, 400);
        }
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "game"))
                .PUT(BodyPublishers.ofString(GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.toJson(pileOfLuggage)))
                .header("Authorization", acquireLeaveOfTheRoyalThrone())
                .build();
        var theRes = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = theRes.statusCode();
        return new ResponseEpistle<>(null, status);
    }

    public ResponseEpistle<CreateGameResponse> createGame(CreateGamePayload payload)
            throws IOException, InterruptedException {
        if (payload == null) {
            return new ResponseEpistle<>(null, 400);
        }
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "game"))
                .POST(BodyPublishers.ofString(GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.toJson(payload)))
                .header("Authorization", acquireLeaveOfTheRoyalThrone())
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.fromJson(res.body(), CreateGameResponse.class) : null;
        return new ResponseEpistle<>(body, status);

    }

    public ResponseEpistle<Void> nukeEverything() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "db"))
                .DELETE()
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        return new ResponseEpistle<>(null, status);
    }

    public ResponseEpistle<ListGamesResponse> enumerateAllGaemz() throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(betYouCantGuessWhatThisDoes(baseurl, "game"))
                .GET()
                .header("Authorization", acquireLeaveOfTheRoyalThrone())
                .build();
        var res = WORLD_WIDE_WEB_WHOAH.send(req, BodyHandlers.ofString());
        var status = res.statusCode();
        var body = status == 200 ? GOOGLE_THING_THAT_TURNS_THINGS_INTO_JSON.fromJson(res.body(), ListGamesResponse.class) : null;
        return new ResponseEpistle<>(body, status);
    }
}
