public class Nqueen {
    // Function to print the chessboard
    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "Q " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Function to check if placing a queen at board[row][col] is safe
    private static boolean isSafe(int[][] board, int row, int col, int n) {
        // Check column
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 1) {
                return false;
            }
        }

        // Check upper-left diagonal
        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 1) {
                return false;
            }
        }

        // Check upper-right diagonal
        for (int i = row, j = col; i >= 0 && j < n; i--, j++) {
            if (board[i][j] == 1) {
                return false;
            }
        }

        return true;
    }

    // Recursive function to solve the N-Queens problem and print solutions
    private static int solveNQueens(int[][] board, int row, int n, int solutionCount) {
        if (row == n) {
            System.out.println("Solution #" + solutionCount + ":");
            printBoard(board);
            return 1;
        }

        int count = 0;
        for (int col = 0; col < n; col++) {
            if (isSafe(board, row, col, n)) {
                board[row][col] = 1; // Place the queen
                count += solveNQueens(board, row + 1, n, solutionCount + count);
                board[row][col] = 0; // Backtrack
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int n = 8; // Size of the chessboard
        int[][] board = new int[n][n]; // Initialize the chessboard

        int solutions = solveNQueens(board, 0, n, 1);
        System.out.println("Total number of solutions: " + solutions);
    }
}
