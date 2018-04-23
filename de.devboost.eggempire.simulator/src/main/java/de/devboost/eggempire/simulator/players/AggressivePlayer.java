package de.devboost.eggempire.simulator.players;

import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.IPlayer;
import de.devboost.eggempire.simulator.ISimulator;

/**
 * Player that plays pessimistic until the number of remaining rounds gets below a given threshold.
 *
 */
public class AggressivePlayer implements IPlayer {

	@Override
	public void play(ISimulator simulator) {

		double maxPurchasePerRound = simulator.getMaxPurchasePerRound();
		
		double pricePerExpectationEgg = simulator.getPricePerExpectationEgg();
		double pricePerSurpriseEgg = simulator.getPricePerSurpriseEgg();

		int maxExpectationEggs = (int) (maxPurchasePerRound / pricePerExpectationEgg);
		int maxSupriseEggs = (int) (maxPurchasePerRound / pricePerSurpriseEgg);

		while (!simulator.isFinished()) {
			if (simulator.getRound() < 6) {
				Set<IEgg> eggs = simulator.buy(0, maxExpectationEggs);
				simulator.putOnBoard(eggs);
			} else {
				Set<IEgg> eggs = simulator.buy(maxSupriseEggs, 0);
				simulator.putOnBoard(eggs);
			}
		}
	}
}
