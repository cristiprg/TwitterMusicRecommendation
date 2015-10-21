package nl.tue.twimu.pagerank;

import java.util.ArrayList;
import java.util.Collections;

public class ProbabilityVector {
	private ArrayList<Double> vector;
	private double sum = 0;
	private static double eps = 0.00000000001;
	
	/**
	 * Create and initialize a probability vector
	 * @param size the size of the vector
	 */
	ProbabilityVector(int size){	
		if(size==0)
			throw new RuntimeException("Size is zero.");
		vector = new ArrayList<Double>(Collections.nCopies(size, 0.0));
		//vector.set(0, 1.0);
	}
	
	/**
	 * Post-condition: the sum has to be 1
	 */
	boolean sanityCheck(){
		/*double sum = 0;
		for (double el : vector){
			sum += el;
		}*/

		boolean sane = Math.abs(sum - 1) < eps || Math.abs(sum) < eps;
		
		if (!sane){
			System.out.println(this);
		}
		
		return sane;
	}
	
	public boolean isZero(){
		return Math.abs(sum) < eps; 
	}

	public void increment(int i) {
		sum += 1;
		vector.set(i, vector.get(i) + 1);		
	}
	

	public void set(int i, double value) {
		sum += value - vector.get(i);
		vector.set(i, value);
	}
	
	@Override
	public String toString(){
		return "sum = " + sum + " " + vector.toString();
	}

	public Double get(int i) {
		return vector.get(i);
	}
	
	public ArrayList<Double> toArrayList(){
		return vector;
	}
}
