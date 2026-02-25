#include <stdio.h>
#include <time.h>

void sse(float a[], float b[], float c[]) {
  asm volatile (
                "movups %[a], %%xmm0\n"
                "movups %[b], %%xmm1\n"
                "mulps %%xmm1, %%xmm0\n"
                "movups %%xmm0, %[c]\n"
                :
                : [a]"m"(*a), [b]"m"(*b), [c]"m"(*c)
                : "%xmm0", "%xmm1");
}

void seq(float a[4], float b[4], float c[4]){
    for (int i = 0; i < 4; i ++){
        c[i] = a[i] * b[i];
    }
}

void print_array(float c[4]) {
    for (int i = 0; i < 4; i++) {
        printf("%f ", c[i]);
    }
    printf("\n");
}

int main(int argc, char** argv) {
  float a[4] = {1.0f, 2.0f, 3.0f, 4.0f};
  float b[4] = {5.0f, 6.0f, 7.0f, 8.0f};
  float c[4];

  const int iterations_num = 1000000;
  clock_t start, end;
  double time_sse, time_seq;
  
  start = clock();
  for (int i = 0; i < iterations_num; i++) {
    sse(a, b, c);
  }
  end = clock();
  time_sse = (double)(end - start) / CLOCKS_PER_SEC;

  printf("SSE result: ");
  print_array(c);
  printf("SSE time: %f sec\n", time_sse);

  start = clock();
  for (int i = 0; i < iterations_num; i++) {
    seq(a, b, c);
  }
  end = clock();
  time_seq = (double)(end - start) / CLOCKS_PER_SEC;

  printf("Sequential result: ");
  print_array(c);
  printf("Sequential time: %f sec\n", time_seq);

  return 0;
}