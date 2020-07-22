#include <stdio.h>
#include <stdlib.h>
#include <time.h>


char** maze;
int** visited;
int rows;
int cols;
int start_row;
int start_col;
int testNum;

enum terrain { //declares different parts of the grid
    empty,
    wall,
    goal,
    crumb
};

void alloc_maze()
{
	maze = malloc(rows * sizeof(char*));

	int i;
	for (i = 0; i < rows; ++i){
		maze[i] = malloc(cols * sizeof(char*));
	}
}

void alloc_visited()
{
	visited = malloc(rows * sizeof(char*));

	int i;
	for (i = 0; i < rows; ++i){
		visited[i] = malloc(cols * sizeof(char*));
	}
}

void get_maze(char* file_name)
{
	char c;
	char rows_s[3] = { '\0' };
	char cols_s[3] = { '\0' };
	int rows_i = 0;
	int cols_i = 0;
	int swap = 0;

	FILE* maze_file = fopen(file_name, "r");

	if (maze_file) {
		while ((c = getc(maze_file)) != EOF) {
			if (c == '\n') {
				break;
			} else if (c == ','){
				swap = 1;
			} else if (!swap) {
				rows_s[rows_i] = c;
				rows_i++;
			} else {
				cols_s[cols_i] = c;
				cols_i++;
			}
		}
	} else {
		printf("No such file!");
		return;
	}

	rows = atoi(rows_s);
	cols = atoi(cols_s);

	alloc_maze();

	int i,j;

	for (i = 0; i < rows; ++i) {
		for (j = 0; j < cols; ++j) {

			if ((c = getc(maze_file)) == '\n') {
				c = getc(maze_file);
			}

			maze[i][j] = c;

			if (c =='S') {
				start_row = i;
				start_col = j;
			}
		}
	}

	fclose(maze_file);
}

void get_visited() {
    alloc_visited();
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (maze[i][j] == '+') {
                visited[i][j] = wall;
            } else if (maze[i][j] == 'G') {
                visited[i][j] = goal;
            } else {
                visited[i][j] = empty;

            }
        }
    }
}

int astar (int row, int col) {

Start = [x1,y1] location
Goal = [x2,y2] location

//Iterative Deepending Search (Start, Goal)
if( Start == Goal ) return( 0 ) // solution length
for( depth = 1; ; depth++ ) {
  result = Search( Start, Goal, 0, depth, -1 );
  if( result != 0 ) break;
}
return( depth );

//Here are three search algorithms:
// Search1( from, to, g, depth, previousmove ) // Simple search
if( from == to ) return( g ) // solution depth
if( g == depth ) return( -1 ) // cannot find solution within 'depth' moves
for each legal move m (left, right, up, down ) {
   newfrom = make move( from, m );
   result = Search( newfrom, to, g+1 depth );
   undo make move
   if( result > 0 ) return( result );
}
return( -1 ) // did not find a solution

Search2( from, to, g, depth, previousmove ) // Simple search -- eliminating redundant moves
if( from == to ) return( g ) // solution depth
if( g == depth ) return( -1 ) // cannot find solution within 'depth' moves
for each legal move m (left, right, up, down ) {
   if( m is the opposite of previousmove ) // eliminate a left followed by a right (and vice versa)
                                                                 // eliminate a down followed by an up (vice versa)
      continue;
   newfrom = make move( from, m );
   result = Search( newfrom, to, g+1, depth, m );
   undo make move
   if( result > 0 ) return( result );
}
return( -1 ) // did not find a solution

Search3( from, to, g, depth, previousmove ) // Use a heuristic
if( from == to ) return( g ) // solution depth
if( g == depth ) return( -1 ) // cannot find solution within 'depth' moves
for each legal move m (left, right, up, down ) {
   // Include this if eliminate redundant moves is enabled
   if( m is the opposite of previousmove ) // eliminate a left followed by a right (and vice versa)
                                                                 // eliminate a down followed by an up (vice versa)
      continue;
   // end of if eliminate redundant moves is enabled
   newfrom = make move( from, m );

   // compute heuristic
   hx = ( x of newfrom - x of to )
   if( hx < 0) hx = -hx   // Turn -ve values to +ve values

   hy = ( y of newfrom - y of to )
   if( hy < 0) hy = -hy   // Turn -ve values to +ve values

   h = hx + hy // minimum # of moves to reach goal

   if( g + h <= depth { //Only search if possible to solve problem within the depth constratint
      result = Search( newfrom, to, g+1, depth, m )
   }
   undo make move
   if( result > 0 ) return( result );
}
return( -1 ) // did not find a solution

*//
}


int dfs(int row, int col) {

    int* current = &visited[row][col];
    testNum++;
    if (*current == goal) {
        return 1;
    }

    if (*current == empty) {
        *current = wall;

        if (dfs(row, col - 1)) {
            *current = crumb; //looks one left and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
            //printf("%s, ", &current[1]);
            return 1;
        }
        if (dfs(row, col + 1)) {
            *current = crumb; //looks one right and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
            //printf("%s, ", &current[1]);
            return 1;
        }
        if (dfs(row - 1, col)) {
            *current = crumb; //looks one up and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
           // printf("%s, ", &current[1]);
            return 1;
        }
        if (dfs(row + 1, col)) {
            *current = crumb; //looks one down and checks if it is an empty spot, if so place a crumb.
       //     printf("%s, ", &current[0]);
       //     printf("%s, ", &current[1]);
            return 1;
        }
    }

    return 0;
}


void add_crumbs(){
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            //printf("(%d, %d)", i, j);
            if (maze[i][j] != 'S') {
                if (visited[i][j] == crumb) {
                    maze[i][j] = '@';

                }
            }
        }
    }
}

void print_maze() {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%c ", maze[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}

void print_visited() {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%d ", visited[i][j]);
        }
        printf("\n");
    }
    printf("\n");
}


int main(){


clock_t t; //counts time
t = clock(); //""
get_maze("maze.txt");
get_visited();
printf("\nOriginal Maze:\n\n");
print_maze();
dfs(start_row, start_col);
add_crumbs();
printf("\nNew Maze:\n\n");
print_maze();
t = clock() - t;
double time_taken = ((double)t)/CLOCKS_PER_SEC;
printf("Program took %f seconds to execute \n", time_taken);
printf("Program computed %f computations per second.\n", testNum / time_taken);
printf("Press ENTER key to Continue\n");
getchar();
return 0;
}

