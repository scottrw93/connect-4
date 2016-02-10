package com.ucd.ai.puzzles.connect4;
//Scott Williams 11356176


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AlphaBeta {
	private static ArrayList<ArrayList<Pair>> killerInfo;
	private static String bestMove;
	private static int orgDepth;
	private static int staticEvals;

	public static String search(Board board, int depth, boolean killer, boolean verbose){
		setUpSearch(depth);
		AlphaBeta.search_help(board, depth, -214748364, 214748364, killer, verbose);
		return bestMove;
	}
	
	private static int search_help(Board board, int depth, int achievable, int hope, boolean killer, boolean verbose){
		if(depth==0 || board.isTerminal()){
			int val = board.staticEvaluation();
			
			staticEvals++;
			return val;
		}

		ArrayList<String> posMoves = killer ? orderMoves(board.possibleMoves(), depth) : board.possibleMoves();

		for(String posMove : posMoves){

			board.makeMove(posMove);
			int temp = -AlphaBeta.search_help(board, depth-1, -hope, -achievable, killer, verbose);
			board.undo();

			if(temp>=hope){
				if(depth==orgDepth)
					bestMove = posMove;
				
				if(killer) 
					updateKiller(depth, posMove);

				return temp;
			}

			if(achievable<temp){
				if(depth==orgDepth)
					bestMove = posMove;
				achievable = temp;
			}
		}
		return achievable;
	}

	public static int staticEvals(){
		return staticEvals;
	}

	private static void setUpSearch(int depth) {
		killerInfo = new ArrayList<ArrayList<Pair>>();
		orgDepth = depth; 
		bestMove = "";
		staticEvals = 0;

		for(int i=0;i<=depth;i++){
			ArrayList<Pair> inner = new ArrayList<Pair>();
			for(int j=1;j<=7;j++){
				inner.add(new Pair("d"+j, 0));
				inner.add(new Pair("v"+j, 0));
			}
			killerInfo.add(inner);
		}
	}

	private static void updateKiller(int depth, String posMove) {
		for(Pair pair : killerInfo.get(depth)){
			if(pair.move.equals(posMove))
				pair.count++;
		}
	}

	private static ArrayList<String> orderMoves(ArrayList<String> possibleMoves, int depth) {
		ArrayList<Pair> list = killerInfo.get(depth);
		ArrayList<String> orderedMoves = new ArrayList<String>();

		Collections.sort(list, new Comparator<Pair>() {
			public int compare(Pair pair1, Pair pair2){
				return pair2.count.compareTo(pair1.count);
			}
		});

		for(Pair pair : list){
			if(possibleMoves.contains(pair.move))
				orderedMoves.add(pair.move);
		}

		return orderedMoves;
	}

	private static class Pair{
		public String move;
		public Integer count;

		public Pair(String move, int count){
			this.move = move;
			this.count = count;
		}
	}
}