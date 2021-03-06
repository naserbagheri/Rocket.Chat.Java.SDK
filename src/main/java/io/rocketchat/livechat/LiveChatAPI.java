package io.rocketchat.livechat;

import com.neovisionaries.ws.client.*;
import io.rocketchat.EventThread;
import io.rocketchat.Socket;
import io.rocketchat.Utils;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.callbacks.InitialDataCallback;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.rpc.LiveChatBasicRPC;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatAPI extends Socket{

    AtomicInteger integer;
    String sessionId;
    WebSocketListener listener;
    LiveChatMiddleware middleware;

    public LiveChatAPI(String url) {
        super(url);
        listener=getListener();
        integer=new AtomicInteger(1);
        middleware=LiveChatMiddleware.getInstance();
    }

    public void getInitialData(final InitialDataCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                middleware.createCallback(uniqueID,callback);
                ws.sendText(LiveChatBasicRPC.getInitialData(uniqueID));
            }
        });
    }

    public void registerGuest(final String name, final String email, final String dept, final GuestCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                middleware.createCallback(uniqueID,callback);
                ws.sendText(LiveChatBasicRPC.registerGuest(uniqueID,name,email,dept));
            }
        });
    }

    public void login(final String token, final GuestCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                middleware.createCallback(uniqueID,callback);
                ws.sendText(LiveChatBasicRPC.login(uniqueID,token));
            }
        });
    }

    @Override
    public void connect() throws IOException {
        createWebsocketfactory();
        ws.addListener(listener);
        super.connect();
    }

    WebSocketListener getListener() {
        return new WebSocketListener() {
            public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
//                System.out.println("on state changed");
            }

            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                integer.set(1);
                websocket.sendText(LiveChatBasicRPC.ConnectObject());
                System.out.println("Connected to server");
            }

            public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("got connect error");
            }

            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                System.out.println("Disconnected to server");
            }

            public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("Got frame");
            }

            public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on continuation frame");
            }

            public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                //   System.out.println("On text frame");
            }

            public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on binary frame");
            }

            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On close frame");
            }

            public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On ping frame" + frame.getPayloadText());
            }

            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on pong frame");
            }

            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                System.out.println("Message is " + text);

                JSONObject object = new JSONObject(text);
                if (object.optString("msg").equals("ping")) {
                    websocket.sendText("{\"msg\":\"pong\"}");
                } else if (object.optString("msg").equals("connected")) {
                    sessionId = object.optString("session");
                } else if (Utils.isInteger(object.optString("id"))) {
                    middleware.processCallback(Long.valueOf(object.optString("id")), object);
                }
            }

            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                System.out.println("on binary message");
            }

            public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on sending frame");
            }

            public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on frame set "+frame.getPayloadText());
            }

            public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on frame unsent");
            }

            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("On error");
            }

            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("On frame error");
            }

            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                System.out.println("On message error");
            }

            public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
                System.out.println("on message decompression error");
            }

            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                System.out.println("on text message error");
            }

            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("on send error");
            }

            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("on unexpected error");
            }

            public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
                System.out.println("handle callback error");
            }

            public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
//                System.out.println("On sending handshake");
            }
        };


    }
}
