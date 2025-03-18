package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Minimax {
    
    public List<Object> execMinimax(Board board, int depth, boolean isMax, int playerId, int alpha, int beta) {
        int opponentId = (playerId == 1) ? 2 : 1;

        if (depth == 0 || board.isGameOver()) {
            List<Object> result = new ArrayList<>();
            result.add(board.getUtility(playerId)); // Base case: return utility
            return result;
        }

        ActionFactory af = new ActionFactory();
        List<Map<String, ArrayList<Integer>>> moves = af.getActions(isMax ? playerId : opponentId, board);

        Map<String, ArrayList<Integer>> bestMoveAction = null;
        List<Object> bestResult = new ArrayList<>();

        if (isMax) { // MAXIMIZER (playerId)
            int bestMove = Integer.MIN_VALUE;

            for (Map<String, ArrayList<Integer>> move : moves) {
                Board testMove = new Board(board.getGameboard()); // Copy board and simulate move
                testMove.updateGameboard(move, playerId);

                List<Object> res = execMinimax(testMove, depth - 1, false, playerId, alpha, beta);
                int value = (int) res.get(0);

                if (value > bestMove) { // Store best move and action
                    bestMove = value;
                    bestMoveAction = move;
                }

                alpha = Math.max(alpha, bestMove);
                if (alpha >= beta) {
                    break; // Alpha-Beta Pruning
                }
            }

            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);
            return bestResult;
        } else { // MINIMIZER (opponentId)
            int bestMove = Integer.MAX_VALUE;

            for (Map<String, ArrayList<Integer>> move : moves) {
                Board testMove = new Board(board.getGameboard()); // Copy board and simulate move
                testMove.updateGameboard(move, opponentId);

                List<Object> res = execMinimax(testMove, depth - 1, true, playerId, alpha, beta);
                int value = (int) res.get(0);

                if (value < bestMove) { // Store best move and action
                    bestMove = value;
                    bestMoveAction = move;
                }

                beta = Math.min(beta, bestMove);
                if (alpha >= beta) {
                    break; // Alpha-Beta Pruning
                }
            }

            bestResult.add(bestMove);
            bestResult.add(bestMoveAction);
            return bestResult;
        }
    }
}