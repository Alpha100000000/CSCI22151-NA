// Name: Alec Clinton
// Class: CSCI2251
// FileName: Server.java
// Assignment: Concurrent Processing Over a Network – Part 1 & 2

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    // Server listens on this port
    private final int PORT = 64757;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            // Server runs indefinitely
            while (true) {
                Socket connection = serverSocket.accept(); // Accept client
                System.out.println("Client connected.");
                processConnection(connection);            // Handle client request
            }

        } catch (IOException e) {
            System.out.println("Error starting server.");
            e.printStackTrace();
        }
    }

    // Process each client connection
    private void processConnection(Socket socket) {
        try (
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Object obj1 = input.readObject(); // First matrix
            Object obj2 = input.readObject(); // Second matrix

            // Validate objects are 2D arrays
            if (obj1 instanceof int[][] && obj2 instanceof int[][]) {
                int[][] matrixA = (int[][]) obj1;
                int[][] matrixB = (int[][]) obj2;

                System.out.println("Received matrices. Starting concurrent addition...");

                // Create result matrix with same dimensions
                int rows = matrixA.length;
                int cols = matrixA[0].length;
                int[][] result = new int[rows][cols];

                // Create 4 threads for 4 quadrants
                Thread t1 = new ThreadOperation(matrixA, matrixB, result, "upper left");
                Thread t2 = new ThreadOperation(matrixA, matrixB, result, "upper right");
                Thread t3 = new ThreadOperation(matrixA, matrixB, result, "lower left");
                Thread t4 = new ThreadOperation(matrixA, matrixB, result, "lower right");

                // Start threads
                t1.start();
                t2.start();
                t3.start();
                t4.start();

                // Wait for all threads to finish
                t1.join();
                t2.join();
                t3.join();
                t4.join();

                System.out.println("Summed result matrix:");
                printMatrix(result);

                // Send result back to client
                output.writeObject(result);
                output.flush();
            }

            // Wait for termination command from client
            Object terminationCheck = input.readObject();
            if (terminationCheck instanceof String &&
                ((String) terminationCheck).equalsIgnoreCase("TERMINATE")) {
                System.out.println("Client requested termination. Closing connection.");
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Error processing client.");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to print matrix to console
    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                System.out.printf("%4d", val);
            System.out.println();
        }
    }
}
