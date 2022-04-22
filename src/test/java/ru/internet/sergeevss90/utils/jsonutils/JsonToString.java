package ru.internet.sergeevss90.utils.jsonutils;

import java.io.*;

public class JsonToString {
    public static String readJsonData(String pactFile) {
        StringBuilder strbuilder = new StringBuilder();
        File myFile = new File(pactFile);
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(inputStreamReader);
            String str;
            while ((str = in.readLine()) != null) {
                strbuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return strbuilder.toString();
    }
}
