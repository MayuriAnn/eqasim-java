package org.eqasim.examples.sao_paulo_drt.mode_choice.cost;

import java.util.List;

import org.eqasim.core.simulation.mode_choice.cost.AbstractCostModel;
import org.eqasim.examples.sao_paulo_drt.mode_choice.parameters.SaoPauloDrtCostParameters;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class DrtCostModel extends AbstractCostModel {
	private final SaoPauloDrtCostParameters parameters;

	public DrtCostModel(SaoPauloDrtCostParameters parameters) {
		super("drt");
		this.parameters = parameters;
	}

	@Override
	public double calculateCost_MU(Person person, DiscreteModeChoiceTrip trip, List<? extends PlanElement> elements) {
		double tripDistance_km = getInVehicleDistance_km(elements);
		return parameters.drtCost_BRL_km * tripDistance_km;
	}
}
