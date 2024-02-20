package org.eqasim.examples.sao_paulo_drt;

import java.io.File;
import java.util.Map;

import org.eqasim.core.analysis.PersonAnalysisFilter;
import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.ParameterDefinition;
import org.eqasim.core.simulation.mode_choice.cost.CostModel;
import org.eqasim.core.simulation.modes.drt.mode_choice.predictors.DefaultDrtPredictor;
import org.eqasim.core.simulation.modes.drt.mode_choice.predictors.DrtPredictor;
import org.eqasim.core.simulation.modes.drt.mode_choice.utilities.estimators.DrtUtilityEstimator;
import org.eqasim.examples.sao_paulo_drt.mode_choice.SaoPauloDrtModeAvailability;
import org.eqasim.examples.sao_paulo_drt.mode_choice.cost.DrtCostModel;
import org.eqasim.examples.sao_paulo_drt.mode_choice.parameters.SaoPauloDrtCostParameters;
import org.eqasim.sao_paulo.mode_choice.parameters.SaoPauloCostParameters;
import org.matsim.core.config.CommandLine;


import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class SaoPauloDrtModule extends AbstractEqasimExtension {
	private final CommandLine commandLine;

	public SaoPauloDrtModule(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	protected void installEqasimExtension() {
		// Configure mode availability
		bindModeAvailability(SaoPauloDrtModeAvailability.NAME).to(SaoPauloDrtModeAvailability.class);

		// Configure choice alternative for DRT
		bindUtilityEstimator("drt").to(DrtUtilityEstimator.class);
		bindCostModel("drt").to(DrtCostModel.class);
		bind(DrtPredictor.class).to(DefaultDrtPredictor.class);

		// Define filter for trip analysis
		bind(PersonAnalysisFilter.class).to(DrtPersonAnalysisFilter.class);

		// Override parameter bindings
		bind(SaoPauloCostParameters.class).to(SaoPauloDrtCostParameters.class);
	}

	@Provides
	@Singleton
	public DrtCostModel provideDrtCostModel(SaoPauloDrtCostParameters parameters) {
		return new DrtCostModel(parameters);
	}

	@Provides
	@Singleton
	public SaoPauloDrtCostParameters provideCostParameters(EqasimConfigGroup config) {
		SaoPauloDrtCostParameters parameters = SaoPauloDrtCostParameters.buildDefault();

		if (config.getCostParametersPath() != null) {
			ParameterDefinition.applyFile(new File(config.getCostParametersPath()), parameters);
		}

		ParameterDefinition.applyCommandLine("cost-parameter", commandLine, parameters);
		return parameters;
	}

	@Provides
	@Named("drt")
	public CostModel provideCarCostModel(Map<String, Provider<CostModel>> factory, EqasimConfigGroup config) {
		return getCostModel(factory, config, "drt");
	}

}
