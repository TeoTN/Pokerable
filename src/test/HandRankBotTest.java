package test;
import model.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Indexes: 
 *      8 - high card
 *  7 - one pair
 *  6 - two pairs
 *  5 - three of a kind
 *  4 - straight
 *  3 - flush
 *  2 - full house
 *  1 - four of a kind
 *  0 - straight flush
 * @param cards
 */
public class HandRankBotTest
{
        Hand hand;
        HandRankBot test, test2;
        HandRankBot twoPairsTest1, twoPairsTest2, twoPairsTest3;
        HandRankBot comparable1, comparable2, comparable3, comparable4;
        HandRankBot fullTest1;
        HandRankBot straightFlush1;
        @Before
        public void init() throws Exception {
                hand = new Hand("D2|S2|S3|S3|C3");
                test = new HandRankBot(hand);
                
                hand = new Hand("D2|S2|S2|S3|C3");
                test2 = new HandRankBot(hand);
                
                /**
                 * Two Pairs Test 1
                 */
                hand = new Hand("S5|H5|H9|S9|SQ");
                twoPairsTest1 = new HandRankBot(hand);
                
                /**
                 * Two Pairs Test 2
                 */
                hand = new Hand("S5|H5|S7|H9|S9");
                twoPairsTest2 = new HandRankBot(hand);
                
                /**
                 * Two Pairs Test 3
                 */
                hand = new Hand("D2|S5|H5|H9|S9");
                twoPairsTest3 = new HandRankBot(hand);
                
                /**
                 * Comparator test 1
                 */
                hand = new Hand("D2|S5|H5|H9|S9");
                comparable1 = new HandRankBot(hand);
                hand = new Hand("D2|S6|H6|H9|S9");
                comparable2 = new HandRankBot(hand);
                
                /**
                 * Comparator test 2
                 */
                hand = new Hand("C3|C4|D5|H7|SJ");
                comparable3 = new HandRankBot(hand);
                hand = new Hand("D2|SQ|H5|C6|C4");
                comparable4 = new HandRankBot(hand);
                
                /**
                 * Full house test 1
                 */
                hand = new Hand("C3|D6|H6|S6|SQ");
                fullTest1 = new HandRankBot(hand);
                
                /**
                 * Straight Flush test 1
                 */
                hand = new Hand("SA|S2|H2|S4|S5");
                straightFlush1 = new HandRankBot(hand);
        }
        @Test
        public void testHR() throws Exception {
                for(int i = 0 ; i < 9 ; i ++){
                        System.out.print(test.missingCards[i]+" ");
                }
                System.out.println("");
                for(int i = 0 ; i < 9 ; i ++){
                        System.out.print(test.bestCard[i].toString()+" ");
                }
                System.out.println("\n");
                for(int i = 0 ; i < 9 ; i ++){
                        System.out.print(test2.missingCards[i]+" ");
                }
                System.out.println("");
                for(int i = 0 ; i < 9 ; i ++){
                        System.out.print(test2.bestCard[i].toString()+" ");
                }
                System.out.println(""); System.out.println("");
                
                System.out.println("Ans: "+test.compareTo(test2));
        }
        
        private void printTest(HandRankBot testSet) {
                System.out.println("Assessed hand: "+testSet.getAssessedHand());
                for(int i = 0 ; i < 9; i++)
                        System.out.print(testSet.missingCards[i]+" ");
                System.out.println("");
                for(int i = 0 ; i < 9; i++)
                        System.out.print(testSet.bestCard[i].toString()+" ");
                System.out.println("");
                
                List<Card> tmp = testSet.getChangeList(testSet.getAssessedHand());
                
                for(int i = 0 ; i < tmp.size(); i ++){
                	System.out.print(tmp.get(i).toString() + " ");
                }
                		
                System.out.print("\n\n");
        }
        
        @Test
        public void twoPairsTest1() {
                printTest(twoPairsTest1);
                assertEquals("Unproperly defined highest card in poker hand.", twoPairsTest1.bestCard[6].getCardValue(), 7);
        }
        
        @Test
        public void twoPairsTest2() {
                printTest(twoPairsTest2);
                assertEquals("Unproperly defined highest card in poker hand.", twoPairsTest1.bestCard[6].getCardValue(), 7);
        }
        
        @Test
        public void twoPairsTest3() {
                printTest(twoPairsTest3);
                assertEquals("Unproperly defined highest card in poker hand.", twoPairsTest1.bestCard[6].getCardValue(), 7);
        }
        
        @Test
        public void comparation1() {
                printTest(comparable1);
                assertEquals("Improper hand was assessed as stronger.",comparable1.compareTo(comparable2), -1);
        }
        
        @Test
        public void comparation2() {
                printTest(comparable3);
                assertEquals("Improper hand was assessed as stronger.",comparable3.compareTo(comparable4), -1);
                assertEquals("Wrong number of missing cards to achieve two pairs", comparable3.missingCards[6], 2);
        }
        
        @Test
        public void fullTest1() {
                printTest(fullTest1);
        }
        
        @Test
        public void straightFlush1() {
        		printTest(straightFlush1);
        }
}