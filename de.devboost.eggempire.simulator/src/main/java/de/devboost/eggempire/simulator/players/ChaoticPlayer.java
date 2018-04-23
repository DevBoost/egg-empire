package de.devboost.eggempire.simulator.players;

import java.util.Random;
import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.IPlayer;
import de.devboost.eggempire.simulator.ISimulator;

/**
 * The {@link ChaoticPlayer} is a player who purchases as many eggs as possible in each round randomly choosing
 * expectation and surprise eggs.
 */
public class ChaoticPlayer implements IPlayer {

	private final static Random RANDOM = new Random(System.currentTimeMillis());

	@Override
	public void play(ISimulator simulator) {

		while (!simulator.isFinished()) {

			double moneyLeft = simulator.getMaxPurchasePerRound();
			double pricePerExpectationEgg = simulator.getPricePerExpectationEgg();
			double pricePerSupriseEgg = simulator.getPricePerSurpriseEgg();

			int expectationEggsToBuy = 0;
			int supriseEggsToBuy = 0;
			while (moneyLeft >= pricePerExpectationEgg) {
				boolean buyExpectationEgg = RANDOM.nextBoolean();
				if (buyExpectationEgg) {
					moneyLeft = moneyLeft - pricePerExpectationEgg;
					expectationEggsToBuy++;
				} else {
					moneyLeft = moneyLeft - pricePerSupriseEgg;
					supriseEggsToBuy++;
				}
			}
			while (moneyLeft >= pricePerSupriseEgg) {
				moneyLeft = moneyLeft - pricePerSupriseEgg;
				supriseEggsToBuy++;
			}
			Set<IEgg> eggs = simulator.buy(supriseEggsToBuy, expectationEggsToBuy);
			simulator.putOnBoard(eggs);
		}
	}
}
