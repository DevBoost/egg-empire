package de.devboost.eggempire.simulator.players;

import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.IPlayer;
import de.devboost.eggempire.simulator.ISimulator;

/**
 * The {@link RiskyPlayer} is a player who purchases as many surprise eggs as possible in each round.
 */
public class RiskyPlayer implements IPlayer {

	@Override
	public void play(ISimulator simulator) {
		double maxPurchasePerRound = simulator.getMaxPurchasePerRound();
		double pricePerSurpriseEgg = simulator.getPricePerSurpriseEgg();
		int surpriseEggsToPurchase = (int) (maxPurchasePerRound / pricePerSurpriseEgg);
		
		while (!simulator.getSimulationState().isFinished()) {
			Set<IEgg> eggs = simulator.buy(surpriseEggsToPurchase, 0);
			simulator.putOnBoard(eggs);
		}
	}
}
