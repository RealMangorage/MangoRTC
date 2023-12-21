package org.mangorage.rtc;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            String cmd = args[0];
            String value = args[1];

            if (cmd.equals("-client") && value.contains(":")) {
                String[] result = value.split(":");
                new AudioClient(result[0], Integer.parseInt(result[1]));
            }
            if (cmd.equals("-server")) {
                // TODO
            }
        }
    }
}
