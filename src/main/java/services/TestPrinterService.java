package services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.RandomAccessFile;

@Path("/hello")
public class TestPrinterService {

    private static String serialPort = "/dev/ttyUSB0";
    private static char GS = (char) 29; // Cutter
    private static char FS = (char) 28; //
    private static char SOH = (char) 2;
    private static RandomAccessFile serialPortFile;

    static {
        try {
            serialPortFile = new RandomAccessFile(serialPort, "rw");
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        String toWrite = "TISCH 1\n"
                + "-------------------------------------\n"
                + "1 x Bier                          3,0\n"
                + "2 x Spritzer                      5,0\n"
                + "-------------------------------------\n"
                + "Summe                             8,0\n"
                + "=====================================\n"
                + "\n\n\n\n"
                + GS + "V1\n"
                + FS + "p" + SOH + "0";


        try {
            serialPortFile.writeUTF(toWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "printing ...?" + toWrite;
    }
}
