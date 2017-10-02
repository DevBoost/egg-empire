package de.devboost.eggempire.simulator.impl;

import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.IPlayer;
import de.devboost.eggempire.simulator.ISimulator;

/**
 * The {@link PessimisticPlayer} is a player who purchases as many expectation eggs as possible in each round.
 */
public class PessimisticPlayer implements IPlayer {

	@Override
	public void play(ISimulator simulator) {
		double maxPurchasePerRound = simulator.getMaxPurchasePerRound();
		double pricePerExpectationEgg = simulator.getPricePerExpectationEgg();
		int eggsToPurchase = (int) (maxPurchasePerRound / pricePerExpectationEgg);
		while (!simulator.isFinished()) {
			Set<IEgg> eggs = simulator.buy(0, eggsToPurchase);
			simulator.putOnBoard(eggs);
		}
	}
}
