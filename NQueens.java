import java.util.ArrayList;
import java.util.List;

public class NQueens {

    // Main function to solve the N-Queens problem
    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        char[][] board = new char[n][n];

        // Initialize the board with '.' (empty spaces)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }

        // Start placing queens from the first row
        placeQueens(0, board, results);
        return results;
    }

    // Helper function to place queens row by row
    private static void placeQueens(int row, char[][] board, List<List<String>> results) {
        int n = board.length;

        // Base case: If we've placed queens in all rows, save the solution
        if (row == n) {
            results.add(convertBoardToList(board));
            return;
        }

        // Try placing a queen in each column of the current row
        for (int col = 0; col < n; col++) {
            if (isSafe(row, col, board)) {
                board[row][col] = 'Q';  // Place the queen
                placeQueens(row + 1, board, results);  // Move to the next row
                board[row][col] = '.';  // Backtrack: Remove the queen
            }
        }
    }

    // Function to check if it's safe to place a queen at board[row][col]
    private static boolean isSafe(int row, int col, char[][] board) {
        int n = board.length;

        // Check column (above the current row)
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q') return false;
        }

        // Check upper-left diagonal
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q') return false;
        }

        // Check upper-right diagonal
        for (int i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
            if (board[i][j] == 'Q') return false;
        }

        return true;  // Position is safe
    }

    // Convert the board into a list of strings for easier output
    private static List<String> convertBoardToList(char[][] board) {
        List<String> result = new ArrayList<>();
        for (char[] row : board) {
            result.add(new String(row));
        }
        return result;
    }

    // Main function to test the solution
    public static void main(String[] args) {
        int n = 4;  // Size of the chessboard (can be changed)
        List<List<String>> solutions = solveNQueens(n);

        System.out.println("Number of solutions: " + solutions.size());
        for (List<String> solution : solutions) {
            for (String row : solution) {
                System.out.println(row);
            }
            System.out.println();
        }
    }
}
