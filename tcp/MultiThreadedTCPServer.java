import java.io.*;
import java.net.*;
import java.util.Arrays;

public class MultiThreadedTCPServer {

    public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Start a new thread for each client connection
                new ClientHandler(socket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle each client in a separate thread
    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                output.println("Welcome to the server!");
                String clientMessage;

                // Echo received messages back to the client
                while ((clientMessage = input.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);
                    String result = processRequest(clientMessage);
                    output.println("Result: " + result);
                }

            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connection with client closed");
            }
        }

        private String processRequest(String message) {
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
}
