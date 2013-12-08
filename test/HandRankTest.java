import static org.junit.Assert.*;
import models.Hand;
import models.HandRank;

import org.junit.Test;


public class HandRankTest
{
	HandRank hr1,hr2;
	
	
	@Test
	public void WhenFirstHandWorseThanSecond_ExpectGoodComparision() throws Exception {
		hr1 = new HandRank(new Hand("H2|H3|H4|H5|H6"));
		hr2 = new HandRank(new Hand("C3|C4|C5|C6|C7"));
		
		assertTrue("Comparision 1",hr1.compareTo(hr2) < 0);
	}
	
	public void WhenFirstHandBetterThanSecond_ExpectGoodComparision() throws Exception {
		hr2 = new HandRank(new Hand("H2|C2|S2|D2|S3"));
		hr1 = new HandRank(new Hand("H9|HT|HJ|HQ|HK"));
		
		assertTrue("Comparision 2",hr1.compareTo(hr2) > 0);
	}
	
	@Test
	public void WhenFullHouseOnHand_ExpectThatFunctionFoundIt() throws Exception {
		hr1 = new HandRank(new Hand("H2|C2|H3|C3|S3"));
		assertTrue("Full house",hr1.missingCards[2] == 0);
	}
	
}
