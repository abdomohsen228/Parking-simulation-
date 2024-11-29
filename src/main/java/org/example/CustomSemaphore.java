package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class CustomSemaphore {
    private int permits;
    private final boolean fair;
    private final Object lock = new Object();
    private final Queue<Thread> waitingThreads = new LinkedList<>();

    public CustomSemaphore(int permits, boolean fair) {
        if (permits < 0) {
            throw new IllegalArgumentException("Permits cannot be negative");
        }
        this.permits = permits;
        this.fair = fair;
    }

    public void acquire() throws InterruptedException {
        synchronized (lock) {
            if (fair) {
                Thread currentThread = Thread.currentThread();
                waitingThreads.add(currentThread);
                while (permits <= 0 || waitingThreads.peek() != currentThread) {
                    lock.wait();
                }
                waitingThreads.poll();
            } else {
                while (permits <= 0) {
                    lock.wait();
                }
            }
            permits--;
        }
    }

    public void release() {
        synchronized (lock) {
            permits++;
            if (fair && !waitingThreads.isEmpty()) {
                lock.notifyAll();
            } else {
                lock.notify();
            }
        }
    }
    public int availablePermits() {
        synchronized (lock) {
            return permits;
        }
    }
}
