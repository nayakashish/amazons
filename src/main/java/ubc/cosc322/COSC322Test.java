
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	// COSC322Test player = new COSC322Test(args[0], args[1]);
		COSC322Test player = new COSC322Test("cosc322", "cosc322");

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
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);

    }
 


    @Override
    public void onLogin() {
    	// System.out.println("Congratualations!!! "
    	// 		+ "I am called because the server indicated that the login is successfully");
    	// System.out.println("The next step is to find a room and join it: "
    	// 		+ "the gameClient instance created in my constructor knows how!"); 

		
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}

		gameClient.joinRoom("Bear Lake");

	

		// gameClient.sendMoveMessage(x, queenPosNew, arrowPos);

    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
		System.out.println("MSG RXD: " + messageType);
		
		if(messageType.equals("cosc322.game-state.board")) {
			System.out.println("Board: " + msgDetails);
			gamegui.setGameState((ArrayList<Integer>) msgDetails.get("game-state"));
			ArrayList<Integer> t1 = new ArrayList<>(Arrays.asList(7, 1));
			ArrayList<Integer> t2 = new ArrayList<>(Arrays.asList(9, 3));
			ArrayList<Integer> t3 = new ArrayList<>(Arrays.asList(2, 10));


			gameClient.sendMoveMessage(t1, t2, t3);
		} else if (messageType.equals("cosc322.game-action.move")) {
			System.out.println("RXD Move Msg: " + msgDetails);


			ArrayList<Integer> test1 =(ArrayList<Integer>) msgDetails.get("queen-position-current");
			ArrayList<Integer> test2 =(ArrayList<Integer>) msgDetails.get("queen-position-next");
			ArrayList<Integer> test3 =(ArrayList<Integer>) msgDetails.get("arrow-position");

			System.out.println(test1.toString());
			System.out.println("UPDATING");
			gamegui.updateGameState(test1, test2, test3);
		


		}



    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 
    	
		/*
		 * 
		 * [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		 *  0, 0, 0, 0, 0, 2, 0, 0, 2, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 2, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		 *  0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0]
		 * 
		 */
    	return true;   	
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
