package de.devboost.eggempire.simulator.impl;

import de.devboost.eggempire.simulator.IEgg;

public class ExpectactionEgg implements IEgg {

	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public boolean isSurprise() {
		return false;
	}
}
