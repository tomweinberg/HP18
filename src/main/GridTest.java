package main;

import static org.junit.Assert.*;

import org.junit.Test;

public class GridTest {
	Grid grid;

	@Test
	public void testGrid() {
		grid = Grid.getInstance(100, Dimensions.TWO);
		assertNotNull(grid);

	}

	@Test
	public void testSetInitialSize() {
		// if we want this test to work we should change the minX,Y,Z and
		// maxX,Y,Z to public in Grid class
		/*
		 * grid=Grid.getInstance(100,Dimensions.TWO); assertEquals(grid.minX,
		 * -10); assertEquals(grid.minY, -10); assertEquals(grid.maxX, 10);
		 * assertEquals(grid.maxY, 10); assertEquals(grid.minZ,grid.maxZ,0);
		 */
	}

	@Test
	public void testSetGetCell() {
		/*
		 * grid=Grid.getInstance(100,Dimensions.TWO); Monomer m=new
		 * Monomer(MonomerType.H,0, null); m.getX(); grid.setCell(0, 0, 0, m);
		 * assertEquals(m, grid.getCell(0, 0, 0));
		 * 
		 * 
		 * for(int j=0;j<10;j++) for(int i=0;i<10;i++) { if
		 * (i==0&&j==0)continue;
		 * 
		 * assertNull(grid.getCell(i, j, 0)); } m=new Monomer(MonomerType.P,0,
		 * null); grid.setCell(99, 99, 99, m); assertEquals(m, grid.getCell(99,
		 * 99, 99)); assertNull(grid.getCell(99, 99, 100));
		 */
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountContacts() {
		fail("Not yet implemented");
	}

	@Test
	public void testXEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testYEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testZEdge() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetProtein() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetProteinMonomer() {
		fail("Not yet implemented");
	}

	@Test
	public void testResetMonomer() {
		fail("Not yet implemented");
	}

	@Test
	public void testTestEmpty() {
		fail("Not yet implemented");
	}

}
