package services;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.RandomAccessFile;

@Path("/print")
public class PrinterService {

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response printerAvailable(){
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String printBestellung(String bestellung) {
        /*
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
         */

        bestellung = bestellung+ "\n\n\n\n"
                + GS + "V1\n"
                + FS + "p" + SOH + "0";

        try {
            serialPortFile.writeUTF(bestellung);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "printing ... = " + bestellung;
    }
}
