package epsilonserver.net;

import epsilonserver.entity.EntityHandler;
import epsilonserver.game.ServerGUI;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

/**
 * SenderThread creates a thread for sending packets to client.
 * Also includes functionality for creating datagram packets.
 *
 * @author mm
 */
public class SenderThread implements Runnable {

    private DatagramSocket socket;
    private EntityHandler eHandler;
    private boolean isRunning;

    // Outgoing packet queue
    private BlockingQueue<DatagramPacket> outgoingPacketQueue;

    /**
     * Constructor
     *
     * @param socket Datagram socket
     * @param outgoingPacketQueue Outgoing packet queue
     * @param eHandler Reference to EntityHandler
     */
    public SenderThread(DatagramSocket socket, BlockingQueue<DatagramPacket> outgoingPacketQueue, EntityHandler eHandler) {
        this.socket = socket;
        this.outgoingPacketQueue = outgoingPacketQueue;
        this.eHandler = eHandler;
    }

    /**
     * Thread that gets a packet from the outgoing packet queue and sends it.
     */
    public void run() {

        isRunning = true;

        ServerGUI.getInstance().setSystemMessage("Sender thread started");

        while (isRunning) {

            try {
                // Get packet from outgoing packet queue
                DatagramPacket packet = outgoingPacketQueue.take();

                // Send packet
                socket.send(packet);
            }
            catch (InterruptedException e) {
                // Queue interrupted while waiting
                // Do nothing
            }
            catch (IOException e) {
                // Cant access socket
                // Do nothing
            }

        }
        
    }

    /**
     * Get a array containing names of all registered players. When the array
     * is iterated a packet is created for every player. The packet contains
     * a game state message that contains names, positions and shot information about every registered
     * player except the player the packet is for, and a hash of the game state
     * message. The purpose of the hash is to make certain that the message 
     * is correct when received.
     * For hashing the SHA algorithm is currently used.
     */
    public void addToSendQueue() {

        // get array of player names
        String[] nameArray = eHandler.getNameArray();

        for (int i = 0; i < nameArray.length; i++) {

            // String added to packet
            String sendString = "";

            // Game state message
            String gameStateString = "";

            // Get game state message
            gameStateString = eHandler.getGameStateString(nameArray[i]);

            try {
                // create a hash of the game state message using SHA algorithm
                MessageDigest hash = MessageDigest.getInstance("SHA");
                byte[] hashSum = hash.digest(gameStateString.getBytes());

                // Create a hexadecimal representation of the hash
                StringBuilder hexString = new StringBuilder();
                for (int j = 0; j < hashSum.length; j++) {
                    hexString.append(Integer.toHexString(0xFF & hashSum[j]));
                }

                // Add hash to end of game state string
                String hashString = hexString.toString();
                sendString = gameStateString + hashString;
            }
            catch (NoSuchAlgorithmException e) {
                ServerGUI.getInstance().setErrorMessage("Could not find hashing algorithm in sender thread");
            }

            // Set data buffer size to lengt of string in bytes
            int bufSize = sendString.length() * 2;
            byte[] buf = new byte[bufSize];

            // Convert final game state message to bytes
            buf = sendString.getBytes();

            // Get players IP address
            InetAddress ip = eHandler.getAddressByName(nameArray[i]);

            // Create datagram packet
            DatagramPacket outgoingPacket =
                    new DatagramPacket(buf, buf.length, ip, NetworkHandler.CLIENT_PORT);

            try {
                // Add packet to outgoing packet queue
                outgoingPacketQueue.put(outgoingPacket);
            }
            catch (InterruptedException e) {
                // Queue interrupted while waiting
                // Do nothing
            }
            
        }
    }

    /**
     * Stop running sender thread
     */
    public void stopSender() {
        isRunning = false;
    }

}
