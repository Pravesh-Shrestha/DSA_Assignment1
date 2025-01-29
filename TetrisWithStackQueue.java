import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class TetrisWithStackQueue extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;

    private final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.RED, 
            Color.GREEN, Color.ORANGE, Color.BLUE, Color.MAGENTA
    };

    private final Point[][][] SHAPES = {
            {{ new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) }},
            {{ new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) }},
            {{ new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) }},
            {{ new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) }},
            {{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) }},
            {{ new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) }},
            {{ new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) }}
    };

    private Stack<int[]> boardStack;
    private Queue<Block> blockQueue;
    private Block currentBlock;

    public TetrisWithStackQueue() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE + 120, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);

        boardStack = new Stack<>();
        blockQueue = new LinkedList<>();
        timer = new Timer(500, this);

        initializeBoard();
        timer.start();

        generateNewBlocks();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!gameOver) handleKeyInput(e);
                repaint();
            }
        });
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boardStack.push(new int[BOARD_WIDTH]);
        }
    }

    private void generateNewBlocks() {
        for (int i = 0; i < 3; i++) {
            blockQueue.offer(generateRandomBlock());
        }
        currentBlock = blockQueue.poll();
    }

    private Block generateRandomBlock() {
        int shapeIndex = (int) (Math.random() * SHAPES.length);
        return new Block(SHAPES[shapeIndex][0], COLORS[shapeIndex]);
    }

    private void handleKeyInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                if (currentBlock.canMove(-1, 0)) currentBlock.move(-1, 0);
            }
            case KeyEvent.VK_RIGHT -> {
                if (currentBlock.canMove(1, 0)) currentBlock.move(1, 0);
            }
            case KeyEvent.VK_DOWN -> moveBlockDown();
            case KeyEvent.VK_UP -> currentBlock.rotate();
        }
    }

    private void moveBlockDown() {
        if (!currentBlock.canMove(0, 1)) {
            currentBlock.place();
            checkAndClearRows();
            currentBlock = blockQueue.poll();
            blockQueue.offer(generateRandomBlock());
            if (!currentBlock.canMove(0, 0)) {
                gameOver = true;
                timer.stop();
            }
        } else {
            currentBlock.move(0, 1);
        }
    }

    private void checkAndClearRows() {
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            int[] currentRow = boardStack.get(row);
            boolean fullRow = Arrays.stream(currentRow).allMatch(cell -> cell != 0);
            if (fullRow) {
                boardStack.remove(row);
                boardStack.push(new int[BOARD_WIDTH]);
                score += 100;
            }
        }
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
        drawPreview(g);
        drawScore(g);
        if (gameOver) drawGameOver(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            int[] currentRow = boardStack.get(row);
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (currentRow[col] != 0) {
                    g.setColor(COLORS[currentRow[col] - 1]);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private void drawPreview(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Next Block:", BOARD_WIDTH * TILE_SIZE + 10, 50);
        Block nextBlock = blockQueue.peek();
        if (nextBlock != null) nextBlock.drawPreview(g, BOARD_WIDTH * TILE_SIZE + 30, 70);
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
            int previewSize = TILE_SIZE / 2;
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
                if (newX < 0 || newX >= BOARD_WIDTH || newY >= BOARD_HEIGHT || boardStack.get(newY)[newX] != 0) {
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
                rotatedShape[i] = new Point(-shape[i].y, shape[i].x);
            }
            Point[] originalShape = shape;
            shape = rotatedShape;
            if (!canMove(0, 0)) shape = originalShape;
        }

        public void place() {
            for (Point p : shape) {
                int finalX = x + p.x;
                int finalY = y + p.y;
                if (finalY >= 0) {
                    boardStack.get(finalY)[finalX] = Arrays.asList(COLORS).indexOf(color) + 1;
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game with Stack and Queue");
        TetrisWithStackQueue game = new TetrisWithStackQueue();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
