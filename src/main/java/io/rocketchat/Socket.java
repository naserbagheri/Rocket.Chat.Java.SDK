package io.rocketchat;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by sachin on 7/6/17.
 */

public class Socket {

    String url;
    WebSocketFactory factory;
    protected WebSocket ws;

    protected Socket(String url){
        this.url=url;
    }

    /**
     * Function for connecting to server
     * @throws IOException
     */

    protected void createWebsocketfactory() throws IOException {
        factory = new WebSocketFactory();
        // Create a WebSocket with a socket connection timeout value.
        ws = factory.createSocket(url);
    }

    public void connect() throws IOException {
        try
        {
            // Connect to the server and perform an opening handshake.
            // This method blocks until the opening handshake is finished.
            ws.connect();
        }
        catch (OpeningHandshakeException e)
        {
            // A violation against the WebSocket protocol was detected
            // during the opening handshake.
            StatusLine sl = e.getStatusLine();
            System.out.println("=== Status Line ===");
            System.out.format("HTTP Version  = %s\n", sl.getHttpVersion());
            System.out.format("Status Code   = %d\n", sl.getStatusCode());
            System.out.format("Reason Phrase = %s\n", sl.getReasonPhrase());

            // HTTP headers.
            Map<String, List<String>> headers = e.getHeaders();
            System.out.println("=== HTTP Headers ===");
            for (Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                // Header name.
                String name = entry.getKey();

                // Values of the header.
                List<String> values = entry.getValue();

                if (values == null || values.size() == 0)
                {
                    // Print the name only.
                    System.out.println(name);
                    continue;
                }

                for (String value : values)
                {
                    // Print the name and the value.
                    System.out.format("%s: %s\n", name, value);
                }
            }
        }
        catch (WebSocketException e)
        {
            System.out.println("Got websocket exception "+e.getMessage());
            // Failed to establish a WebSocket connection.
        }
    }

}
