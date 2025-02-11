package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements IObservable {
    private List<IObserver> clients = new ArrayList<>();
    //private static volatile ChatServer instance;

    /*
    public static ChatServer getInstance() {
        if (instance == null || ChatServer.instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }

     */

    public void startServer(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Starting server on port " + port);
            while (true) {
                Socket client = server.accept();
                Runnable runnable = new ClientHandler(client, this);
                new Thread(runnable).start();
                IObserver clientHandler = (IObserver) runnable;
                clients.add(clientHandler);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void broadcast(String message) {
        for (IObserver observer : clients) {
            observer.notify(message);
        }
    }

    public List<IObserver> getClients() {
        return clients;
    }
}

