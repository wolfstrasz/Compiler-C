.data
############################################################
# Struct declarations: 
# struct st { 
	# INT  a : offset = 0 : size = 4
	# CHAR  b : offset = 4 : size = 4
	# CHAR [6]  str : offset = 8 : size = 8
	# INT [3]  arr : offset = 16 : size = 12
	# } size = 28
############################################################
# st s1
s1:	.space 28
# STRING
$STR0: .asciiz "STRING\00"
############################################################
	# Load all temp registers
	addi $sp,$sp,0
	# Store all temp registers
	addi $sp,$sp,0
.text
	j main
main: 
	addi $sp, $sp, -28
	la   $t9, s1
	li  $t8, 0
	add  $t9, $t9, $t8
	li $t8,2
	# WORD STORING
	sw   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 4
	add  $t9, $t9, $t8
	li $t8,'B'
	# WORD STORING
	sw   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,'S'
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,'T'
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	la   $t8, $STR0
	li $s7,2
	li  $s6, 1
	mul $s7, $s7, $s6
	add  $t8, $t8, $s7
	lb   $t8, 0($t8)
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,3
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,'I'
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,4
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,'N'
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,5
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,'G'
	# BYTE STORING
	sb   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,10
	# WORD STORING
	sw   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,11
	# WORD STORING
	sw   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	li $t8,12
	# WORD STORING
	sw   $t8, 0($t9)
	la   $t9, s1
	li  $t8, 0
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 4
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	li $t9,'\n'
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,3
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,4
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,5
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	la   $t9, s1
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	li $t9,'\n'
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	li $t9,'\n'
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	la   $t8, s1
	# STRUCT STORING
	lw   $s7, 0($t8)
	sw   $s7, 0($t9)
	lw   $s7, 4($t8)
	sw   $s7, 4($t9)
	lw   $s7, 8($t8)
	sw   $s7, 8($t9)
	lw   $s7, 12($t8)
	sw   $s7, 12($t9)
	lw   $s7, 16($t8)
	sw   $s7, 16($t9)
	lw   $s7, 20($t8)
	sw   $s7, 20($t9)
	lw   $s7, 24($t8)
	sw   $s7, 24($t9)
	addi $t9, $fp, -28
	li  $t8, 0
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 4
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	li $t9,'\n'
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,3
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,4
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 8
	add  $t9, $t9, $t8
	li $t8,5
	li  $s7, 1
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lb   $t9, 0($t9)
	li   $v0, 11
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,0
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,1
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	addi $t9, $fp, -28
	li  $t8, 16
	add  $t9, $t9, $t8
	li $t8,2
	li  $s7, 4
	mul $t8, $t8, $s7
	add  $t9, $t9, $t8
	lw   $t9, 0($t9)
	li   $v0, 1
	add  $a0, $0, $t9
	syscall
	addi $sp, $sp, 28
