package pdfboxutility.pdfboxutility_demo;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Added code to check the revert functionality first time-
public class PdfBox {
    private static Logger logger = LoggerFactory.getLogger(PdfBox.class);

    private static void executeUtility(String inputFilePath, String outputFileName, String pdfName) throws IOException {

        logger.info("Executing the PDFBox utility");
        PrintWriter writer = new PrintWriter(new FileWriter(outputFileName));

        try (PDDocument document = PDDocument.load(new File(inputFilePath))) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            int count = document.getNumberOfPages();

            String text = "";
            Set<String> processedumid = new HashSet<String>();
            Pattern pattern = Pattern.compile("<~!(.*?)!~>", Pattern.DOTALL);

            for (int i = 1; i <= count; i++) {
                pdfStripper.setStartPage(i);
                pdfStripper.setEndPage(i);

                String pageText = pdfStripper.getText(document);
                Matcher matcher = pattern.matcher(pageText);

                while (matcher.find()) {
                    String umid = matcher.group(1).trim();
                    if (!processedumid.contains(umid)) {
                        if (i != 1) {
                            writer.println("<TotalPages>" + (i - 1) + "</TotalPages>");
                            writer.println("<PDF>" + pdfName + "</PDF>");
                            writer.println("</MailSet>");
                        }
                        writer.println("<MailSet>");
                        writer.println("<EnvelopeId>" + umid + "</EnvelopeId>");
                        writer.println("<StartPage>" + i + "</StartPage>");

                        processedumid.add(umid);
                    }
                }
                if (i == count) {
                    writer.println("<TotalPages>" + i + "</TotalPages>");
                    writer.println("<PDF>+" + pdfName + "</PDF>");
                    writer.println("</MailSet>");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws IOException {

        executeUtility("src//main//resources//eligic.pdf", "ELIGIC.xml", "UMDST_ELIGIC001_12012025_120000");
    }
}

