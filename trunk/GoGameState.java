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

import java.awt.Point;
import java.util.Arrays;

/**
 *
 * @author Kyle
 */
public class GoGameState implements GameState{

   private final int playerTurn, boardSize;
   private final boolean lastTurnPass, gameOver;
   private final int [][] board;
   private final GoGameState lastState;
   private final int[][] illegalState;
   private final Point move;

   public GoGameState(int playerTurn, int boardSize, boolean lastTurnPass, boolean gameOver, int[][] board, int[][] illegalState)
   {
      this.playerTurn = playerTurn;
      this.boardSize = boardSize;
      this.lastTurnPass = lastTurnPass;
      this.gameOver = gameOver;
      this.board = board;
      this.illegalState = illegalState;
      this.lastState = null;
      this.move = new Point(-1, -1);
   }
   
//   public GoGameState(int playerTurn, int boardSize, boolean lastTurnPass, boolean gameOver, Point move, GoGameState lastState, GoGameState illegalState)
//   {
//      this.playerTurn = playerTurn;
//      this.boardSize = boardSize;
//      this.lastTurnPass = lastTurnPass;
//      this.gameOver = gameOver;
//      this.board = lastState.board;
//      this.illegalState = illegalState;
//      this.lastState = lastState;
//      this.move = move;
//      this.regionTrackers = new ConcurrentLinkedQueue<>();
//   }

   public Point getMove()
   {
      return move;
   }
   
   public int[][] getIllegalState()
   {
      return illegalState;
   }
  
   public boolean equals(GoGameState g)
   {
      if (this.playerTurn != g.playerTurn)
         return false;
      if (this.boardSize != g.boardSize)
         return false;
      if (this.lastTurnPass != g.lastTurnPass)
         return false;
      if (this.gameOver != g.gameOver)
         return false;
      for (int y = 0; y < boardSize; y++)
         for (int x = 0; x < boardSize; x++)
            if (g.board[x][y] != board[x][y])
               return false;
      return true;
   }

   public int[][] getBoard()
   {
      return board;
   }

   public int getIntersection(Point p)
   {
      if (move == p)
         return lastState.playerTurn;
      else
         return board[p.x][p.y];
   }

   
   public int getIntersection(int x, int y)
   {
      return getIntersection(new Point(x, y));
   }
   
   public int getBoardSize()
   {
      return boardSize;
   }

   public boolean isGameOver()
   {
      return gameOver;
   }

   public boolean isLastTurnPass()
   {
      return lastTurnPass;
   }

   public int getPlayerTurn()
   {
      return playerTurn;
   } 
   
   @Override
   public boolean equals(GameState g)
   {
      if (g instanceof GoGameState)
         return equals((GoGameState)g);
      else
         return false;
   }

   @Override
   public boolean equals(Object g)
   {
      if (g instanceof GoGameState)
         return equals((GoGameState)g);
      else
         return false;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 29 * hash + this.playerTurn;
      hash = 29 * hash + this.boardSize;
      hash = 29 * hash + (this.lastTurnPass ? 1 : 0);
      hash = 29 * hash + (this.gameOver ? 1 : 0);
      hash = 29 * hash + Arrays.deepHashCode(this.board);
      return hash;
   }
   
}
