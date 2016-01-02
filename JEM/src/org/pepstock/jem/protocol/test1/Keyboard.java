package org.pepstock.jem.protocol.test1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Keyboard {
    static BufferedReader buffer;
    static {
        InputStreamReader bis = new InputStreamReader(System.in);
        buffer = new BufferedReader(bis);
    }

    public static String readline() throws IOException {
        return buffer.readLine();
    }
}
