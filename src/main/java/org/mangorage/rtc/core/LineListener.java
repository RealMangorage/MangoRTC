package org.mangorage.rtc.core;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.function.BiConsumer;

public final class LineListener extends Thread {
    private final BiConsumer<byte[], Integer> playback; // byte array / Bytes read
    private final byte[] buffer;

    public LineListener(BiConsumer<byte[], Integer> playback, int bufferSize) {
        this.playback = playback;
        this.buffer = new byte[bufferSize];
    }

    @Override
    public void run() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, Constants.DEFAULT_FORMAT);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(Constants.DEFAULT_FORMAT);
            targetLine.start();

            while (true) {
                int bytesRead = targetLine.read(buffer, 0, buffer.length);
                playback.accept(buffer, bytesRead);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
