package client;
import dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Optional;

import com.google.gson.Gson;

public class BackendClient {
    private final String baseurl;
    final static HttpClient http = HttpClient.newHttpClient();
    final static Gson gson = new Gson();

    private Optional<String> authentication;

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

    public ServerResponse<RegisterResponse> register(RegisterPayload payload){
        var req = HttpRequest.newBuilder(makeUri(baseurl, "user"))
                .POST(BodyPublishers.ofString(gson.toJson(payload)))
                .build();
        
    }

    public ServerResponse<LoginResponse> login(LoginPayload payload){
    
    }

    public ServerResponse<Void> logout(){

    }

    public ServerResponse<Void> joinGame(JoinGamePayload payload){

    }

    public ServerResponse<CreateGameResponse> createGame(CreateGamePayload payload){

    }

    public ServerResponse<Void> clearDb(){

    }

    public ServerResponse<ListGamesResponse> listGames(){

    }
}
