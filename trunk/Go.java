
/**
 * Copyright (c) 2012 Kyle Hughart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Credit for algorithm goes to:
 *
 * Kocsis L. & Szepesvari C. (September 2006). Bandit based Monte-Carlo
 * Planning. Unpublished paper presented European Conference on Machine
 * Learning, Berlin, Germany.
 *
 * Chaslot, Guillaume et al. (October, 2008). Monte-Carlo Tree Search: A New
 * Framework for Game AI. Unpublished paper presented at the Fourth Artificial
 * Intelligence and Interactive Digital Entertainment Conference, Maastricht,
 * The Netherlands.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kyle
 */
public class Go implements Game{

   private int boardSize;
//   
//   private HashSet<Integer> black = new HashSet<>();
//   private HashSet<Integer> white = new HashSet<>();
//   private HashSet<Integer> regions.getNeutral() = new HashSet<>();
//   private HashSet<Integer> hasLiberty = new HashSet<>();
//   private HashSet<Integer> regions.getTouchedBlack() = new HashSet<>();
//   private HashSet<Integer> regions.getTouchedWhite() = new HashSet<>();
//   private HashMap<Integer, Integer> regionCounts = new HashMap<>();
   //private GoRegionTracker regions;
   private final ConcurrentLinkedQueue<GoRegionTracker> regionTrackers;
   //private final ConcurrentLinkedQueue<int[][]> boards;

   public Go(int boardSize)
   {
      this.boardSize = boardSize;
      this.regionTrackers = new ConcurrentLinkedQueue<>();
      for (int i = 0; i < 4; i++)
         regionTrackers.add(new GoRegionTracker());
//      this.boards = new ConcurrentLinkedQueue<>();
//      for (int i = 0; i < 100; i++)
//         boards.add(new int[boardSize][boardSize]);
      //for (int i = 3; i <=50; i++)
         //regionCounts.put(i, 0);
   }
     
   @Override
   public ArrayList<? extends GameState> getPossibleMoves(GameState state)
   {
      
      //regions.clear();
      GoGameState g =  (GoGameState) state;
      ArrayList<GoGameState> posMoves = new ArrayList<>();
      //if the game is over, there are no possible moves
      if (g.isGameOver())
         return posMoves;
      GoRegionTracker regions = regionTrackers.remove();//new GoRegionTracker();
      //add the "pass" move
      posMoves.add(new GoGameState((g.getPlayerTurn()%2) + 1 , boardSize, true, g.isLastTurnPass(), g.getBoard(), null));    

      
      HashSet<Integer> activeColor;
      HashSet<Integer> passiveColor;
      if (g.getPlayerTurn() == 1)
      {
         activeColor = regions.getBlack();
         passiveColor = regions.getWhite();
      }
      else
      {
         activeColor = regions.getWhite();
         passiveColor = regions.getBlack();
      }      
      
      for (int y = 0; y < boardSize; y++)
      {
         for (int x = 0; x < boardSize; x++)
         {
            if (g.getBoard()[x][y] == 0) //if we have an open spot
            {               
               
               boolean enemyCaptured = false;
               boolean suicide = false;
               int[][] board = copyBoard(g.getBoard()); //copy the board
               board[x][y] = g.getPlayerTurn();
               
               //if (interestingMove(g.getPlayerTurn(), outerX, outerY, board))
               //{
//                  //flag all regions
//                  int flag = 3;
//                  for (int y = 0; y < boardSize; y++)
//                  {
//                     for (int x = 0; x < boardSize; x++)
//                     {
//                        if (board[x][y] == 1 || board[x][y] == 2)
//                           flagRegion(flag++, x, y, board);
//                     }
//                  }
                  int flag = 3;
                  int adjacent;
                  boolean suicideImpossible = false;
                  if (x != 0)
                  {
                     adjacent = (board[x-1][y]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x - 1, y, board, regions);
                  }
                  if (x != boardSize - 1)
                  {
                     adjacent = (board[x+1][y]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x + 1, y, board, regions);
                  }
                  if (y != 0)
                  {
                     adjacent = (board[x][y-1]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x, y - 1, board, regions);
                  }
                  if (y != boardSize - 1)
                  {
                     adjacent = (board[x][y+1]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x, y + 1, board, regions);
                  }
                  if (!suicideImpossible)
                     flagRegion(flag++, x, y, board, regions);

                  //process captures and suicides
                  for(int i = 3; i <= flag; i++)
                  {
                     if (!regions.getHasLiberty().contains(i) && passiveColor.contains(i))
                     {
                        enemyCaptured = true;
                        replaceInBoard(i, 0, board); //clear that region
                     }
                     else if (!suicideImpossible && activeColor.contains(i) && !regions.getHasLiberty().contains(i))
                        suicide = true;
                  }
                  
                  

                  //restore regions
                  for(int i = 3; i <= flag; i++)
                  {
                     if (regions.getBlack().contains(i))
                        replaceInBoard(i, 1, board);
                     else if (regions.getWhite().contains(i))
                        replaceInBoard(i, 2, board);
                     else
                        replaceInBoard(i, 0, board);
                  }
                  regions.clear();
               //}

               GoGameState newGameState = new GoGameState((g.getPlayerTurn()%2) + 1 , boardSize, false, false, board, g.getBoard());
               if (!(suicide && !enemyCaptured) && (g.getIllegalState() == null || !g.getIllegalState().equals(newGameState))) //if move is legal
               {
                  //copy.add(g);
                  //printState(posMove);
                  posMoves.add(newGameState);
               }
               //for (int i = 3; i <=50; i++)
                 // regionCounts.put(i, 0);
            }
         }
      }
      regionTrackers.add(regions);
      return posMoves;
   }
   
   
   private boolean interestingMove(int playerTurn, int x, int y, int[][] board)
   {
      boolean suicideImpossible = false;
      boolean possibleCapture = false;
      ArrayList<Integer> adjacents = new ArrayList<>();
      if (x != 0)
         adjacents.add(board[x-1][y]);
      if (x != boardSize - 1)
         adjacents.add(board[x+1][y]);
      if (y != 0)
         adjacents.add(board[x][y-1]);
      if (y != boardSize - 1)
         adjacents.add(board[x][y+1]);
      for (Integer i : adjacents)
      {
         if (i == 0) //if any adjacent square are empty, this cannot be a suicide
            suicideImpossible = true;
         if (i == (playerTurn % 2) + 1) //if any adjacent squares are enemies, this may be a capture
            possibleCapture = true;
      }
      return (!suicideImpossible) || possibleCapture; //a move is interesting if it might be a suicide or a capture
      
   }
   
   
   private int[][] copyBoard(int[][] src)
   {
      int[][] board;
//      if (!boards.isEmpty())
//         board = boards.remove();
//      else
         board = new int[boardSize][boardSize];
      for (int y = 0; y < boardSize; y++)
         for (int x = 0; x < boardSize; x++)
            board[x][y] = src[x][y];
      return board;
   }
   
   
   private void replaceInBoard(int val, int newVal, int[][] board)
   {
      for (int y = 0; y < boardSize; y++)
         for (int x = 0; x < boardSize; x++)
            if (board[x][y] == val)
               board[x][y] = newVal;
   }
   
   // y = i, x = j;
   //private int recursiveCount;
   private void flagRegion(int regionFlag, int x, int y, int[][] board, GoRegionTracker regions)
   {
      //recursiveCount = 0;
//      TestRun.startTime = System.currentTimeMillis();
      flagRegionRecusive(regionFlag, x, y, board, regions);
//      if (System.currentTimeMillis() - TestRun.startTime > 100)
//      {
//         System.out.print("CHECK: " + (System.currentTimeMillis() - TestRun.startTime) + " ");      
//         System.out.print("RECURS: " + recursiveCount + " ");
//      }
   }
   private void flagRegionRecusive(int regionFlag, int x, int y, int[][] board, GoRegionTracker regions)
   {
      //recursiveCount++;
      int value = board[x][y]; //check the value of the current node
      board[x][y] = regionFlag; // flag it
      //increment the count
      //TestRun.startTime = System.currentTimeMillis();
      if (regions.getRegionCounts().get(regionFlag) == null)
         regions.getRegionCounts().put(regionFlag, 1);
      else
         regions.getRegionCounts().put(regionFlag, regions.getRegionCounts().get(regionFlag) + 1 );
      //if (System.currentTimeMillis() - TestRun.startTime > 100)
        // System.out.print("CHECK: " + (System.currentTimeMillis() - TestRun.startTime) + " ");      
      //add its flag to the appropriate lists based upon its adjacents
      if (x != 0)
         handleAdjacent(value, regionFlag, x - 1, y, board, regions);
      if (y != 0)
         handleAdjacent(value, regionFlag, x, y - 1, board, regions);
      if (x != boardSize - 1)
         handleAdjacent(value, regionFlag, x + 1, y, board, regions);
      if (y != boardSize - 1)
         handleAdjacent(value, regionFlag, x, y + 1, board, regions);
   }
   
   
   private void handleAdjacent(int value, int regionFlag, int x, int y, int[][] board, GoRegionTracker regions)
   {
         int adjacentValue = board[x][y];
         if (value == adjacentValue && value <= 2) //if the adjacent is the same state, scan it
            flagRegionRecusive(regionFlag, x, y, board, regions);
         //else if (value == 0) //if the current space is blank (should never be called unless value is 0, 1 or 2)
         //{
            //if (adjacentValue == 1 || black.contains(adjacentValue)) //and the next space is black
               //regions.getTouchedBlack().add(regionFlag);
            //else if (adjacentValue == 2 || white.contains(adjacentValue)) //and the next space is white
               //regions.getTouchedWhite().add(regionFlag);
         //}
         else if (value == 1) //if the current space is black
         {
            regions.getBlack().add(regionFlag);
            if (adjacentValue == 0 || regions.getNeutral().contains(adjacentValue)) //and the next space is blank
               regions.getHasLiberty().add(regionFlag);
         }
         else if (value == 2) //if the current space is white
         {
            regions.getWhite().add(regionFlag);
            if (adjacentValue == 0) //and the next space is blank
               regions.getHasLiberty().add(regionFlag);
         }
         else if (value == 0)
            regions.getNeutral().add(regionFlag);
   }

   @Override
   public status gameStatus(GameState state)
   {
      GoRegionTracker regions = new GoRegionTracker();
      GoGameState  g = (GoGameState) state;
      if (!g.isGameOver())
         return status.ONGOING;
      else
      {
         //copy the board
         int[][] board = copyBoard(g.getBoard()); 
                 
         //flag all regions empty
         int flag = 3;
         for (int y = 0; y < boardSize; y++)
         {
            for (int x = 0; x < boardSize; x++)
            {
               if (board[x][y] <= 2)
                  flagRegionRecusive(flag++, x, y, board, regions);
            }
         }

         //score
         int whiteScore = 0;
         int blackScore = 0;
         
         for(int i = 0; i <= flag; i++)
         {
            if (regions.getWhite().contains(i)) //if a region is white
               whiteScore += regions.getRegionCounts().get(i);
            else if (regions.getTouchedWhite().contains(i) && !regions.getTouchedBlack().contains(i)) //or touching white and not black
               whiteScore += regions.getRegionCounts().get(i);
            
            if (regions.getBlack().contains(i)) //if a region is black
               blackScore += regions.getRegionCounts().get(i);
            else if (regions.getTouchedBlack().contains(i) && !regions.getTouchedWhite().contains(i)) //or touching black and not white
               blackScore += regions.getRegionCounts().get(i);
         }

         
//         //restore regions
//         for(int i = 0; i <= flag; i++)
//         {
//            if (black.contains(i))
//               replaceInBoard(i, 1, board);
//            else if (white.contains(i))
//               replaceInBoard(i, 2, board);
//            else
//               replaceInBoard(i, 0, board);
//         }
         
         regions.clear();
         //for (int i = 3; i <=50; i++)
            //regionCounts.put(i, 0);
         
         if (blackScore > whiteScore)
            return status.PLAYER1WIN;
         else if (whiteScore > blackScore)
            return status.PLAYER2WIN;
         else
            return status.DRAW;
      }
   }

   @Override
   public GameState getStartingState()
   {
      int[][] board = new int[boardSize][boardSize];
      for (int y = 0; y < boardSize; y++)
         for (int x = 0; x < boardSize; x++)
            board[x][y] = 0;

//      board[1][0] = 1;
//      board[0][1] = 2;
//      board[1][1] = 2;
//      board[2][0] = 2;
//      board[1][1] = 2;
//      board[2][1] = 1;
//      board[3][0] = 1;
//      board[3][1] = 1;
//      board[3][0] = 2;
//      board[3][3] = 1;
//      board[3][5] = 1;
//      board[3][4] = 1;
//      
//      board[5][5] = 2;
//      board[5][3] = 2;
//      board[5][4] = 2;
      return new GoGameState(1, boardSize, false, false, board, null);
   }

   public void printState(GameState state)
   {
      GoRegionTracker regions = new GoRegionTracker();
      GoGameState g =  (GoGameState) state;
      //ArrayList<GoGameState> posMoves = new ArrayList<>();
      //if the game is over, there are no possible moves
      //if (g.isGameOver())
        // return posMoves;
      //add the "pass" move
      //posMoves.add(new GoGameState((g.getPlayerTurn()%2) + 1 , boardSize, true, g.isLastTurnPass(), g.getBoard(), null));    

      
      HashSet<Integer> activeColor;
      HashSet<Integer> passiveColor;
      if (g.getPlayerTurn() == 1)
      {
         activeColor = regions.getBlack();
         passiveColor = regions.getWhite();
      }
      else
      {
         activeColor = regions.getWhite();
         passiveColor = regions.getBlack();
      }      
      int count = 1;
      for (int y = 0; y < boardSize; y++)
      {
         for (int x = 0; x < boardSize; x++)
         {
            int[][] board = copyBoard(g.getBoard()); //copy the board
            if (g.getBoard()[x][y] == 0) //if we have an open spot
            {               
               
               boolean enemyCaptured = false;
               boolean suicide = false;
               board[x][y] = g.getPlayerTurn();
               
               //if (interestingMove(g.getPlayerTurn(), outerX, outerY, board))
               //{
//                  //flag all regions
//                  int flag = 3;
//                  for (int y = 0; y < boardSize; y++)
//                  {
//                     for (int x = 0; x < boardSize; x++)
//                     {
//                        if (board[x][y] == 1 || board[x][y] == 2)
//                           flagRegion(flag++, x, y, board);
//                     }
//                  }
                  int flag = 3;
                  int adjacent;
                  boolean suicideImpossible = false;
                  if (x != 0)
                  {
                     adjacent = (board[x-1][y]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x - 1, y, board, regions);
                  }
                  if (x != boardSize - 1)
                  {
                     adjacent = (board[x+1][y]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x + 1, y, board, regions);
                  }
                  if (y != 0)
                  {
                     adjacent = (board[x][y-1]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x, y - 1, board, regions);
                  }
                  if (y != boardSize - 1)
                  {
                     adjacent = (board[x][y+1]);
                     if (adjacent == 0)
                        suicideImpossible = true;
                     else if (adjacent == (g.getPlayerTurn() % 2) + 1)
                        flagRegion(flag++, x, y + 1, board, regions);
                  }
                  if (!suicideImpossible)
                     flagRegion(flag++, x, y, board, regions);

                  //process captures and suicides
                  for(int i = 3; i <= flag; i++)
                  {
                     if (!regions.getHasLiberty().contains(i) && passiveColor.contains(i))
                     {
                        enemyCaptured = true;
                        replaceInBoard(i, 0, board); //clear that region
                     }
                     else if (!suicideImpossible && activeColor.contains(i) && !regions.getHasLiberty().contains(i))
                        suicide = true;
                  }
                  
                  

                  //restore regions
                  for(int i = 3; i <= flag; i++)
                  {
                     if (regions.getBlack().contains(i))
                        replaceInBoard(i, 1, board);
                     else if (regions.getWhite().contains(i))
                        replaceInBoard(i, 2, board);
                     else
                        replaceInBoard(i, 0, board);
                  }
               //}

               GoGameState newGameState = new GoGameState((g.getPlayerTurn()%2) + 1 , boardSize, false, false, board, g.getBoard());
               if (!(suicide && !enemyCaptured) && (g.getIllegalState() == null || !g.getIllegalState().equals(newGameState))) //if move is legal
               {
                  //copy.add(g);
                  //printState(posMove);
                  //posMoves.add(newGameState);
                  System.out.print(count);
                  if (x < boardSize - 1)
                     for (int i = count; i < 1000; i = i * 10)
                        System.out.print("_");
                  count++;
               }
               else
               {
                     System.out.print(".");
                     if (x < boardSize - 1)
                        System.out.print("___");
               }
                  
               regions.clear();
               //for (int i = 3; i <=50; i++)
                 // regionCounts.put(i, 0);
            }
            else
            {
               switch ((board[x][y]))
               {
                  case 0:
                     System.out.print(".");
                     if (x < boardSize - 1)
                        System.out.print("___");
                  break;
                  case 1:
                     System.out.print("☻");
                     if (x < boardSize - 1)
                        System.out.print("___");
                  break;
                  case 2:
                     System.out.print("☺");
                     if (x < boardSize - 1)
                        System.out.print("___");
                  break;
                  default:
                     System.out.print(board[x][y]);
               }
            }
         }
            System.out.println();
            if (y < boardSize - 1)
            {
               for (int i = 0; i < boardSize - 1; i++)
                  System.out.print("|   ");
               System.out.println("|");
            }
         
      }
      System.out.println();
      
      //return posMoves;
   }
   
   //@Override
   public void printstate(GameState state)
   {
      GoGameState g =  (GoGameState) state;
      int[][] board = g.getBoard();
      System.out.println();
      int count = 1;
      for (int y = 0; y < boardSize; y++)
      {
         for (int x = 0; x < boardSize; x++)
         {
            switch ((board[x][y]))
            {
               case 0:
                  System.out.print(count);
                  if (x < boardSize - 1)
                     for (int i = count; i < 1000; i = i * 10)
                        System.out.print("_");
                  count++;
               break;
               case 1:
                  System.out.print("☻");
                  if (x < boardSize - 1)
                     System.out.print("___");
               break;
               case 2:
                  System.out.print("☺");
                  if (x < boardSize - 1)
                     System.out.print("___");
               break;
               default:
                  System.out.print(board[x][y]);
            }
         }
         System.out.println();
         if (y < boardSize - 1)
         {
            for (int i = 0; i < boardSize - 1; i++)
               System.out.print("|   ");
            System.out.println("|");
         }
      }
      System.out.println();
   }

}
