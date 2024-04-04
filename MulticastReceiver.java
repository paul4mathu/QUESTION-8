//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.IOException;
import java.net.*;

public class MulticastReceiver extends Thread {
    private static final String MULTICAST_GROUP = "224.0.0.1";
    private static final int PORT = 8888;

    private static int countA = 0;
    private static int countB = 0;

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                socket.joinGroup(group);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received vote: " + received);

                    // Count votes
                    if (received.equals("A")) {
                        countA++;
                    } else if (received.equals("B")) {
                        countB++;
                    }

                    // Display winner
                    determineWinner();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void determineWinner() {
        System.out.println("Vote counting completed.");
        if (countA > countB) {
            System.out.println("Candidate A wins with " + countA + " votes.");
        } else if (countB > countA) {
            System.out.println("Candidate B wins with " + countB + " votes.");
        } else {
            System.out.println("It's a tie.");
        }
    }

    public static void main(String[] args) {
        // Create and start multicast receiver
        MulticastReceiver receiver = new MulticastReceiver();
        receiver.start();
    }
}
