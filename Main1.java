// Question No 6 a.

import java.util.concurrent.Semaphore;

class NumberPrinter {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int num) {
        System.out.print(num);
    }

    public void printOdd(int num) {
        System.out.print(num);
    }
}

class ThreadController {
    private int n;
    private NumberPrinter printer;
    private Semaphore zeroSemaphore = new Semaphore(1);
    private Semaphore evenSemaphore = new Semaphore(0);
    private Semaphore oddSemaphore = new Semaphore(0);

    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
    }

    public void startThreads() {
        Thread zeroThread = new Thread(() -> {
            try {
                for (int i = 1; i <= n; i++) {
                    zeroSemaphore.acquire();
                    printer.printZero();
                    if (i % 2 == 0) {
                        evenSemaphore.release();
                    } else {
                        oddSemaphore.release();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                for (int i = 2; i <= n; i += 2) {
                    evenSemaphore.acquire();
                    printer.printEven(i);
                    zeroSemaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                for (int i = 1; i <= n; i += 2) {
                    oddSemaphore.acquire();
                    printer.printOdd(i);
                    zeroSemaphore.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        zeroThread.start();
        evenThread.start();
        oddThread.start();
    }
}

public class Main1 {
    public static void main(String[] args) {
        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(5, printer);
        controller.startThreads();
    }
}
