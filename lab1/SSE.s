	.file	"SSE.c"
	.text
	.p2align 4
	.globl	sse
	.def	sse;	.scl	2;	.type	32;	.endef
	.seh_proc	sse
sse:
	.seh_endprologue
/APP
 # 5 "SSE.c" 1
	movups (%rcx), %xmm0
movups (%rdx), %xmm1
mulps %xmm1, %xmm0
movups %xmm0, (%r8)

 # 0 "" 2
/NO_APP
	ret
	.seh_endproc
	.p2align 4
	.globl	seq
	.def	seq;	.scl	2;	.type	32;	.endef
	.seh_proc	seq
seq:
	.seh_endprologue
	xorl	%eax, %eax
.L4:
	movss	(%rcx,%rax), %xmm0
	mulss	(%rdx,%rax), %xmm0
	movss	%xmm0, (%r8,%rax)
	addq	$4, %rax
	cmpq	$16, %rax
	jne	.L4
	ret
	.seh_endproc
	.section .rdata,"dr"
.LC0:
	.ascii "%f \0"
	.text
	.p2align 4
	.globl	print_array
	.def	print_array;	.scl	2;	.type	32;	.endef
	.seh_proc	print_array
print_array:
	pushq	%rsi
	.seh_pushreg	%rsi
	pushq	%rbx
	.seh_pushreg	%rbx
	subq	$40, %rsp
	.seh_stackalloc	40
	.seh_endprologue
	movq	%rcx, %rbx
	leaq	16(%rcx), %rsi
.L7:
	pxor	%xmm1, %xmm1
	leaq	.LC0(%rip), %rcx
	addq	$4, %rbx
	cvtss2sd	-4(%rbx), %xmm1
	movq	%xmm1, %rdx
	call	printf
	cmpq	%rsi, %rbx
	jne	.L7
	movl	$10, %ecx
	addq	$40, %rsp
	popq	%rbx
	popq	%rsi
	jmp	putchar
	.seh_endproc
	.section .rdata,"dr"
.LC4:
	.ascii "SSE result: \0"
.LC5:
	.ascii "SSE time: %f sec\12\0"
.LC6:
	.ascii "Sequential result: \0"
.LC7:
	.ascii "Sequential time: %f sec\12\0"
	.section	.text.startup,"x"
	.p2align 4
	.globl	main
	.def	main;	.scl	2;	.type	32;	.endef
	.seh_proc	main
main:
	pushq	%rsi
	.seh_pushreg	%rsi
	pushq	%rbx
	.seh_pushreg	%rbx
	subq	$104, %rsp
	.seh_stackalloc	104
	.seh_endprologue
	call	__main
	movaps	.LC1(%rip), %xmm0
	movaps	%xmm0, 48(%rsp)
	movaps	.LC2(%rip), %xmm0
	movaps	%xmm0, 64(%rsp)
	call	clock
	movl	%eax, %ebx
	movl	$1000000, %eax
	.p2align 4
	.p2align 3
.L10:
/APP
 # 5 "SSE.c" 1
	movups 48(%rsp), %xmm0
movups 64(%rsp), %xmm1
mulps %xmm1, %xmm0
movups %xmm0, 80(%rsp)

 # 0 "" 2
 # 5 "SSE.c" 1
	movups 48(%rsp), %xmm0
movups 64(%rsp), %xmm1
mulps %xmm1, %xmm0
movups %xmm0, 80(%rsp)

 # 0 "" 2
/NO_APP
	subl	$2, %eax
	jne	.L10
	call	clock
	leaq	.LC4(%rip), %rcx
	pxor	%xmm1, %xmm1
	subl	%ebx, %eax
	cvtsi2sdl	%eax, %xmm1
	divsd	.LC3(%rip), %xmm1
	movsd	%xmm1, 40(%rsp)
	call	printf
	leaq	80(%rsp), %rcx
	call	print_array
	movsd	40(%rsp), %xmm1
	leaq	.LC5(%rip), %rcx
	movq	%xmm1, %rdx
	call	printf
	call	clock
	movaps	48(%rsp), %xmm0
	mulps	64(%rsp), %xmm0
	movl	%eax, %esi
	movaps	%xmm0, 80(%rsp)
	call	clock
	leaq	.LC6(%rip), %rcx
	pxor	%xmm1, %xmm1
	subl	%esi, %eax
	cvtsi2sdl	%eax, %xmm1
	divsd	.LC3(%rip), %xmm1
	movsd	%xmm1, 40(%rsp)
	call	printf
	leaq	80(%rsp), %rcx
	call	print_array
	movsd	40(%rsp), %xmm1
	leaq	.LC7(%rip), %rcx
	movq	%xmm1, %rdx
	call	printf
	xorl	%eax, %eax
	addq	$104, %rsp
	popq	%rbx
	popq	%rsi
	ret
	.seh_endproc
	.section .rdata,"dr"
	.align 16
.LC1:
	.long	1065353216
	.long	1073741824
	.long	1077936128
	.long	1082130432
	.align 16
.LC2:
	.long	1084227584
	.long	1086324736
	.long	1088421888
	.long	1090519040
	.align 8
.LC3:
	.long	0
	.long	1083129856
	.def	__main;	.scl	2;	.type	32;	.endef
	.ident	"GCC: (MinGW-W64 x86_64-ucrt-posix-seh, built by Brecht Sanders, r5) 15.2.0"
	.def	printf;	.scl	2;	.type	32;	.endef
	.def	putchar;	.scl	2;	.type	32;	.endef
	.def	clock;	.scl	2;	.type	32;	.endef
