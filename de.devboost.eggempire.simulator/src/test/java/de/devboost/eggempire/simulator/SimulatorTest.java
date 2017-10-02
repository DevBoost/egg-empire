package de.devboost.eggempire.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.devboost.eggempire.simulator.impl.AggressivePlayer;
import de.devboost.eggempire.simulator.impl.ChaoticPlayer;
import de.devboost.eggempire.simulator.impl.PessimisticPlayer;
import de.devboost.eggempire.simulator.impl.RiskyPlayer;
import de.devboost.eggempire.simulator.impl.Simulator;
import de.devboost.eggempire.simulator.impl.StatisticalPlayer;

@RunWith(Parameterized.class)
public class SimulatorTest {

	private static final int ITERATIONS = 20000;
	
	private static Map<String, List<Integer>> roundsPerPlayer = new LinkedHashMap<>();

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { new PessimisticPlayer() }, { new RiskyPlayer() },
				{ new ChaoticPlayer() }, { new StatisticalPlayer() }, { new AggressivePlayer() } });
	}

	private IPlayer player;

	public SimulatorTest(IPlayer player) {
		this.player = player;
	}

	@AfterClass
	public static void calculateWhoWins() {
		System.out.println("\nPlayer comparision: ");
		System.out.println("=================== ");
		Set<String> keySet = roundsPerPlayer.keySet();

		Map<String, Integer> gamesWon = new LinkedHashMap<>();
		for (String playerName : keySet) {
			gamesWon.put(playerName, 0);
		}

		for (int i = 0; i < ITERATIONS; i++) {
			int currentMin = Integer.MAX_VALUE;
			String bestPlayerInRound = "";
			for (String playerName : keySet) {
				Integer roundsInIteration = roundsPerPlayer.get(playerName).get(i);
				if (roundsInIteration < currentMin) {
					bestPlayerInRound = playerName;
					currentMin = roundsInIteration;
				}
			}
			Integer games = gamesWon.get(bestPlayerInRound);
			gamesWon.put(bestPlayerInRound, games + 1);
		}

		List<String> sortedPlayers = new ArrayList<>(keySet);
		Collections.sort(sortedPlayers);
		for (String playerName : sortedPlayers) {
			System.out.print(playerName + ",");
		}
		System.out.println();
		for (String playerName : sortedPlayers) {
			Integer games = gamesWon.get(playerName);
			System.out.print((1.0d * games / ITERATIONS * 100 + "\t").replace(".", ","));
		}
		System.out.println();
	}

	@Test
	public void testPlayer() {
		List<Integer> roundsNeeded = new ArrayList<>();
		for (int i = 0; i < ITERATIONS; i++) {
			ISimulator simulator = playWithPlayer(player);
			roundsNeeded.add(simulator.getRound());
		}

		double averageRounds = getAvg(roundsNeeded);
		int minRounds = getMin(roundsNeeded);
		int maxRounds = getMax(roundsNeeded);
		String playerName = player.getClass().getSimpleName();
		System.out.println(playerName + " rounds played AVG: " + averageRounds + "  | MIN: " + minRounds + " | MAX: "
				+ maxRounds + "");
		roundsPerPlayer.put(playerName, roundsNeeded);
	}

	private Simulator createSimulator() {
		double pricePerSurpriseEgg = 2;
		double pricePerExpectationEgg = pricePerSurpriseEgg * 2.5;
		double maxPurchasePerRound = 10;
		double probabilityForGoodSurpriseEgg = 0.6;
		int boardSize = 18;
		double featureInteractionDegree = 0.5;
		int maxRounds = 100;
		return new Simulator(pricePerSurpriseEgg, pricePerExpectationEgg, maxPurchasePerRound,
				probabilityForGoodSurpriseEgg, boardSize, featureInteractionDegree, maxRounds);
	}

	private double getAvg(List<Integer> roundsNeeded) {
		double sum = 0;
		double count = 0;
		for (Integer rounds : roundsNeeded) {
			sum += rounds;
			count++;
		}
		return sum / count;
	}

	private int getMin(List<Integer> roundsNeeded) {
		return Collections.min(roundsNeeded);
	}

	private int getMax(List<Integer> roundsNeeded) {
		return Collections.max(roundsNeeded);
	}

	private ISimulator playWithPlayer(IPlayer player) {
		ISimulator simulator = createSimulator();
		assertEquals("No round played yet", 0, simulator.getRound());

		player.play(simulator);
		assertTrue("No round played by player", simulator.getRound() > 0);
		assertTrue("Game not finished", simulator.isFinished());
		return simulator;
	}
}