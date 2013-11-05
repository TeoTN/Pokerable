package test;
import model.*;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HandTest
{
	@Test
	public void test() throws Exception{
		Hand pair = new Hand("S2|H2|DA|D9|D4"); pair.sort();
		assertTrue("Sth is very wrong", pair.toString().equals("S2|H2|D4|D9|DA"));
		assertTrue("Simple Check",pair.includes("S2"));
	}
}
