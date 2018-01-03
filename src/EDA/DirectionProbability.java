package EDA;

import main.MonomerDirection;

public class DirectionProbability {
	float probability;
	MonomerDirection dir;
	
	public DirectionProbability(float probility, MonomerDirection dir){
		this.probability = probility;
		this.dir = dir;
	}
	public DirectionProbability(DirectionProbability prob){
		this.probability = prob.getProbability();
		this.dir = prob.getDir();
	}
	
	public String toString() {
		return "DirectionProbabaility "+dir+" "+probability+"\t";
	}
	public void setProbabilty(float probability){
		this.probability = probability;
	}
	public void addToProbabilty(float probability){
		this.probability += probability;
	}

	public float getProbability() {
		return probability;
	}

	
	public MonomerDirection getDir() {
		return dir;
	}


}
