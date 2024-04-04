//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MulticastSender extends Thread {
    private static final String MULTICAST_GROUP = "224.0.0.1";
    private static final int PORT = 8888;

    private char vote;

    public MulticastSender(char vote) {
        this.vote = vote;
    }

    @Override
    public void run() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                socket.joinGroup(group);
                byte[] data = String.valueOf(vote).getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
                socket.send(packet);
                System.out.println("Your vote (" + vote + ") was successfully sent.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char[] votes = new char[5];

        // Prompt the user to enter votes
        for (int i = 0; i < 5; i++) {
            System.out.print("Enter vote for electorate " + (i+1) + " (A/B): ");
            String input = scanner.nextLine().toUpperCase();
            if (input.length() > 0 && (input.charAt(0) == 'A' || input.charAt(0) == 'B')) {
                votes[i] = input.charAt(0);
            } else {
                System.out.println("Invalid input. Please enter 'A' or 'B'.");
                i--; // Retry for this electorate
            }
        }

        // Send votes using multicast sender
        for (char vote : votes) {
            MulticastSender sender = new MulticastSender(vote);
            sender.start();
        }

        // Receive winner message from MulticastReceiver
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                socket.joinGroup(group);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String winnerMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Winner: " + winnerMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
}
