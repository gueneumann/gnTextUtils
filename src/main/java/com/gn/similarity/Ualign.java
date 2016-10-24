/**
 * Created on 26.11.2012
 * by gune00 
 * LT Lab.
 * German Research Center for Artificial Intelligence
 * (Deutsches Forschungszentrum fuer Kuenstliche Intelligenz GmbH = DFKI)
 * http://www.dfki.de
 * Saarbruecken, Saarland, Germany
 */
package com.gn.similarity;


/**
 * <p>Idea of Ukkonen (1985): for given k, stop further processing if for a prefix it is clear that it violates k, i.e., never compute the bottom
 * portion of a column if those entries can be inferred to be greater than k.</p>
 * <p>Approach: keep a index variable C_j which stores the maximum i such that D(P_i,W_j) =< k for the given j (C_j=0 if no such i).
 * Given C_(j-1), compute D(P_i,W_j) up to i =< C(j-1)+1, and then set C_j to the largest i (0=<i=<C(j-1)+1), s.t., D(P_i,W_j) =< k
 * Initialization: starting from C_0 with max allowed value in initial row vector (basically always k)
 * Early pruning: stop with "not-similar up to k", if C_j=0, which means that all values in that row are > k !</p>
 * 
 * @author gune00
 * 
 */

public class Ualign extends Palign {

	//private means only visible in this class
	private String s21;

	private String s22;

	/**
	 * Cutoff variable k: the maximum  number of k operations allowed during approximate pattern matching
	 * All pairs which need more than k operations are discarded.
	 */
	private int k = 0;

	// Constructors

	public Ualign() {
		super();
	}

	public Ualign(int w1, int w2, int w3) {
		super(w1, w2, w3);
	}

	// Setter and getter 
	public String getSourceString () {
		return this.s21;
	}

	public String getTargetString () {
		return this.s22;
	}

	public void setSourceString (String source) {
		this.s21 = source;
	}

	public void setTargetString (String target) {
		this.s22 = target;
	}

	public void setCutOff (int cutoff) {
		k = cutoff;
	}

	public int getCutOff () {
		return k;
	}

	// Methods
	
	/**
	 * Computes the largest i for which D(p_i,w_j) <= k. Method: from given i, check distance value and stop, 
	 * if condition is met; else decrease i;
	 * @param i maximum i <= C_(j-1)+1
	 * @param j current j
	 * @return largest i with D(i,j) <= k or 0
	 */
	public int getColumnMaxRaw(int i, int j) {
		while (i > 0){
			//System.out.println("raw value ["+i+","+j+"] = "+ distanceMatrix[i][j]);
			if (distanceMatrix[i][j] <= getCutOff()) break;
			i = i - 1;
		}
		//System.out.println("final raw  value " + i);
		return i;
	}
	
	/**
	 * Main method for approximate pattern matching using k-cutoff ala Ukkonen, 1985 and Chang & Lampe, 1992.
	 * @param string1 pattern
	 * @param string2 string
	 * @return zero or distance
	 * 
	 * CHECK: seems to require that source string is not larger than target string !
	 */

	public final int kapm (String string1, String string2) {
		setSourceString(" ".concat(string1));
		setTargetString(" ".concat(string2));

		int sLength = getSourceString().length();
		int tLength = getTargetString().length();

		// NOTE: since the initialization value of int is 0, all cells of the array are automatically 
		// initialized with 0 as well!
		distanceMatrix = new int[sLength][tLength];

		// Initialize column pointer c_0 = k. cf. Chang & Lampe, 1992, page 5.
		int column = getCutOff();

		outerloop1:
			for ( int i = 1; i < sLength; i++ ) {
				// Initialization of pattern string by matching the empty string:
				// define d_w(0:s1:i,0:s2:0) = d_w(0:s1:i-1,0:s2:0) + w(i,-)
				// Simulates complete deletion of pattern from text
				distanceMatrix[i][0] = computeDeletionCosts(getSourceString().charAt(i), '-', distanceMatrix[i-1][0]);
				// Fill it up only up to d_w(0:s1:i,0:s2:0) <= k
				if (distanceMatrix[i][0] > column) break outerloop1;
			}
		outerloop:
			for ( int j = 1; j < tLength; j++ ) {
				// incrementally process char by char target string (columns), with all chars of pattern string
				// Basically, it means: check all prefixes 0-i of pattern with current prefix 0-j of text
				// Initially compare with empty char of pattern
				// define d_w(0:s1:0,0:s2:j) = d_w(0:s1:0,0:s2:j) + w(-,j)
				// This means: complete insertion of target string into pattern
				distanceMatrix[0][j] = computeInsertionCosts('-', getTargetString().charAt(j), distanceMatrix[0][j-1]);
				
				innerloop:
					// for current column, process raws
					for ( int i = 1; i < sLength; i++ ) {
						// Compare complete pattern (up to cutoff) with current char of target taking into account previous
						// amount of minimal changes
						if (i <= (column + 1)) {
							// This if says: process up to raw C_(j-1)+1
							//System.out.println("   j="+j+":"+"i="+i+":"+"C_j="+column);
							distanceMatrix[i][j] = 
									Math.min(Math.min(
											// replacement or matching, i.e., check whether ss1(i) and ss2(j) are equal or not
											// previous costs from left diagonal cell (north-west)
											computeReplacementCosts(getSourceString().charAt(i), getTargetString().charAt(j), distanceMatrix[i-1][j-1])
											, 
											// deletion of character a in s1 which means insert gap operator '-'
											// previous cost from upper cell (north)
											computeDeletionCosts(getSourceString().charAt(i), '-', distanceMatrix[i-1][j])
											)
											,
											// insertion; previous cost from left element (west)
											computeInsertionCosts('-', getTargetString().charAt(j), distanceMatrix[i][j-1])
											);

						}
						else break innerloop;
					}
				// set new column to maximum i whose distance is not larger than k;
				column = getColumnMaxRaw(Math.min(column+1, sLength-1), j);
				// This IF says: all distance values of each i in current column are larger than k, so stop further processing
				if (column == 0) break outerloop;
			}
			// Finally, either return 0 (meaning no match) or max_i (maximum distance operation needed)
			return column;
	}
}
