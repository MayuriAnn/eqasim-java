package org.eqasim.examples.sao_paulo_drt;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



import org.eqasim.core.components.config.EqasimConfigGroup;
import org.eqasim.core.components.transit.EqasimTransitQSimModule;
import org.eqasim.core.simulation.EqasimConfigurator;
import org.eqasim.core.simulation.analysis.EqasimAnalysisModule;
import org.eqasim.core.simulation.mode_choice.AbstractEqasimExtension;
import org.eqasim.core.simulation.mode_choice.EqasimModeChoiceModule;
import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;
import org.eqasim.core.simulation.modes.drt.analysis.DrtAnalysisModule;
import org.eqasim.core.simulation.modes.drt.utils.AdaptConfigForDrt;
import org.eqasim.core.simulation.modes.feeder_drt.MultiModeFeederDrtModule;
import org.eqasim.core.simulation.modes.feeder_drt.analysis.run.RunFeederDrtPassengerAnalysis;
import org.eqasim.core.simulation.modes.feeder_drt.config.MultiModeFeederDrtConfigGroup;
import org.eqasim.core.simulation.modes.feeder_drt.mode_choice.FeederDrtModeAvailabilityWrapper;
import org.eqasim.core.simulation.modes.feeder_drt.utils.AdaptConfigForFeederDrt;
import org.eqasim.core.simulation.modes.transit_with_abstract_access.mode_choice.TransitWithAbstractAccessModeAvailabilityWrapper;
import org.eqasim.examples.sao_paulo_drt.mode_choice.SaoPauloDrtModeAvailability;
import org.eqasim.examples.sao_paulo_drt.rejections.RejectionConstraint;
import org.eqasim.examples.sao_paulo_drt.rejections.RejectionModule;
import org.eqasim.sao_paulo.SaoPauloConfigurator;
import org.eqasim.sao_paulo.mode_choice.SaoPauloModeChoiceModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.drt.analysis.zonal.DrtZonalSystemParams;
import org.matsim.contrib.drt.optimizer.insertion.DrtInsertionSearchParams;
import org.matsim.contrib.drt.optimizer.insertion.selective.SelectiveInsertionSearchParams;
import org.matsim.contrib.drt.optimizer.rebalancing.RebalancingParams;
import org.matsim.contrib.drt.optimizer.rebalancing.mincostflow.MinCostFlowRebalancingStrategyParams;
import org.matsim.contrib.drt.routing.DrtRoute;
import org.matsim.contrib.drt.routing.DrtRouteFactory;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.DrtConfigGroup.OperationalScheme;
import org.matsim.contrib.drt.run.DrtConfigs;
import org.matsim.contrib.drt.run.MultiModeDrtConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.dvrp.run.MultiModal;
import org.matsim.contribs.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import org.matsim.contribs.discrete_mode_choice.model.mode_availability.ModeAvailability;
import org.matsim.contribs.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import org.matsim.core.config.CommandLine;
import org.matsim.core.config.CommandLine.ConfigurationException;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ScoringConfigGroup.ModeParams;
import org.matsim.core.config.groups.ControllerConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.StarttimeInterpretation;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This is an example run script that runs the Sao Paulo  scenario with an
 * on-demand vehicle fleet using DRT.
 * 
 */
public class RunSaoPauloDrtSimulation {

	static public void main(String[] args) throws ConfigurationException, IOException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.allowOptions("use-rejection-constraint") //
				.allowPrefixes("mode-parameter", "cost-parameter") //
				.build();
		
		String originalConfig = "C:\\Users\\mayur\\OneDrive\\simulations\\eqasim-may\\examples\\src\\main\\resources\\sao_paulo\\sao_paulo_config.xml";
		String feederConfig = "C:\\Users\\mayur\\OneDrive\\simulations\\eqasim-may\\examples\\src\\main\\resources\\sao_paulo\\sao_paulo_feeder_config.xml";
		String drtVehiclesFile = "C:\\Users\\mayur\\OneDrive\\simulations\\eqasim-may\\examples\\src\\main\\resources\\sao_paulo\\drtfleetVehicles1000_car_r3300_6seats.xml";
		
		
		AdaptConfigForDrt.main(new String[]{
                "--input-config-path", originalConfig,
                "--output-config-path", feederConfig,
                "--mode-names", "drt",
                "--vehicles-paths", drtVehiclesFile
        });

		
        AdaptConfigForFeederDrt.main(new String[]{
                "--input-config-path", feederConfig,
                "--output-config-path", feederConfig,
                "--mode-names", "feeder",
                "--base-drt-modes", "drt",
                "--access-egress-transit-stop-modes", "rail|tram|subway"
        });
        
        
		
		URL configUrl = Resources.getResource("sao_paulo/sao_paulo_feeder_config.xml");
		//URL configUrl = Resources.getResource("sao_paulo/sao_paulo_config.xml");

		SaoPauloConfigurator configurator = new SaoPauloConfigurator();
		Config config = ConfigUtils.loadConfig(configUrl, configurator.getConfigGroups());

		config.controller().setLastIteration(3);
		
		@SuppressWarnings("unused")
		DvrpConfigGroup dvrpConfig = ConfigUtils.addOrGetModule( config, DvrpConfigGroup.class );
		
		@SuppressWarnings("unused")
		MultiModeFeederDrtConfigGroup feeder = ConfigUtils.addOrGetModule( config, MultiModeFeederDrtConfigGroup.class );
		/**
		{ // Configure DVRP
			DvrpConfigGroup dvrpConfig = new DvrpConfigGroup();
					
			Set<String> netModes = new HashSet<>();
			netModes.add("drt");
			
			dvrpConfig.networkModes = netModes;
			
			config.addModule(dvrpConfig);
		}
		*/
		
		MultiModeDrtConfigGroup multiModeDrtConfig = ConfigUtils.addOrGetModule(config, MultiModeDrtConfigGroup.class);
		
		//MultiModeDrtConfigGroup multiModeDrtConfig = new MultiModeDrtConfigGroup();
		
		
		/**
		{ // Configure DRT
			
			config.addModule(multiModeDrtConfig);

			DrtConfigGroup drtConfig = new DrtConfigGroup();
			drtConfig.mode = "drt";
			drtConfig.operationalScheme = OperationalScheme.door2door;
			drtConfig.stopDuration = 15.0;
			drtConfig.maxWaitTime = 600.0;
			drtConfig.maxTravelTimeAlpha = 1.5;
			drtConfig.maxTravelTimeBeta = 300.0;
			drtConfig.rejectRequestIfMaxWaitOrTravelTimeViolated = false;
			drtConfig.vehiclesFile =Resources.getResource("sao_paulo/drtfleetVehicles1000_car_r3300_3seats.xml").toString();
			drtConfig.numberOfThreads = 4;
			DrtInsertionSearchParams searchParams = new SelectiveInsertionSearchParams();
			drtConfig.addDrtInsertionSearchParams(searchParams);
			
			// Configura parâmetros de rebalanceamento
			RebalancingParams rebalancingParams = new RebalancingParams();
						
			// Adiciona a estratégia de rebalanceamento
			MinCostFlowRebalancingStrategyParams minCostFlowParams = new MinCostFlowRebalancingStrategyParams();
			
			minCostFlowParams.targetAlpha = 0.5;
			minCostFlowParams.targetBeta = 0.5;
		    rebalancingParams.addParameterSet(minCostFlowParams);			
			drtConfig.addParameterSet(rebalancingParams);
			
			DrtZonalSystemParams drtZonalSystemParams = new DrtZonalSystemParams();
			
			drtZonalSystemParams.zonesGeneration = DrtZonalSystemParams.ZoneGeneration.GridFromNetwork;
			drtZonalSystemParams.cellSize = 500.;
			drtZonalSystemParams.targetLinkSelection = DrtZonalSystemParams.TargetLinkSelection.mostCentral;
			drtConfig.addParameterSet(drtZonalSystemParams);
			
			multiModeDrtConfig.addParameterSet(drtConfig);
			
			DrtConfigs.adjustMultiModeDrtConfig(multiModeDrtConfig, config.scoring(), config.routing());

			// Additional requirements
			config.qsim().setStartTime(0.0);
			config.qsim().setSimStarttimeInterpretation(StarttimeInterpretation.onlyUseStarttime);
		}
		*/
		cmd.applyConfiguration(config);
		/**
		{ // Add the DRT mode to the choice model
			DiscreteModeChoiceConfigGroup dmcConfig = DiscreteModeChoiceConfigGroup.getOrCreate(config);

			// Add DRT to the available modes
			dmcConfig.setModeAvailability(SaoPauloDrtModeAvailability.NAME);

			// Add DRT to cached modes
			Set<String> cachedModes = new HashSet<>();
			cachedModes.addAll(dmcConfig.getCachedModes());
			cachedModes.add("drt");
			dmcConfig.setCachedModes(cachedModes);
			
			
			// Set up choice model
			EqasimConfigGroup eqasimConfig = EqasimConfigGroup.get(config);
			eqasimConfig.setCostModel("drt", "spdrt");
			eqasimConfig.setEstimator("drt", "spdrt");
			eqasimConfig.setEstimator("taxi", "spTaxiEstimator");

			// Add rejection constraint
			if (cmd.getOption("use-rejection-constraint").map(Boolean::parseBoolean).orElse(false)) {
				Set<String> tripConstraints = new HashSet<>(dmcConfig.getTripConstraints());
				tripConstraints.add(RejectionConstraint.NAME);
				dmcConfig.setTripConstraints(tripConstraints);
			}

			// Set analysis interval
			eqasimConfig.setAnalysisInterval(2);
		}

		{ // Set up some defaults for MATSim scoring
			ModeParams modeParams = new ModeParams("drt");
			modeParams.setMarginalUtilityOfTraveling(-0.8520000000000001);
			config.scoring().addModeParams(modeParams);
		}
		*/
		Scenario scenario = ScenarioUtils.createScenario(config);
		configurator.configureScenario(scenario);

		{ // Add DRT route factory
			scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DrtRoute.class,
					new DrtRouteFactory());
		}

		ScenarioUtils.loadScenario(scenario);

		Controler controller = new Controler(scenario);
		configurator.configureController(controller);
		controller.addOverridingModule(new EqasimAnalysisModule());
		controller.addOverridingModule(new EqasimModeChoiceModule());
		controller.addOverridingModule(new SaoPauloModeChoiceModule(cmd));
		
		{ // Configure controller for DRT
			controller.configureQSimComponents(components -> {
				DvrpQSimComponents.activateAllModes(multiModeDrtConfig).configure(components);

				// Need to re-do this as now it is combined with DRT
				EqasimTransitQSimModule.configure(components, config);
			});
		}

		{ // Add overrides for SP + DRT
			controller.addOverridingModule(new SaoPauloDrtModule(cmd));
			controller.addOverridingModule(new RejectionModule(Arrays.asList("drt")));
			controller.addOverridingModule(new DrtAnalysisModule());
			//controller.addOverridingModule(new MultiModeFeederDrtModule());
			controller.addOverridingModule(new AbstractEqasimExtension() {
	            @Override
	            protected void installEqasimExtension() {
	                bind(ModeParameters.class);
	                bindModeAvailability("DefaultModeAvailability").toProvider(new Provider<>() {
	                    @Inject
	                    private Config config;

	                    @Override
	                    public ModeAvailability get() {
	                        FeederDrtModeAvailabilityWrapper feederDrtModeAvailabilityWrapper = new FeederDrtModeAvailabilityWrapper(config, new SaoPauloDrtModeAvailability());
	                        return new TransitWithAbstractAccessModeAvailabilityWrapper(config, feederDrtModeAvailabilityWrapper);
	                    }
	                }).asEagerSingleton();
	            }
	        });
		}
		//ConfigUtils.writeConfig(config, feederConfig);
		
		controller.run();
		
		RunFeederDrtPassengerAnalysis.main(new String[]{
                "--config-path", feederConfig,
                "--events-path", "simulation_output/output_events.xml.gz",
                "--network-path", "simulation_output/output_network.xml.gz",
                "--output-path", "simulation_output/eqasim_feeder_drt_trips_standalone.csv"
        });
				
	}
	
	
	
}
