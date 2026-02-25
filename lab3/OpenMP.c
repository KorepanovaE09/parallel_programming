// $ gcc OpenMP.c -o lab_openmp -fopenmp -lm
// ./lab_openmp 4

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h>
#include <omp.h>

void heavy_task(){
    int limit = 1e8;
    for (int i = 0; i < limit; i++) {
        sqrt(i);
    }
}

void openmp(int thread_num){
    #pragma omp parallel for num_threads(thread_num)
    for (int i = 0; i < thread_num; i++){
        heavy_task();
    }
}

double elapsed(struct timeval start, struct timeval end) {
    return (end.tv_sec - start.tv_sec)
         + (end.tv_usec - start.tv_usec) / 1e6;
}

int main(int argc, char **argv){
    int n = atoi(argv[1]);
    clock_t start, end;

    start = clock();
    openmp(n);
    end = clock();
    printf("Time: %.3f sec\n", (double)(end - start) / CLOCKS_PER_SEC);

    return 0;
}
