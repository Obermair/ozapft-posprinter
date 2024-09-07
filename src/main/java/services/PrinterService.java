package services;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

@Path("/print")
public class PrinterService {

    private static String serialPortPattern = "/dev/ttyUSB";
    private static RandomAccessFile serialPortFile;
    private static char GS = (char) 29; // Cutter
    private static char FS = (char) 28; //
    private static char SOH = (char) 2;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response printerAvailable() {
        // Check if we can find an available serial port
        if (getAvailableSerialPort() != null) {
            return Response.ok().entity("Printer available").build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Printer not available").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String printBestellung(String bestellung) {
        bestellung = bestellung + "\n\n\n\n"
                + GS + "V1\n"
                + FS + "p" + SOH + "0";

        try {
            // Attempt to open the serial port
            String port = getAvailableSerialPort();
            if (port != null) {
                serialPortFile = new RandomAccessFile(port, "rw");
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Printer not available").build();
            }
            
            serialPortFile.writeUTF(bestellung);
            //serialPortFile.getFD().sync(); Ensure data is written out before closing
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "printing ... = " + bestellung;
    }

    private String handleSerialPortReconnection(String bestellung) {
        try {
            if (serialPortFile != null) {
                serialPortFile.close();
            }
            // Try to reconnect to the available USB port
            String port = getAvailableSerialPort();
            if (port != null) {
                serialPortFile = new RandomAccessFile(port, "rw");
                serialPortFile.writeUTF(bestellung);
                return "Reconnected and printing ... = " + bestellung;
            } else {
                return "Error: No available printer port after reconnection attempt.";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Error: Unable to reconnect to printer.";
        }
    }

    private String getAvailableSerialPort() {
        // List all files matching /dev/ttyUSB*
        File devDir = new File("/dev");
        File[] usbPorts = devDir.listFiles((dir, name) -> name.startsWith("ttyUSB"));

        if (usbPorts != null && usbPorts.length > 0) {
            // Sort by the USB number to always use the first available port (e.g., ttyUSB0 before ttyUSB1)
            Arrays.sort(usbPorts, Comparator.comparing(File::getName));

            // Return the first available port
            return usbPorts[0].getAbsolutePath();
        }

        // No available USB serial port found
        return null;
    }
}
