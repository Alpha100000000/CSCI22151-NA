import java.io.*;
// Name: Alec Clinton
// Class: CSCI2251
// FileName: Server.java
// Assignment: Concurrent Processing Over a Network – Part 1 & 2 

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(64757)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket connection = serverSocket.accept();
                System.out.println("Client connected.");
                processConnection(connection);
            }

        } catch (IOException e) {
            System.out.println("Server failed to start.");
            e.printStackTrace();
        }
    }

    private void processConnection(Socket socket) {
        try (
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream())
        ) {
            Object objA = input.readObject();
            Object objB = input.readObject();

            if (objA instanceof int[][] && objB instanceof int[][]) {
                int[][] matrixA = (int[][]) objA;
                int[][] matrixB = (int[][]) objB;

                System.out.println("Matrix A:");
                printMatrix(matrixA);
                System.out.println("Matrix B:");
                printMatrix(matrixB);
            }

            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error processing client connection.");
            e.printStackTrace();
        }
    }

    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                System.out.printf("%4d", val);
            System.out.println();
        }
    }
}
