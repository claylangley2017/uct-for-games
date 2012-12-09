
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
import java.util.HashMap;
import java.util.HashSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kyle
 */
public class GoRegionTracker {

      
   private final HashSet<Integer> black = new HashSet<>();
   private final HashSet<Integer> white = new HashSet<>();
   private final HashSet<Integer> neutral = new HashSet<>();
   private final HashSet<Integer> hasLiberty = new HashSet<>();
   private final HashSet<Integer> touchedBlack = new HashSet<>();
   private final HashSet<Integer> touchedWhite = new HashSet<>();
   private final HashMap<Integer, Integer> regionCounts = new HashMap<>();

   public HashSet<Integer> getBlack()
   {
      return black;
   }

   public HashSet<Integer> getHasLiberty()
   {
      return hasLiberty;
   }

   public HashSet<Integer> getNeutral()
   {
      return neutral;
   }

   public HashMap<Integer, Integer> getRegionCounts()
   {
      return regionCounts;
   }

   public HashSet<Integer> getTouchedBlack()
   {
      return touchedBlack;
   }

   public HashSet<Integer> getTouchedWhite()
   {
      return touchedWhite;
   }

   public HashSet<Integer> getWhite()
   {
      return white;
   }
   
   
   public void clear()
   {
      black.clear();
      white.clear();
      neutral.clear();
      hasLiberty.clear();
      touchedBlack.clear();
      touchedWhite.clear();         
      regionCounts.clear();
   }
   
   
   
}
