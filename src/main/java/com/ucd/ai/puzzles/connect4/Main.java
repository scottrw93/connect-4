package com.ucd.ai.puzzles.connect4;
//Scott Williams 11356176


import java.util.Scanner;


public class Main {

	private static int DEPTH = 8;
	private static boolean KILLER = true;
	private static boolean USER_FIRST = true;
	private static boolean VERBOSE = false;

	public static void main(String[] args){

		if(args.length==3){
			DEPTH = Integer.valueOf(args[0]);
			KILLER = Boolean.valueOf(args[1]);
			USER_FIRST = Boolean.valueOf(args[2]);
			onePlayerGame(USER_FIRST);
		} else if(args.length==0){
			onePlayerGame(USER_FIRST);
		}
	}

	private static void onePlayerGame(boolean userFirst){
		Board board = new Board(userFirst ? Board.YELLOW : Board.RED);

		Scanner scan = new Scanner(System.in);
		int result;

		do{

			if(!userFirst){
				String move = AlphaBeta.search(board, DEPTH, KILLER, VERBOSE);
				result = board.makeMove(move);
				//bw.write("\n"+AlphaBeta.staticEvals()+"\t"+move);


				if(result!=Board.NO_RESULT){
					clear();
					System.out.println(board.toString());
					System.out.println();
					System.out.println(result==Board.DRAW ? "It's a draw!" : "You lost!");
					return;
				}
			}

			clear();
			System.out.println(board.toString());
			System.out.println();
			System.out.println("Possible Moves:");
			System.out.println(board.possibleMoves());
			System.out.println("You are Yellow!");
			//String pre_move;
			do{
				System.out.println();
				System.out.print("Enter Move: ");
				String move = scan.nextLine();
				//pre_move = move;
				result = board.makeMove(move.toLowerCase());
			}while(result==Board.ERROR);//Invalid Move

			if(userFirst){
				if(result!=Board.NO_RESULT){
					clear();
					System.out.println(board.toString());
					System.out.println();
					System.out.println(result==Board.DRAW ? "It's a draw!" : "You win!");
					return;
				}

				String move = AlphaBeta.search(board, DEPTH, KILLER, VERBOSE);
				result = board.makeMove(move);
			}

		} while(result==Board.NO_RESULT);

		clear();
		System.out.println(board.toString());
		System.out.println();
		if(userFirst)
			System.out.println(result==Board.DRAW ? "It's a draw!" : "You lost!");
		else 
			System.out.println(result==Board.DRAW ? "It's a draw!" : "You win!");
		
		scan.close();
	}

	private static void clear() {
		for(int i=0;i<100;i++){
			System.out.println();
		}
	}
}
