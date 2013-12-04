import static org.junit.Assert.*;
import models.Hand;

import org.junit.Test;


public class HandTest
{
	Hand testHand1, testHand2;
	
	@Test
	public void WhenHandInitialized_ExpectValidHand() throws Exception {
		testHand1 = new Hand("H5|H6|C5|C6|SA");
		assertEquals("Valid hand","H5|C5|H6|C6|SA",testHand1.toString()); // sorted
	}
	
	@Test (expected = Exception.class)
	public void WhenHandInitializedWithBadValue_ExpectException() throws Exception {
		testHand1 = new Hand("I <3 boobies");
	}
	
	@Test (expected = Exception.class)
	public void WhenHandInitializedWithBadCardAmount_ExpectException() throws Exception {
		testHand1 = new Hand("H5|H6|C5|C6|SA|HA");
	}
	
	@Test
	public void WhenHandInitialized_ExpectHandIncludesCards() throws Exception {
		testHand1 = new Hand("H5|H6|C5|C6|SA");
		
		assertTrue("Includes H5",testHand1.includes("H5"));
		assertTrue("Includes C5",testHand1.includes("C5"));
	}
}
