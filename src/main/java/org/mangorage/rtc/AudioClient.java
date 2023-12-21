package org.mangorage.rtc;

import org.mangorage.rtc.core.Constants;
import org.mangorage.rtc.core.LineListener;
import org.mangorage.rtc.core.LineWriter;

import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AudioClient extends Thread {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final LineListener lineListener;
    private final LineWriter lineWriter;

    public AudioClient(String IP, int port) {
        try {
            Socket socket = new Socket(IP, port);
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.lineWriter = new LineWriter();
            this.lineListener = new LineListener((b, i) -> {
                try {
                    outputStream.write(b, 0, i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, Constants.BUFFER_SIZE);

            lineListener.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[Constants.BUFFER_SIZE];
        while (true) {
            try {
                int bytesReceived = inputStream.read(buffer);
                lineWriter.write(buffer, bytesReceived);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new AudioClient("10.0.0.170", Constants.PORT).start();
    }
}
