
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
/**
 * An MCTSPlayer that detects game-ending moves in the simulation phase.
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.Random;

public class DeathDetectingRollout extends MCTSPlayer
{

   public DeathDetectingRollout(Game g, boolean player1, int thinkTime)
   {
      super(g, player1, thinkTime);
   }

   /**
    * Simulates a random play-through from a given state and returns the result.
    *
    * @param state The state to be simulated from.
    * @return the game status at the end of the simulation.
    */
   private int simCounter = 0;
   @Override
   protected Game.status simulateFrom(GameState state, boolean myTurn)
   {
      simCounter++;
      //TestRun.startTime = System.currentTimeMillis();
      Game.status s = g.gameStatus(state);
      //if (System.currentTimeMillis() - TestRun.startTime > 100)
            //System.out.print("CHECK: " + (System.currentTimeMillis() - TestRun.startTime) + " ");
      if (s != Game.status.ONGOING) {
         //System.out.print("(" + simCounter + ") ");
         simCounter = 0;
         return s;
      } else {
         return simulateFrom(getMoveFrom(state, myTurn), !myTurn);
      }
   }

   /**
    * Gets a random move from a given state.
    *
    * @param gameState a game state from which a random child state is desired.
    * @return a random child state of the passed state.
    */
   private GameState getMoveFrom(GameState gameState, boolean myTurn)
   {
      //TestRun.startTime = System.currentTimeMillis();
      ArrayList<? extends GameState> moves = g.getPossibleMoves(gameState);
      //if (System.currentTimeMillis() - TestRun.startTime > 100)
                     //System.out.print("CHECK_TWO: " + (System.currentTimeMillis() - TestRun.startTime) + " ");                           
      Random rand = new Random();
      if (myTurn) //if it's my turn, and I can win, do it.
         for (int i = 0; i < moves.size(); i++)
            if (IWin(g.gameStatus(moves.get(i))))
            {
               return moves.get(i);
            }
      if (!myTurn) //if it's their turn, and they can win, they will.
         for (int i = 0; i < moves.size(); i++)
            if (ILose(g.gameStatus(moves.get(i))))
               return moves.get(i);
      int r = rand.nextInt(moves.size());
      return moves.get(r);
   }
}
