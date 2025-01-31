package org.eqasim.core.simulation.modes.feeder_drt.router.access_egress_stop_search;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.eqasim.core.scenario.cutter.extent.ScenarioExtent;
import org.eqasim.core.scenario.cutter.extent.ShapeScenarioExtent;
import org.eqasim.core.simulation.modes.feeder_drt.config.AccessEgressStopSearchParams;
import org.eqasim.core.simulation.modes.feeder_drt.config.FeederDrtConfigGroup;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeModule;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.modal.ModalAnnotationCreator;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class AccessEgressStopSearchModule extends AbstractDvrpModeModule {

    private final AccessEgressStopSearchParams config;
    private final FeederDrtConfigGroup feederDrtConfigGroup;
    private final DrtConfigGroup coveredDrtConfigGroup;

    public AccessEgressStopSearchModule(AccessEgressStopSearchParams accessEgressStopSearchParams, FeederDrtConfigGroup feederDrtConfigGroup, DrtConfigGroup coveredDrtConfigGroup) {
        super(feederDrtConfigGroup.mode);
        this.config = accessEgressStopSearchParams;
        this.feederDrtConfigGroup = feederDrtConfigGroup;
        this.coveredDrtConfigGroup = coveredDrtConfigGroup;
    }

    @Override
    public void install() {
        bindModal(AccessEgressStopSearch.class).toProvider(getAccessEgressStopSearchProvider(config)).asEagerSingleton();
    }

    private AccessEgressStopSearch getAccessEgressStopSearch(AccessEgressStopSearchParams accessEgressStopSearchParams, Injector injector) {
        ModalAnnotationCreator<DvrpMode> modalAnnotationCreator = DvrpModes::mode;
        Provider<Network> networkProvider = injector.getProvider(modalAnnotationCreator.key(Network.class, feederDrtConfigGroup.accessEgressModeName));
        TransitSchedule transitSchedule = injector.getInstance(TransitSchedule.class);

        if(accessEgressStopSearchParams instanceof TransitStopByModeAccessEgressStopSearchParameterSet transitStopByModeAccessEgressStopSearchParameterSet) {
            ScenarioExtent serviceAreaExtent = null;
            if (coveredDrtConfigGroup.operationalScheme.equals(DrtConfigGroup.OperationalScheme.serviceAreaBased)) {
                URI extentPath;
                try {
                    extentPath = ConfigGroup.getInputFileURL(getConfig().getContext(), coveredDrtConfigGroup.drtServiceAreaShapeFile).toURI();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                try {
                    serviceAreaExtent = new ShapeScenarioExtent.Builder(new File(extentPath), Optional.empty(), Optional.empty()).build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new TransitStopByModeAccessEgressStopSearch(transitStopByModeAccessEgressStopSearchParameterSet, networkProvider.get(), transitSchedule, serviceAreaExtent);
        } else if(accessEgressStopSearchParams instanceof TransitStopByIdAccessEgressStopSearchParameterSet transitStopByIdAccessEgressStopSearchParameterSet) {
            return new TransitStopByIdAccessEgressStopSearch(transitStopByIdAccessEgressStopSearchParameterSet, transitSchedule, networkProvider.get());
        } else if(accessEgressStopSearchParams instanceof CompositeAccessEgressStopSearchParameterSet compositeAccessEgressStopSearchParameterSet) {
            List<AccessEgressStopSearch> delegates = compositeAccessEgressStopSearchParameterSet.getDelegateAccessEgressStopSearchParamSets().stream().map(params -> getAccessEgressStopSearch(params, injector)).toList();
            return new CompositeAccessEgressStopSearch(delegates, networkProvider.get());
        } else {
            throw new IllegalStateException(String.format("Unhandled subclass of AccessEgressStopSearchParams '%s'", accessEgressStopSearchParams.getClass().toString()));
        }
    }

    private Provider<AccessEgressStopSearch> getAccessEgressStopSearchProvider(AccessEgressStopSearchParams accessEgressStopSearchParams) {
        return new Provider<>() {

            @Inject
            private Injector injector;

            @Override
            public AccessEgressStopSearch get() {
                return getAccessEgressStopSearch(accessEgressStopSearchParams, injector);
            }
        };
    }
}
