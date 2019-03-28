package com.gn.trie;

import com.gn.trie.Trie;

/**
 * 
 * TrieDictionaryCreator defines a class for
 * creating dictionary file from given entries which are stored in a Trie.
 * <p> I will start by creating a NemexA like dictionary. 
 * <p> The form of a NemexA lexicon is:
 * <p> Initial line is: 
 * <p> 0 utf-8 EN 4 4 
 * <p> 
 * <p> 0 encoding language #entries #unique-tokens (of MWL)
 * <p>
 * <p> and then each entry is of formL
 * <p> 1 -9.197762 abacterial#abdominoperineal NG:1:-9.197762 
 * <p> or
 * <p> 1 -9.197762 abacterial#abdominoperineal NG:1:-9.197762 PP:1:-9.197762
 * <p>
 * <p> IDX LN-Prob entry#entry POS:FREQ:LN-PROB-OF-Reading +
 *  	
 * 
 * @author gune00
 *
 */
public class TrieDictionaryCreator {
	private String toFile;
	private String toEncoding = "UTF-8";
	private String category = "MWL";
	private boolean tokenized = true;
	public static enum DictType {SIMPLE, NEMEX};
	private DictType dictType = DictType.NEMEX;
	private  int counter = 0;

	private Trie<String> trie = new Trie<String>(this.isTokenized());
	
	// Getters and setters

		public DictType getDictType() {
			return dictType;
		}
		public void setDictType(DictType dictType) {
			this.dictType = dictType;
		}

		public Trie<String> getTrie() {
			return trie;
		}
		public void setTrie(Trie<String> trie) {
			this.trie = trie;
		}
		public String getToFile() {
			return toFile;
		}
		public void setToFile(String toFile) {
			this.toFile = toFile;
		}
		public String getToEncoding (){
			return toEncoding;
		}
		public void setFromEncoding (String toEncoding){
			this.toEncoding = toEncoding;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}

		public boolean isTokenized() {
			return tokenized;
		}
		public void setTokenized(boolean tokenized) {
			this.tokenized = tokenized;
		}

		// Initialization

		public TrieDictionaryCreator(){
		}
		
		//TODO
		/*
		 * Given a string or MWL entry (?)
		 * - add it to trie or increase counter; currently I assume single category !!!
		 * - when all elements are added
		 * - write to file and create nemex output format
		 * - and compute probabilities
		 * 
		 */

}
