#include "Testing nameanalysis"

struct s {
    int l;
};

int a;
int *p;
struct s k;
void f(){

}
int main(){

    // Valid
    a = 10;
    f();
    k.l;
     *p;
    
    read_i();
    read_c();

    // Invalid
    {
        int i;
        i = read_i();
        {
            int block2;
        }
        block2; // error
    }
    i;  // error
    b;// error
    s;// error
    c;// error
    d();// error
    l;// error
    *pp;// error
}