import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient {
    private static final int TIMEOUT = 1000; // milliseconds
    private static final int NUM_PINGS = 10;
    private static final int PACKET_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.out.println("Usage: java PingClient <hostname> <port>");
            return;
        }

        // Get the hostname and port number from command line arguments
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // Create a datagram socket for sending and receiving UDP packets
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT); // Set the timeout for receiving packets

        // Send pings
        for (int i = 0; i < NUM_PINGS; i++) {
            // Create message for ping
            String message = "PING " + i + " " + System.currentTimeMillis() + "\r\n";
            byte[] sendData = message.getBytes();

            // Create datagram packet
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(hostname), port);

            // Send the packet
            socket.send(sendPacket);

            try {
                // Prepare to receive the response from the server
                DatagramPacket receivePacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);

                // Receive the response from the server
                socket.receive(receivePacket);

                // Get the received data and print
                byte[] receivedData = receivePacket.getData();
                int length = receivePacket.getLength();
                String receivedMessage = new String(receivedData, 0, length);
                System.out.println("Received from " + receivePacket.getAddress().getHostAddress() + ": " + receivedMessage.trim());

            } catch (SocketTimeoutException e) {
                // Timeout occurred, ping lost
                System.out.println("Ping " + i + " lost: Request timed out.");
            }

            // Wait for some time before sending the next ping
            Thread.sleep(1000);
        }

        // Close the socket
        socket.close();
    }
}
