#include "Test[5] -> Expected: Pass"
#include "Tests: Struct, Var, Func declaration"
// struct /* declarations */ //
struct my {
    int x;
    char z;
    void a;
};

struct my {
    struct yours b;
};

/*
 A lot of var decl
 */
int i;
int * i_ptr;
int iArray[10];
int *array_i_ptr[10];

char c;
char *c_pointer ;
char cArray [10];
char * array_c_ptr[10];

void v;
void * v_ptr;
void vArray[302];
void * array_v_ptr [300];

struct MyStruct s;
struct YourStruct* s_ptr;
struct TheirStruct sArray[20];
struct OurStruct * array_s_ptr[300];

// Functions
char returnChar (){}
int main (int args){
    int i;
}

void* nothing(int no, int yes, struct alpha a){
    char* chai[200];
    void v;
} 