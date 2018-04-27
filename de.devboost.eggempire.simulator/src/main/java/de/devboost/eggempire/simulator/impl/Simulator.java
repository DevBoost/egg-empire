package de.devboost.eggempire.simulator.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.ISimulator;

public class Simulator implements ISimulator {

	private final double pricePerSurpriseEgg;
	private final double pricePerExpectationEgg;
	private final double maxPurchasePerRound;
	private final double probabilityForGoodSurpriseEgg;
	private final int boardSize;
	private final double featureInteractionDegree;
	private final int maxRounds;

	private Set<IEgg> board = new LinkedHashSet<>();
	private double budget;
	private int round = 0;

	public Simulator(double pricePerSurpriseEgg, double pricePerExpectationEgg, double maxPurchasePerRound,
			double probabilityForGoodSurpriseEgg, int boardSize, double featureInteractionDegree, int maxRounds,
			double maxBudgetPerPlayer) {
		this.pricePerSurpriseEgg = pricePerSurpriseEgg;
		this.pricePerExpectationEgg = pricePerExpectationEgg;
		this.maxPurchasePerRound = maxPurchasePerRound;
		this.probabilityForGoodSurpriseEgg = probabilityForGoodSurpriseEgg;
		this.boardSize = boardSize;
		this.featureInteractionDegree = featureInteractionDegree;
		this.maxRounds = maxRounds;
		this.budget = maxBudgetPerPlayer;
	}

	@Override
	public Set<IEgg> buy(int surpriseEggs, int expectationEggs) throws IllegalArgumentException {
		double price = surpriseEggs * pricePerSurpriseEgg + expectationEggs * pricePerExpectationEgg;
		if (price > maxPurchasePerRound) {
			throw new IllegalArgumentException("Too many eggs purchased in round");
		}

		Set<IEgg> eggs = new LinkedHashSet<>();

		for (int i = 0; i < surpriseEggs; i++) {
			eggs.add(createSurpriseEgg());
		}
		for (int i = 0; i < expectationEggs; i++) {
			eggs.add(new ExpectactionEgg());
		}

		this.budget = budget - price;
		endOfRound();
		return eggs;
	}

	private SurpriseEgg createSurpriseEgg() {
		boolean isOk = Math.random() < probabilityForGoodSurpriseEgg;
		SurpriseEgg surpriseEgg = new SurpriseEgg(isOk);
		return surpriseEgg;
	}

	private void endOfRound() {
		round++;
		List<IEgg> surpriseEggsOnBoard = new ArrayList<>();
		Iterator<IEgg> iterator = board.iterator();
		while (iterator.hasNext()) {
			IEgg next = iterator.next();
			if (next.isSurprise()) {
				surpriseEggsOnBoard.add(next);
			}
		}

		int eggsToReplace = (int) (surpriseEggsOnBoard.size() * featureInteractionDegree);
		for (int i = 0; i < eggsToReplace && i < surpriseEggsOnBoard.size(); i++) {
			board.remove(surpriseEggsOnBoard.get(i));
			board.add(createSurpriseEgg());
		}

		removeBrokenEggsFromBoard();
	}

	private void removeBrokenEggsFromBoard() {
		Iterator<IEgg> iterator = board.iterator();
		while (iterator.hasNext()) {
			IEgg next = iterator.next();
			if (!next.isOk()) {
				iterator.remove();
			}
		}
	}

	@Override
	public void putOnBoard(Set<IEgg> eggs) {
		for (IEgg egg : eggs) {
			if (egg.isOk()) {
				board.add(egg);
			}
		}
	}

	@Override
	public Set<IEgg> getBoard() {
		return Collections.unmodifiableSet(board);
	}

	@Override
	public SimulationState getSimulationState() {
		return new SimulationState(boardWasFilled(), maxRoundsReached(), outOfBudget(), getRound());
	}

	private boolean outOfBudget() {
		return budget < 0;
	}

	private boolean maxRoundsReached() {
		return getRound() >= maxRounds;
	}

	private boolean boardWasFilled() {
		return board.size() >= boardSize;
	}

	@Override
	public int getRound() {
		return round;
	}

	@Override
	public double getPricePerSurpriseEgg() {
		return pricePerSurpriseEgg;
	}

	@Override
	public double getPricePerExpectationEgg() {
		return pricePerExpectationEgg;
	}

	@Override
	public double getMaxPurchasePerRound() {
		return maxPurchasePerRound;
	}

	@Override
	public int getBoardSize() {
		return boardSize;
	}
}
