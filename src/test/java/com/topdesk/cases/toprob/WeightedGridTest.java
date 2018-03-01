package com.topdesk.cases.toprob;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.topdesk.cases.toprob.helper.GridFactory;
import com.topdesk.cases.toprob.yoursolution.WeightedGrid;

public class WeightedGridTest {

	@Test
	public void testCalculation() {
		Grid grid = GridFactory.create(
				".ok..",
				"B..o.",
				".ooo.",
				"..r.A");
		
		WeightedGrid wGrid = new WeightedGrid(grid);
		
		assertEquals(2, wGrid.getWeight(new Coordinate(1, 1)));
		assertEquals(1, wGrid.getWeight(new Coordinate(3, 0)));
		assertEquals(-1, wGrid.getWeight(new Coordinate(2, 0)));
		assertEquals(Integer.MAX_VALUE, wGrid.getWeight(new Coordinate(1, 0)));
		assertEquals(Integer.MAX_VALUE, wGrid.getWeight(new Coordinate(-1, 2)));
		assertEquals(Integer.MAX_VALUE, wGrid.getWeight(new Coordinate(1, -2)));
	}

	@Test
	public void testMinNeighbour() {
		Grid grid = GridFactory.create(".ok..", "B..o.", ".ooo.", "..r.A");

		WeightedGrid wGrid = new WeightedGrid(grid);

		assertEquals(new Coordinate(2, 1), wGrid.getMinWeighNeighbourCoordinate(new Coordinate(1, 1)));
		assertEquals(new Coordinate(4, 2), wGrid.getMinWeighNeighbourCoordinate(new Coordinate(4, 3)));

	}

}
