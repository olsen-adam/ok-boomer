//Accept an int value for width, and another for height
//Accept a 1D char array, convert entries in array to int array

//SOMETIME BEFORE RUNNING GET_MAZE
//jchar *jarr = (*jenv)->GetCharArrayElements(jenv, jCharArr, 0);
	
void get_maze()
{
	char c;
	
	//Will be read in from java function
		int inRows = 2; //Direct parameter from java function
		int inCols = 2; //Direct parameter from java function
		int jArrayLength = inRows * inCols;
		char mapData[jArrayLength] = { '+', 'G', 'S', ' ' }; 
	//----------------------------------	

	rows = inRows;
	cols = inCols;
	alloc_maze();

	for (i = 0; i < jArrayLength; ++i) {
		c = mapData[i];

		maze[i/inRows][i%inCols] = c;

		if (c =='S') {
			start_row = i/inRows;
			start_col = i%inCols;
		}
	}
}

//AFTER LOADING THE MAP
	//(*jenv)->ReleaseIntArrayElements(jenv, jCharArr, jarr, 0);

//-----------------------------------------------------------------------------------------//
//----------------------THE REST OF THE APPLICATION'S CALCULATIONS-------------------------//
//-----------------------------------------------------------------------------------------//

/*END OF APPLICATION {
	jintArray arrayOfMoves = env->NewIntArray(numberOfMoves);
	jint *arrMovesPointer = env->GetIntArrayElements(arrayOfMoves, NULL);
	
	for(int i = 0; i < numberOfMoves; i++)
	{
		arrMovesPointer[i] = x;
			//Where x == an integer from 0-3 (inclusive)
			//0 == Move up
			//1 == Move right
			//2 == Move down
			//3 == Move left
	}
	
	env->ReleaseIntArrayElements(arrayOfMoves, arrMovesPointer, NULL);
	
	return arrayOfMoves;	
} */