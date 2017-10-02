package de.devboost.eggempire.simulator.impl;

import de.devboost.eggempire.simulator.IEgg;

public class SurpriseEgg implements IEgg {

	private final boolean ok;

	public SurpriseEgg(boolean ok) {
		this.ok = ok;
	}

	@Override
	public boolean isOk() {
		return ok;
	}

	@Override
	public boolean isSurprise() {
		return true;
	}
}
