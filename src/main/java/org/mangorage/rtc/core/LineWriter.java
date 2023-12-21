package org.mangorage.rtc.core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.function.BiConsumer;

public class LineWriter {
    private final BiConsumer<byte[], Integer> playback;
    public LineWriter() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(format);
            sourceLine.start();
            playback = (b, i) -> {
                sourceLine.write(b, 0, i);
            };
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] bytes, int bytesRead) {
        playback.accept(bytes, bytesRead);
    }
}
