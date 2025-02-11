package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable, IObserver {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ChatServer server;
    private String message;
    private String username = "";

    public ClientHandler(Socket client, ChatServer server) throws IOException {
        this.client = client;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            out.println("Hello client, type your name: ");
            username = in.readLine();

            if (username == null || username.trim().isEmpty()) {
                out.println("Invalid username. Disconnecting...");
                client.close();
                return;
            } else {
                server.broadcast("New client [" + username + "] joined the chat. [" + client.getRemoteSocketAddress()+ "]");
            }

            //TODO Dato, chatsymbol, whisper

            while ((message = in.readLine()) != null) {
                if (message.startsWith("#LEAVE")) {
                    server.broadcast("Client [" + username + "] left the chat");
                    break;

                } else if (message.startsWith("#MESSAGE")) {
                    String chatMessage = message.substring(9).trim();
                    if (!chatMessage.isEmpty()) {
                        server.broadcast("[" + username + "]: " + chatMessage);
                    }
                } else if (message.startsWith("#CONN")) {
                    StringBuilder clientList = new StringBuilder("Connected clients: ");
                    for (IObserver c : server.getClients()) {
                        if (c instanceof ClientHandler) {
                            ClientHandler clientHandler = (ClientHandler) c;
                            clientList.append("[").append(clientHandler.getUsername()).append("] ");
                        }
                    }
                    server.broadcast(clientList.toString());
                } else {
                    server.broadcast("[" + username + "]: " + message);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.getClients().remove(this);
        }
    }

    @Override
    public void notify(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}