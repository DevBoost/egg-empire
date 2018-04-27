package de.devboost.eggempire.simulator.impl;

public class SimulationState {

	private final boolean boardWasFilled;
	private final boolean maxRoundsReached;
	private final boolean outOfBudget;
	private final int rounds;

	public SimulationState(boolean boardWasFilled, boolean maxRoundsReached, boolean outOfBudget, int rounds) {
		this.boardWasFilled = boardWasFilled;
		this.maxRoundsReached = maxRoundsReached;
		this.outOfBudget = outOfBudget;
		this.rounds = rounds;
	}

	public boolean isBoardWasFilled() {
		return boardWasFilled;
	}

	public boolean isMaxRoundsReached() {
		return maxRoundsReached;
	}

	public boolean isOutOfBudget() {
		return outOfBudget;
	}

	public boolean isFinished() {
		return boardWasFilled || maxRoundsReached || outOfBudget;
	}

	public int getRounds() {
		return rounds;
	}

}
