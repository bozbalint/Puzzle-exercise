package com.topdesk.cases.toprob.yoursolution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.topdesk.cases.toprob.Coordinate;
import com.topdesk.cases.toprob.Grid;
import com.topdesk.cases.toprob.Instruction;

/**
 * @author bozbalint
 * 
 *         It is a kind'a decorator class of the Grid. Calculates and presents the
 *         Kitchen distance from each cell. The optimal route from the Room to
 *         the Kitchen is continuously making a step to a cell which has the
 *         minimal weights, which means it is closer to the Kitchen.
 * 
 *         If more optimal route is available than takes the fist one.
 */
public class WeightedGrid {

	/**
	 * Array represents the grid but with weights each weight is a distance from the
	 * Kitchen
	 */
	private int[][] gridWithDistances;

	private final Grid grid;

	// the possible movements from a cell
	private final List<Instruction> neighbours = Arrays.asList(Instruction.NORTH, Instruction.SOUTH, Instruction.EAST,
			Instruction.WEST);

	
	public WeightedGrid(Grid grid) {
		this.grid = grid;
		calculateDistances();
	}

	/**
	 * Calculates the distance from a cell to the Kitchen. If a route is available
	 * from the room to the kitchen than it stops the calculation. The
	 * gridWithDistances will not be fully filled.
	 */
	private void calculateDistances() {
		gridWithDistances = new int[grid.getWidth()][grid.getHeight()];

		// set the kitchen position in the array as -1. 0 is the default int array
		// value...
		gridWithDistances[grid.getKitchen().getX()][grid.getKitchen().getY()] = -1;

		// will set distance weight value of the kitchen neighbours at first
		Set<Coordinate> toCheckList = new LinkedHashSet<>();
		toCheckList.add(grid.getKitchen());
		boolean ready = false;

		// loop index, and the actual distance from the Kitchen
		int distance = 1;

		// the iteration start with the closes cells to the Kitchen than each
		// iteration get one step further
		do {
			Set<Coordinate> nextToCheck = new LinkedHashSet<>();

			for (Coordinate toCheck : toCheckList) {
				
				Set<Coordinate> validSteps = getStepableNeighbourCoordinates(toCheck);

				for (Coordinate stepCoordinate : validSteps) {
					// do not overwrite calculated values.
					if (gridWithDistances[stepCoordinate.getX()][stepCoordinate.getY()] == 0) {
						gridWithDistances[stepCoordinate.getX()][stepCoordinate.getY()] = distance;
						if (stepCoordinate.equals(grid.getRoom()))
							ready = true;
					}
					nextToCheck.add(stepCoordinate);
				}
			}
			toCheckList = nextToCheck;
			distance++;
		} while (!ready);

	}

	/**
	 * @param coordinate of a cell
	 * @return the cell and the Kitchen distance
	 * 
	 * 	Int max value will be skipped at the search of the min value
	 */
	public int getWeight(Coordinate coordinate) {
		if (isCoordinatePartOfGrid(coordinate)) {
			int weight = gridWithDistances[coordinate.getX()][coordinate.getY()];
			// 0 is the default initial value that means not a valid weight
			return weight == 0 ? Integer.MAX_VALUE : weight;
		} else
			return Integer.MAX_VALUE;
	}

	/**
	 * @param coordinate of a cell
	 * @return a neighbour of the cell which is the closes to the Kitchen
	 */
	public Coordinate getMinWeighNeighbourCoordinate(Coordinate coordinate) {
		if (isCoordinatePartOfGrid(coordinate)) {
			Set<Coordinate> neighbourList = getStepableNeighbourCoordinates(coordinate);

			final Comparator<Coordinate> comp = (c1, c2) -> Integer.compare(getWeight(c1), getWeight(c2));
			Coordinate lowestWeight = neighbourList.stream().min(comp).get();
			return lowestWeight;
		} else
			throw new IndexOutOfBoundsException("Coordinate is not part of the Grid: " + coordinate.toString());
	}

	
	/**
	 * @param toCheck coordinate for surrounding neighbours
	 * @return NORTH, SOUTH, EAST, WEST neighbours of the cell but filtered if Holes or out of the Grid
	 */
	private Set<Coordinate> getStepableNeighbourCoordinates(Coordinate toCheck) {
		return neighbours.stream().map(p -> p.execute(toCheck)).filter(p -> isStepableGridCoordinates(p))
				.collect(Collectors.toSet());
	}

	/**
	 * @param coordinate for check
	 * @return is not hole and part of the Grid
	 */
	private boolean isStepableGridCoordinates(Coordinate coordinate) {
		boolean isNoHole = !grid.getHoles().contains(coordinate);
		return isNoHole && isCoordinatePartOfGrid(coordinate);
	}

	/**
	 * @param coordinate for check
	 * @return if the coordinate is part of the grid
	 */
	private boolean isCoordinatePartOfGrid(Coordinate coordinate) {
		boolean isOnWith = coordinate.getX() >= 0 && coordinate.getX() < grid.getWidth();
		boolean isOnHeigh = coordinate.getY() >= 0 && coordinate.getY() < grid.getHeight();
		return isOnWith && isOnHeigh;
	}
}
