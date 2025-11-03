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

public class BackendClient {
    private final String baseurl;
    final static HttpClient http = HttpClient.newHttpClient();
    final static Gson gson = new Gson();

    private Optional<String> auth;
    private String getAuth(){
        return auth.isPresent() ? auth.get() : "";
    }


    public BackendClient(String baseurl) {
        this.baseurl = baseurl;
    }

    private static URI makeUri(String base, String path){
        try {
            return new URI(base + path);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ServerResponse<RegisterResponse> register(RegisterPayload payload) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(makeUri(baseurl, "user"))
                .POST(BodyPublishers.ofString(gson.toJson(payload)))
                .header("Authorization", getAuth())
                .build();
    }

    public ServerResponse<LoginResponse> login(LoginPayload payload) throws IOException, InterruptedException {
    
    }

    public ServerResponse<Void> logout() throws IOException, InterruptedException {
        auth = Optional.empty();
    }

    public ServerResponse<Void> joinGame(JoinGamePayload payload) throws IOException, InterruptedException {

    }

    public ServerResponse<CreateGameResponse> createGame(CreateGamePayload payload) throws IOException, InterruptedException {

    }

    public ServerResponse<Void> clearDb() throws IOException, InterruptedException {

    }

    public ServerResponse<ListGamesResponse> listGames() throws IOException, InterruptedException {

    }
}
