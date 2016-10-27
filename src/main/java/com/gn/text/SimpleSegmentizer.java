package com.gn.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A reimplementation of our morphix-reader which is a very simple but effective and fast automaton for mapping a string into a list of tokens.
 * It handles cardinals and ordinals and special chars between words.
 * Punctuation are also isolated as single strings so can be used to split the token list into a list of sentences.
 * Output tokens can be lowCased or not.
 * Currently, developed for EN/DE like languages.
 * @author gune00
 *
 */
public class SimpleSegmentizer {

	// the last one should be #\^D, the Fill Down character
	private List<Character> specialChars = 
			Arrays.asList('.', ',', ';', '!', '?', ':', '(', ')', '{', '}', '[', ']', '$', '€', '\'', '\b'); 

	private List<Character> eosChars = 
			Arrays.asList('.', '!', '?'); 

	private List<Character> delimiterChars = 
			Arrays.asList('-', '_');

	private List<Character> tokenSepChars = 
			Arrays.asList(' ', '\n', '\t');

	private boolean splitString = false;
	private boolean lowerCase = false;
	private boolean createSentence = false;
	private boolean isCandidateAbrev = false;

	private String inputString = "";
	private List<String> tokenList = new ArrayList<String>();
	private List<List<String>> sentenceList = new ArrayList<List<String>>();

	public SimpleSegmentizer(){
	}

	public SimpleSegmentizer(boolean lowerCase, boolean splitString){
		this.lowerCase = lowerCase;
		this.splitString = splitString;
	}

	/*
	 * The idea is to define to points s and e which define a possible span over the input string vector.
	 * Depending on the type of char, a substring is extracted using current span information and a token is created.
	 * By making a new string form the substring and eventually lowercase the char or not.
	 * Thus the input string should be processed as a global variable
	 */

	private String makeToken(int start, int end, boolean lowerCase){
		int sl = Math.max(1, (end - start));
		char c = '\0';
		StringBuilder newToken = new StringBuilder(sl);

		for (int i = 0; i < sl; i++){
			c = (lowerCase)?
					(Character.toLowerCase(this.inputString.charAt(i+start))):
						this.inputString.charAt(i+start);
					newToken.append(c);
		}	
		//String outputString = newToken.toString()+"["+(start)+":"+(start+sl)+"]";
		String outputString = newToken.toString();
		return outputString;
	}

	private String convertToCardinal(String newToken) {
		return newToken; //+":card";
	}

	private String convertToOrdinal(String newToken) {
		return newToken; //+":ord";
	}

	private String convertToCardinalAndOrdinal(String newToken) {
		String cardinalString = newToken.substring(0, (1- newToken.length()));
		String ordinalString = newToken;
		//String outputString = cardinalString+"card:or:ord:"+ordinalString;
		String outputString = newToken;
		return outputString;
	}

	// TODO -> loosk already not that bad
	// identify sentence boundary
	// NOTE: what to do if we have no sentence boundary but only newline ?
	// try some heuristics here
	// thus make sure to collect some look-a-head elements before deciding when to create a sentence 
	// also: capture some specific HTML patterns like "HTTP/1.1", cf. GarbageFilter

	// works but often not enough, e.g., when abrev is at end of sentence or token is larger than 3 chars, e.g.,
	// bzw. domainname . de -> . als sentence boundary
	// I need left/right context
	private void setCandidateAbrev(String token){
		//System.err.println("Abrev? " + token);
		if ((token.length() <= 3)
				){
			this.isCandidateAbrev = true;
		}
		else
			this.isCandidateAbrev = false;
		//System.err.println("this.isCandidateAbrev=" + this.isCandidateAbrev);
	}

	private void setCreateSentenceFlag(char c){
		//System.err.println("Create sent: " + c);
		if (this.eosChars.contains(c) &&
				!this.isCandidateAbrev)
			this.createSentence = true;
		//System.err.println("this.createSentence=" + this.createSentence);
	}
	private void extendSentenceList(){
		// make a sentence
		this.sentenceList.add(this.tokenList);
		// reset sensible class parameters
		this.createSentence = false;
		this.tokenList = new ArrayList<String>();

	}
	/*
	 * This will be a loop which is terminated inside;
	 */
	public void scanText (String inputString){
		// Initialization
		this.inputString = inputString;
		int il = this.inputString.length();
		int state = 0;
		int start = 0;
		int delimCnt = 0;
		int end = 0;
		char c = '\0'; // used as dummy instead of nil or null

		// System.err.println("String length: " + il);

		while(true){
			//System.err.println("State " + state + " start: " + start + " end: " + end);

			if (end > il) break;

			if (end == il) {
				c  = '\0';
			}
			else {
				c = this.inputString.charAt(end);
			}

			if (this.createSentence) {
				this.extendSentenceList();
			}


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
							this.setCandidateAbrev(newToken);
							this.tokenList.add(Character.toString(c));
							this.setCreateSentenceFlag(c);
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
						// newToken is a char-string like "!"
						this.isCandidateAbrev = false;
						this.setCreateSentenceFlag(c);
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
				if ((c == '\0') || this.tokenSepChars.contains(c)){
					String newToken = this.makeToken(start, end, lowerCase);
					String cardinalString = convertToCardinal(newToken);
					this.tokenList.add(cardinalString);
					this.setCreateSentenceFlag(c);
					state = 0; start = (1+ end);	
				}
				else{
					if (c == '.') {
						state = 4;
					}
					else
						if (c == ',') {
							state = 3;
						}
						else
							if (this.specialChars.contains(c)) {
								String newToken = this.makeToken(start, end, lowerCase);
								String cardinalString = convertToCardinal(newToken);
								this.tokenList.add(cardinalString);
								this.tokenList.add(Character.toString(c));
								this.setCreateSentenceFlag(c);
								state = 0; start = (1+ end);
							} 
							else
								if (Character.isDigit(c)) {
								}
								else
								{
									state = 1;
								}

				}
				break;

			case 3: // state three: floating point designated by #\,
				if ((c == '\0') || this.tokenSepChars.contains(c)){
					String newToken = this.makeToken(start, (1- end), lowerCase);
					String cardinalString = convertToCardinal(newToken);
					this.tokenList.add(cardinalString);
					this.tokenList.add(",");
					state = 0; start = (1+ end);	
				}
				else {
					if (this.specialChars.contains(c)) {
						String newToken = this.makeToken(start, (1- end), lowerCase);
						String cardinalString = convertToCardinal(newToken);
						this.tokenList.add(cardinalString);
						this.tokenList.add(",");
						this.tokenList.add(Character.toString(c));
						state = 0; start = (1+ end);
					} 
					else {
						if (Character.isDigit(c)) {
							state = 5;
						}
						else 
						{
							String newToken = this.makeToken(start, (1- end), lowerCase);
							String cardinalString = convertToCardinal(newToken);
							this.tokenList.add(cardinalString);
							this.tokenList.add(",");
							state = 1; start = end;
						}
					}
				}
				break;

			case 4: // state four: floating point designated by #\.

				if ((c == '\0')){
					String newToken = this.makeToken(start, end, lowerCase);
					String numberString = convertToCardinalAndOrdinal(newToken);
					this.tokenList.add(numberString);
					this.tokenList.add(".");
					state = 0; start = end;
				}
				else {
					if (this.tokenSepChars.contains(c)){
						String newToken = this.makeToken(start, end, lowerCase);
						String numberString = convertToOrdinal(newToken);
						this.tokenList.add(numberString);
						state = 0; start = (1+ end);	
					}
					else {
						if (this.specialChars.contains(c)) {

							String newToken = this.makeToken(start, end, lowerCase);
							String numberString = convertToOrdinal(newToken);
							this.tokenList.add(numberString);
							this.tokenList.add(Character.toString(c));
							this.setCreateSentenceFlag(c);
							state = 0; start = (1+ end);
						}
						else {
							if (Character.isDigit(c)) {
								state = 5;
							}
							else {
								String newToken = this.makeToken(start, end, lowerCase);
								String numberString = convertToOrdinal(newToken);
								this.tokenList.add(numberString);
								state = 1; start = end;
							}
						}
					}
				}
				break;

			case 5: // state five: digits
				if ((c == '\0') || this.tokenSepChars.contains(c)){
					String newToken = this.makeToken(start, end, lowerCase);
					String cardinalString = convertToCardinal(newToken);
					this.tokenList.add(cardinalString);
					state = 0; start = (1+ end);	
				}
				else {
					if (this.specialChars.contains(c)) {
						String newToken = this.makeToken(start, end, lowerCase);
						String cardinalString = convertToCardinal(newToken);
						this.tokenList.add(cardinalString);
						this.tokenList.add(Character.toString(c));
						this.setCreateSentenceFlag(c);
						state = 0; start = (1+ end);
					} 
					else {
						if (Character.isDigit(c)) {
						}
						else {
							String newToken = this.makeToken(start, end, lowerCase);
							String cardinalString = convertToCardinal(newToken);
							this.tokenList.add(cardinalString);
							state = 1; start = end;
						}	
					}
				}
				break;

			case 6: // state six: handle delimiters like #\-
				if ((c == '\0') || this.tokenSepChars.contains(c)){
					String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
					this.tokenList.add(newToken);
					state = 0; delimCnt = 0; start = (1+ end);	
				}
				else {
					if (this.delimiterChars.contains(c)){
						delimCnt++;
					}
					else {
						if (this.specialChars.contains(c)) {
							String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
							this.tokenList.add(newToken);
							this.tokenList.add(Character.toString(c));
							this.setCreateSentenceFlag(c);
							state = 0; delimCnt = 0; start = (1+ end);
						}
						else {
							if (Character.isDigit(c)) {
								state = 0;
							}
							else {
								String newToken = this.makeToken(start, (end - delimCnt), lowerCase);
								this.tokenList.add(newToken);
								state = 1; delimCnt = 0; start = end;
							}
						}
					}
				}
				break;
			}
			end++;
		}
	}

	private String tokenListToString(List<String> tokenList){
		String outputString = "";
		for (String token : tokenList){
			outputString += token + " " ;
		}
		return outputString;
	}

	public String sentenceListToString(){
		String outputString = "";
		int id = 0;
		for (List<String> tokenList : this.sentenceList){
			outputString += id + ": " + this.tokenListToString(tokenList) + "\n";
			id++;
		}
		return outputString;
	}

	public void reset (){
		tokenList = new ArrayList<String>();
		sentenceList = new ArrayList<List<String>>();
	}

	public static void main(String[] args) throws Exception {
		SimpleSegmentizer segmentizer = new SimpleSegmentizer(false, false);

		long time1 = System.currentTimeMillis();
		segmentizer.scanText("Der Abriss wird schätzungsweise etwa 40 Jahre dauern, sagt Dr. Günter Neumann, der 3. Reiter danach!");
		long time2 = System.currentTimeMillis();
		System.out.println(segmentizer.sentenceListToString());
		System.err.println("System time (msec): " + (time2-time1));


		segmentizer.reset();
		segmentizer.scanText("Current immunosuppression protocols to prevent lung transplant rejection reduce pro-inflammatory and T-helper type 1 "
				+ "(Th1) cytokines. However, Th1 T-cell pro-inflammatory cytokine production is important in host defense against bacterial "
				+ "infection in the lungs. Excessive immunosuppression of Th1 T-cell pro-inflammatory cytokines leaves patients susceptible to infection.");
		System.out.println(segmentizer.sentenceListToString());

		segmentizer.reset();
		segmentizer.scanText("CELLULAR COMMUNICATIONS INC. sold 1,550,000 common shares at $21.75 each "
				+ "yesterday, according to lead underwriter L.F. Rothschild & Co. . Doof ist das ! ");
		System.out.println(segmentizer.sentenceListToString());
	}
}
