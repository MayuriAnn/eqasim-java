package org.eqasim.examples.sao_paulo_drt.mode_choice.parameters;

import org.eqasim.sao_paulo.mode_choice.parameters.SaoPauloCostParameters;

public class SaoPauloDrtCostParameters extends SaoPauloCostParameters {
	public double drtCost_BRL_km;
	public double drtCost_BRL;

	public static SaoPauloDrtCostParameters buildDefault() {
		// Copy & paste

		SaoPauloDrtCostParameters parameters = new SaoPauloDrtCostParameters();

		parameters.drtCost_BRL_km = 0.3;
		parameters.drtCost_BRL = 5;

		return parameters;
	}
}
