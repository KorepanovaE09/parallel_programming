package lab5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MySemaphore extends Semaphore {

    private final AtomicInteger permits;

    public MySemaphore(int initialPermits) {
        super(initialPermits);
        this.permits = new AtomicInteger(initialPermits);
    }

    public int acquireAndGet() throws InterruptedException {
        String threadName = Thread.currentThread().getName();

        while (true) {
            int current = permits.get();

            if (current == 0) {
                Thread.yield();
                continue;
            }

            // CAS
            if (permits.compareAndSet(current, current - 1)) {
                int after = current - 1;

                System.out.println(threadName + " ЗАХВАТИЛ permit (" + current + " -> " + after + ")");

                return after;
            }

            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public void acquire() throws InterruptedException {
        acquireAndGet();
    }

    @Override
    public void release() {
        String threadName = Thread.currentThread().getName();
        int after = permits.incrementAndGet();

        System.out.println(threadName + " ОСВОБОДИЛ permit: " + after);
    }

    @Override
    public int availablePermits() {
        return permits.get();
    }
}