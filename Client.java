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
    private JTextField enterField;
    private JTextArea displayArea;

    public Client() {
        super("Matrix Client GUI");

        // Set up GUI layout
        enterField = new JTextField("Enter matrix file name here");
        displayArea = new JTextArea();
        add(enterField, BorderLayout.NORTH);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Action when Enter key is pressed
        enterField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filename = e.getActionCommand();
                try {
                    int[][][] matrices = readMatricesFromFile(filename);
                    displayArea.setText("Matrix A:\n");
                    displayMatrix(matrices[0]);
                    displayArea.append("\nMatrix B:\n");
                    displayMatrix(matrices[1]);

                    // Send matrices and receive result
                    int[][] result = sendToServer(matrices[0], matrices[1]);

                    // Display result
                    displayArea.append("\nResult Matrix:\n");
                    displayMatrix(result);

                } catch (Exception ex) {
                    displayArea.append("\nError: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
            }
        });

        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Reads two matrices from file
    private int[][][] readMatricesFromFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner input = new Scanner(file);

        int rows = input.nextInt();
        int cols = input.nextInt();

        int[][] matrixA = new int[rows][cols];
        int[][] matrixB = new int[rows][cols];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrixA[i][j] = input.nextInt();

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                matrixB[i][j] = input.nextInt();

        input.close();
        return new int[][][] { matrixA, matrixB };
    }

    // Displays matrix on GUI
    private void displayMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                displayArea.append(String.format("%4d", val));
            displayArea.append("\n");
        }
    }

    // Sends matrices to server and receives summed result
    private int[][] sendToServer(int[][] matrixA, int[][] matrixB) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 64757); // Connect to server
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Send both matrices
        out.writeObject(matrixA);
        out.writeObject(matrixB);
        out.flush();

        // Receive result matrix
        Object resultObj = in.readObject();
        int[][] resultMatrix = (int[][]) resultObj;

        // Send termination signal
        out.writeObject("TERMINATE");
        out.flush();

        out.close();
        in.close();
        socket.close();

        return resultMatrix;
    }

    // Launch client GUI
    public static void main(String[] args) {
        new Client();
    }

    // For launching from ClientStart.java
    public void startClient() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
