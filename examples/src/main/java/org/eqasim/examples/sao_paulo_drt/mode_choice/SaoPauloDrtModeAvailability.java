package org.eqasim.examples.sao_paulo_drt.mode_choice;

import java.util.Collection;
import java.util.List;

import org.eqasim.sao_paulo.mode_choice.SaoPauloModeAvailability;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.contribs.discrete_mode_choice.model.mode_availability.ModeAvailability;

public class SaoPauloDrtModeAvailability implements ModeAvailability {
	static public final String NAME = "SaoPauloDrtModeAvailability";

	private final ModeAvailability delegate = new SaoPauloModeAvailability();

	@Override
	public Collection<String> getAvailableModes(Person person, List<DiscreteModeChoiceTrip> trips) {
		Collection<String> modes = delegate.getAvailableModes(person, trips);

		if (modes.contains(TransportMode.walk)) {
			modes.add("feeder");
			//modes.add("drt_for_feeder_a");
		}

		return modes;
	}
}
