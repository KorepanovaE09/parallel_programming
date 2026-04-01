    // javac Main.java
    // java Main

    import java.util.*;
    import java.util.concurrent.*;

    public class Main {

        public static final int THREADS = 50;
        public static final int ITERATIONS = 1000;
        public static final double NSEC = 1000_000_000.0;
        public static final int MAP_SIZE = 3;
        public static final int SAMPLES = 5;

        public static Map<String, Integer> hashMap = new HashMap<>();
        public static Map<String, Integer> hashTable = new Hashtable<>();
        public static Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
        public static Map<String, Integer> cHashMap = new ConcurrentHashMap<>();

        public static void main(String[] args) {

            System.out.println("Collections:");

            double hashMapTime = compute(hashMap) / NSEC;
            double hashTableTime = compute(hashTable) / NSEC;
            double syncMapTime = compute(syncMap) / NSEC;
            double cHashMapTime = compute(cHashMap) / NSEC;

            System.out.println("Execution time:");

            System.out.println(String.format(
                    "\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.",
                    hashMapTime, hashTableTime, syncMapTime, cHashMapTime));
        }

        private static long compute(Map<String, Integer> map) {

            System.out.print(String.format("\t%s", map.getClass().getName()));

            map.clear();

            for (int i = 0; i < MAP_SIZE; i++) {
                map.put("key" + i, 0);
            }

            long start = 0;
            long stop = 0;

            for (int k = 0; k < SAMPLES; k++) {
                start = System.nanoTime();

                ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
                List<Callable<String>> tasks = new ArrayList<>();
                List<Future<String>> results;

                for (int i = 0; i < THREADS; i++) {
                    tasks.add(() -> {
                        String threadName = Thread.currentThread().getName();
                        Random random = new Random();

                        for (int j = 0; j < ITERATIONS; j++) {
                            String key = "key" + random.nextInt(MAP_SIZE);
                            Integer value = map.get(key);

                            if (value == null) {
                                value = 0;
                            }
                            if (random.nextInt(10) == 0) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            map.put(key, value + 1);

                            // Вывод состояния карты для hashMap

                            // if (map instanceof HashMap && j % 100 == 0) {
                            //     System.out.println(Thread.currentThread().getName() + " -> " + map);
                            // }
                        }
                        return "Thread " + threadName + " done";
                    });
                }

                try {
                    results = executorService.invokeAll(tasks);
                    for (Future<String> result : results) {
                        result.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                executorService.shutdown();
                stop = System.nanoTime();
            }
            System.out.println(" ...done.");

            int sum = 0;
            for (int i = 0; i < MAP_SIZE; i++) {
                sum += map.get("key" + i);
            }
            System.out.println(" ...done. Sum of values: " + sum + " (expected: " + (THREADS * ITERATIONS) + ")");


            return stop - start;
        }
    }