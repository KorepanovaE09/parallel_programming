package lab5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MySemaphore extends Semaphore {

    private final AtomicInteger permits;

    public MySemaphore(int initialPermits) {
        super(initialPermits);
        this.permits = new AtomicInteger(initialPermits);
    }

    @Override
    public void acquire() throws InterruptedException {
        while (true) {
            int current = permits.get();

            if (current == 0) {
                Thread.yield(); 
                continue;
            }

            if (permits.compareAndSet(current, current - 1)) {
                break; 
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public void release() {
        permits.incrementAndGet();
    }

    @Override
    public int availablePermits() {
        return permits.get();
    }
}