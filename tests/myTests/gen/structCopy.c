
struct st {
	int a;
	char c;
};


void copy(struct st s, struct st* sp){

	*sp = s;

}

int main(){

	struct st s;
	struct st *sp;
    struct st sarr[10];

	sp = (struct st*)mcmalloc(sizeof(struct st));
	s.a = 200;
	s.c = 'c';

	copy (s,sp);
	print_i((*sp).a);


    sarr[2].a = 300;
    copy(sarr[2],sp);
    print_i((*sp).a);
	return 0;
}