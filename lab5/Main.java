// powershell
// javac lab5/*.java
// java lab5.Main

package lab5;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final int THREADS = 4;
    public static final int COUNT = 2;

    public static MySemaphore mySemaphore = new MySemaphore(COUNT);
    public static Semaphore regSemaphore = new Semaphore(COUNT);

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("Regular semaphore:");
        runTask(regSemaphore);

        System.out.println("");
        System.out.println("My semaphore:");
        runTask(mySemaphore);
    }
 
    public static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                String threadName = Thread.currentThread().getName();
                try {
                    System.out.println(threadName + " пытается войти");
                    semaphore.acquire();

                    System.err.println(threadName + " Вошел, доступно: " + semaphore.availablePermits());
                    Thread.sleep(2000);

                    System.out.println(threadName + " выходит");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }

                return "Thread " + threadName + " done";
            });
        }
        try {
            List<Future<String>> results = es.invokeAll(tasks);

            for (Future<String> result : results) {
                result.get();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        es.shutdown();
    }
}   
