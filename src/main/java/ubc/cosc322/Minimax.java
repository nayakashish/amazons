package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;
import java.time.Duration;

public class Minimax {

    //This is basic minimax algorithm. Following this function is algorithm with Alpha-Beta pruning.
    //normal node returns will look like List<Map<String, ArrayList<Integer>>>
    //terminal node returns will only be List<Object> of arraylist with just the utility result.
    public List<Object> execMinimax(Board board, int depth, boolean isMax, int playerId) {
        int opponantId = playerId == 1 ? 2:1;

        if(depth == 0 || board.isGameOver()) {
            List<Object> result = new ArrayList<>();
            result.add(board.getUtility(playerId)); //base case only has a utility. No move
            return result;
        }

        if(isMax) { //MAXIMIZER (playerId)
            int bestMove = Integer.MIN_VALUE;
            Map<String, ArrayList<Integer>> bestMoveAction = null;
            List<Object> bestResult = new ArrayList<>();
            
            ActionFactory af = new ActionFactory();
            List<Map<String, ArrayList<Integer>>> moves = af.getActions(playerId, board);
            for (Map<String, ArrayList<Integer>> move : moves) { //for every child node
                Board testMove = new Board(board.getGameboard()); //make copy of board and simulate move
                testMove.updateGameboard(move, playerId);
                
                List<Object> res = execMinimax(testMove, depth - 1, !isMax, playerId);
                int value = (int) res.get(0);
                
                // bestMove = Math.max(value, bestMove);
                if(value > bestMove) { //instead of just taking the max we check the values and store value and move.
                    bestMove = value;
                    bestMoveAction = move;
                }
            }

            //Add best value and move to return.
            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);
            return bestResult;

        } else { //MINIMIZER (opponantId)
            int bestMove = Integer.MAX_VALUE;
            Map<String, ArrayList<Integer>> bestMoveAction = null;
            
            List<Object> bestResult = new ArrayList<>();

            ActionFactory af = new ActionFactory();
            List<Map<String, ArrayList<Integer>>> moves = af.getActions(opponantId, board);
            for (Map<String, ArrayList<Integer>> move : moves) { //for every child node
                Board testMove = new Board(board.getGameboard()); //make copy of board and simulate move
                testMove.updateGameboard(move, opponantId);
              
                List<Object> res = execMinimax(testMove, depth - 1, !isMax, playerId);
                int value = (int) res.get(0);
                
                
                // bestMove = Math.min(value, bestMove);
                if(value < bestMove) { //instead of just taking the min we check the value and store the value and move.
                   bestMove = value;
                   bestMoveAction = move;
                }
            }

            //addd best value and move to return.
            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);
            return bestResult;
        }
    }
 
    //Alpha-Beta pruning added to minimax algorithm.
    //before hashmap
    //public List<Object> execAlphaBetaMinimax(Board board, int depth, boolean isMax, int playerId, int alpha, int beta, Instant endTime) {
    

    public List<Object> execAlphaBetaMinimax(Board board, HashMap<Integer, Object> moveData) {
    
        //Board board = (Board) moveData.get(0);
        int depth = (int) moveData.get(1);
        boolean isMax = (boolean) moveData.get(2);
        int playerId = (int) moveData.get(3);
        int alpha = (int) moveData.get(4);
        int beta = (int) moveData.get(5);
        Instant endTime = (Instant) moveData.get(6);
        
       

        int opponantId = playerId == 1 ? 2:1;

        if(depth == 0 || board.isGameOver() || Instant.now().isAfter(endTime)) { //if time limit exceeded.
           /*change this to a hashmap!*/
            List<Object> result = new ArrayList<>();
            result.add(board.getUtility(playerId)); //base case only has a utility. No move


            return result;


        }

        if(isMax) { //MAXIMIZER (playerId)
            int bestMove = Integer.MIN_VALUE;
            Map<String, ArrayList<Integer>> bestMoveAction = null;
            
            //change lists to a hashmap.
            List<Object> bestResult = new ArrayList<>();
            
            ActionFactory af = new ActionFactory();

            List<Map<String, ArrayList<Integer>>> moves = af.getActions(playerId, board);

            for (Map<String, ArrayList<Integer>> move : moves) { //for each child of node recursive call till terminal
                Board testMove = new Board(board.getGameboard()); //make copy of board and simulate move
                testMove.updateGameboard(move, playerId);
                
                HashMap<Integer, Object> moveDataRecursive = new HashMap<Integer, Object>();
               
		        //moveDataRecursive.put(0, testMove);
		        moveDataRecursive.put(1, depth-1);
		        moveDataRecursive.put(2, false);
		        moveDataRecursive.put(3, playerId);
		        moveDataRecursive.put(4, alpha);
		        moveDataRecursive.put(5, beta);
		        moveDataRecursive.put(6, endTime);

                //remember to change result to a hashmap.
                List<Object> res = execAlphaBetaMinimax(testMove, moveDataRecursive);

                int value = (int) res.get(0); //get utility the returned recursive call
                // int res = execAlphaBetaMinimax(testMove, depth - 1, false, playerId, alpha, beta);
                // int value = res;
                
                // bestMove = Math.max(value, bestMove);
                if(value > bestMove) { //instead of just taking the max we check the values and store value and move.
                    bestMove = value;
                    bestMoveAction = move;
                }

                
                alpha = Math.max(alpha, bestMove);
                moveData.replace(4, alpha);

                if(beta <= alpha) {
                    // System.out.println("branch pruned");
                    break; //prune branches.
                }

                
            }
            
            //Add best value and move to return.
            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);

            return bestResult;
            // return bestMove;

        } else { //MINIMIZER (opponantId)
            int bestMove = Integer.MAX_VALUE;
            Map<String, ArrayList<Integer>> bestMoveAction = null;
            
            List<Object> bestResult = new ArrayList<>();

            ActionFactory af = new ActionFactory();
            List<Map<String, ArrayList<Integer>>> moves = af.getActions(opponantId, board);
            for (Map<String, ArrayList<Integer>> move : moves) {
                Board testMove = new Board(board.getGameboard()); //make copy of board and simulate move
                testMove.updateGameboard(move, opponantId);

                HashMap<Integer, Object> moveDataRecursive = new HashMap<Integer, Object>();
                
		        //moveDataRecursive.put(0, testMove);
		        moveDataRecursive.put(1, depth-1);
		        moveDataRecursive.put(2, true); //this is different from the max node.
		        moveDataRecursive.put(3, playerId);
		        moveDataRecursive.put(4, alpha);
		        moveDataRecursive.put(5, beta);
		        moveDataRecursive.put(6, endTime);
              
                List<Object> res = execAlphaBetaMinimax(testMove, moveDataRecursive);
                int value = (int) res.get(0);
                // int res = execAlphaBetaMinimax(testMove, depth - 1, true, playerId, alpha, beta);
                // int value = res;
                
                // bestMove = Math.min(value, bestMove);
                if(value < bestMove) { //instead of just taking the min we check the value and store the value and move.
                   bestMove = value;
                   bestMoveAction = move;
                }

                beta = Math.min(beta, bestMove);
                moveData.replace(5, beta);


                if(beta <= alpha) {
                    // System.out.println("branch pruned");
                    break; //prune branch
                }
            }
            
            //add best value and move to return.
            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);
            return bestResult;
            // return bestMove;
        }
        
    }
}