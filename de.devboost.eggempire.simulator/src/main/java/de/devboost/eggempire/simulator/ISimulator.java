package de.devboost.eggempire.simulator;

import java.util.Set;

import de.devboost.eggempire.simulator.impl.SimulationState;

public interface ISimulator {

	Set<IEgg> buy(int surpriseEggs, int expectationEggs) throws IllegalArgumentException;
	
	void putOnBoard(Set<IEgg> eggs);
	
	Set<IEgg> getBoard();
	
	int getRound();

	double getPricePerSurpriseEgg();

	double getPricePerExpectationEgg();

	double getMaxPurchasePerRound();

	int getBoardSize();

	SimulationState getSimulationState();
}
