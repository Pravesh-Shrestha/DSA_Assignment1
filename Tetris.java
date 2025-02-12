// Question No 3 b
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Tetris extends JPanel implements ActionListener, KeyListener, MouseListener {
    private final int BOARD_WIDTH = 12;
    private final int BOARD_HEIGHT = 20;
    private final int TILE_SIZE = 30;
    private Timer timer;
    private boolean[][] board;
    private Shape currentShape, nextShape;
    private int currentX, currentY;
    private boolean gameOver;
    private Random random;

    public Tetris() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE + 150, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(500, this);
        timer.start();
        random = new Random();

        nextShape = new Shape(random.nextInt(4));
        spawnShape();
    }

    private void restartGame() {
        board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        gameOver = false;
        timer.start();
        nextShape = new Shape(random.nextInt(4));
        spawnShape();
        repaint();
    }

    private void spawnShape() {
        currentShape = nextShape;
        nextShape = new Shape(random.nextInt(4));
        currentX = BOARD_WIDTH / 2 - 1;
        currentY = 0;

        if (collision()) {
            gameOver = true;
            timer.stop();
        }
    }

    private boolean collision() {
        for (int i = 0; i < currentShape.getHeight(); i++) {
            for (int j = 0; j < currentShape.getWidth(); j++) {
                if (currentShape.isFilled(i, j)) {
                    int x = currentX + j;
                    int y = currentY + i;

                    if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT || (y >= 0 && board[y][x])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void placeShape() {
        for (int i = 0; i < currentShape.getHeight(); i++) {
            for (int j = 0; j < currentShape.getWidth(); j++) {
                if (currentShape.isFilled(i, j)) {
                    board[currentY + i][currentX + j] = true;
                }
            }
        }
        clearLines();
        spawnShape();
    }

    private void clearLines() {
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (!board[i][j]) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new boolean[BOARD_WIDTH];
                i++;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            currentY++;
            if (collision()) {
                currentY--;
                placeShape();
            }
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the game board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j]) {
                    g.setColor(Color.CYAN);
                    g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw the current shape
        if (currentShape != null) {
            for (int i = 0; i < currentShape.getHeight(); i++) {
                for (int j = 0; j < currentShape.getWidth(); j++) {
                    if (currentShape.isFilled(i, j)) {
                        g.setColor(currentShape.getColor());
                        g.fillRect((currentX + j) * TILE_SIZE, (currentY + i) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Draw the "Next Shape" preview box
        int previewX = BOARD_WIDTH * TILE_SIZE + 20;
        int previewY = 100;
        g.setColor(Color.WHITE);
        g.drawRect(previewX - 10, previewY - 10, 4 * TILE_SIZE, 4 * TILE_SIZE);
        g.drawString("Next:", previewX, previewY - 20);

        // Draw the next shape inside the preview box
        if (nextShape != null) {
            for (int i = 0; i < nextShape.getHeight(); i++) {
                for (int j = 0; j < nextShape.getWidth(); j++) {
                    if (nextShape.isFilled(i, j)) {
                        g.setColor(nextShape.getColor());  // Use the next shape's color
                        g.fillRect(previewX + j * TILE_SIZE, previewY + i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        // Display "Game Over" message if game ends
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String message = "GAME OVER - Press Space to Restart";
            FontMetrics fm = g.getFontMetrics();
            int x = (BOARD_WIDTH * TILE_SIZE - fm.stringWidth(message)) / 2;
            int y = BOARD_HEIGHT * TILE_SIZE / 2;
            g.drawString(message, x, y);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            restartGame();
            return;
        }

        if (gameOver) return;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            currentX--;
            if (collision()) currentX++;
        } else if (key == KeyEvent.VK_RIGHT) {
            currentX++;
            if (collision()) currentX--;
        } else if (key == KeyEvent.VK_DOWN) {
            currentY++;
            if (collision()) {
                currentY--;
                placeShape();
            }
        } else if (key == KeyEvent.VK_UP) {
            currentShape.rotate();
            if (collision()) currentShape.rotateBack();
        } else if (key == KeyEvent.VK_PAGE_UP) {
            currentShape.rotateBack();
            if (collision()) currentShape.rotate();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) { if (gameOver) restartGame(); }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris tetris = new Tetris();
        frame.add(tetris);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Shape {
    private int[][] shape;
    private Color color;

    public Shape(int type) {
        switch (type) {
            case 0: 
                shape = new int[][]{{1, 0}, {1, 0}, {1, 1}}; 
                color = Color.ORANGE; // L shape
                break;
            case 1: 
                shape = new int[][]{{1, 1, 1}, {0, 1, 0}}; 
                color = Color.MAGENTA; // T shape
                break;
            case 2: 
                shape = new int[][]{{1, 1, 1, 1}}; 
                color = Color.CYAN; // I shape
                break;
            case 3: 
                shape = new int[][]{{1, 1}, {1, 1}}; 
                color = Color.YELLOW; // Square shape
                break;
            default:
                shape = new int[][]{{1}}; 
                color = Color.WHITE; // Default
                break;
        }
    }

    public int getWidth() { return shape[0].length; }
    public int getHeight() { return shape.length; }
    public boolean isFilled(int row, int col) { return shape[row][col] == 1; }
    public Color getColor() { return color; }

    public void rotate() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                rotated[j][shape.length - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }

    public void rotateBack() {
        int[][] rotated = new int[shape[0].length][shape.length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                rotated[shape[0].length - 1 - j][i] = shape[i][j];
            }
        }
        shape = rotated;
    }
}
