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

int newManhatDist(int row, int col, int d, int g) {

int* current = &visited[row][col];

if (*current == goal) {
/* JS wrong result! */
// return 0;
return 1;
}

/* I put the error test first so we do not have to indent the rest of the program.
  All this meant is moving the error message from the end of the program to here.
  Also, in your code, when it isnot empty, you return a 1. Shouldn;t it be a 0?
*/
//if (*current != empty) {
// printf("Error could not pathfind");
// return 0;
//}

/* From now on we know that we are on an empty square */

/* You did the following to have the program avoid going back on itself.
  For the first option -- simple search -- we don't mind if the man does the stupid thing and
  goes back from where he was from. In the next step up, we add the capability to avoid this repeat. */
        //*current = wall;
        testNum++;
        lastX[testNum] = col;
        lastY[testNum] = row;


/* Debugging */
        //printf("(%d, %d) ", col, row);  //This calls the whole coordinates.
                                        //Call row and col right here as these are the directions it would take.


/* Here is the big thing -- I want to eliminate redundant code */
/* Here I use 0, 1, 2, 3 for left, right, up, down.
  You can use more meaningful names than that.
*/
/* JS This has to be inside the loop */
// newcol = col;
// newrow = row;
success = 0; /* 0 = fail to find a solution; 1 = found a solution */
for( i = 0; i < 4; i++ ) {
/* JS Moved to inside loop */
newcol = col;
newrow = row;
switch( i ) {
   case 0: /* left */
newcol = col - 1;
break;
   case 1: /* right */
newcol = col + 1;
break;
   case 2: /* up */
newrow = row - 1;
break;
   case 3: /* down */
newrow = row + 1;
break;
   default:
printf( "Should never happen!\n" );
}


/* See if the move will hit a wall */
if(visited[newrow][newcol] == wall) {
/* If so, move on to consider the next move */
continue;
}

/* See how we now have one calculation that handles all four cases! */
manDist = abs( newcol - goal_col ) + abs( newrow - goal_row );

/* For debugging */
          printf("Move to (%d,%d), MD=%d | ", newcol, newrow, manDist );

/* Here is where MD saves us. This is the key line of code that cuts off huge swaths of the search */
/* JS off by one error */
/* if( g + manDist > d ) { */
if( g+1 + manDist > d ) {
/* If we have moved down the tree g moves and we have at least manDist to go, but that exceeds
  the maximum depth we are allowed to search, then stop searching along this line!
*/
continue;
}

/* Pass the new row/col down recursively and add 1 to g -- we are now one move deeper in the search */
success = newManhatDist(newrow, newcol, d, g + 1);
if(visited[newrow][newcol] == goal ) {
printf( "Found a solution!!\n" ); /* No need to continue searching */
break;
}

}
/* If we go through all 4 cases above and fail, success is a 0 */
return( success );

}
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
        manDistNewLeft = abs((col - 1) - goal_col) + abs(row - goal_row); // calculates man distance between each possible move.
        manDistNewRight = abs((col + 1)  - goal_col) + abs(row - goal_row);
        manDistNewUp = abs(col - goal_col) + abs((row - 1) - goal_row);
        manDistNewDown = abs(col - goal_col) + abs((row + 1) - goal_row);

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
//printf("Hello, would you like to execute:\n\n[1] DFS\n[2] With Man Distance\n\n");
//scanf("%d", &userInput);
clock_t t; //clock
t = clock(); //clock
get_maze("maze.txt"); //get maze file
get_visited();
printf("\nOriginal Maze:\n\n");
print_maze();
/*if (userInput == 1) {
    dfs(start_row, start_col); //This calls smart one and only uses spaces that are nessecary. And not repeating
} else if (userInput == 2) {
    manhatDist(start_row, start_col);
} else if (userInput == 3) {
    newManhatDist(start_row, start_col, d, g);
}*/
for( depth = 1; depth < 200; depth++ )
   {
/* JS */
printf( "Depth %d start\n", depth );
      success = newManhatDist(start_col, start_row, depth, 0 );
/* JS */
printf( "Depth %d result %d\n", depth, success );
if( success == 1 ) {
break;
}
   }
add_crumbs();
printf("\n\nNew Maze:\n\n");
print_maze(); //clock
t = clock() - t; //clock
double time_taken = ((double)t)/CLOCKS_PER_SEC; //clock
printf("\nStats:\n\n");
printf("Program took %f seconds to execute \n", time_taken); //clock
printf("Program computed %f moves per second.\n", testNum / time_taken); //clock
printf("It took %d moves!\n", testNum);
printf("Start is at: (%d, %d)\n", start_col, start_row);
printf("Goal is at: (%d, %d)\n\n\n", goal_col, goal_row);
printf("%d, %d", lastX[250], lastY[250]); // used to test that the array worked. It did :)


return 0;
}
