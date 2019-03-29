package com.gn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TreeMap;

import com.gn.trie.TrieDictionaryFileReader;
import com.gn.trie.TrieDictionaryFileReader.DictType;

/*
 * 
 * NemexF:
 * Formally, the problem of approximate entity matching can be described as follows: 
 * Given a dictionary of natural language entities E = {e1, e2, . . . , en}, 
 * a document D, a similarity function, and a threshold: 
 * find all “similar” pairs <s, ei> with respect to the given function and threshold, 
 * where s is a substring of D and ei ∈ E. 
 * Note, that this notion assumes that the document is just considered as a simple text string and 
 * thus does not require that it is pre-processed, e.g., by means of a tokenizer.
 * 
 * Here: 
 * the document d is specified as a query.
 * 
 * NOTE:
 * The difference to NemexA:
 * NemexA assumes that the query is a candidate entries, so it does not consider substrings.
 * 
 * Run NemexF on each sentence (line) of each text file of Amplexor corpus
 */

public class TrieDirectoryRunner {
	
	private int fileCnt = 0;

	public String inDir = "/local/data/AmplexorData/EMA_EPAR_sentences";

	public String dictionary = "/local/data/AmplexorData/CSD_Data_Delivery_v1/Controlled_Vocabulary/entriesType-nemex.txt";
	
	public String outFile = "/local/data/AmplexorData/EMA_EPAR_trieMatches.txt";
	
	public TrieDictionaryFileReader trie = new TrieDictionaryFileReader();

	public TrieDirectoryRunner() {
		long time1;
		long time2;
		
		System.out.println("Loading dictionary ..." + dictionary);
		time1 = System.currentTimeMillis();
		
		trie.initDictWithFile(dictionary, "NO", DictType.NEMEX, false);
		trie.readFromWordListDictionary();
		
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2 - time1));
	}
	
	public TrieDirectoryRunner(String inDir, String dictionary) {
		long time1;
		long time2;
		
		this.inDir = inDir;
		
		System.out.println("Loading dictionary ..." + dictionary);
		time1 = System.currentTimeMillis();
		
		trie.initDictWithFile(dictionary, "NO", DictType.NEMEX, false);
		trie.readFromWordListDictionary();
		
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2 - time1));
	}
	
	private TreeMap<Integer,TreeMap<Integer,String>> queryWithTrie(String dictionary2, String line) {
		return trie.getTrie().getSubstringLongestMatch(line, false, false);	
		
	}
	
	/*
	 * Given the output of the trie matcher which is a map of start and (end type) entries,
	 * write out all found entries for each line in the form:
	 * <line> "the sentence"
	 * (<substring:type>* each line with a found substring start,end and its type or NOTHING
	 * newline
	 */
	private void outputFoundEntries(TreeMap<Integer,TreeMap<Integer,String>> foundEntries,
			String line, BufferedWriter outStream) throws IOException {
		outStream.write(line); outStream.newLine();
		for (Map.Entry<Integer, TreeMap<Integer,String>> entry : foundEntries.entrySet()) {
			Integer start = entry.getKey();
			// Always only one value ?
			for (Map.Entry<Integer, String> value : entry.getValue().entrySet()) {
				Integer end = value.getKey();
				String type = value.getValue();
				outStream.write(line.substring(start, end));
				outStream.write(":"+type);
				outStream.newLine();
			}
		}	
		outStream.newLine();
	}

	public void withCorpusFileQueryTrie(File filename, BufferedWriter writer) throws IOException {

		String line = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		long time1;
		long time2;
		time1 = System.currentTimeMillis();
		System.out.println("Processing file " + this.fileCnt++ + ": " + filename.getName());

		while ((line = reader.readLine()) != null) {
			
			outputFoundEntries(queryWithTrie(dictionary, line), line, writer);
		}

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2 - time1));

		reader.close();
		
	}

	
	
	public void processAmpCorpusDir(String inDir, String outfilename) throws IOException {
		File path = new File(inDir);
		File[] files = path.listFiles();
		
		File outFile = new File(outfilename);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
		
		long time1;
		long time2;
		time1 = System.currentTimeMillis();
		System.out.println("Processing corpus: " + inDir);
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				this.withCorpusFileQueryTrie(files[i], writer);
			}
		}
		writer.close();
		time2 = System.currentTimeMillis();
		System.out.println("System time (msec) whole corpus: " + (time2 - time1));
	}

	public static void main(String[] args) throws IOException {

		TrieDirectoryRunner testRun = new TrieDirectoryRunner();
		
		int[] counts = testRun.trie.getTrie().countNodesEntries();
		System.out.println("Nodes " + counts[0] + "; Entries " + counts[1]);
		

		testRun.processAmpCorpusDir(testRun.inDir, testRun.outFile);

	}

}
