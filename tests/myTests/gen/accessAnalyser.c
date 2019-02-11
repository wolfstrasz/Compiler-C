#include "sasda"

struct st {
	int a;
	char b;
};
struct st2 {
	int a[100];
	struct st s2;
};

struct st s[10];
struct st2 s2[10];
struct st ss;
struct st2 ss2;
int a[20];
char b[50];

int main() {
	int c[10];
	char d[10];

	// SIMPLE ARRAY ACCESS
	s2[0];
	s[0];
	a[0];
	b[0];
	c[0];
	d[0];
	
	// SIMPLE FIELD ACCESS
	ss.a;
	ss.b;
	ss2.a;
	ss2.s2;

	// FIELD ACCESS OF ARRAY ACCESS
	ss2.a[0];

	// ARRAY ACCESS OF FIELD ACCESS
	s[0].a;
	s[0].b;

	// FIELD ACCESS OF FIELD ACCESS
	ss2.s2.a;
	ss2.s2.b;

	// CHECK ARRAY ACCESS OF STRING LIT
	"howdy"[0];
}