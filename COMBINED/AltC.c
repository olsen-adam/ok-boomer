#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <string.h>
#include "AltJ.h"

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
int movesTaken;
int lastMoveTaken[50000];
enum terrain { //declares different parts of the grid
    empty,
    wall,
    goal,
    crumb
};
long long Nodes;
typedef struct {
    int isOpen;
    int fValueNode;
    int gValueNode;
    int hValueNode;
} node;

void alloc_maze();
void alloc_visited();
void get_maze();
void get_visited();
int dfs();
int newManhatDist();
int aStar();
void add_crumbs();
void print_maze();
void print_visited();

JNIEXPORT jintArray JNICALL Java_AltJ_cFunctionName
(JNIEnv* jenv, jobject jobj, 
jcharArray jMapChars, jint jMapW, jint jMapH, jint jSearchType)
{
	//set rows and cols, and user input
		rows = jMapH;
		cols = jMapW;
		userInput = jSearchType;
	//Get the length, and entries of java map data array
		const jsize jLength = (*jenv)->GetArrayLength(jenv, jMapChars);
		jchar *jArray = (*jenv)->GetCharArrayElements(jenv, jMapChars, 0);
	//Copy java array elements to a C array
		char mapData[jLength];
		int i;
		for (i = 0; i < jLength; i++) {
			mapData[i] = jArray[i];
		}
	//New get_maze function
		get_maze(mapData, jLength);
		get_visited();
	//No longer need java version of map data
		//(It is copied to C variable maze[][])
		(*jenv)->ReleaseCharArrayElements(jenv, jMapChars, jArray, 0);
	
			printf("\n"); printf("Map data from Java, printed from C:"); printf("\n");
			print_maze();
	
			//----------------------------------------------------------
			//Now that the map data is initialized, calculations go here
			//----------------------------------------------------------
				if (userInput == 1) {
					//-----------------------DFS-------------------------
					dfs(start_row, start_col); //Self-loops
					
				} else if ((userInput == 2)||(userInput == 3)) {
					//-------------MANHATTAN DISTANCE-------------------------
					// 2 == Without Eliminate Redundancies
					// 3 == With Eliminate Redundancies
					Nodes = 0;
					for( depth = 1; depth < 500; depth++ )
					{
						//printf( "Depth %2d = ", depth );
						success = newManhatDist(start_col, start_row, depth, 0, currMove);
						//printf( "Depth %2d = %15lld nodes\n", depth, Nodes );
						if( success == 1 ) {
							break;
						}
					}
				} else if (userInput == 4) {
					//------------------------A*-------------------------
					Nodes = 0;
					for ( depth = 1; depth < 500; depth++) {

						node numNode[62500];
						success = aStar(start_col, start_row, depth, 0, currMove);
						//printf("(F: %d, G: %d, H: %d) ", numNode[Nodes].fValueNode, numNode[Nodes].gValueNode, numNode[Nodes].hValueNode);
						if (success == 1) {
							break;
						}
					}
				}
			//----------------------------------------------------------
			//----------------------------------------------------------

			jintArray arrayOfMoves = (*jenv)->NewIntArray(jenv, testNum);
			jint *arrMovesPointer = (*jenv)->GetIntArrayElements(jenv, arrayOfMoves, 0);
		
			movesTaken = testNum;
			while (movesTaken > 1) {
				if (lastX[movesTaken] - lastX[movesTaken - 1] == 1) {
					//moved right
					lastMoveTaken[movesTaken] = 1;
				}else if(lastX[movesTaken] - lastX[movesTaken - 1] == -1) {
					//moved left
					lastMoveTaken[movesTaken] = 3;
				}else if(lastY[movesTaken] - lastY[movesTaken - 1] == 1) {
					//moved down
					lastMoveTaken[movesTaken] = 2;
				}else if(lastY[movesTaken] - lastY[movesTaken - 1] == -1) {
					//moved up
					lastMoveTaken[movesTaken] = 0;
				}
				arrMovesPointer[movesTaken] = lastMoveTaken[movesTaken];
				//printf("(LM: %d, MT: %d) ",lastMoveTaken[movesTaken], movesTaken);
				movesTaken--;
			}
			(*jenv)->ReleaseIntArrayElements(jenv, arrayOfMoves, arrMovesPointer, 0);
			
		return arrayOfMoves;	
}

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
	
void get_maze(char tempMapData[], int length)
{
	char c;

	alloc_maze();
	
	int i;
	for (i = 0; i < length; ++i) {
		c = tempMapData[i];

		maze[i/cols][i%cols] = c;

		if (c =='S') {
			start_row = i/cols;
			start_col = i%cols;
		}
	}
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
		
		if (userInput == 3) { //DO ELIM REDUND
			if (abs( currMove - lastMove ) == 1) {
				continue;
			}
		}

		manDist = abs( newcol - goal_col ) + abs( newrow - goal_row );

		if( g+1 + manDist > d ) {
			continue;
		}

		success = newManhatDist(newrow, newcol, d, g + 1, currMove);

		/* JS FIX */
		if( success ) {
			/* Added printing out the solution -- comes out in reverse order */
			//printf( "SOLUTION %2d: (%d,%d)\n", g, newcol, newrow );
			lastX[g + 1] = newcol;
			lastY[g + 1] = newrow;
			testNum++;

			break;
		}
	}
	return( success );
}

int aStar(int row, int col, int d, int g, int lastMove) {
    int currNodeG;
    int currNodeH;
    int currNodeF;
    int currCol;
    int currRow;
    int oldRow;
    int oldCol;

    int* current = &visited[row][col];

    Nodes++;
    node openNumNode[62500];
    node closeNumNode[62500];


    if (*current == goal) {
		return 1;
	}

	success = 0;

	for( i = 0; i < 4; i++ ) {
		newcol = col;
		newrow = row;

		switch( i ) {
			case 0: /* left */
			//if (lastMove != 1) {

			//}
			break;
			case 1: /* right */
			//if (lastMove != 0) {

		   // }
			break;
			case 2: /* up */
			//if (lastMove != 3) {

			//}
			break;
			case 3: /* down */
			//if (lastMove != 2) {

			//}
			break;
			default:
			printf( "Should never happen!\n" );
		}
	}
    //currCol

    row = 6; //used for testing
    col = 2; //used for testing

	openNumNode[1].gValueNode = abs( start_col - newcol ) + abs( start_row - newrow );
	openNumNode[1].hValueNode = pow( abs( goal_col - newcol ), 2) + pow( abs( goal_row - newrow ), 2);
	openNumNode[Nodes].fValueNode = currNodeH + currNodeG;

	success = aStar(newrow, newcol, d, g + 1, currMove);

	return( success );
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