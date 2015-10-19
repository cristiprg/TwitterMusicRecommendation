package nl.tue.twimu.pagerank;

import java.util.ArrayList;

public class ProbabilityMatrix {
	private ArrayList<ProbabilityVector> matrix;
	
	/**
	 * Creates and initializes the probability matrix
	 * @param size the size of the matrix, how many nodes there are
	 */
	ProbabilityMatrix(int size) {
		matrix = new ArrayList<ProbabilityVector>(size);
		for(int i = 0; i < size; ++i){
			matrix.add(new ProbabilityVector(size));
		}
	}
	
	/**
	 * Pretty expensive, but still ...
	 * Post-condition: every entry has to be a prob-vector, i.e. sum = 1 
	 */
	boolean sanityCheck(){
		for(ProbabilityVector v : matrix){
			if (!v.sanityCheck())
				return false;
		}
		return true;
	}

	public Double get(int i, int j){
		return matrix.get(i).get(j);
	}
	
	public void set(int i, int j, double value){
		matrix.get(i).set(j, value);
	}
	
	public void increment(int i, int j) {
		matrix.get(i).increment(j);
	}
	
	public boolean isZero(int i){
		return matrix.get(i).isZero();
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		for(ProbabilityVector v : matrix){
			s.append(v.toString() + "\n");
		}
		return s.toString();
	}
}
