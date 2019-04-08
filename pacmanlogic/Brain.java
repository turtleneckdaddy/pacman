package pacmanlogic;

import java.util.Arrays;


/**
 * brain
 */
public class Brain {
    public String[][] initialArray = {
        {" ", " ", " ", " ", "W", "W", "W", " ", "W", " "},

        {" ", "W", " ", " ", " ", " ", " ", " ", "W", " "},

        {" ", "W", " ", "W", " ", "W", " ", " ", "W", " "},

        {" ", "W", " ", "W", "W", "W", " ", " ", " ", " "},

        {" ", "W", " ", " ", " ", " ", " ", "W", "W", " "},

        {" ", " ", " ", " ", " ", " ", " ", "W", " ", " "},

     };
    
     //Instance variables
    private int width = initialArray.length;
    private int height = initialArray[0].length;
    private final int[] initialPlayerPosition = {0, 0};
    private final int[] initialGhostPosition = {2, 4}; 
    private Pacman player = new Pacman(initialPlayerPosition);
    private Ghost ghost1 = new Ghost(initialGhostPosition);
    private World gameWorld = new World(initialArray, player.getPosition(), ghost1.getPosition());
    
    //Getters and setters
   public int getWidth(){
       return width;
   }

   public int getHeight(){
       return height;
   }

   public Pacman getPlayer(){
       return player;
   }

   public Ghost getGhost(){
       return ghost1;
   }

   public World getGameWorld(){
       return gameWorld;
   }

   /**
   	checks for valid moves of movable objects, moving them accordingly
   */
    public void validateMove(int[] position, int[] move) {
        int toCheck = position[move[0]] + move[1];
        int ceiling;
        int w = position[0];
        int h = position[1];
        if (move[0] == 0){
            ceiling = width;
            w += move[1];
        } else {
            ceiling = height;
            h += move[1];
        }

        if (toCheck < 0 || toCheck >= ceiling || gameWorld.getCoinArr()[w][h] == "W"){
            // do nothing
            System.out.println("Couldn't move, Did nothing");
        } else {
            move(position, move);
        }

    }

    // updates position of movable object
    public void move(int[] position, int[] move) {
		//Resets the position if they intersect, depending on the Pacmans status (powered up or not)
		//Current parameters: Pacman gets sent to top left, ghost gets sent to inside the box
		//BUG: can not get the GUI version to take any keyboard input until counter runs out
		//     the game halts until the counter runs out for the power pellet
		
		if (player.getPosition() == ghost1.getPosition()) {
			if(!ghost1.getPowerStatus()){
				resetPosition();
			}
			else if (ghost1.getPowerStatus()){
				resetPosition();
			}
		}
		else {
			String[][] newArr = gameWorld.copyArr(gameWorld.getMovingArr());
            String character = newArr[position[0]][position[1]];
                        
            newArr[position[0]][position[1]] = " ";
            position[move[0]] = position[move[0]] + move[1]; 
            int[] newLocation = position;

            if(character.equals("P")){
                player.setPosition(newLocation);
            }
            else if(character.equals("G")||character.equals("g")){
                ghost1.setPosition(newLocation);
            }
			newArr[position[0]][position[1]] = character;

			gameWorld.setMovingArr(newArr);
		}
    }

    /**
    	Checks if pacman obtained a power pellet or coin	
    */
    public void checkCoins() {
        String[][] coins = gameWorld.getCoinArr();
        String[][] newCoins = gameWorld.copyArr(coins);
        int[] playerPosition = player.getPosition();
						
        if (coins[playerPosition[0]][playerPosition[1]] != " "){
            if (coins[playerPosition[0]][playerPosition[1]] == "C") {
                player.addScore(100);

            } else if (coins[playerPosition[0]][playerPosition[1]] == "O"){
                player.addScore(250);
				newCoins[playerPosition[0]][playerPosition[1]] = " ";
				gameWorld.setCoinArr(newCoins);
				System.out.println("Collected PowerPellet!");
				activatePowerUp();
            }
            newCoins[playerPosition[0]][playerPosition[1]] = " ";
            gameWorld.setCoinArr(newCoins);
            System.out.println("Collected!");
        }
    }
 
	/**
	    Checks lives of player or ghost and resets the position if necessary
	*/
	public void checkLives(){
        
        boolean intersect = (player.getPosition() == ghost1.getPosition());
		if (intersect && player.getLives() > 0 && !ghost1.getPowerStatus()) {
			player.loseLife();
			resetPosition();
			System.out.println("You lost a life!");
		}
		else if(intersect && ghost1.getPowerStatus()){
			resetPosition();
		}
	}
	
	/**
		returns the game board
	*/
    public String[][] getDisplayArr() {
        String[][] moving = gameWorld.getMovingArr();
        String[][] coins = gameWorld.getCoinArr();
        String[][] board = new String[moving.length][moving[0].length];
        
        for(int i =0; i< moving.length;i++){
            for (int j =0;j < moving[0].length; j++) {
                if (moving[i][j] != " ") {
                    board[i][j] = moving[i][j];
                } else if (coins[i][j] != " ") {
                    board[i][j] = coins[i][j];
                } else {
                    board[i][j] = " ";
                }
            }
        }
        return board;
    }    
	
	/**
		Allows pacman to eat the ghosts after obtaining a power pellet (only functional in the Text base version)
	*/
	public void activatePowerUp(){
		ghost1.setPowerStatus(true); 
		gameWorld.setPowerUpArr();
    }
    
    public void deactivatePowerUp(){
		ghost1.setPowerStatus(false); 
		gameWorld.resetFromPowerUpArr();  
    }
	
	/**
		resets pacman to the first block if lives stil remain, and ghost to the inside box if consumed
	*/
	public void resetPosition() {
		
        String[][] newMoveArr = gameWorld.copyArr(gameWorld.getMovingArr());
		
		if (!ghost1.getPowerStatus()){

			newMoveArr[player.getPosition()[0]][player.getPosition()[1]] = "G";
           
            player.setPosition(new int[] {0,0});

			newMoveArr[player.getPosition()[0]][player.getPosition()[1]] = "P";
		}
		else if(ghost1.getPowerStatus()){
			
			newMoveArr[ghost1.getPosition()[0]][ghost1.getPosition()[1]] = "P";
        
            ghost1.setPosition(new int[] {2,4});

			newMoveArr[ghost1.getPosition()[0]][ghost1.getPosition()[1]] = "g";
		}

        gameWorld.setMovingArr(newMoveArr);
	}
	
 
	/**
		games over when lives have run out
	*/
    public boolean checkGameOver() {
        
        return player.getLives() == 0;
    }

    //For displaying arrays in  matrix form (good for debugging)
	public void display(String display[][]){
		for(int i =0; i< display.length;i++){
            System.out.print("| ");
				for (int j = 0; j < display[0].length; j++){
					System.out.print(display[i][j]+" ");
                }
            System.out.print("|");
            System.out.println();
        }
		System.out.println();        
    }
    
    // for displaying the actual game board
    public void displayBoard() {
        String[][] moving = gameWorld.getMovingArr();
        String[][] coins = gameWorld.getCoinArr();

        for(int i =0; i< moving.length;i++){
            System.out.print("|");
				for (int j =0;j < moving[0].length; j++){
                    if (moving[i][j] != " ") {
                        System.out.print(moving[i][j] + " ");
                    } else if (coins[i][j] != " "){
                        System.out.print(coins[i][j] + " ");
                    } else {
                        System.out.print("  ");
                    }
                }
            System.out.print("|");
            System.out.println();
        }
		System.out.println();

    }
}
