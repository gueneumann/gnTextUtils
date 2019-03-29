package com.gn.trie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.gn.text.SimpleSegmentizer;
import com.gn.trie.Trie;

/**
 * 
 * TrieDictionaryFileReader defines a class for
 * loading a dictionary from dictionary file
 * and creating a Trie data structure
 * <p>Currently, two modes are supported when a trie entry is determined
 * 	 <p>- the NE is tokenized and each token defines its own trie-entry
 *   <p>- the whole NE is used as entry.
 *   <p>- NOTE: in principle, we can also have a mix of both
 *  <p>- the flag tokenized triggers between the modes
 *  <p>The enum type DictType is used to handle different formats. Currently supported are
 *  <p>- SIMPLE:
 * 	  <p>- each line represents a NE 
 *    <p>- and all NE have same type
 *   <p> - in this case the type has to be specified as a parameter
 *   <p>- NEMEX:
 *   	<p>- each line represents a nemex entry
 *      <p>- each can have different type readings
 *      <p>- in this case the type info is computed
 *  <p> BOOLEAN lowercase: lower case whole entry before it is entered into the trie
 *  
 *  <p>MISSING:
 *  	<p>- parsing of an entry in order to identify noise
 *  	
 * 
 * @author gune00
 *
 */
public class TrieDictionaryFileReader {
	private String fromFile;
	private String fromEncoding = "UTF-8";
	private String neType = "PER";
	private boolean tokenized = true;
	private boolean lowerCase = true;
	public static enum DictType {SIMPLE, NEMEX};
	private DictType dictType = DictType.SIMPLE;
	private  int counter = 0;

	private Trie<String> trie = new Trie<String>(this.isTokenized());
	
	private SimpleSegmentizer segmentizer = new SimpleSegmentizer(false, false);

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
	public String getFromFile() {
		return fromFile;
	}
	public void setFromFile(String fromFile) {
		this.fromFile = fromFile;
	}
	public String getFromEncoding (){
		return fromEncoding;
	}
	public void setFromEncoding (String newEncoding){
		fromEncoding = newEncoding;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}

	public boolean isTokenized() {
		return tokenized;
	}
	public void setTokenized(boolean tokenized) {
		this.tokenized = tokenized;
	}

	// Initialization
	
	public TrieDictionaryFileReader(){
	}

	public TrieDictionaryFileReader(boolean lowerCase){
		this.lowerCase = lowerCase;
	}
	
	public TrieDictionaryFileReader(String inFile, String neType, boolean lowerCase){
		this.setFromFile(inFile);
		this.setNeType(neType);
		this.lowerCase = lowerCase;
	}

	public TrieDictionaryFileReader(String inFile, String neType, boolean lowerCase, boolean tokenized){
		this.setFromFile(inFile);
		this.setNeType(neType);
		this.lowerCase = lowerCase;
		this.setTokenized(tokenized);
	}

	// Methods

	/**
	 * Reset the trie data structure and specifies new dictionary parameters.
	 * @param inFile
	 * @param neType
	 * @param tokenized
	 */
	public void initDictWithFile(String inFile, String neType, DictType dictType, boolean tokenized){
		this.setTrie(new Trie<String>());

		this.setFromFile(inFile);
		this.setNeType(neType);
		this.setTokenized(tokenized);
		this.setDictType(dictType);
	}

	/**
	 * Specify new dictionaries parameter which will be used to extend the current trie.
	 * @param inFile
	 * @param neType
	 * @param tokenized
	 */
	public void addDictWithFile(String inFile, String neType, DictType dictType, boolean tokenized){
		this.setFromFile(inFile);
		this.setNeType(neType);
		this.setTokenized(tokenized);
		this.setDictType(dictType);
	}

	public void insertTokenizedSegment(List<String> tokenizedSegment, String neType) {
		for ( java.util.ListIterator<String> i = tokenizedSegment.listIterator(); i.hasNext(); ) 
		{ 
			// Get the next node, which is a string here !
			String token = i.next();
			this.counter++;
			CharSequence segment = new StringBuilder(token);
			this.getTrie().addEntry(segment, neType);
		}
	}

	/** receives a unit, tokenizes it and inserts each token into the trie 
	 * 
	 * @param segmentBuffer
	 */
	public void processTokenizedSegment(String segmentBuffer, String neType){
		// do tokenization with morphAdorner
		this.segmentizer.scanText(segmentBuffer);
		// System.out.println(this.getTokenList().toString());
		insertTokenizedSegment(this.segmentizer.getTokenList(), neType);
	}

	/** receives a unit and inserts the whole string as entry into trie 
	 * 
	 * @param segmentBuffer
	 */
	public void processWholeSegment(String segment, String neType) {
		this.getTrie().addEntry(segment, neType);
	}

	/**
	 * A simple reader which assume that a dictionary entry just consists of a MW, and
	 * each entry has the same type.
	 * @param line
	 * @param neType
	 */
	private void computeFromSimpleEntry(String line, String neType){
		if (!line.isEmpty())
		{
			String segment = (this.lowerCase)?line.toLowerCase():line;
			if (this.isTokenized()){
				this.processTokenizedSegment(segment, neType);
			}
			else
				this.processWholeSegment(segment, neType);
		}
		else
			System.err.println("Empty word!");
	}

	/**
	 * Transforms a Nemex dictionary with entries of form
	 * "4 -9.197762 DFKI ORG:1:-9.197762 LOC:1:-9.197762"
	 * into a trie, by using MW as key, and concatenation of readings as content.
	 * OR just the entry's index
	 * @param line
	 */
	private void computeFromNemexEntry(String line){
		// "4 -9.197762 DFKI ORG:1:-9.197762 LOC:1:-9.197762"
		String[] nemexEntry = line.split(" ");

		String nemexWord = nemexEntry[2].replace("#", " ");

		// Only keep type
		String nemexType = nemexEntry[3].split(":")[0];
		//System.out.println("Word: " + nemexWord + "; Type: " + nemexType);
		

		// Just concat multiple types together which have been split above
		for (int i=4; i < nemexEntry.length; i++){
			nemexType = nemexType + ":" + nemexEntry[i].split(":")[0];
		}

		//Only add type information
		computeFromSimpleEntry(nemexWord, 
				//				nemexEntry[0] 
				//						+ "="
				//						+ nemexWord+":"+
				nemexType
				);
	}

	// Main dictionary reader loop
	/**
	 * Main dictionary file reader: reads in a dictionary line-wise and creates a trie based on the specific 
	 * dictionary format, which is currently either SIMPLE (each line corresponds to a MW) or NEMEX (each line
	 * follows an NEMEX entry).
	 */
	public void readFromWordListDictionary() {
		BufferedReader inStream = null; 
		String line;
		int cnt = 0;
		int unitOffSet = 100000;

		try {
			// inStream = the file buffer of the input stream reader; encoding is explicitly set!
			inStream = new BufferedReader
					(new InputStreamReader
							(new FileInputStream(fromFile), fromEncoding));
			// Assuming each line corresponds to an entry
			while ((line = inStream.readLine()) != null){

				switch (this.getDictType()) {
				case SIMPLE: 
					computeFromSimpleEntry(line, this.getNeType());
					break;
				case NEMEX:
					// ignore first line of a nemex dictionary
					if (cnt != 0) 
						computeFromNemexEntry(line);
					break;
				default:
					break;
				}
				++cnt;
				if ( (cnt % unitOffSet) == 0) {
					System.out.println("Dictionary lines processed: " + cnt);
				}
			}
			System.out.println("Dictionary tokens scanned: " + this.counter);
		} 
		catch (IOException e) { e.printStackTrace();}
		finally {
			if ( inStream != null )
				try { inStream.close(); } catch ( IOException e ) { e.printStackTrace(); }
		}
	}
}
