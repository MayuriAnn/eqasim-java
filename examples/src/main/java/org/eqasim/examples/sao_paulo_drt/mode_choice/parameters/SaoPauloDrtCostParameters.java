package org.eqasim.examples.sao_paulo_drt.mode_choice.parameters;

import org.eqasim.sao_paulo.mode_choice.parameters.SaoPauloCostParameters;

public class SaoPauloDrtCostParameters extends SaoPauloCostParameters {
	public double drtCost_BRL_km;

	public static SaoPauloDrtCostParameters buildDefault() {
		// Copy & paste

		SaoPauloDrtCostParameters parameters = new SaoPauloDrtCostParameters();

		parameters.carCost_BRL_km = 0.15;
		parameters.drtCost_BRL_km = 0.3;

		return parameters;
	}
}
