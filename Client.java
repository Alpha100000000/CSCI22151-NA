// Name: Alec Clinton
// Class: CSCI2251
// FileName: Client.java
// Assignment: Concurrent Processing Over a Network – Part 1 & 2 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame {
    private JTextField enterField;     // Field for user to enter filename
    private JTextArea displayArea;     // Area to display matrix contents and status

    public Client() {
        super("Matrix Client GUI");

        // Set up GUI layout
        enterField = new JTextField("Enter matrix file name here");
        displayArea = new JTextArea();
        add(enterField, BorderLayout.NORTH);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Handle Enter key press in text field
        enterField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filename = e.getActionCommand();
                try {
                    int[][][] matrices = readMatricesFromFile(filename); // [0] = A, [1] = B
                    displayArea.setText("Matrix A:\n");
                    displayMatrix(matrices[0]);
                    displayArea.append("\nMatrix B:\n");
                    displayMatrix(matrices[1]);
                    sendToServer(matrices[0], matrices[1]);
                } catch (Exception ex) {
                    displayArea.append("\n❌ Error: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        setSize(500, 400);
        setVisible(true);
    }

    // Reads two matrices from file
    private int[][][] readMatricesFromFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner input = new Scanner(file);

        int rows = input.nextInt();
        int cols = input.nextInt();

        int[][] matrixA = new int[rows][cols];
        int[][] matrixB = new int[rows][cols];

        // Fill matrix A
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrixA[i][j] = input.nextInt();

        // Fill matrix B
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrixB[i][j] = input.nextInt();

        return new int[][][] { matrixA, matrixB };
    }

    // Displays a matrix in the text area
    private void displayMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                displayArea.append(String.format("%4d", val));
            displayArea.append("\n");
        }
    }

    // Sends matrices to server via socket
    private void sendToServer(int[][] matrixA, int[][] matrixB) throws IOException {
        Socket socket = new Socket("127.0.0.1", 64757);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        out.writeObject(matrixA);
        out.writeObject(matrixB);
        out.flush();

        displayArea.append("\n✅ Matrices sent to server.\n");

        socket.close();
    }

    // Entry point if you want to run Client directly
    public static void main(String[] args) {
        Client client = new Client();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Optional method for ClientStart.java to call
    public void startClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

