package com.gn.similarity;

/******************************************************************************
	 *  Compilation:  javac LongestCommonSubstring.java
	 *  Execution:    java  LongestCommonSubstring file1.txt file2.txt
	 *  Dependencies: SuffixArray.java In.java StdOut.java
	 *  
	 *  Read in two text files and find the longest substring that
	 *  appears in both texts.
	 * 
	 *  % java LongestCommonSubstring tale.txt mobydick.txt
	 *  ' seemed on the point of being '
	 *
	 ******************************************************************************/

	/**
	 *  The <tt>LongestCommonSubstring</tt> class provides a {@link SuffixArray}
	 *  client for computing the longest common substring that appears in two
	 *  given strings.
	 *  <p>
	 *  This implementation computes the suffix array of each string and applies a
	 *  merging operation to determine the longest common substring.
	 *  For an alternate implementation, see
	 *  <a href = "http://algs4.cs.princeton.edu/63suffix/LongestCommonSubstringConcatenate.java.html">LongestCommonSubstringConcatenate.java</a>.
	 *  <p>
	 *  For additional documentation,
	 *  see <a href="http://algs4.cs.princeton.edu/63suffix">Section 6.3</a> of
	 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
	 *  <p>
	 *     
	 *  @author Robert Sedgewick
	 *  @author Kevin Wayne
	 */
	public class LongestCommonSubstring {

	    // Do not instantiate.
	    private LongestCommonSubstring() { }

	    // return the longest common prefix of suffix s[p..] and suffix t[q..]
	    private static String lcp(String s, int p, String t, int q) {
	        int n = Math.min(s.length() - p, t.length() - q);
	        for (int i = 0; i < n; i++) {
	            if (s.charAt(p + i) != t.charAt(q + i))
	                return s.substring(p, p + i);
	        }
	        return s.substring(p, p + n);
	    }

	    // compare suffix s[p..] and suffix t[q..]
	    private static int compare(String s, int p, String t, int q) {
	        int n = Math.min(s.length() - p, t.length() - q);
	        for (int i = 0; i < n; i++) {
	            if (s.charAt(p + i) != t.charAt(q + i))
	                return s.charAt(p+i) - t.charAt(q+i);
	        }
	        if      (s.length() - p < t.length() - q) return -1;
	        else if (s.length() - p > t.length() - q) return +1;
	        else                                      return  0;
	    }

	    /**
	     * Returns the longest common string of the two specified strings.
	     *
	     * @param  s one string
	     * @param  t the other string
	     * @return the longest common string that appears as a substring
	     *         in both <tt>s</tt> and <tt>t</tt>; the empty string
	     *         if no such string
	     */
	    public static String lcs(String s, String t) {
	        SuffixArray suffix1 = new SuffixArray(s);
	        SuffixArray suffix2 = new SuffixArray(t);

	        // find longest common substring by "merging" sorted suffixes 
	        String lcs = "";
	        int i = 0, j = 0;
	        while (i < s.length() && j < t.length()) {
	            int p = suffix1.index(i);
	            int q = suffix2.index(j);
	            String x = lcp(s, p, t, q);
	            if (x.length() > lcs.length()) lcs = x;
	            if (compare(s, p, t, q) < 0) i++;
	            else                         j++;
	        }
	        return lcs;
	    }
	    
	    /**
	     * Returns the longest common string of the two specified strings.
	     *
	     * @param  s one string
	     * @param  t the other string
	     * @return the longest common string that appears as a substring
	     *         in both <tt>s</tt> and <tt>t</tt>; the empty string
	     *         if no such string
	     */
	    public static String lcsConc(String s, String t) {
	        int N1 = s.length();

	        // concatenate two string with intervening '\1'
	        String text  = s + '\1' + t;
	        int N  = text.length();

	        // compute suffix array of concatenated text
	        SuffixArray suffix = new SuffixArray(text);

	        // search for longest common substring
	        String lcs = "";
	        for (int i = 1; i < N; i++) {

	            // adjacent suffixes both from first text string
	            if (suffix.index(i) < N1 && suffix.index(i-1) < N1) continue;

	            // adjacent suffixes both from secondt text string
	            if (suffix.index(i) > N1 && suffix.index(i-1) > N1) continue;

	            // check if adjacent suffixes longer common substring
	            int length = suffix.lcp(i);
	            if (length > lcs.length()) {
	                lcs = text.substring(suffix.index(i), suffix.index(i) + length);
	            }
	        }
	        return lcs;
	    }

	    /**
	     * Unit tests the <tt>lcs()</tt> method.
	     * Reads in two strings from files specified as command-line arguments;
	     * computes the longest common substring; and prints the results to
	     * standard output.
	     */
	    public static void main(String[] args) {
	    	String s = "hausern";
	        String t = "gehaus";
	        System.out.println("'" + lcs(s, t) + "'");
	    }
	}
