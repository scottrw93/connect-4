package connect4;
//Scott Williams 11356176

import java.util.ArrayList;
import java.util.Stack;


public class Board {
	public static int FREE = 0;
	public static int YELLOW = 1;
	public static int RED = 2;

	public static int ERROR = -1;
	public static int NO_RESULT = 0;
	public static int DRAW = 1;
	public static int WIN = 2;

	private int NUM_ROWS = 5;
	private int NUM_COLS = 7;
	private int IN_A_ROW = 4;

	private int FULL = 1;
	private int BLOCKED = 2;

	private int[][] board;
	private int whosTurn;
	private int[] validMoves;
	private Stack<String> pastMoves;

	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_RESET = "\u001B[0m";

	public Board(int startingColor){
		board = new int[NUM_ROWS][NUM_COLS];
		whosTurn = startingColor;
		validMoves = new int[NUM_COLS+1];
		pastMoves = new Stack<String>();
	}

	public ArrayList<String> possibleMoves(){
		ArrayList<String> possibleMoves = new ArrayList<String>();

		for(int i=1;i<=NUM_COLS;i++){
			if(isValidMove("d"+i))
				possibleMoves.add("d"+i);
		}
		for(int i=1;i<=NUM_COLS;i++){
			if(isValidMove("v"+i))
				possibleMoves.add("v"+i);
		}
		return possibleMoves;
	}

	public boolean undo(){
		if(pastMoves.isEmpty()){
			return false;
		}
		char[] temp = pastMoves.pop().toCharArray();
		char typeOfMove = temp[0];
		int col = Character.getNumericValue(temp[1])-1;
		int row = getRow(col);

		if(typeOfMove=='d'){
			board[row][col] = FREE;
			whosTurn = whosTurn == YELLOW ? RED : YELLOW;
			validMoves[col] = FREE;

			if(!pastMoves.isEmpty()){
				char[] prevMove = pastMoves.peek().toCharArray();
				if(prevMove[0]=='v'){
					validMoves[Character.getNumericValue(prevMove[1])-1] = BLOCKED;
					validMoves[NUM_COLS] = BLOCKED;
				} else if(pastMoves.size()>1){
					String store = pastMoves.pop();
					char[] prevPrevMove = pastMoves.peek().toCharArray();
					if(prevPrevMove[0]=='v'){
						validMoves[NUM_COLS] = BLOCKED;
					}
					pastMoves.add(store);
				}
			}
		} else{
			whosTurn = whosTurn == YELLOW ? RED : YELLOW;
			validMoves[NUM_COLS] = FREE;
			validMoves[col] = FREE;
		}
		return true;
	}

	public int makeMove(String move){
		if(!isValidMove(move)){
			return ERROR;
		}

		char[] temp = move.toCharArray();
		char typeOfMove = temp[0];
		int colToDrop = Character.getNumericValue(temp[1])-1;

		pastMoves.add(move);

		if(typeOfMove=='d'){
			if(drop(colToDrop))//If col is now full block it
				validMoves[colToDrop] = FULL;
			unblock();
		} 
		else if(typeOfMove=='v'){
			validMoves[colToDrop] = BLOCKED;
			validMoves[NUM_COLS] = BLOCKED; 
		}

		whosTurn = whosTurn == YELLOW ? RED : YELLOW;//Changes who's turn it is
		return checkForResult(colToDrop, typeOfMove);
	}

	public int staticEvaluation(){
		int opColor = whosTurn;
		int color = opColor == YELLOW ? RED : YELLOW;
		int score = 0;

		for(int row=0; row<2;row++){
			for(int col=0;col<=3;col++){
				if(board[row][col]==color && board[row+1][col+1]==color 
						&& board[row+2][col+2]==color && board[row+3][col+3]==color){	
					return -1500000;//Definite loss for player to move; no need to check more
				} 
				else if(xInADiagRight(3, color, row, col)){// check for 3 out of 4 in a row diagonally
					score+=-10;
				}
				else if(xInADiagRight(2, color, row, col)){
					score+=-2;
				}
				if(board[row][col]==opColor && board[row+1][col+1]==opColor 
						&& board[row+2][col+2]==opColor && board[row+3][col+3]==opColor){
					score = 1000000;
				}
				if(xInADiagRight(3, opColor, row, col)){
					score = 1000000;//Can't return in case later there is a definite loss for player to move
				}
				else if(xInADiagRight(2, opColor, row, col)){
					score+=10;
				}
				else if(xInADiagRight(1, opColor, row, col)){
					score+=2;
				}
			}
		}

		for(int row=0; row<2;row++){
			for(int col=3;col<NUM_COLS;col++){
				if(board[row][col]==color && board[row+1][col-1]==color 
						&& board[row+2][col-2]==color && board[row+3][col-3]==color){
					return -1500000;
				} 
				else if(xInADiagLeft(3, color, row, col)){
					score+=-10;
				}
				else if(xInADiagLeft(2, color, row, col)){
					score+=-2;
				}
				if(board[row][col]==opColor && board[row+1][col-1]==opColor 
						&& board[row+2][col-2]==opColor && board[row+3][col-3]==opColor){
					score = 1000000;
				}
				if(xInADiagLeft(3, opColor, row, col)){
					score = 1000000;
				}
				else if(xInADiagLeft(2, opColor, row, col)){
					score+=10;
				}
				else if(xInADiagLeft(1, opColor, row, col)){
					score+=2;
				}
			}
		}

		for(int row = 0; row < NUM_ROWS; row++){
			for(int col = 0; col < 4; col++){
				if(board[row][col]==color && board[row][col+1]==color 
						&& board[row][col+2]==color && board[row][col+3]==color){
					return -1500000;
				} 
				else if(xInARow(3, color, row, col)){
					score+=-10;
				}
				else if(xInARow(2, color, row, col)){
					score+=-2;
				}
				if(board[row][col]==opColor && board[row][col+1]==opColor 
						&& board[row][col+2]==opColor && board[row][col+3]==opColor){
					score = 1000000;
				}
				if(xInARow(3, opColor, row, col)){
					score = 1000000;
				}
				else if(xInARow(2, opColor, row, col)){
					score+=10;
				}
				else if(xInARow(1, opColor, row, col)){
					score+=2;
				}
			}
		}

		for(int row = 0; row < 2; row++){
			for(int col = 0; col < NUM_COLS; col++){
				if(board[row][col]==color && board[row+1][col]==color 
						&& board[row+2][col]==color && board[row+3][col]==color){
					return -1500000;
				} 
				else if(board[row][col]==color && board[row+1][col]==color 
						&& board[row+2][col]==color && board[row+3][col]==FREE){
					score += -10;
				} 
				else if(board[row][col]==color && board[row+1][col]==color 
						&& board[row+2][col]==FREE && board[row+3][col]==FREE){
					score += -2;
				} 
				if(board[row][col]==opColor && board[row+1][col]==opColor 
						&& board[row+2][col]==opColor && board[row+3][col]==opColor){
					score = 1000000;
				}
				if(board[row][col]==opColor && board[row+1][col]==opColor 
						&& board[row+2][col]==opColor && board[row+3][col]==FREE){
					score = 1000000;
				}
				else if(board[row][col]==opColor && board[row+1][col]==opColor 
						&& board[row+2][col]==FREE && board[row+3][col]==FREE){
					score += 10;
				}
				else if(board[row][col]==opColor && board[row+1][col]==FREE 
						&& board[row+2][col]==FREE && board[row+3][col]==FREE){
					score += 2;
				}
			}
		}

		return score;
	}

	public boolean isTerminal() {
		if(!pastMoves.isEmpty()){
			char[] move = pastMoves.peek().toCharArray();
			int col = Character.getNumericValue(move[1])-1;
			char type = move[0];			
			return checkForResult(col, type)!=NO_RESULT || possibleMoves().isEmpty();
		}
		return possibleMoves().isEmpty();
	}

	public String toString(){
		String str = "";
		for(int i=NUM_ROWS;i>0;i--){
			for(int j=1;j<=NUM_COLS;j++){
				int piece = board[i-1][j-1];
				//Use commented part instead if Unix OS terminal, will color print
				if(piece==YELLOW) /*str+="[Y] ";//*/str+="["+ANSI_YELLOW+"Y"+ANSI_RESET+"] ";
				if(piece==RED) /*str+="[R] ";//*/str+="["+ANSI_RED+"R"+ANSI_RESET+"] ";
				if(piece==FREE) str+="[ ] ";
			}
			str = str.trim()+"\n";
		}
		return str.trim();
	}

	private boolean xInADiagRight(int x, int color, int row, int col) {
		int count = 0;
		int i = row;
		int j = col;

		while(i<=row+3){
			if(i!=0 && board[i-1][j]==FREE){
				return false;
			}
			if(board[i][j]==color){
				count+=1;
			} 
			else if(board[i][j]!=FREE){
				return false;
			}
			i++; j++;
		}

		return count==x;
	}

	private boolean xInADiagLeft(int x, int color, int row, int col) {
		int count = 0;
		int i = row;
		int j = col;

		while(i<=row+3){
			if(i!=0 && board[i-1][j]==FREE){
				return false;
			}
			if(board[i][j]==color){
				count+=1;
			} 
			else if(board[i][j]!=FREE){
				return false;
			}
			i++;j--;
		}
		return count==x;
	}

	private boolean xInARow(int x, int color, int row, int col){
		int count = 0;
		for(int i=col;i<=col+3;i++){
			if(row!=0 && board[row-1][i]==FREE){
				return false;
			}
			if(board[row][i]==color){
				count+=1;
			} 
			else if(board[row][i]!=FREE){
				return false;
			}
		}
		return count==x;
	}

	private int checkForResult(int col, char typeOfMove) {
		int color = whosTurn == YELLOW ? RED : YELLOW;

		if(typeOfMove=='v'){
			return checkForDraw();
		} 

		int row = getRow(col);

		for(int i=row;i>=0;i--){
			for(int j=col;j<NUM_COLS;j++){
				if(i < 2 && j > 2 && board[i][j]==color && board[i+1][j-1]==color 
						&& board[i+2][j-2]==color && board[i+3][j-3]==color){
					return WIN;
				}
			}
		}
		for(int i=row;i>=0;i--){
			for(int j=col;j>=0;j--){
				if(i+IN_A_ROW <= NUM_ROWS && (j+IN_A_ROW)<= NUM_COLS && board[i][j]==color 
						&& board[i+1][j+1]==color && board[i+2][j+2]==color && board[i+3][j+3]==color){
					return WIN;
				}
			}
		}
		for(int i=col; i>=0; i--){
			if(i+IN_A_ROW <= NUM_COLS && board[row][i] == color && board[row][i+1] == color 
					&& board[row][i+2] == color && board[row][i+3] == color){
				return WIN;
			}
		}
		if(row>=3 && board[row-1][col]==color && board[row-2][col]==color 
				&& board[row-3][col]==color){
			return WIN;
		}
		return checkForDraw();
	}

	private int getRow(int col) {
		for(int row=1;row<NUM_ROWS;row++){
			if(board[row][col]==FREE){
				return row-1;
			}
		}
		return NUM_ROWS-1;
	}

	private int checkForDraw() {
		for(int i=0;i<NUM_COLS;i++){
			if(validMoves[i]==FREE){
				return NO_RESULT;
			}
		}
		return DRAW;
	}

	private void unblock() {
		for(int i=0;i<NUM_COLS;i++){
			if(validMoves[i]==BLOCKED){
				validMoves[i]=FREE;
				return;
			}
		}
		validMoves[NUM_COLS]=FREE;
	}

	private boolean drop(int col) {
		for(int row=0;row<NUM_ROWS;row++){
			if(board[row][col]==FREE){
				board[row][col] = whosTurn;
				return row==(NUM_ROWS-1);//col is now full if true
			}
		}
		return false;
	}

	private boolean isValidMove(String move) {
		char[] temp = move.toCharArray();
		if(temp.length!=2){
			return false;
		}
		char typeOfMove = temp[0];
		int colToDrop = Character.getNumericValue(temp[1])-1;

		if(typeOfMove=='d' && colToDrop>=0 && colToDrop<NUM_COLS){
			return validMoves[colToDrop]==FREE;
		} 
		else if(typeOfMove=='v' && colToDrop>=0 && colToDrop<NUM_COLS){
			return validMoves[colToDrop]==FREE && validMoves[NUM_COLS]==FREE; 
		}
		return false;
	}
}
