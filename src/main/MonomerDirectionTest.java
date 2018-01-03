package main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MonomerDirectionTest {

	@Test
	public void testbyNumber() {

		assertEquals(MonomerDirection.byNumber(0), MonomerDirection.FORWARD);
		assertEquals(MonomerDirection.byNumber(2), MonomerDirection.RIGHT);
		assertEquals(MonomerDirection.byNumber(4), MonomerDirection.DOWN);
		assertEquals(MonomerDirection.byNumber(6), MonomerDirection.FIRST);
		try {
			MonomerDirection.byNumber(8);
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}
