package temperature;

import main.Configuration;

public class LinearCooling implements TemperatureManager {
	private final float initialTemperature;
	private final float deltaT;
	private float currentTemperature;
	public LinearCooling(Configuration config) {
		float finalTemperature;
		int numberOfSteps;

		
		initialTemperature = config.initialTemperature;
		finalTemperature = config.finalTemperature;
		numberOfSteps = config.numberOfGenerations;
		deltaT = (initialTemperature - finalTemperature)/numberOfSteps;
		currentTemperature = initialTemperature;
	}
	public void reset(){
		currentTemperature = initialTemperature;
	}
	public float getNextTemprature() {
		float out = currentTemperature;
		currentTemperature -= deltaT;
		return out;
	}


}
