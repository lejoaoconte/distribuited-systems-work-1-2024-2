import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPServer {

    public static void main(String[] args) {
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server is listening on port " + port);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);

                socket.receive(requestPacket);
                String received = new String(requestPacket.getData(), 0, requestPacket.getLength());
                System.out.println("Received from client: " + received);

                // Process the request
                String responseMessage = processRequest(received);
                byte[] responseData = responseMessage.getBytes();

                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                socket.send(responsePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String message) {
        try {
            // Remove brackets from array and split by comma
            message = message.replaceAll("[\\[\\]]", "");
            String[] parts = message.split(",");

            // Verify if the message has at least 3 parts example: [+,2,3]
            if (parts.length < 2) {
                return "Error: insufficient arguments";
            }

            // Get operator and numbers
            String operation = parts[0].trim();
            double[] numbers = Arrays.stream(parts, 1, parts.length)
                    .mapToDouble(Double::parseDouble)
                    .toArray();

            // Do the math
            double result;
            switch (operation) {
                case "+":
                    result = Arrays.stream(numbers).sum();
                    break;
                case "-":
                    result = Arrays.stream(numbers).reduce((a, b) -> a - b).orElse(0);
                    break;
                case "*":
                    result = Arrays.stream(numbers).reduce((a, b) -> a * b).orElse(1);
                    break;
                case "/":
                    result = Arrays.stream(numbers).reduce((a, b) -> a / b).orElse(Double.NaN);
                    if (Double.isNaN(result) || Double.isInfinite(result)) {
                        return "Error: division by zero or insufficient arguments";
                    }
                    break;
                default:
                    return "Error: unsupported operation '" + operation + "'";
            }

            return "[" + result + "]";
        } catch (NumberFormatException e) {
            return "Error: invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
