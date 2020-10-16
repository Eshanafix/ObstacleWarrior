/**
 * Name: Eshan Kwatra
 * Date: 09/18/2020
 * Class: CS 2336.503
 * Professor: David Sims
 */

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.util.Scanner;
import java.util.Random;


/**
 * ObstaclesWarrior
 *
 */
public class Main {
    public static void main( String[] args ){
        menu();
    }

    //This method is the menu, it holds most of the outputs and controls the variables of the program (Add-on method)
    public static void menu(){
        
        //Every time program is run the input file is created using this add-on method
        createFile();
        
        Scanner input = new Scanner(System.in);
        
        Position startPosition = new Position(0,0);
        Position exitPosition = new Position(0,0);
        Position currentPosition = new Position(0,0);

        String[][] boardState;

        //This is getting the name of the filepath from the user, to use this type C:\ and then the name of the file. In this case the input is C:\Board.dat
        System.out.print("Enter the board data file path: ");
        String FILE_NAME = input.nextLine();
        if(FILE_NAME.length() < 3){
            System.out.println("Error file not found\nExiting Program...");
            System.exit(0);
        }
        FILE_NAME = FILE_NAME.substring(3);
        
        //Runs the method that reads the file, this method assigns values to the Start Position, exit position and the Board Array
        boardState = ReadBoardFromFile(FILE_NAME, startPosition, exitPosition);
        
        //This records if the user wants to start or exit the game different inputs will cause different results
        System.out.print("Type \"Start\" to start the game or \"Exit\" to exit the game: ");
        String response = input.nextLine();

        if((response.toLowerCase()).equals("start")){
            //This begins the game, the time starts being recorded and the warrior starts moving through the obstacle course
            runGame(startPosition, exitPosition, currentPosition, boardState);
        }
        else if((response.toLowerCase()).equals("exit")){
            System.exit(0);
        }
        else{
            System.exit(0);
        }

        //Once the game is complete the resulting board array is written to the ResultBoard.dat file
        WriteBoardToFile("ResultBoard.dat", boardState);

        //Once all outputs are done the user is asked if they want to exit the program. Pressing enter will achieve this
        String recordInput;
        System.out.print("Press Enter to exit! ");
        recordInput = input.nextLine();
        if(recordInput.equals("")){
            System.exit(0);
        }
        input.close();
    }

    //This method creates the input file(add-on method)
    public static void createFile(){

        final String FILE_NAME = "Board.dat";
        File file = new File(FILE_NAME);

        try {
            PrintWriter printToFile = new PrintWriter(FILE_NAME); 
   
            printToFile.println("5 5");
            printToFile.println("0 0");
            printToFile.println("4 4");
            printToFile.println("# # # # -10");
            printToFile.println("# # -2 # #");
            printToFile.println("-4 # 0 # #");
            printToFile.println("# # # -1 #"); 
            printToFile.println("# # -8 # #");
   
            printToFile.close();
   
         } 
         catch (FileNotFoundException e) {
            System.out.println(FILE_NAME + " Not found");
         }
    }

    //This method controls the running of the game. The warrior score and movement are calculated here(add-on method)
    public static void runGame(Position startPosition, Position exitPosition, Position currentPosition, String[][] boardState){

        //Begins recording run time of game
        long startOfGame = System.currentTimeMillis();

        int tempDirection;
        Boolean moveHappened;
        int currentScore = 0;
        int moves = 0;
        int isOn0 = 1;

        //Boolean condition states that while the current position (x,y) is not equal to exit position(x,y) keep running the code
        while(!((currentPosition.getX() == exitPosition.getX()) && (currentPosition.getY() == exitPosition.getY()))){

            tempDirection = GenerateDirection();
            
            //This moves the warrior and checks if the move is valid
            moveHappened = MoveWarrior(tempDirection, boardState, currentPosition);

            //If the move is not Valid the moveWarrior method will return false and the while loop will reset back to the beginning 
            if(!moveHappened){
                continue;
            }
            //Moves increments only if it gets past the continue statement
            moves++;

            //If the warrior is moved onto a 0 the movement will be handled in the rungame method
            isOn0 = CalculateWarriorScore(currentScore, currentPosition, boardState);
            currentScore = isOn0;
            if(boardState[currentPosition.getX()][currentPosition.getY()].equals("0")){
                boardState[currentPosition.getX()][currentPosition.getY()] = "#";
                currentPosition.setX(startPosition.getX());
                currentPosition.setY(startPosition.getY());
            }
        }
        
        //Stops recording the runtime of the game because the while loop is over
        long endOfGame = System.currentTimeMillis();

        //This displays the results, the run time is casted as an Integer
        System.out.println(DisplayResults(currentScore, moves, (int)(endOfGame - startOfGame), boardState));
    }
    
    //This method reads the file and assigns values to the positions used in the code(given method)
    public static String[][] ReadBoardFromFile(String fileName, Position startPosition, Position exitPosition){
        
        File boardName = new File(fileName);

        String[][] boardState;
        //False return created so I can have a return outside of the Try statement
        String[][] falseReturn = new String[1][1];


        try(Scanner readFile = new Scanner(boardName)){
            
            //Dimensions, start and end positions found by Parsing the given lines
            int xDimension = Integer.parseInt(readFile.next());
            int yDimension = Integer.parseInt(readFile.next());
            int xStartPosition = Integer.parseInt(readFile.next());
            int yStartPosition = Integer.parseInt(readFile.next());
            int xEndPosition = Integer.parseInt(readFile.next());
            int yEndPosition = Integer.parseInt(readFile.next());

            //Start and end are set
            startPosition.setX(xStartPosition);
            startPosition.setY(yStartPosition);
            exitPosition.setX(xEndPosition);
            exitPosition.setY(yEndPosition);

            boardState = new String[xDimension][yDimension];


            //Board Array is created
            for(int row = 0; row < boardState.length; row++){
                for(int col = 0; col < boardState.length; col++){
                    boardState[row][col] = readFile.next();
                }
            }
            return boardState;
        }
        catch(FileNotFoundException e){
            //If the inputed file path is not correct this will output an error
            System.out.println("Error: File not found\nExiting Program...");
            System.exit(0);
        }

        return falseReturn;
    } 
    
    //This method writes the updated board to the ResultBoard.dat file(given method)
    public static boolean WriteBoardToFile(String fileName, String[][] boardArray){

        File file = new File(fileName);
        StringBuilder fileWriter = new StringBuilder();

        try {
            PrintWriter printToFile = new PrintWriter(fileName); 
   
            //A nested loop is used to used. Everything on one line of the 2-D array is added onto one string builder and written on to the file then the String builder is cleared and the process is repeated
            for(int row = 0; row < boardArray.length;row++){
                fileWriter.append(boardArray[row][0] + " ");
                for(int col = 1; col < boardArray.length - 1; col++){
                    fileWriter.append(boardArray[row][col] + " ");
                }
                fileWriter.append(boardArray[row][boardArray.length - 1]);
                printToFile.println(fileWriter.toString());
                fileWriter = new StringBuilder();
            }
            printToFile.close();
   
         } 
         catch (FileNotFoundException e) {
            System.out.println(fileName + " Not found");
         }

        return true;
    } 
    
    //This method generates a random number between 0 - 7, this uses the Random class(given method)
    public static int GenerateDirection(){

        Random rand = new Random();

        int direction = rand.nextInt(8);

        return direction;
    } 
    
    //This method moves the warrior using a switch case and checkes if it is out of bounds
    public static Boolean MoveWarrior(int direction, String[][] boardArray, Position currentPosition){
        
        int lowerBound = 0;
        int upperBound = boardArray.length - 1;

        int xPosition = currentPosition.getX();
        int yPosition = currentPosition.getY();

        //Switch case used for potential 0-7 choice
        switch(direction){
            case 0:
                --xPosition;
                break;
            case 1:
                ++xPosition;
                break;
            case 2:
                --yPosition;
                break;
            case 3:
                ++yPosition;
                break;
            case 4:
                --xPosition;
                ++yPosition;
                break;
            case 5:
                ++xPosition;
                ++yPosition;
                break;
            case 6:
                --xPosition;
                --yPosition;
                break;
            case 7:
                ++xPosition;
                --yPosition;
                break;
        }
        
        //Once a movement is made this conditional checks if x or y are out of bounds of the dimensions of the input array, if they are a false is returned
        if((xPosition < lowerBound) || (xPosition > upperBound)){
            return false;
        }
        if((yPosition < lowerBound) || (yPosition > upperBound)){
            return false;
        }

        //If the move is valid the position of the actual CurrentPosition constructor is modified and a True is returned
        currentPosition.setX(xPosition);
        currentPosition.setY(yPosition);

        return true;
    } 
    
    //This method takes the current value of the warrior and calcuates a score for the warrior(given method)
    public static int CalculateWarriorScore(int currentScore, Position currentPosition, String[][] boardArray){
        
        int xPosition = currentPosition.getX();
        int yPosition = currentPosition.getY();
        
        //If the warrior is on a the updated score will be returned but everything else will be updated in the run game method
        if(boardArray[xPosition][yPosition].equals("0")){
            return currentScore;
        }
        //If the value is a # then the current score + 1 is returned
        if(boardArray[xPosition][yPosition].equals("#")){
            return currentScore + 1;
        }

        //If there is a negative then the value is subtracted and then returned
        int subtract;
        subtract = Integer.parseInt(boardArray[xPosition][yPosition]);
        boardArray[xPosition][yPosition] = "#";
        return currentScore + subtract; 
    } 
    
    //This method return the results of the game
    public static String DisplayResults(int currentScore, int numberOfMoves, int timeElapsed, String[][] boardArray ) {
        
        //String builder is used because String builder is the best 
        StringBuilder resultOfGame = new StringBuilder();
        StringBuilder printBoard = new StringBuilder();
        resultOfGame.append("The warrior made ");
        resultOfGame.append(numberOfMoves + " valid moves in ");
        resultOfGame.append(timeElapsed + " milliseconds. The final score is ");
        resultOfGame.append(currentScore + " points.");
        resultOfGame.append("\n\n");
        
        //Nested loop to access everything
        for(int row = 0; row < boardArray.length; row++){
            for(int col = 0; col < boardArray.length; col++){
                //If the current position is not a # or a 0 then it's a negative number, 3 spaces are used instead of 4 to maintain proper format
                if((!boardArray[row][col].equals("#")) && (!boardArray[row][col].equals("0"))){
                    printBoard.append("   " + boardArray[row][col]);
                }
                else{
                    printBoard.append("    " + boardArray[row][col]);
                }
            }
            
            resultOfGame.append(printBoard.toString());
            resultOfGame.append("\n");
            printBoard = new StringBuilder();
        }
        return resultOfGame.toString();
    }

    

    
}