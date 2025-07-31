// Name: Alec Clinton
// Class: CSCI2251
// FileName: ThreadOperation.java
// Assignment: Concurrent Processing Over a Network – Part 1 & 2

public class ThreadOperation extends Thread {

    // Complete matrix references and bounds for assigned quadrant
    private int[][] A, B, C;
    private int rowStart, rowEnd, colStart, colEnd;

    // Constructor receives entire matrices and quadrant name
    public ThreadOperation(int[][] A, int[][] B, int[][] C, String quadrant) {
        this.A = A;
        this.B = B;
        this.C = C;

        // Determine the start and end indexes for this thread's quadrant
        int[] idx = getQuadrantIndexes(A.length, A[0].length, quadrant);
        this.rowStart = idx[0];
        this.rowEnd = idx[1];
        this.colStart = idx[2];
        this.colEnd = idx[3];
    }

    // Performs the matrix addition for the assigned quadrant
    @Override
    public void run() {
        for (int r = rowStart; r < rowEnd; r++) {
            for (int c = colStart; c < colEnd; c++) {
                C[r][c] = A[r][c] + B[r][c];
            }
        }
    }

    /**
     * Returns the start and end bounds (rowStart, rowEnd, colStart, colEnd)
     * based on quadrant name and matrix dimensions.
     */
    public static int[] getQuadrantIndexes(int rows, int cols, String quadrant) {
        int midRow = rows / 2;
        int midCol = cols / 2;

        if (quadrant.equalsIgnoreCase("upper left"))
            return new int[] {0, midRow, 0, midCol};
        else if (quadrant.equalsIgnoreCase("upper right"))
            return new int[] {0, midRow, midCol, cols};
        else if (quadrant.equalsIgnoreCase("lower left"))
            return new int[] {midRow, rows, 0, midCol};
        else // default to lower right
            return new int[] {midRow, rows, midCol, cols};
    }
}
