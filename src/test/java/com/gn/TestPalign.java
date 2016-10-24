/**
 * Created on 22.11.2012
 * by gune00 
 * LT Lab.
 * German Research Center for Artificial Intelligence
 * (Deutsches Forschungszentrum fuer Kuenstliche Intelligenz GmbH = DFKI)
 * http://www.dfki.de
 * Saarbruecken, Saarland, Germany
 */
package com.gn;

import com.gn.similarity.Lalign;
import com.gn.similarity.Palign;
import com.gn.similarity.Ualign;

public class TestPalign {

	public TestPalign() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */

	public static void testPalign(String s, String t) {
		// TODO Auto-generated method stub
		System.out.println("Testing Palign.dp ...");
		Palign palign = new Palign(1,1,1);

		long time1 = System.currentTimeMillis();
		System.out.println("Distance number: " + 
				palign.dp(s, t));
		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
		palign.printDistanceMatrix();
	}

	public static void testLalign(String s, String t) {
		// TODO Auto-generated method stub
		System.out.println("Testing Lalign.apm (either use 0 or 1 for insertion) ...");
		Lalign lalign = new Lalign(1,1,1);
		
		long time1 = System.currentTimeMillis();
		System.out.println("Distance number: " + 
				lalign.apm(s,t));
		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
		lalign.printDistanceMatrix();
		lalign.spanOfMatchingSubsequence();
		System.out.println("Spans: " + lalign.getSpan().length);
		lalign.printMatchingSubsequence();
	}
	
	public static void testUalign(String s, String t, int k) {
		// TODO Auto-generated method stub
		System.out.println("Testing Ualign.kapm with k = " + k);
		Ualign ualign = new Ualign(1,1,1);
		ualign.setCutOff(k);
		
		long time1 = System.currentTimeMillis();
		System.out.println("Largest valid raw: " + 
				ualign.kapm(s,t));
		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
		ualign.printDistanceMatrix();
	}

	public static void main(String[] args) {
		//TestPalign.testPalign("kaushic chaduri", "kaushuk chadhui");
		//TestPalign.testLalign("ART", "HATGgAFFFATGABBB");
		TestPalign.testPalign("umgeschaut", "umschauen");
		TestPalign.testLalign("survey", "surgery");
		//TestPalign.testPalign("kleiner Pual", "kleine Paula   ");
		TestPalign.testUalign("kleiner Pual", "kleine Paula   ", 2);

	}
}
