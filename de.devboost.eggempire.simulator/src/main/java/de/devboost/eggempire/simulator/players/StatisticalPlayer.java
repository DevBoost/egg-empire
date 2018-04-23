package de.devboost.eggempire.simulator.players;

import java.util.Iterator;
import java.util.Set;

import de.devboost.eggempire.simulator.IEgg;
import de.devboost.eggempire.simulator.IPlayer;
import de.devboost.eggempire.simulator.ISimulator;

public class StatisticalPlayer implements IPlayer {

	@Override
	public void play(ISimulator simulator) {

		int maxExpectationEggs = (int) (simulator.getMaxPurchasePerRound() / simulator.getPricePerExpectationEgg());
		int maxSupriseEggs = (int) (simulator.getMaxPurchasePerRound() / simulator.getPricePerSurpriseEgg());

		int maxRoundsWithPessimistic = (int) (simulator.getBoardSize() / maxExpectationEggs);
		int goodSupriseEggs = 0;
		int badSupriseEggs = 0;

		while (!simulator.isFinished()) {

			// we determine the ratio of good surprise eggs by looking into the game history
			// IMPROVEMENT: (if we would play against
			// opponents, we could do this with their eggs and play pessimistic from the very beginning)
			int totalSupriseEggs = goodSupriseEggs + badSupriseEggs;
			double supriseEggQuality = 0;
			if (totalSupriseEggs > 0) {
				supriseEggQuality = 1.0d * goodSupriseEggs / totalSupriseEggs;
			}

			int expectationEggsToBuy = 0;
			int supriseEggsToBuy = 0;
			// we spent some rounds in the beginning to determine the egg quality
			if (simulator.getRound() < maxRoundsWithPessimistic * 0.1) {
				expectationEggsToBuy = maxExpectationEggs - 1;
				supriseEggsToBuy = (int) ((simulator.getMaxPurchasePerRound()
						- expectationEggsToBuy * simulator.getPricePerExpectationEgg())
						/ simulator.getPricePerSurpriseEgg());
			} else {
				double expectedGoodEggs = supriseEggQuality * maxSupriseEggs;
				int numberOfGoodEggs = getNumberOfGoodEggs(simulator.getBoard());
				// as soon as there is some good probability that we can finish the round given the experienced egg
				// quality and the number of missing eggs on our board, we play aggressively
				if (numberOfGoodEggs + expectedGoodEggs > simulator.getBoardSize()) {
					expectationEggsToBuy = 0;
					supriseEggsToBuy = maxSupriseEggs;
					// as long as we miss more eggs than we can expect to get from good surprise eggs, we play
					// pessimistic
					// to fill the board
				} else {
					expectationEggsToBuy = maxExpectationEggs;
					supriseEggsToBuy = (int) ((simulator.getMaxPurchasePerRound()
							- expectationEggsToBuy * simulator.getPricePerExpectationEgg())
							/ simulator.getPricePerSurpriseEgg());
				}
			}
			
			Set<IEgg> eggs = simulator.buy(supriseEggsToBuy, expectationEggsToBuy);
			
			// we take some statistics here
			int goodSupriseEggsInRound = getGoodSupriseEggs(eggs);
			int badSupriseEggsInRound = supriseEggsToBuy - goodSupriseEggsInRound;
			goodSupriseEggs += goodSupriseEggsInRound;
			badSupriseEggs += badSupriseEggsInRound;
			
			simulator.putOnBoard(eggs);
		}
	}

	private int getNumberOfGoodEggs(Set<IEgg> board) {
		int goodEggs = 0;
		Iterator<IEgg> iterator = board.iterator();
		while (iterator.hasNext()) {
			IEgg iEgg = (IEgg) iterator.next();
			if (iEgg.isSurprise()) {
				continue;
			}
			goodEggs++;
		}
		return goodEggs;
	}

	private int getGoodSupriseEggs(Set<IEgg> eggs) {
		int goodSupriseEggs = 0;
		Iterator<IEgg> iterator = eggs.iterator();
		while (iterator.hasNext()) {
			IEgg iEgg = (IEgg) iterator.next();
			if (!iEgg.isSurprise()) {
				continue;
			}
			if (!iEgg.isOk()) {
				continue;
			}
			goodSupriseEggs++;
		}
		return goodSupriseEggs;
	}
}
