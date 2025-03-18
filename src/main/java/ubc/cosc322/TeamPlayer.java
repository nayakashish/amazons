
package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;

public class TeamPlayer extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;

	private Board board;
	private int playerId;
	private int opponantId;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	// COSC322Test player = new COSC322Test(args[0], args[1]);
		TeamPlayer player = new TeamPlayer("Team#18", "cosc322");

    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public TeamPlayer(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	this.gamegui = new BaseGameGUI(this);

		this.board = new Board();
    }

    @Override
    public void onLogin() {
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}

		gameClient.joinRoom("Bear Lake");
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
		System.out.println("MSG RXD: " + messageType);

		if(messageType.equals("cosc322.game-state.board")) {
			System.out.println("RXD Board: " + msgDetails.get("game-state"));
			gamegui.setGameState((ArrayList<Integer>) msgDetails.get("game-state"));
			
			board.setGameboard((ArrayList<Integer>)msgDetails.get("game-state"));


			
		} else if (messageType.equals("cosc322.game-action.move")) {
			if(board.isGameOver()) {
				System.err.println("GAME OVER; Good game.");
				return false;
			}

			System.out.println("RXD Move Msg: " + msgDetails);

			ArrayList<Integer> queenCurr =(ArrayList<Integer>) msgDetails.get("queen-position-current");
			ArrayList<Integer> queenNext =(ArrayList<Integer>) msgDetails.get("queen-position-next");
			ArrayList<Integer> arrowPos =(ArrayList<Integer>) msgDetails.get("arrow-position");

			//Update gui and local board variable
			gamegui.updateGameState(queenCurr, queenNext, arrowPos);
			board.updateGameboard(queenCurr, queenNext, arrowPos, opponantId);

			//return with move.
			makeMove();

		} else if(messageType.equals("cosc322.game-action.start")) {
			System.out.println("GAME START.");
			String player1 = (String) msgDetails.get("player-white");
			String player2 = (String) msgDetails.get("player-black");

			System.out.println("Player Black is " + player2);
			System.out.println("Player White is " + player1);
			System.out.println(player2 + " goes first.");

			playerId = player1.equals("Team#18") ? 1:2; //Set player Id based on active player usernames;
			opponantId = playerId==1 ? 2:1; //Set opponantId based on playerId;

			if(playerId == 2) {
				//if player 2, start by making a move. //Black moves first, according to lecture.
				makeMove();
			} else {
				//if p1 do nothing. wait for move msg rx.			
				System.out.println("I MOVE SECOND.");
			}
		}

    	return true;   	
    }

	public void makeMove() { //temporary jst for quick switching between random moves and minimax
		makeMinMaxMove();
		// makeRandomMove();
	}

	public void makeMinMaxMove() {
		Minimax m = new Minimax();
		List<Object> minimax = m.execMinimax(board, 2, true, playerId, Integer.MIN_VALUE, Integer.MAX_VALUE);
	
		Map<String, ArrayList<Integer>> bestMove = (Map<String, ArrayList<Integer>>) minimax.get(1);
	
		ArrayList<Integer> queen_pos_curr = bestMove.get("queen-position-current");
		ArrayList<Integer> queen_pos_next = bestMove.get("queen-position-next");
		ArrayList<Integer> arrow_pos = bestMove.get("arrow-position");
	
		System.out.println("MY MOVE: " + queen_pos_curr + ", " + queen_pos_next + ", " + arrow_pos);
	
		gameClient.sendMoveMessage(queen_pos_curr, queen_pos_next, arrow_pos);
		gamegui.updateGameState(queen_pos_curr, queen_pos_next, arrow_pos);
		board.updateGameboard(queen_pos_curr, queen_pos_next, arrow_pos, playerId);
	}

	public void makeRandomMove() {
		ActionFactory af = new ActionFactory();
		List<Map<String,ArrayList<Integer>>> possibleActions = af.getActions(playerId, board);

		Random rand = new Random();
		Map<String, ArrayList<Integer>> randomAction = null;
		try { //try getting a random action, if unable game over.
			randomAction = possibleActions.get(rand.nextInt(possibleActions.size()));			
		} catch (IllegalArgumentException i) {
			System.err.println("GAME OVER; Good game.");
		}
		
		ArrayList<Integer> queen_pos_curr = randomAction.get("queen-position-current");
		ArrayList<Integer> queen_pos_next = randomAction.get("queen-position-next");
		ArrayList<Integer> arrow_pos = randomAction.get("arrow-position");
		
		System.out.println("MY MOVE: " + queen_pos_curr +", "+ queen_pos_next +", "+ arrow_pos);
		//Update client, gui, and local board of move.
		gameClient.sendMoveMessage(queen_pos_curr, queen_pos_next, arrow_pos);
		gamegui.updateGameState(queen_pos_curr, queen_pos_next, arrow_pos);
		board.updateGameboard(randomAction, playerId);
	}

    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub

		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
