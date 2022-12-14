package de.base2code.clansystem.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TransferCliClient {
    public static final String TRANSFER_URL = "https://transfer.base2code.dev/";

    public static URL postToTransfer(String contentString) {
        try {
            File tempFile = File.createTempFile("log", null);

            ArrayList<String> content = new ArrayList<>(Arrays.asList(contentString.split("\n")));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                for (String s : content) {
                    bw.write(s);
                    bw.write(System.lineSeparator()); // new line
                }
            }

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("curl --upload-file " + tempFile.getAbsolutePath() + " " + TRANSFER_URL + "log.txt");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String responseURL = "";

// Read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                responseURL = s;
            }

// Read any errors from the attempted command

            responseURL = responseURL.replace(TRANSFER_URL.toString(), TRANSFER_URL + "get/");

            return new URL(responseURL);
        } catch (Exception e) {
            try {
                return new URL(TRANSFER_URL);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
