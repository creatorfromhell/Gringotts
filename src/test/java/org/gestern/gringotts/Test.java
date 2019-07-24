package org.gestern.gringotts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Test {
    public static void main(String[] args) throws IOException {
        Path test = Paths.get("./", "test.csv");

        if (!Files.exists(test)) {
            Files.write(test, "Time, Value".getBytes(), StandardOpenOption.CREATE);
        }

        run(test);
    }

    public static void run(Path test) {
        new Thread(() -> {
            try {
                Files.write(test, "\n2019-07-16T12:42:10.433Z,4.57222209452977".getBytes(), StandardOpenOption.WRITE);

                Thread.sleep(500);

                run(test);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
