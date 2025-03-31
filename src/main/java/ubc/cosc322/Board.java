package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Board {
    
    private ArrayList<Integer> gameboard;

    public Board() {
    } 

    public Board(ArrayList<Integer> gameboard) {
        this.gameboard = new ArrayList<>(gameboard);
    }
    

    //*INCOMPLETE*/
    public boolean validateMove(Map<String, ArrayList<Integer>> gameMove) {
        ArrayList<Integer> currentPos = gameMove.get("queen-position-current");
        ArrayList<Integer> nextPos = gameMove.get("queen-position-next");
        ArrayList<Integer> arrowPos = gameMove.get("arrow-position");
        
        //Check if move lands in a occupied space:
        if(this.gameboard.get(convertXYToBoard(nextPos)) != 0) {
            System.err.println("Move invalid, player move occupied. validateMove() - SmartPlayer.java");
            return false;
        }

        //Check if move is within bounds:
        if(nextPos.get(0) <= 0 || nextPos.get(0) >= 11 || nextPos.get(1) >= 11 || nextPos.get(1) <= 0) {
            System.err.println("Move out of bounds index <=0 or >=11. validateMove() - SmartPlayer.java");
            return false;
        }
        
        //Check if arrow lands in occupied space:
        if(this.gameboard.get(convertXYToBoard(arrowPos)) != 0) {
            System.err.println("Move invalid, arrow shot occupied. validateMove() - SmartPlayer.java");
            return false;
        }

        //Check that move is horizontal or vertical.
        //First I check for horizontal move:

        


        

        return true;
    }

    
    /*INCOMPLETE*/
    public void updateGameboard(Map<String, ArrayList<Integer>> gameMove, int playerId) {
        ArrayList<Integer> currentPos = gameMove.get("queen-position-current");
        ArrayList<Integer> nextPos = gameMove.get("queen-position-next");
        ArrayList<Integer> arrowPos = gameMove.get("arrow-position");
        
    
        setBoardPosition(currentPos, 0);
        setBoardPosition(nextPos, playerId);
        setBoardPosition(arrowPos, 3); //SHOULD THIS BE 3?

    }

    //Overloaded function
    public void updateGameboard(ArrayList<Integer> queenCurr, ArrayList<Integer> queenNext, ArrayList<Integer> arrowPos, int playerId) {
        setBoardPosition(queenCurr, 0);
        setBoardPosition(queenNext, playerId);
        setBoardPosition(arrowPos, 3);
    }

    public boolean setBoardPosition(ArrayList<Integer> move, Integer value) { //Helper function to update single positions.
        this.gameboard.set(convertXYToBoard(move), value);
        return true;
    }
    
    public Integer getBoardPosition(ArrayList<Integer> position) { //Helper function param arraylist of x,y. Returns value at location. Ex [7,1]=1. Function Overload.
        return this.gameboard.get(convertXYToBoard(position));
    }

    public Integer getBoardPosition(int x, int y) { //Helper function param ints x, y. Returns value at location. Function Overload.
        ArrayList<Integer> position = new ArrayList<>();
        position.add(x);
        position.add(y);
        return getBoardPosition(position);
    }

    public Integer getBoardPosition(int boardIndex) { //Helper funciton param board index. Returns value at location. Function Overload.
        return this.gameboard.get(boardIndex);
    }


    public static Integer convertXYToBoard(ArrayList<Integer> loc) {
        // Maps [row, col] pair to a single value in the gameboard.
        //loc.get(0) is row, loc.get(1) is col.
        
        return (11-loc.get(0))*11+(loc.get(1)); //removed the plus one from the formula below because of array indexing starting from zero.

        /* game-state.board
         * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0]
         */
        
        /* Corresponding col/rows for indexing
         * [xx, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
         *  10, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
         *  09, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  08, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  07, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
         *  06, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  05, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  04, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
         *  03, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  02, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         *  01, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0]
         * 
         *  (11-row)*11+(1+col) = location?
         *  [4,1] = (11-4)*11+(1+1) = 79
         *  [1,7] = (11-1)*11+(1+7) = 118
         *  
         */
    }

    public static ArrayList<Integer> convertBoardtoXY(int location) {
        int xVal = 11 - (location / 11);
        int yVal = (location % 11);

        ArrayList<Integer> res = new ArrayList<>();
        res.add(xVal);
        res.add(yVal);

        return res;
    }

    public List<ArrayList<Integer>> getQueenPositions(int playerId) { //Returns queen positions in ArrayList. Loops from start of board to end finding playerId.
        List<ArrayList<Integer>> positions = new ArrayList<>();

        for(int i = 11; i < this.gameboard.size(); i++) { //start at 11 because the first row is always empty.
            if(getBoardPosition(i) == playerId) {
                positions.add(convertBoardtoXY(i));
            }
        }

        return positions;
    }

    public void setGameboard(ArrayList<Integer> gameboard) {
        this.gameboard = gameboard;
    }

    public ArrayList<Integer> getGameboard() {
        return this.gameboard;
    }

    public boolean isGameOver() { //Checks if either player has no moves left. Returns true/false;
        ActionFactory af = new ActionFactory(); 
        return af.getActions(1, this).isEmpty() || af.getActions(2, this).isEmpty();
        
    }

    /*INCOMPLETE*/ //For now, utility is just number of moves possible. 
    // public int getUtility(int playerId) {
    //     ActionFactory af = new ActionFactory();

    //     //Temporary utility calculation. In this function it just gets number of actions possible and takes difference.
    //     if(playerId == 1)
    //         return af.getActions(playerId, this).size() - af.getActions(2, this).size();
    //     else
    //         return af.getActions(playerId, this).size() - af.getActions(1, this).size();

    // }

    public int getUtility(int playerId) {
        int opponentId = (playerId == 1) ? 2 : 1;
    
        // Compute Voronoi territories
        int[][] territory = computeVoronoi();
    
        int p1squares = 0, p2squares = 0;
    
        // Count territories controlled by each player
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                if (territory[i][j] == 1) p1squares++;
                else if (territory[i][j] == 2) p2squares++;
            }
        }
    
        // Return the utility as the difference in controlled area
        return (playerId == 1) ? (p1squares - p2squares) : (p2squares - p1squares);
    }
    

    private int[][] computeVoronoi() {
        int[][] territory = new int[11][11];
        int[][] distance = new int[11][11];
        for (int[] row : distance) Arrays.fill(row, Integer.MAX_VALUE);

        Queue<int[]> queue = new LinkedList<>();

        // Initialize queue with player pieces
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                int boardIndex = convertXYToBoard(new ArrayList<>(Arrays.asList(i, j)));
                int piece = gameboard.get(boardIndex);

                if (piece == 1 || piece == 2) {
                    queue.offer(new int[]{i, j, 0, piece});
                    distance[i][j] = 0;
                    territory[i][j] = piece; // Assign initial territory
                }
            }
        }

        // BFS for multi-source shortest paths
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int x = cell[0], y = cell[1], d = cell[2], owner = cell[3];

            for (int[] dir : directions) {
                int nx = x + dir[0], ny = y + dir[1];

                if (nx >= 1 && nx <= 10 && ny >= 1 && ny <= 10) {
                    int boardIndex = convertXYToBoard(new ArrayList<>(Arrays.asList(nx, ny)));

                    if (gameboard.get(boardIndex) == 0) {  // If empty square
                        if (d + 1 < distance[nx][ny]) {  // If we found a shorter path
                            distance[nx][ny] = d + 1;
                            territory[nx][ny] = owner;
                            queue.offer(new int[]{nx, ny, d + 1, owner});
                        } else if (d + 1 == distance[nx][ny]) {  // If contested zone
                            territory[nx][ny] = 0;  // Neutral zone
                        }
                    }
                }
            }
        }

        return territory;
    }


    //This is only for direct testing, the smart player class should not be run as main.
    public static void main(String[] args) {
        System.out.println("SmartPlayer Testing Main Function");
        Board b = new Board();
        
        ArrayList<Integer> testBoard = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0)); 
        b.setGameboard(testBoard);

        /*
        ArrayList<Integer> testBoard = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 2, 3, 0, 2, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 1, 3, 3, 3)); 
        b.setGameboard(testBoard);

        for(Integer i : testBoard) {
            if(testBoard.get(i).equals(0)) {
                testBoard.set(i, 3);
            }
        }
        */

        // ArrayList<Integer> testLoc = new ArrayList<>(Arrays.asList(7,1));
        // System.out.println("Convert XY to board: " + convertXYToBoard(testLoc));

        // System.out.println(114 % 11);
        // System.out.println("Convert board to XY: " + convertBoardtoXY(45));
        // System.out.println(my.getBoardPosition(45));

        // System.out.println(my.getQueenPositions(1));

        ActionFactory af = new ActionFactory();
        List<Map<String, ArrayList<Integer>>> test = af.getActions(2, b);
        int i =0 ;
        for (Map<String, ArrayList<Integer>> action : test) {
              
            System.out.print(action.get("queen-position-current") + ", ");  
            System.out.print(action.get("queen-position-next") + ", ");  
            System.out.print(action.get("arrow-position") + "\n");  
            
            // System.out.println(testBoard);
        }
        
        System.out.println(test.size());
        System.out.println(b.isGameOver());

        Minimax m = new Minimax();
        
        List<Object> try1 = m.execMinimax(b, 1, true, 1);
        System.out.println(try1.get(0));
        System.out.println(try1.get(1));
        
        
    }

     
}
