package ubc.cosc322;

import java.util.Random;

public class ZobristHash {
    
    public long[][] zobristTable; //we use long since it is 64bit as zobrist uses

    public ZobristHash(Board board) {
        Random rand = new Random();
        int boardSize = board.getGameboard().size();
        zobristTable = new long[4][boardSize]; //4 levels for each possible value, 0-3 for player number, arrow shot, or blank

        for(int i = 0; i < 4; i++) { //loop through the 4 lvls
            for(int position = 11; position < boardSize; position++) { //start at 11 to skip padded row. @TODO: remove check of padded column.
                zobristTable[i][position] = rand.nextLong(); //fill the table with random hashes.
            }
        }


    }

    public long computeBoardHash(Board board) {
        long hash = 0;

        for(int idx = 0; idx < board.getGameboard().size(); idx++) { //loop through whole gameboard @TODO: Remove loop through padded cells.
            int piece = board.getBoardPosition(idx);
            hash ^= zobristTable[piece][idx]; //enter the hash value into the table by xor-ing.
        }
        return hash;
    }  

    
}
