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
            // Set up audio capture
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(format);
            targetLine.start();

            ServerSocket serverSocket = new ServerSocket(Constants.PORT);
            System.out.println("Audio server is running...");

            while (true) {
                // Handle Incoming connections
                Socket clientSocket = serverSocket.accept();
                var client = new ConnectedClient(this, clientSocket);
                clients.add(client);
                client.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closed(ConnectedClient client) {
        clients.remove(client);
    }

    public void sendToAllExceptThis(ConnectedClient client, byte[] data, int bytesRead) {
        try {
            for (ConnectedClient clientB : clients) {
                if (clientB == client) continue; //  Don't send to this client
                var stream = clientB.getStream();
                if (stream == null) continue;
                stream.write(data, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
