package com.topdesk.cases.toprob.yoursolution;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import com.topdesk.cases.toprob.Coordinate;
import com.topdesk.cases.toprob.Grid;
import com.topdesk.cases.toprob.Instruction;
import com.topdesk.cases.toprob.Solution;

/**
 * @author bozbalint
 * 
 *         This is my solution. Walk from Room to the Kitchen than walk back.
 *         Close to the optimal route. I do not implement an alternative way
 *         back.
 *
 */
public class YourSolution implements Solution {

	// the pause in the kitchen
	private static final int SEC_TO_WAIT_IN_THE_KITCHEN = 5;

	// the possible movements from a cell
	private final List<Instruction> neighbours = Arrays.asList(Instruction.NORTH, Instruction.SOUTH, Instruction.EAST,
			Instruction.WEST);

	@Override
	public List<Instruction> solve(Grid grid, int startTime) {

		if (startTime < 0) {
			throw new IllegalArgumentException("Start time is negative:" + startTime);
		}

		if (grid == null) {
			throw new NullPointerException("Grid is not set. It is NULL");
		}

		int timer = startTime + 1;

		// provides weights on each cell of the grid
		WeightedGrid weightedGrid = new WeightedGrid(grid);

		// check if the puzzle solvable.
		if (weightedGrid.getWeight(grid.getRoom()) > grid.getHeight() * grid.getWidth())
			return null;

		// collection of the steps from the Room to the Kitchen
		List<Instruction> theInstructionToTheKitchen = new LinkedList<>();

		// Rob steps in the Room, the starting location
		Coordinate stepFrom = grid.getRoom();
		Coordinate stepTo;

		// walk from the room to the kitchen
		while (!stepFrom.equals(grid.getKitchen())) {
			stepTo = weightedGrid.getMinWeighNeighbourCoordinate(stepFrom);

			while (stepTo.equals(grid.getBug(timer))) {
				// meet a bug
				timer++;
				theInstructionToTheKitchen.add(Instruction.PAUSE);
			}

			final Coordinate stepFromFinal = stepFrom;
			final Coordinate stepToFinal = stepTo;
			// reverse engineering the Instruction from the from-to step
			Optional<Instruction> theInstruction = neighbours.stream()
					.filter(p -> p.execute(stepFromFinal).equals(stepToFinal)).findFirst();

			if (theInstruction.isPresent()) {
				timer++;
				theInstructionToTheKitchen.add(theInstruction.get());
			} else {
				throw new IllegalStateException("No instruction to the neighbour cell:" + stepFrom + " -> " + stepTo);
			}

			// step to the new cell
			stepFrom = stepTo;
		}

		// stay in the kitchen
		List<Instruction> theInstructionInTheKitchen = new LinkedList<>();
		for (int i = 0; i < SEC_TO_WAIT_IN_THE_KITCHEN; i++) {
			timer++;
			theInstructionInTheKitchen.add(Instruction.PAUSE);
		}

		// let's go back to the room, rob is lazy not going to find new way back to the
		// room.
		List<Instruction> theInstructionToTheRoom = new LinkedList<>();

		// Rob is in the Kitchen
		Coordinate backStepFrom = grid.getKitchen();

		// if was a way to the Kitchen than must be a way to back
		ListIterator<Instruction> iterator = theInstructionToTheKitchen.listIterator(theInstructionToTheKitchen.size());
		while (iterator.hasPrevious()) {
			Instruction instruction = iterator.previous();
			Instruction stepToDirection;
			// ignore the pause, bug not necessary be on the same cell
			if (instruction.equals(Instruction.PAUSE)) {
				continue;
			} else {
				switch (instruction) {
				case NORTH:
					stepToDirection = Instruction.SOUTH;
					break;
				case SOUTH:
					stepToDirection = Instruction.NORTH;
					break;
				case EAST:
					stepToDirection = Instruction.WEST;
					break;
				case WEST:
					stepToDirection = Instruction.EAST;
					break;
				default:
					System.out.println("Unknown state: " + instruction);
					throw new IllegalStateException("Unknown state: " + instruction);
				}

				// check the step
				Coordinate backStepTo = stepToDirection.execute(backStepFrom);
				while (backStepTo.equals(grid.getBug(timer))) {
					// bug time
					timer++;
					theInstructionToTheRoom.add(Instruction.PAUSE);
				}

				// make the step
				timer++;
				theInstructionToTheRoom.add(stepToDirection);
				backStepFrom = backStepTo;
			}
		}

		// to the kitchen + in the kitchen + to the room
		List<Instruction> retValue = theInstructionToTheKitchen;
		retValue.addAll(theInstructionInTheKitchen);
		retValue.addAll(theInstructionToTheRoom);

		return retValue;
	}
}
