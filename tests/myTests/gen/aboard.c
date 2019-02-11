struct st {
	int row;
	int column;
	char colour;
};

struct st board[64];

struct st* tile[64];


void copy(struct st s, struct st* sp){
	*sp = s;
}

void printTile(struct st s){
	print_i(s.row);
	print_c(':');
	print_i(s.column);
	print_c('=');
	print_c(s.colour);
	print_c('\n');
}

int main (){
	int i;
	int j;
	int board_length;
	board_length = 3;


	// init board
	i = 0;
	while (i < board_length){
		j = 0;
		while (j < board_length){
			int item;
			item = i*board_length + j;
			// Init board
			board[item].row = i;
			board[item].column = j;
			if ( (i + j) % 2 == 0)
				board[item].colour = 'W';
			else board[item].colour = 'B';

			printTile(board[item]); 

			// init pointer
			tile[item] = (struct st *) mcmalloc(sizeof(struct st));
			copy (board[item], tile[item]);

			printTile(*(tile[item]));
			j = j + 1;
		}
		i = i + 1;
	}

	// init pointer



}