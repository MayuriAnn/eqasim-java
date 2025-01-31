package org.eqasim.core.components.transit.events;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.HasPersonId;
import org.matsim.api.core.v01.population.Person;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

public class PublicTransitEvent extends GenericEvent implements HasPersonId {
	final public static String TYPE = "pt_transit";

	final private Id<Person> personId;
	final private Id<TransitLine> transitLineId;
	final private Id<TransitRoute> transitRouteId;
	final private Id<TransitStopFacility> accessStopId;
	final private Id<TransitStopFacility> egressStopId;
	final private Id<Departure> departureId;
	final private double vehicleDepartureTime;
	final private double travelDistance;

	public PublicTransitEvent(double arrivalTime, Id<Person> personId, Id<TransitLine> transitLineId,
			Id<TransitRoute> transitRouteId, Id<TransitStopFacility> accessStopId, Id<TransitStopFacility> egressStopId, Id<Departure> departureId,
			double vehicleDepartureTime, double travelDistance) {
		super(TYPE, arrivalTime);

		this.personId = personId;
		this.transitLineId = transitLineId;
		this.transitRouteId = transitRouteId;
		this.accessStopId = accessStopId;
		this.egressStopId = egressStopId;
		this.departureId = departureId;
		this.vehicleDepartureTime = vehicleDepartureTime;
		this.travelDistance = travelDistance;
	}

	public PublicTransitEvent(double now, PublicTransitEvent delegate) {
		this(now, delegate.getPersonId(), delegate.getTransitLineId(), delegate.getTransitRouteId(),
				delegate.getAccessStopId(), delegate.getEgressStopId(), delegate.getDepartureId(), delegate.getVehicleDepartureTime(),
				delegate.getTravelDistance());
	}

	public Id<TransitLine> getTransitLineId() {
		return transitLineId;
	}

	public Id<TransitRoute> getTransitRouteId() {
		return transitRouteId;
	}

	public Id<TransitStopFacility> getAccessStopId() {
		return accessStopId;
	}

	public Id<TransitStopFacility> getEgressStopId() {
		return egressStopId;
	}

	public Id<Departure> getDepartureId() {
		return departureId;
	}

	public double getVehicleDepartureTime() {
		return vehicleDepartureTime;
	}

	public double getTravelDistance() {
		return travelDistance;
	}

	@Override
	public Id<Person> getPersonId() {
		return personId;
	}

	@Override
	public String getEventType() {
		return TYPE;
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attributes = super.getAttributes();
		attributes.put("person", personId.toString());
		attributes.put("line", transitLineId.toString());
		attributes.put("route", transitRouteId.toString());
		attributes.put("accessStop", accessStopId.toString());
		attributes.put("egressStop", egressStopId.toString());
		attributes.put("departure", departureId.toString());
		attributes.put("vehicleDepartureTime", String.valueOf(vehicleDepartureTime));
		attributes.put("travelDistance", String.valueOf(travelDistance));
		return attributes;
	}

	public static PublicTransitEvent convert(GenericEvent genericEvent) {
		if(!TYPE.equals(genericEvent.getEventType())) {
			throw new IllegalStateException(String.format("Can't convert genericEvent of type '%s' to a PublicTransitEvent, a '%s' event type is needed", genericEvent.getAttributes(), TYPE));
		}
		Map<String, String> attributes = genericEvent.getAttributes();
		Id<Person> personId = Id.createPersonId(attributes.get("person"));
		Id<TransitLine> transitLineId = Id.create(attributes.get("line"), TransitLine.class);
		Id<TransitRoute> transitRouteId = Id.create(attributes.get("route"), TransitRoute.class);
		Id<TransitStopFacility> accessStopId = Id.create(attributes.get("accessStop"), TransitStopFacility.class);
		Id<TransitStopFacility> egressStopId = Id.create(attributes.get("egressStop"), TransitStopFacility.class);
		Id<Departure> departureId = Id.create(attributes.get("departure"), Departure.class);
		double vehicleDepartureTime = Double.parseDouble(attributes.get("vehicleDepartureTime"));
		double travelDistance = Double.parseDouble(attributes.get("travelDistance"));

		return new PublicTransitEvent(genericEvent.getTime(), personId, transitLineId, transitRouteId, accessStopId, egressStopId, departureId, vehicleDepartureTime, travelDistance);
	}
}
