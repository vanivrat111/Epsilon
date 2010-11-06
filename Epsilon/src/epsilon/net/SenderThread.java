package epsilon.net;

import epsilon.game.Game;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

/**
 * SenderThread creates a thread that gets the players position and sends it to the server
 * @author mm
 */
public class SenderThread implements Runnable {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private String clientName;
    private Game game;

    private boolean isRunning = true;

    private BlockingQueue<DatagramPacket> outgoingPacketQueue;

    /**
     * Constructor
     * @param socket
     * @param serverAddress
     * @param name
     */
    public SenderThread(DatagramSocket socket, InetAddress serverAddress, 
            String name, BlockingQueue<DatagramPacket> outgoingPacketQueue) {
        this.socket = socket;
        this.serverAddress = serverAddress;
        this.clientName = name;
        game = Game.get();
        this.outgoingPacketQueue = outgoingPacketQueue;
    }

    /**
     * Thread for creating a packet with player position information and
     * sending the information to the server
     */
    public void run() {
        System.out.println("Sender thread started");
        while (isRunning) {

            try {
                DatagramPacket packet = outgoingPacketQueue.take();
                socket.send(packet);
            }
            catch (IOException ioe) {
                System.out.println("Could not get packet from outgoing packet queue");
            }
            catch (InterruptedException ie) {
                System.out.println("Problem accessing socket");
            }

        }

    }

    /**
     * Add packets to ougoing packet queue
     */
    public void addToSendQueue() {

        byte[] buf = new byte[NetworkHandler.BUFFER_SIZE];
        double[] posArray = game.getPlayerPosition();

        String playerPosString = clientName + " " + posArray[0] + " " + posArray[1];
        String sendString = "";

        if (!playerPosString.isEmpty()) {
            //System.out.println("\n" + "Sending string: " + playerPosString + "\n");
        }

        try {
            MessageDigest hash = MessageDigest.getInstance("SHA");
            byte[] hashSum = hash.digest(playerPosString.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hashSum.length; i++) {
                hexString.append(Integer.toHexString(0xFF & hashSum[i]));
            }

            String hashString = hexString.toString();
            sendString = playerPosString + " " + hashString;

        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Could not hash outgoing message");
        }

        buf = sendString.getBytes();

        DatagramPacket outgoingPacket =
                new DatagramPacket(buf, buf.length, serverAddress, NetworkHandler.SERVER_PORT);

        try {
            outgoingPacketQueue.put(outgoingPacket);
        }
        catch (InterruptedException e) {
            System.out.println("Could not add packet to outgoing packet queue");
        }
        
    }

    /**
     * Stop sender thread
     */
    public void stopSender() {
       isRunning = false;
    }

}
