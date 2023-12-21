package org.mangorage.rtc.core;

import org.mangorage.rtc.AudioServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectedClient extends Thread {
    private final AudioServer server;
    private final Socket clientSocket;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    public ConnectedClient(AudioServer server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.inputStream = new DataInputStream(clientSocket.getInputStream());
    }

    public DataOutputStream getStream() {
        if (isAlive()) return outputStream;
        return null;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (clientSocket.isConnected() || !clientSocket.isClosed()) {
            try {
                int read = inputStream.read(buffer);
                server.sendToAllExceptThis(this, buffer, read);
            } catch (IOException e) {
                try {
                    clientSocket.close();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        System.out.println("Client Closed");
        server.closed(this);
    }
}
