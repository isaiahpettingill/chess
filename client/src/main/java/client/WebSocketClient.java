package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class WebSocketClient {   
    private final WebSocket ws;
    private static final HttpClient HTTP = HttpClient.newHttpClient();;

    public WebSocketClient(String uri, Consumer<String> onMessage){
        this.ws = HTTP.newWebSocketBuilder()
            .buildAsync(URI.create(uri), new Listener(){
                StringBuilder buffer = new StringBuilder();
                public void onOpen(WebSocket w) {
                    w.request(1);
                }

                public CompletionStage<?> onText(WebSocket w, CharSequence data, boolean last) {
                    buffer.append(data);
                    if (last) {
                        onMessage.accept(buffer.toString());
                        buffer.setLength(0);
                    }
                    w.request(1);
                    return null;
                }
            }).join();
    }

    public void send(String message) {
        ws.sendText(message, true);
    }

    public void close(){
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "Disconnecting")
            .thenRun(HTTP::close);
    }
}
