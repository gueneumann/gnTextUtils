package com.gn.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A reimplementation of our morphix-reader which is a very simple but easay and fast automaton for mapping a string into a list of tokens.
 * It handles cardinals and ordinals and special chars between words.
 * Punctuation are also isolated as single strings so can be used to split the token list into a list of sentences.
 * Out put tokens can be low-concate or not.
 * Currently, developed for EN/DE like languages.
 * @author gune00
 *
 */
public class SimpleSegmentizer {

	// the last one should be #\^D, the Fill Down character
	private List<Character> specialChars = 
			Arrays.asList('.', ',', ';', '!', '?', ':', '(', ')', '{', '}', '[', ']', '$', '€', '\'', '\b'); 


	private List<Character> delimiterChars = 
			Arrays.asList('-', '_');

	private List<Character> tokenSepChars = 
			Arrays.asList(' ', '\n', '\t');

	private boolean splitString = false;
	private boolean lowerCase = false;

	private String inputString = "";

	private List<String> tokenList = new ArrayList<String>();
	private List<List<String>> sentenceList = new ArrayList<List<String>>();

	/*
	 * The idea is to define to points s and e which define a possible span over the input string vector.
	 * Depending on the type of char, a substring is extracted using current span information and a token is created.
	 * By making a new string form the substring and eventually lowercase the char or not.
	 * Thus the input string should be processed as a global variable
	 */

	private String makeToken(int start, int end, boolean lowerCase){
		int sl = Math.max(1, (end - start));
		char c;
		StringBuilder newToken = new StringBuilder(sl);

		for (int i = start; i < sl; i++){
			c = (lowerCase)?(Character.toLowerCase(this.inputString.charAt(i))):this.inputString.charAt(i);
			newToken.append(c);
		}	
		return newToken.toString();
	}

	/*
	 * This will be a loop which is terminated inside;
	 */
	public void scan (String inputString){
		// Initialization
		this.inputString = inputString;
		int il = this.inputString.length();
		int state = 0;
		int start = 0;
		int delimCnt = 0;
		int end = 0;
		char c = '\0'; // used as dummy instead of nil or null

		while(true){
			if (end > il) break;

			if (end != il) 
				c = this.inputString.charAt(end);

			switch (state) {
			// state actions
			
			case 1: // 1 is the character state, so most likely
				if ((c == '\0') || this.tokenSepChars.contains(c)) {
					String newToken = this.makeToken(start, end, lowerCase);
					this.tokenList.add(newToken);
					state = 0; start = (1+ end);
				}
				else {
					if (this.splitString && this.delimiterChars.contains(c)){
						state = 6; delimCnt++;
					}
					else {
						if (this.specialChars.contains(c)) {
							String newToken = this.makeToken(start, end, lowerCase);
							this.tokenList.add(newToken);
							this.tokenList.add(Character.toString(c));
							state = 0; start = (1+ end);
						}
					}
				}
				break;

			case 0: // state zero covers: space, tab, specials
				if ((c == '\0') || this.tokenSepChars.contains(c)) {
					start++;
				}
				else {
					if (this.specialChars.contains(c)){
						String newToken = this.makeToken(start, end, lowerCase);
						this.tokenList.add(newToken);
						start++;
					}
					else {
						if (Character.isDigit(c)) {
							state = 2;
						}
						else {
							state = 1;
						}
					}
				}
				break;
				
			case 2: // state two: integer part of digit
				// TODO
				break;
			case 3: // state three: floating point designated by #\,
				break;
			case 4: // state four: floating point designated by #\.
				break;
			case 5: // state five: digits
				break;
			case 6: // state six: handle delimiters like #\-
				break;

			}

			end++;
		}
	}
}
