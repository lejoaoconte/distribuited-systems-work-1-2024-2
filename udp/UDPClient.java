import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)) {
            InetAddress serverAddress = InetAddress.getByName(hostname);

            while (true) {
                System.out.println("\nEnter the operation in the format: [op,arg1,arg2,...]");
                System.out.println("Example: [+,5,10,15]");
                System.out.println("Type 'exit' to quit");
                System.out.print("Enter message (type 'exit' to quit): ");
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Encerrando o cliente.");
                    break;
                }

                byte[] buffer = input.getBytes();
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                socket.send(requestPacket);

                byte[] responseBuffer = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(responsePacket);
                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

                System.out.println("Resultado do servidor: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
