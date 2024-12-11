package org.eqasim.sao_paulo.mode_choice.parameters;

import org.eqasim.core.simulation.mode_choice.parameters.ModeParameters;

public class SaoPauloModeParameters extends ModeParameters {
	public class SaoPauloWalkParameters {
		public double alpha_walk_city = 0.0;
	}
	
	public class SaoPauloCarParameters {
		public double alpha_car_city = 0.0;
	}
	
	public class SaoPauloPTParameters {
		public double alpha_pt_city = 0.0;
		public double alpha_age = 0.0;

	}
	
	public class SaoPauloIncomeElasticity {
		public double lambda_income = 0.0;
	}
	
	public class SaoPauloAvgHHLIncome {
		public double avg_hhl_income = 0.0;
	}
	
	public class SaoPauloTaxiParameters {
		public double alpha_taxi_city = 0.0;
		public double beta_TravelTime_u_min = 0.0;
		
		public double betaAccessEgressWalkTime_min = 0.0;
		public double betaWaitingTime_u_min = 0.0;
		public double alpha_u = 0.0;
		
		public SaoPauloTaxiParameters() {
			this.alpha_taxi_city = 0.0;
		}
		
	}
	
	public final SaoPauloWalkParameters spWalk = new SaoPauloWalkParameters();
	public final SaoPauloPTParameters spPT = new SaoPauloPTParameters();
	public final SaoPauloCarParameters spCar = new SaoPauloCarParameters();
	public final SaoPauloIncomeElasticity spIncomeElasticity = new SaoPauloIncomeElasticity();
	public final SaoPauloAvgHHLIncome spAvgHHLIncome = new SaoPauloAvgHHLIncome();
	public final SaoPauloTaxiParameters spTaxi = new SaoPauloTaxiParameters();

	public static SaoPauloModeParameters buildDefault() {
		SaoPauloModeParameters parameters = new SaoPauloModeParameters();

		// Cost
		//impact of travel cost on utility
		//negative values indicate that higher costs decrease utility
		parameters.betaCost_u_MU = -0.0606;
		
		//how the cost scales with distance
		parameters.lambdaCostEuclideanDistance = 0.0;
		
		//defines a reference distance (in kilometers) used in the cost estimation. 
		//It can be used to normalize cost over different trip distances.
		parameters.referenceEuclideanDistance_km = 40.0;
		
		
		//how sensitive the mode choice is to changes in income. 
		//A negative value suggests that as income increases, 
		//cost sensitivity decreases.
        parameters.spIncomeElasticity.lambda_income = -0.2019;
        
        //average household income used as a baseline 
        //for adjusting utility based on income
        parameters.spAvgHHLIncome.avg_hhl_income = 4215;
        
		// Car
        //mode-specific constant for the car mode, 
        //influencing the base utility for choosing a car. 
        //A value of 0.0 means no inherent preference for or against the car mode.
		parameters.car.alpha_u = 0.0;
		
		//The coefficient for the travel time spent in the car, in utility per minute. 
		//A negative value means that longer car travel times 
		//decrease utility
		parameters.car.betaTravelTime_u_min = -0.0246;
        
		//The extra walking time associated with using a car 
		//(e.g., walking to/from the parking spot). 
		//Here, it is set to 0.0, meaning there is no penalty for walking.
		parameters.car.additionalAccessEgressWalkTime_min = 0.0;
		
		//The time penalty (in minutes) for searching for parking. 
		//In this case, it’s set to 0.0, meaning no parking search penalty is applied.
		parameters.car.constantParkingSearchPenalty_min = 0.0;
		
		//A car-specific parameter related to city-specific behavior. 
		//The value -0.1597 suggests a slight negative impact on car usage in the city environment.
		parameters.spCar.alpha_car_city = -0.1597;

		// PT
		//The mode-specific constant for public transport. 
		//A value of -0.2 suggests a slight disfavor toward public transport in the base utility.
		parameters.pt.alpha_u = -0.2;
		
		//The coefficient for line switching 
		//(i.e., transferring between different lines or modes in public transport). 
		//Set to 0.0, meaning no additional disutility is applied for transfers.
		parameters.pt.betaLineSwitch_u = 0.0;
		
		//The coefficient for in-vehicle time (time spent traveling on public transport). 
		//A value of -0.0142 indicates that longer travel times reduce the utility.
		parameters.pt.betaInVehicleTime_u_min = -0.0142;
		
		//The coefficient for waiting time before boarding public transport. 
		//It has the same value (-0.0142) as in-vehicle time, 
		//implying waiting time is considered equally inconvenient as travel time.
		parameters.pt.betaWaitingTime_u_min = -0.0142;
		
		//The coefficient for the access/egress time 
		//(time spent walking to/from the PT stops or stations). 
		//Again, the value is -0.0142, similar to waiting and in-vehicle time.
		parameters.pt.betaAccessEgressTime_u_min = -0.0142;
		
		//A city-specific constant for public transport. 
		//The value 0.0 means there’s no additional adjustment 
		//for city-specific effects on public transport utility.
		parameters.spPT.alpha_pt_city = 0.0;
		
		//A coefficient to adjust utility based on age, 
		//possibly representing the ease of use for different age groups. 
		//It is set to 0.0, meaning age is not considered.
		parameters.spPT.alpha_age = 0.0;
		// Bike
		parameters.bike.alpha_u = 0.0;
		parameters.bike.betaTravelTime_u_min = 0.0;
		parameters.bike.betaAgeOver18_u_a = 0.0;

		// Walk
		//The mode-specific constant for walking, set to 2.2, 
		//indicating a strong positive preference for walking.
		parameters.walk.alpha_u = 2.2;
		//The coefficient for walking time. 
		//A negative value of -0.1257 indicates that longer walking times decrease utility (disutility).
		parameters.walk.betaTravelTime_u_min = -0.1257;
		//A city-specific parameter for walking, set to 0.0, 
		//indicating no special adjustment for walking in the city environment.
		parameters.spWalk.alpha_walk_city = 0.0;
		
		//Taxi
		//A city-specific parameter for taxis, set to 0.0, 
		//meaning no city-specific adjustment is made for taxi usage.
		parameters.spTaxi.alpha_taxi_city = 0.0;
		
		//The coefficient for the travel time in taxis. 
		//A value of -0.15 means longer taxi rides decrease utility significantly.
		parameters.spTaxi.beta_TravelTime_u_min = -0.15;
		
		//The coefficient for the waiting time for taxis. 
		//It shares the same value as the public transport waiting time parameter,
		parameters.spTaxi.betaWaitingTime_u_min = parameters.pt.betaWaitingTime_u_min;
		
		//The coefficient for access/egress walk time to/from the taxi, 
		//also shared with public transport parameters.
		parameters.spTaxi.betaAccessEgressWalkTime_min = parameters.pt.betaAccessEgressTime_u_min;
		
		//The mode-specific constant for taxis, 
		//set to -3.0, meaning there is a strong inherent disutility 
		//or bias against using taxis as a mode of transport.
		parameters.spTaxi.alpha_u = -3.0;
		
		//DRT - inserido em 14/10 por mayuri
		parameters.drt.alpha_u = -0.2;
		parameters.drt.betaTravelTime_u_min = -0.0142;
		parameters.drt.betaWaitingTime_u_min = -0.0142;
		parameters.drt.betaAccessEgressTime_u_min = -0.0142;
		
		return parameters;
	}
}
