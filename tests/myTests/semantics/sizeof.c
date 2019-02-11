#include "testing sizeof"

struct st {
    int a;
};
void main(){

    int arr[20];
    int *ar[20];
    int a;
    a = sizeof(int);
    a = sizeof(char);
    a = sizeof(void);
    a = sizeof(struct st);

    a = sizeof(int *);  
    a = sizeof(char *);
    a = sizeof(void *);
    a = sizeof(struct st);

    // Get answer from question in Piazza
    //a = sizeof(arr);
    //a = sizeof(*arr);
    //a = sizeof(20 * sizeof(int));
    //a = sizeof(1);
    //a = sizeof('a');
}