/**
 * Created on 23.11.2012
 * by gune00 
 * LT Lab.
 * German Research Center for Artificial Intelligence
 * (Deutsches Forschungszentrum fuer Kuenstliche Intelligenz GmbH = DFKI)
 * http://www.dfki.de
 * Saarbruecken, Saarland, Germany
 */
package com.gn.similarity;

public class Lalign extends Palign {
	// private means only visible in this class
	private String s21;

	private String s22;
	
	/**
	 * span will keep the left and right position of the aligned subsequence in the target string.
	 */
	private int[] span = new int[]{0,0};

	// Constructor
	
	public Lalign() {
		super();
	}

	/**
	 * Create class with w1=insertion, w2=deletion, and w3=replacement weights
	 * @param w1
	 * @param w2
	 * @param w3
	 */
	
	public Lalign(int w1, int w2, int w3) {
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
	
	/**
	 * @return the span
	 */
	public int[] getSpan() {
		return span;
	}

	/**
	 * @param span the span to set
	 */
	public void setSpan(int[] span) {
		this.span = span;
	}
	

	// This computes a local alignment of s1 in s2 assuming that s1 is reasonable smaller than s2
	// it is also called approximate pattern match
	// It is quite similar to the method dp !
	// Core idea of local alignment is to charge no costs for deletion for 
	// (1) prefix 0:t:i and (2) suffix i:t:n
	//
	// NOTE: it only checks first occurrence of pattern in target!
	/**
	 * <p<b>APPROXIMATE PATTERN MATCHING</b> definition of local alignment which is also known as
	 * approximate pattern matching. Core idea:
	 * let 0:s:m and 0:t:n (m < n). Find i:t:j such that d_w(s,i:t:j) 
	 * is minimal among all choices of 0<=i<=j<=n: (i.e., seek that subunit of t which s aligns best with)
	 * using function palign.dp() as starting point, palign.apm()  means:
	 * no costs charged for: deletion of a prefix 0:t:i; insertion of a suffix j:t:n
	 * </p>
	 * @param string1
	 * @param string2
	 * @return distance
	 */

	public int apm (String string1, String string2) 
		throws RuntimeException
		{
		// This is a naive way to avoid taking care of different starting indices of 
		// Strings and array :-)
		
		setSourceString(" ".concat(string1));
		setTargetString(" ".concat(string2));

		if (getTargetString().length() < getSourceString().length()){
			throw new RuntimeException("Source string '"
					+ string1
					+"' should not be larger than '"
					+ string2+"'");
		}

		distanceMatrix = new int[getSourceString().length()][getTargetString().length()];

		for ( int i = 1; i < getSourceString().length(); i++ ) {
			// Initialize first column (empty string)
			// define d_w(0:s1:i,0:s2:0) = d_w(0:s1:i-1,0:s2:0) + w(i,-)	
			distanceMatrix[i][0] = computeDeletionCosts(getSourceString().charAt(i), '-', distanceMatrix[i-1][0]);
		}

		for ( int j = 1; j < getTargetString().length(); j++ ) {
			// Incrementally process target string: the pattern string is completely checked against target string.
			// Difference (1) compared to palign.dp():
			// Case (1) actually means define d_w(0:s1:0,0:s2:j) = 0, i.e., all elements of first raw is 0

			//No costs for deletion on prefix of target string
			distanceMatrix[0][j] = 0;

			for ( int i = 1; i < getSourceString().length(); i++ ) {

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
								// NOTE: if costs are 1, then Lalign performs search, and returns smallest match
								// if set to 0, it performs best alignment on text
								computeInsertionCosts(getSourceString().charAt(i), '-', distanceMatrix[i][j-1])
								);
			}
		}
		// Extract distance
		setDistance();
		// return distance value d
		return getDistance();
		}

	/** 
	 * determine the span of the smallest substring in s2 which matches the patterns s1
	 * with smallest edits operations.
	 * only applicable for method apm()
	 * compute span of matching subsequence in s2 with lowest distance: j = min{k| d_m_k = d_m_n};
	 * i = is the point where the optimal path leading to d_m_j starts from the first row
	 * @return two-element array of the span of the matching subsequence found in string s2
	 */
	public int[] spanOfMatchingSubsequence () {
		//TODO not really correct !!!
		// I think, I need to loop from the last row m backwards starting from the rightmost column j
		// and then check each j which has lower or equal value as found distance
		// and then traverse the smallest path back to get until raw 1 to get the starting point
		
		int s1Length = getSourceString().length() - 1;
		int s2Length = getTargetString().length() - 1;

		// loop backwards from right to left in last raw
		// and stop if a cell has been found with larger value as distance
		// endpos is the rightmost element

		int smallest = s2Length;
				
		while (s2Length != 0){
			if (distanceMatrix[s1Length][s2Length] > getDistance()){
				span[1] = smallest; break;
			}	
			s2Length--;
			if (distanceMatrix[s1Length][smallest] >= distanceMatrix[s1Length][s2Length])
				{smallest = s2Length;}
			
		}

		// Jump to first raw and loop to that column whose distance is smaller
		// then previous one.
		// NOTE: I am assuming here that I consider complete local approximate match of the pattern
		// note substrings from the pattern.
		// Does not work proper ! see above
		for ( int j = 0; j < span[1]; j++) {
			if (distanceMatrix[1][j] <= span[0]){
				span[0] = j; break;
			}
		}
		return span;
	}

	/**
	 * A simple printing function: it braces the found subsequence in s2 by [ ].
	 */
	public void printMatchingSubsequence () {

		int leftBoundary = span[0];
		int rightBoundary = span[1]+1;

		// I need to adjust rightBoundary because of artificial " " added in front of
		// string and because matrix starts from 0
		String subsequence = this.getTargetString().substring(leftBoundary, rightBoundary+1);
		System.out.print(getSourceString()+" SMALLEST PARTIAL MATCH FOUND AT ");
		System.out.println("["+ leftBoundary +","+ rightBoundary + "]");
		if (span[0]==0 && span[1]==0) 
			System.out.println(getTargetString());
		else
			System.out.println(getTargetString().substring(0, Math.max(leftBoundary,0))
				+"["+subsequence+"]"+getTargetString().substring(rightBoundary+1)+"\n");
	}

	

}
