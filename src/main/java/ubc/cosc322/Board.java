package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Board {

    private ArrayList<Integer> gameboard;

    public Board() {}

    public Board(ArrayList<Integer> gameboard) {
        this.gameboard = new ArrayList<>(gameboard);
    }

    public boolean validateMove(Map<String, ArrayList<Integer>> gameMove) {
        ArrayList<Integer> currentPos = gameMove.get("queen-position-current");
        ArrayList<Integer> nextPos = gameMove.get("queen-position-next");
        ArrayList<Integer> arrowPos = gameMove.get("arrow-position");

        if (!isValidPosition(nextPos) || !isValidPosition(arrowPos)) return false;
        if (!isPathClear(currentPos, nextPos) || !isPathClear(nextPos, arrowPos)) return false;
        return true;
    }

    private boolean isPathClear(ArrayList<Integer> start, ArrayList<Integer> end) {
        int dx = Integer.compare(end.get(0), start.get(0));
        int dy = Integer.compare(end.get(1), start.get(1));
        int x = start.get(0) + dx;
        int y = start.get(1) + dy;

        while (x != end.get(0) || y != end.get(1)) {
            if (getBoardPosition(x, y) != 0) return false;
            x += dx;
            y += dy;
        }
        return getBoardPosition(end) == 0;
    }

    public void updateGameboard(Map<String, ArrayList<Integer>> gameMove, int playerId) {
        ArrayList<Integer> currentPos = gameMove.get("queen-position-current");
        ArrayList<Integer> nextPos = gameMove.get("queen-position-next");
        ArrayList<Integer> arrowPos = gameMove.get("arrow-position");

        setBoardPosition(currentPos, 0);
        setBoardPosition(nextPos, playerId);
        setBoardPosition(arrowPos, 3);
    }

    public void updateGameboard(ArrayList<Integer> queenCurr, ArrayList<Integer> queenNext, ArrayList<Integer> arrowPos, int playerId) {
        setBoardPosition(queenCurr, 0);
        setBoardPosition(queenNext, playerId);
        setBoardPosition(arrowPos, 3);
    }

    public boolean setBoardPosition(ArrayList<Integer> move, Integer value) {
        int index = convertXYToBoard(move);
        if (index >= 0 && index < gameboard.size()) {
            gameboard.set(index, value);
            return true;
        }
        return false;
    }

    public Integer getBoardPosition(ArrayList<Integer> position) {
        int index = convertXYToBoard(position);
        if (index >= 0 && index < gameboard.size()) {
            return gameboard.get(index);
        }
        return -1;
    }

    public Integer getBoardPosition(int x, int y) {
        return getBoardPosition(new ArrayList<>(Arrays.asList(x, y)));
    }

    public Integer getBoardPosition(int boardIndex) {
        return (boardIndex >= 0 && boardIndex < gameboard.size()) ? gameboard.get(boardIndex) : -1;
    }

    public static Integer convertXYToBoard(ArrayList<Integer> loc) {
        return (11 - loc.get(0)) * 11 + (loc.get(1));
    }

    public static ArrayList<Integer> convertBoardtoXY(int location) {
        int xVal = 11 - (location / 11);
        int yVal = (location % 11);
        return new ArrayList<>(Arrays.asList(xVal, yVal));
    }

    public List<ArrayList<Integer>> getQueenPositions(int playerId) {
        List<ArrayList<Integer>> positions = new ArrayList<>();
        for (int i = 0; i < this.gameboard.size(); i++) {
            if (gameboard.get(i) == playerId) {
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

    public boolean isGameOver() {
        ActionFactory af = new ActionFactory();
        return af.getActions(1, this).isEmpty() || af.getActions(2, this).isEmpty();
    }

    public int getUtility(int playerId) {
        int opponentId = (playerId == 1) ? 2 : 1;
        int[][] territory = computeVoronoi();
        int p1squares = 0, p2squares = 0, dangerScore = 0, isolationPenalty = 0, trapPenalty = 0, blockReward = 0, selfBlockPenalty = 0, overlapPenalty = 0;

        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        int[][] centralityWeight = new int[11][11];
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                int dx = Math.abs(5 - i);
                int dy = Math.abs(5 - j);
                centralityWeight[i][j] = 10 - (dx + dy);
            }
        }

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                if (territory[i][j] == 1) p1squares += centralityWeight[i][j];
                else if (territory[i][j] == 2) p2squares += centralityWeight[i][j];

                if (territory[i][j] == playerId) {
                    for (int[] dir : directions) {
                        int ni = i + dir[0], nj = j + dir[1];
                        if (ni >= 1 && ni <= 10 && nj >= 1 && nj <= 10 && territory[ni][nj] == opponentId) {
                            dangerScore--;
                        }
                    }
                }
            }
        }

        List<ArrayList<Integer>> myQueens = getQueenPositions(playerId);
        List<ArrayList<Integer>> oppQueens = getQueenPositions(opponentId);
        List<Set<String>> queenRegions = new ArrayList<>();

        for (ArrayList<Integer> queen : myQueens) {
            Set<String> region = new HashSet<>();
            Queue<ArrayList<Integer>> q = new LinkedList<>();
            q.add(queen);
            boolean[][] localVisited = new boolean[11][11];
            localVisited[queen.get(0)][queen.get(1)] = true;

            while (!q.isEmpty()) {
                ArrayList<Integer> pos = q.poll();
                int x = pos.get(0), y = pos.get(1);
                String key = x + "," + y;
                region.add(key);
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    while (nx >= 1 && nx <= 10 && ny >= 1 && ny <= 10) {
                        int val = getBoardPosition(nx, ny);
                        if (val != 0 && val != playerId) break;
                        if (!localVisited[nx][ny]) {
                            localVisited[nx][ny] = true;
                            q.add(new ArrayList<>(Arrays.asList(nx, ny)));
                        }
                        nx += dir[0];
                        ny += dir[1];
                    }
                }
            }

            int reach = region.size();
            if (reach < 10) trapPenalty -= (10 - reach);
            if (reach < 5) isolationPenalty -= 5;
            if (reach <= 1) selfBlockPenalty -= 10;
            else if (reach <= 3) selfBlockPenalty -= 8;
            else if (reach <= 6) selfBlockPenalty -= 5;
            else if (reach <= 10) selfBlockPenalty -= 2;

            for (Set<String> otherRegion : queenRegions) {
                for (String tile : region) {
                    if (otherRegion.contains(tile)) {
                        overlapPenalty -= 1;
                    }
                }
            }
            queenRegions.add(region);
        }

        for (ArrayList<Integer> oppQueen : oppQueens) {
            Set<String> oppRegion = new HashSet<>();
            Queue<ArrayList<Integer>> q = new LinkedList<>();
            q.add(oppQueen);
            boolean[][] visited = new boolean[11][11];
            visited[oppQueen.get(0)][oppQueen.get(1)] = true;

            while (!q.isEmpty()) {
                ArrayList<Integer> pos = q.poll();
                int x = pos.get(0), y = pos.get(1);
                oppRegion.add(x + "," + y);
                for (int[] dir : directions) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];
                    while (nx >= 1 && nx <= 10 && ny >= 1 && ny <= 10) {
                        int val = getBoardPosition(nx, ny);
                        if (val != 0 && val != opponentId) break;
                        if (!visited[nx][ny]) {
                            visited[nx][ny] = true;
                            q.add(new ArrayList<>(Arrays.asList(nx, ny)));
                        }
                        nx += dir[0];
                        ny += dir[1];
                    }
                }
            }
            if (oppRegion.size() < 6) blockReward += (6 - oppRegion.size());
        }

        int emptyTiles = 0;
        for (int i = 0; i < gameboard.size(); i++) {
            if (gameboard.get(i) == 0) emptyTiles++;
        }

        double phaseFactor = emptyTiles / 121.0;

        int territoryScore = (playerId == 1) ? (p1squares - p2squares) : (p2squares - p1squares);
        ActionFactory af = new ActionFactory();
        int mobilityScore = af.getActions(playerId, this).size() - af.getActions(opponentId, this).size();

        return (int)(phaseFactor * territoryScore + (1 - phaseFactor) * mobilityScore + dangerScore + isolationPenalty + trapPenalty + blockReward + selfBlockPenalty + overlapPenalty);
    }

    private int[][] computeVoronoi() {
        int[][] territory = new int[11][11];
        int[][] distance = new int[11][11];
        for (int[] row : distance) Arrays.fill(row, Integer.MAX_VALUE);

        Queue<int[]> queue = new LinkedList<>();
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                int boardIndex = convertXYToBoard(new ArrayList<>(Arrays.asList(i, j)));
                int piece = gameboard.get(boardIndex);
                if (piece == 1 || piece == 2) {
                    queue.offer(new int[]{i, j, 0, piece});
                    distance[i][j] = 0;
                    territory[i][j] = piece;
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int x = cell[0], y = cell[1], d = cell[2], owner = cell[3];

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];
                while (nx >= 1 && nx <= 10 && ny >= 1 && ny <= 10) {
                    int boardIndex = convertXYToBoard(new ArrayList<>(Arrays.asList(nx, ny)));
                    if (gameboard.get(boardIndex) != 0) break;

                    if (d + 1 < distance[nx][ny]) {
                        distance[nx][ny] = d + 1;
                        territory[nx][ny] = owner;
                        queue.offer(new int[]{nx, ny, d + 1, owner});
                    } else if (d + 1 == distance[nx][ny] && territory[nx][ny] != owner) {
                        territory[nx][ny] = 0;
                    }

                    nx += dir[0];
                    ny += dir[1];
                }
            }
        }

        return territory;
    }

    private boolean isValidPosition(ArrayList<Integer> pos) {
        return pos != null && pos.size() == 2 && pos.get(0) >= 1 && pos.get(0) <= 10 && pos.get(1) >= 1 && pos.get(1) <= 10;
    }
}
