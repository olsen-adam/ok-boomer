#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <string.h>


char** maze;
int** visited;
int rows;
int cols;
int start_row;
int start_col;
int goal_row;
int goal_col;
int testNum;
int localDirection = 69;
int randomMove;
int userInput;
int hasMovedFromStart = 0;
int manDistOld;
int manDistNewLeft;
int manDistNewRight;
int manDistNewUp;
int manDistNewDown;
int manDistNextMove = 0;
int lastX[10000];
int lastY[10000];
int i;
int depth;
int newcol;
int newrow;
int success;
int g;
int d;
int manDist;
int lastMove;
int currMove;



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
                            //printf("Start is at: (%d, %d)\n", start_col, start_row);
                            // printf("Goal is at: (%d, %d)\n\n", j, i);
                goal_row = i;
                goal_col = j;

            } else {
                visited[i][j] = empty;

            }
        }
    }
}

/*

Start = [x1,y1] location
Goal = [x2,y2] location

Iterative Deepending Search (Start, Goal)
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
   }(row, col + 1)
   undo make move
   if( result > 0 ) return( result );
}
return( -1 ) // did not find a solution


}
*/

/*

I no longer use stupidDfs, just the regular DFS and ManDist

int stupidDfs(int row, int col) {

    randomMove = ( rand()%4 + 1 );

    int* current = &visited[row][col];

    if (*current == goal) {
        return 1;
    }
    if (*current == empty) { //if current position player is on is empty, it will place a crumb
        *current = crumb;



    //printf("(%d, %d) ", col, row); //This calls the whole coordinates.
        //Call row and col right here as these are the directions it would take.

    //printf("[[%d]]",randomMove); testing to make sure random is random
    testNum++;


        if (stupidDfs(row, col - 1) && randomMove == 1) {
            *current = crumb;
            //int localDirection = 0;

            //looks one left and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
            //printf("%s, ", &current[1]);
            return 1;
        }
        if (stupidDfs(row - 1, col) && randomMove == 2) {
            *current = crumb;
            //int localDirection = 2;
            //printf("(%d, %d)", row, col);
             //looks one right and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
            //printf("%s, ", &current[1]);
            return 1;
        }
        if (stupidDfs(row, col + 1) && randomMove == 3) {
            *current = crumb;
            //int localDirection = 1;
           //printf("(%d, %d)", row, col);
             //looks one up and checks if it is an empty spot, if so place a crumb.
            //printf("%s, ", &current[0]);
           // printf("%s, ", &current[1]);
            return 1;
        }
        if (stupidDfs(row + 1, col) && randomMove == 4) {
            *current = crumb;
            //int localDirection = 3;
            //printf("(%d, %d)", row, col);
             //looks one down and checks if it is an empty spot, if so place a crumb.
       //     printf("%s, ", &current[0]);
       //     printf("%s, ", &current[1]);
            return 1;
        }
    }

    return 0;
}
*/

int dfs(int row, int col) {
int* current = &visited[row][col];

if (*current == goal) {
return 1;
}

if (*current == empty) {
*current = crumb;
testNum++;

        lastX[testNum] = col;
        lastY[testNum] = row;

        //printf("(%d, %d) ", col, row); //This calls the whole coordinates.
        //Call row and col right here as these are the directions it would take.



if (dfs(row, col - 1)){
*current = crumb;
return 1;
}
if (dfs(row + 1, col)){
*current = crumb;
return 1;
}
if (dfs(row, col + 1)){
*current = crumb;
return 1;
}
if (dfs(row - 1, col)){
*current = crumb;
return 1;
}
}

return 0;
}

/* JS LOOK -- deleted necessary code and made changes */

/* Use this to count how big the tree is */
/* Might be a big number! */
long long Nodes;

int newManhatDist(int row, int col, int d, int g, int lastMove) {

	int i;
	int newrow;
	int newcol;
	int success;
	int manDist;


	int* current = &visited[row][col];

	// Count how many nodes in our search tree */
	Nodes++;

	if (*current == goal) {
		return 1;
	}

        //*current = wall;
/* JS DELETE -- this is a bug. Can you gigure out why? */
        // testNum++;
        // lastX[testNum] = col;
        // lastY[testNum] = row;

	success = 0; /* 0 = fail to find a solution; 1 = found a solution */
	for( i = 0; i < 4; i++ ) {

		newcol = col;
		newrow = row;

		switch( i ) {
   		    case 0: /* left */
            //if (lastMove != 1) {
			newcol = col - 1;
			currMove = 0;
			//}
			break;
   		    case 1: /* right */
            //if (lastMove != 0) {
			newcol = col + 1;
			currMove = 1;
           // }
			break;
   		    case 2: /* up */
            //if (lastMove != 3) {
			newrow = row - 1;
			currMove = 3;
            //}
			break;
   		    case 3: /* down */
			//if (lastMove != 2) {
			newrow = row + 1;
			currMove = 4;
			//}
			break;
   		    default:
			printf( "Should never happen!\n" );
		}


		/* See if the move will hit a wall */
		if (visited[newrow][newcol] == wall) {
			continue;
		}
		if (abs( currMove - lastMove ) == 1) {
            continue;
		}

		manDist = abs( newcol - goal_col ) + abs( newrow - goal_row );

		if( g+1 + manDist > d ) {
			continue;
		}

		success = newManhatDist(newrow, newcol, d, g + 1, currMove);

		/* JS FIX */
		if( success ) {
			/* Added printing out the solution -- comes out in reverse order */
			printf( "SOLUTION %2d: (%d,%d)\n", g, newcol, newrow );
			lastX[g + 1] = newcol;
			lastY[g + 1] = newrow;

			break;
		}
	}

	return( success );
}

/* JS LOOK END */


int manhatDist(int row, int col) {



    int* current = &visited[row][col];


    if (*current == goal) {
        return 1;
    }


    //printf("%d, ",localDirection);
    if (*current == empty) {
        *current = wall;
        testNum++;

        lastX[testNum] = col;
        lastY[testNum] = row;

        //printf("(%d, %d) ", col, row);  //This calls the whole coordinates.
                                        //Call row and col right here as these are the directions it would take.


/*      //this next line calculates the manhat distance before moving, in order to compare if the next move is better or worse
        //SIKE I AINT USING IT :))))))) it is useless so...

        if (hasMovedFromStart == 0) { //with manhatten distance, the lower the number is the closer the user is to the goal
            hasMovedFromStart = 1; //measures manh distance from start and end if this hasnt been acsessed since beginning
            manDistOld = abs(start_col - goal_col) + abs(start_row - goal_row);
        } else if (hasMovedFromStart != 0) {
            manDistOld = abs(col - goal_col) + abs(row - goal_row);
        } else {
        printf("Error");
        return 0;
        }
*/
        manDistNewLeft = abs(( col - 1 ) - goal_col) + abs( row - goal_row); // calculates man distance between each possible move.
        manDistNewRight = abs(( col + 1)  - goal_col) + abs( row - goal_row);
        manDistNewUp = abs( col - goal_col) + abs(( row - 1) - goal_row);
        manDistNewDown = abs( col - goal_col) + abs(( row + 1) - goal_row);

        if (visited[row][col + 1] == wall) {
            manDistNewRight = 1337;

        }
        if (visited[row][col - 1] == wall) {
            manDistNewLeft = 1337;
        }
        if (visited[row + 1][col] == wall) {
            manDistNewDown = 1337;
        }
        if (visited[row - 1][col] == wall) {
            manDistNewUp = 1337;
        }

        if (manDistNewLeft <= manDistNewRight && manDistNewLeft <= manDistNewUp && manDistNewLeft <= manDistNewDown) {
          manDistNextMove = 1;
          printf("(%dL, %dR, %dU, %dD, %d Next) | ", manDistNewLeft, manDistNewRight, manDistNewUp, manDistNewDown, manDistNextMove);
        } else if (manDistNewRight <= manDistNewLeft && manDistNewRight <= manDistNewUp && manDistNewRight <= manDistNewDown) {
          manDistNextMove = 2;
          printf("(%dL, %dR, %dU, %dD, %d Next) | ", manDistNewLeft, manDistNewRight, manDistNewUp, manDistNewDown, manDistNextMove);
        } else if (manDistNewUp <= manDistNewRight && manDistNewUp <= manDistNewLeft && manDistNewUp <= manDistNewDown) {
          manDistNextMove = 3;
          printf("(%dL, %dR, %dU, %dD, %d Next) | ", manDistNewLeft, manDistNewRight, manDistNewUp, manDistNewDown, manDistNextMove);
        } else if (manDistNewDown <= manDistNewRight && manDistNewDown <= manDistNewUp && manDistNewDown <= manDistNewLeft) {
          manDistNextMove = 4;
          printf("(%dL, %dR, %dU, %dD, %d Next) | ", manDistNewLeft, manDistNewRight, manDistNewUp, manDistNewDown, manDistNextMove);
        } else {
        printf("Error while calculating man dist");
        }

        if (manDistNextMove == 1) {
            if (manhatDist(row, col - 1)) {
                *current = crumb;
                return 1;
                }

        }
        if (manDistNextMove == 2) {
            if (manhatDist(row, col + 1)) {
                *current = crumb;
                return 1;
            }
        }
        if (manDistNextMove == 3) {
            if (manhatDist(row - 1, col)) {
                *current = crumb;
                return 1;
            }
        }
        if (manDistNextMove == 4) {
            if (manhatDist(row + 1, col)) {
                *current = crumb;
                return 1;
            }
        }
    }
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
	printf("Start\n");
	printf("Would you like to execute:\n[1] Dfs\n[2] ManHat");
	scanf("%d", &userInput);
	clock_t t; //clock
	t = clock(); //clock
	get_maze("maze.txt"); //get maze file
	get_visited();
	printf("\nOriginal Maze:\n\n");
	print_maze();


/* JS LOOK START */
	/* Added Nodes to count tree size */

/* JS LOOK END */
if (userInput == 1) {
dfs(start_row, start_col);
add_crumbs();
printf("\n\nNew Maze:\n\n");
print_maze();
} else if (userInput == 2) {
	Nodes = 0;
	for( depth = 1; depth < 100; depth++ )
   	{
		//printf( "Depth %2d = ", depth );
		success = newManhatDist(start_col, start_row, depth, 0, currMove);
		printf( "Depth %2d = %15lld nodes\n", depth, Nodes );
		if( success == 1 ) {
			break;
		}
   	}
}
t = clock() - t; //clock
double time_taken = ((double)t)/CLOCKS_PER_SEC; //clock
printf("\nStats:\n\n");
printf("Program took %f seconds to execute \n", time_taken); //clock
printf("Program computed %f moves per second.\n", testNum / time_taken); //clock
printf("It took %d moves!\n", testNum);
printf("Start is at: (%d, %d)\n", start_col, start_row);
printf("Goal is at: (%d, %d)\n\n\n", goal_col, goal_row);
printf("%d, %d", lastX[4], lastY[4]); // used to test that the array worked. It did :)


return 0;
}
