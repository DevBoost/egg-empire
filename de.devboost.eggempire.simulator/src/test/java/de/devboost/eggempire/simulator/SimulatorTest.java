package de.devboost.eggempire.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.devboost.eggempire.simulator.impl.SimulationState;
import de.devboost.eggempire.simulator.impl.Simulator;
import de.devboost.eggempire.simulator.players.AggressivePlayer;
import de.devboost.eggempire.simulator.players.ChaoticPlayer;
import de.devboost.eggempire.simulator.players.PessimisticPlayer;
import de.devboost.eggempire.simulator.players.RiskyPlayer;
import de.devboost.eggempire.simulator.players.StatisticalPlayer;

@RunWith(Parameterized.class)
public class SimulatorTest {

	private static final int ITERATIONS = 20000;

	private static Map<String, List<SimulationState>> resultsPerPlayer = new LinkedHashMap<>();

	private IPlayer player;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { new PessimisticPlayer() }, { new RiskyPlayer() },
				{ new ChaoticPlayer() }, { new StatisticalPlayer() }, { new AggressivePlayer() } });
	}

	public SimulatorTest(IPlayer player) {
		this.player = player;
	}

	@Test
	public void testPlayer() {
		List<SimulationState> simulationResults = new ArrayList<>();
		for (int i = 0; i < ITERATIONS; i++) {
			ISimulator simulator = playWithPlayer(player);
			simulationResults.add(simulator.getSimulationState());
		}

		double averageRounds = getAvg(simulationResults);
		int minRounds = getMin(simulationResults);
		int maxRounds = getMax(simulationResults);
		long outOfBudgetRounds = getRoundsOutOfBudget(simulationResults);
		double outOfBudgetPercentage = 100d * outOfBudgetRounds / simulationResults.size();
		String playerName = player.getClass().getSimpleName();
		System.out.println(playerName + " played round in AVG: " + averageRounds + "  | at MIN: " + minRounds
				+ " | at MAX: " + maxRounds + ". He ran out of budget in " + outOfBudgetPercentage + "% games.");
		resultsPerPlayer.put(playerName, simulationResults);
	}

	private long getRoundsOutOfBudget(List<SimulationState> simulationResults) {
		long countOutOfBudget = simulationResults.stream().filter(result -> result.isOutOfBudget()).count();
		return countOutOfBudget;
	}

	@AfterClass
	public static void calculateStatistics() {
		System.out.println("\nPlayer comparision: ");
		System.out.println("=================== ");
		Set<String> keySet = resultsPerPlayer.keySet();

		Map<String, Integer> gamesWon = new LinkedHashMap<>();
		for (String playerName : keySet) {
			gamesWon.put(playerName, 0);
		}

		for (int i = 0; i < ITERATIONS; i++) {
			int currentMin = Integer.MAX_VALUE;
			String bestPlayerInRound = "";
			for (String playerName : keySet) {
				SimulationState stateInIteration = resultsPerPlayer.get(playerName).get(i);
				int roundsInIteration = stateInIteration.getRounds();
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
			Integer games = gamesWon.get(playerName);
			double winProbability = 1.0d * games / ITERATIONS * 100;
			double roundedProbability = Math.round(winProbability * 100.0) / 100.0;
			String percentageWins = (roundedProbability + " %").replace(".", ",");
			System.out.println(playerName + " won " + percentageWins + " of games.");
		}
	}

	private Simulator createSimulator() {
		double pricePerSurpriseEgg = 0.2;
		double pricePerExpectationEgg = 0.5;
		double maxPurchasePerRound = 1;
		double probabilityForGoodSurpriseEgg = 0.6;
		int boardSize = 18;
		double featureInteractionDegree = 0.5;
		int maxRounds = 1000;
		double maxBudgetPerPlayer = 10;
		return new Simulator(pricePerSurpriseEgg, pricePerExpectationEgg, maxPurchasePerRound,
				probabilityForGoodSurpriseEgg, boardSize, featureInteractionDegree, maxRounds, maxBudgetPerPlayer);
	}

	private double getAvg(List<SimulationState> simulationResults) {
		double sum = 0;
		double count = 0;
		for (SimulationState result : simulationResults) {
			if (!result.isBoardWasFilled()) {
				continue;
			}
			sum += result.getRounds();
			count++;
		}
		return sum / count;
	}

	private int getMin(List<SimulationState> simulationResults) {
		List<Integer> roundsWithBoardFilled = getRoundsWithBoardFilled(simulationResults);
		return Collections.min(roundsWithBoardFilled);
	}

	private List<Integer> getRoundsWithBoardFilled(List<SimulationState> simulationResults) {
		List<Integer> roundsOfSuccessfulPlays = simulationResults.stream().filter((result) -> result.isBoardWasFilled())
				.map((result) -> result.getRounds()).collect(Collectors.toList());
		return roundsOfSuccessfulPlays;
	}

	private int getMax(List<SimulationState> simulationResults) {
		List<Integer> roundsWithBoardFilled = getRoundsWithBoardFilled(simulationResults);
		return Collections.max(roundsWithBoardFilled);
	}

	private ISimulator playWithPlayer(IPlayer player) {
		ISimulator simulator = createSimulator();
		assertEquals("No round played yet", 0, simulator.getRound());

		player.play(simulator);
		assertTrue("No round played by player", simulator.getRound() > 0);
		assertTrue("Game not finished", simulator.getSimulationState().isFinished());
		return simulator;
	}
}
