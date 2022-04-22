package ru.internet.sergeevss90.utils;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.assertj.Assertions;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import ru.internet.sergeevss90.utils.jsonutils.JsonToString;
import ru.internet.sergeevss90.utils.jsonutils.Person;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ZipWorker {
    static ClassLoader cl = ZipWorker.class.getClassLoader();
    static boolean flag = false;

    public void downloadCheck(String source, String text) throws Exception {
        open(source);
        File textFile = $("#raw-url").download();
        try (InputStream stream = new FileInputStream(textFile)) {
            byte[] fileContent = stream.readAllBytes();
            String content = new String(fileContent, StandardCharsets.UTF_8);
            Assertions.assertThat(content).contains(text);
        }
    }

    public void initialize(String archiveName, String... names) throws Exception {
        System.out.println("Scanning archive " + archiveName);
        try {
            File file = new File(Objects.requireNonNull(cl.getResource("zip/" + archiveName)).getFile());
            ZipFile zf = new ZipFile(file);
            for (String name : names) {
                if (zf.stream().anyMatch(str -> str.getName().equals(name))) {
                    System.out.println("File " + name + " is found in the archive ");
                } else {
                    System.out.println("File " + name + " is not found in the archive");
                }
            }
            zf.close();
        } catch (NullPointerException e) {
            System.out.println("File " + archiveName + " is not found in the /zip directory\n");
        }
        System.out.println();
    }

    public void pdfChecks(String archiveName, String pdfName, int amount, String text) throws Exception {
        System.out.println("Looking for .pdf file " + pdfName);
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/" + archiveName));
        ZipInputStream is = new ZipInputStream(Objects.requireNonNull(cl.getResourceAsStream("zip/" + archiveName)));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().equals(pdfName)) {
                flag = true;
                System.out.println(".pdf file " + pdfName + " is found. Analizing...");
                try (InputStream stream = zf.getInputStream(entry)) {
                    assert stream != null;
                    PDF pdf = new PDF(stream);
                    if (amount == pdf.numberOfPages) {
                        System.out.println("Total amount of pages is equal to expected");
                    } else {
                        System.out.println("Total amount of pages is not equal to expected");
                    }
                    if (pdf.text.contains(text)) {
                        System.out.println("Desired text is found in the .pdf file\n");
                    } else {
                        System.out.println("Desired text is not found in the .pdf file\n");
                    }
                }
            }
        }
        zf.close();
        if (!flag) {
            System.out.println("File " + pdfName + " is not found in the archive\n");
        } else {
            flag = false;
        }
    }

    public void csvChecks(String archiveName, String csvName, String part1, String part2, String part3) throws Exception {
        System.out.println("Looking for .csv file " + csvName);
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/" + archiveName));
        ZipInputStream is = new ZipInputStream(Objects.requireNonNull(cl.getResourceAsStream("zip/" + archiveName)));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().equals(csvName)) {
                System.out.println(".csv file " + csvName + " is found. Analizing...");
                try (CSVReader reader = new CSVReader(new InputStreamReader(zf.getInputStream(entry), StandardCharsets.UTF_8))) {
                    List<String[]> content = reader.readAll();
                    org.assertj.core.api.Assertions.assertThat(content).contains(
                            new String[]{part1, part2, part3}
                    );
                    System.out.println("Everything is correct\n");
                }
            }
        }
        zf.close();
    }

    public void xlsxChecks(String archiveName, String xlsxName, String data) throws Exception {
        System.out.println("Looking for .xlsx file " + xlsxName);
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/" + archiveName));
        ZipInputStream is = new ZipInputStream(Objects.requireNonNull(cl.getResourceAsStream("zip/" + archiveName)));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().equals(xlsxName)) {
                System.out.println(".xlsx file " + xlsxName + " is found. Analizing...");
                try (InputStream stream = zf.getInputStream(entry)) {
                    assert stream != null;
                    XLS xls = new XLS(stream);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains(data);
                    System.out.println("Everything is correct\n");
                }
            }
        }
        zf.close();
    }

    public void jsonChecks(String jsonName, String firstName, String lastName, String address) {
        Gson gson = new Gson();
        Person person = gson.fromJson(JsonToString.readJsonData("src/test/resources/json/" + jsonName), Person.class);
        System.out.println("Analizing json file " + jsonName);
        org.assertj.core.api.Assertions.assertThat(person.firstName).contains(firstName);
        org.assertj.core.api.Assertions.assertThat(person.lastName).contains(lastName);
        org.assertj.core.api.Assertions.assertThat(person.address.streetAddress).contains(address);
        System.out.println("Everything is correct\n");
    }
}
