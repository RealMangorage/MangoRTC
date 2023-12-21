package org.mangorage.rtc;

import org.mangorage.rtc.core.ConnectedClient;
import org.mangorage.rtc.core.Constants;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class AudioServer {

    public static void main(String[] args) {
        new AudioServer();
    }

    private final CopyOnWriteArrayList<ConnectedClient> clients = new CopyOnWriteArrayList<>();

    public AudioServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(Constants.PORT);
            System.out.println("Audio server is running...");

            while (true) {
                // Handle Incoming connections
                Socket clientSocket = serverSocket.accept();
                var client = new ConnectedClient(this, clientSocket);
                clients.add(client);
                System.out.println("Client Connected");
                client.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closed(ConnectedClient client) {
        clients.remove(client);
        client.disconnect();
        System.out.println("Client Disconnected");
    }

    public void sendToAllExceptThis(ConnectedClient client, byte[] data, int bytesRead) {
        for (ConnectedClient clientB : clients) {
            //if (clientB == client) continue; //  Don't send to this client
            var stream = clientB.getStream();
            if (stream == null) continue;
            try {
                stream.write(data, 0, bytesRead);
            } catch (IOException e) {
                closed(clientB);
            }
        }
    }
}
