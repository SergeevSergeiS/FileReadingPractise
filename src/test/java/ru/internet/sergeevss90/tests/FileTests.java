package ru.internet.sergeevss90.tests;

import org.junit.jupiter.api.Test;
import ru.internet.sergeevss90.utils.ZipWorker;

public class FileTests {
    String archiveName = "archive.zip";
    String txtName = "text.txt";
    String xlsxName = "extendedCells.xlsx";
    String csvName = "commaSeparatedValues.csv";
    String pdfName = "portableDocumentFormat.pdf";
    String missedName = "notExist.completly";
    String jsonName = "Potter.json";
    ZipWorker zw = new ZipWorker();

    @Test
    void testDownloadFile() throws Exception {
        zw.downloadCheck("https://github.com/junit-team/junit5/blob/main/README.md", "JUnit 5");
    }

    @Test
    void compositionTest() throws Exception {
        zw.initialize(archiveName, txtName, xlsxName, csvName, pdfName, missedName);
    }

    @Test
    void pdfChecks() throws Exception {
        zw.pdfChecks(archiveName, pdfName, 166, "blackfriday");
    }

    @Test
    void csvChecks() throws Exception {
        zw.csvChecks(archiveName, csvName, "ab", "cd", "ef");
    }

    @Test
    void xlsxChecks() throws Exception {
        zw.xlsxChecks(archiveName, xlsxName, "a");
    }

    @Test
    void jsonChecks() {
        zw.jsonChecks(jsonName, "Harry", "Potter", "Privet Drive");
    }
}
