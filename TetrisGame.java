import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class TetrisGame extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;

    private final Color[] COLORS = {
        Color.CYAN, Color.YELLOW, Color.RED, Color.GREEN, 
        Color.ORANGE, Color.BLUE, Color.MAGENTA
    };

    private final Point[][][] SHAPES = {
        // I-Shape
        { { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) } },
        // O-Shape
        { { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) } },
        // T-Shape
        { { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) } },
        // S-Shape
        { { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) } },
        // Z-Shape
        { { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) } },
        // L-Shape
        { { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) } },
        // J-Shape
        { { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) } }
    };
    

    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private Block currentBlock;
    private Block nextBlock;

    public TetrisGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE + 120, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        timer = new Timer(500, this);
        timer.start();
        currentBlock = generateBlock();
        nextBlock = generateBlock();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!gameOver) handleKeyInput(e);
                repaint();
            }
        });
    }

    private void handleKeyInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (currentBlock.canMove(-1, 0)) currentBlock.move(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                if (currentBlock.canMove(1, 0)) currentBlock.move(1, 0);
                break;
            case KeyEvent.VK_DOWN:
                moveBlockDown();
                break;
            case KeyEvent.VK_UP:
                currentBlock.rotate();
                break;
        }
    }

    private void moveBlockDown() {
        if (!currentBlock.canMove(0, 1)) {
            currentBlock.place();
            clearFullRows();
            currentBlock = nextBlock;
            nextBlock = generateBlock();
            if (!currentBlock.canMove(0, 0)) {
                gameOver = true;
                timer.stop();
            }
        } else {
            currentBlock.move(0, 1);
        }
    }

    private void clearFullRows() {
        int cleared = 0;
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean full = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                cleared++;
                for (int r = row; r > 0; r--) {
                    board[r] = Arrays.copyOf(board[r - 1], BOARD_WIDTH);
                }
                Arrays.fill(board[0], 0);
            }
        }
        score += cleared * 100;
    }

    private Block generateBlock() {
        int shapeIndex = (int) (Math.random() * SHAPES.length);
        return new Block(SHAPES[shapeIndex][0], COLORS[shapeIndex]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) moveBlockDown();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        currentBlock.draw(g);
        drawNextBlockPreview(g);
        drawScore(g);
        if (gameOver) drawGameOver(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    g.setColor(COLORS[board[row][col] - 1]);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
    

    private void drawNextBlockPreview(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Next Block:", BOARD_WIDTH * TILE_SIZE + 10, 50);
        nextBlock.drawPreview(g, BOARD_WIDTH * TILE_SIZE + 30, 70);
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, BOARD_WIDTH * TILE_SIZE + 10, 200);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Game Over", 50, 300);
    }

    private class Block {
        private Point[] shape;
        private Color color;
        private int x = 3, y = 0;

        public Block(Point[] shape, Color color) {
            this.shape = shape;
            this.color = color;
        }

        public void draw(Graphics g) {
            g.setColor(color);
            for (Point p : shape) {
                int drawX = (x + p.x) * TILE_SIZE;
                int drawY = (y + p.y) * TILE_SIZE;
                g.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
            }
        }

        public void drawPreview(Graphics g, int startX, int startY) {
            int previewSize = TILE_SIZE / 2; // Scale down for preview
            g.setColor(color);
            for (Point p : shape) {
                int drawX = startX + p.x * previewSize;
                int drawY = startY + p.y * previewSize;
                g.fillRect(drawX, drawY, previewSize, previewSize);
                g.setColor(Color.BLACK);
                g.drawRect(drawX, drawY, previewSize, previewSize);
            }
        }
        

        public boolean canMove(int dx, int dy) {
            for (Point p : shape) {
                int newX = x + p.x + dx;
                int newY = y + p.y + dy;
                if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT || (newY >= 0 && board[newY][newX] != 0)) {
                    return false;
                }
            }
            return true;
        }

        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        public void rotate() {
            Point[] rotatedShape = new Point[shape.length];
            for (int i = 0; i < shape.length; i++) {
                rotatedShape[i] = new Point(-shape[i].y, shape[i].x); // Clockwise rotation
            }
            Point[] originalShape = shape;
            shape = rotatedShape;
            if (!canMove(0, 0)) {
                shape = originalShape; // Revert if rotation is invalid
            }
        }
        

        public void place() {
            for (Point p : shape) {
                int finalX = x + p.x;
                int finalY = y + p.y;
                if (finalY >= 0) board[finalY][finalX] = Arrays.asList(COLORS).indexOf(color) + 1;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
