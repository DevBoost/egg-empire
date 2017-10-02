package de.devboost.eggempire.simulator;

import java.util.Set;

public interface ISimulator {

	Set<IEgg> buy(int surpriseEggs, int expectationEggs) throws IllegalArgumentException;
	
	void putOnBoard(Set<IEgg> eggs);
	
	Set<IEgg> getBoard();
	
	boolean isFinished();
	
	int getRound();

	double getPricePerSurpriseEgg();

	double getPricePerExpectationEgg();

	double getMaxPurchasePerRound();

	int getBoardSize();
}
