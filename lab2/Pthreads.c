// gcc Pthreads.c -o Pthreads -pthread -lm
// ./Pthreads 4

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <sys/time.h>

int counter = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

void *heavy_task(void *arg) {
    int thread_num = *((int*)arg);

    printf("Thread %d started\n", thread_num);

    pthread_mutex_lock(&mutex);
    counter++;
    printf("Thread %d: mutex lock, counter = %d\n", thread_num, counter);
    pthread_mutex_unlock(&mutex);
    printf("Thread %d: mutex unlock, counter = %d\n", thread_num, counter);

    for (int i = 0; i < 1e8; i++) { 
        sqrt(i);
    }

    printf("Thread %d finished\n", thread_num);
    fflush(stdout);
    free(arg);
    return NULL;   
}

void pthreads(int threads_num) {
    pthread_t threads[threads_num];

    for (int i = 0; i < threads_num; i++) {
        int *id = malloc(sizeof(int));
        *id = i;
        pthread_create(&threads[i], NULL, heavy_task, id);
    }

    for (int i = 0; i < threads_num; i++) {
        pthread_join(threads[i], NULL);
    }
}

void sequential(int task_num) {
    for (int i = 0; i < task_num; i++) {
        int *id = malloc(sizeof(int));
        *id = i;
        heavy_task(id);
    }
}

// Измерение времени
double elapsed(struct timeval start, struct timeval end) {
    return (end.tv_sec - start.tv_sec)
         + (end.tv_usec - start.tv_usec) / 1e6;
}

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("Usage: %s <num_threads>\n", argv[0]);
        return 1;
    }

    int n = atoi(argv[1]);
    clock_t start, end;

    printf("=== Multithreaded ===\n");
    start = clock();
    pthreads(n);
    end = clock();
    printf("Time: %.3f sec\n", (double)(end - start) / CLOCKS_PER_SEC);

    counter = 0;

    printf("=== Sequential ===\n");
    start = clock();
    sequential(n);
    end = clock();
    printf("Time: %.3f sec\n", (double)(end - start) / CLOCKS_PER_SEC);

    pthread_mutex_destroy(&mutex);
    return 0;
}