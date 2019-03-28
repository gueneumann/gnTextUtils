package com.gn.trie;

import com.gn.trie.Trie;
import com.gn.trie.TrieDictionaryFileReader.DictType;

public class TrieRunner {

	public static void test1(String[] args) {
		// test string content
		Trie<String> t = new Trie<String>(true);
		t.addEntry("Peter Müller", "PER1");
		System.out.println(t.getEntry("Peter Müller").getContent());
		t.addEntry("Peter", "PER2");
		System.out.println(t.getEntry("Peter").getContent());
		t.addEntry("Berlin", "LOC1");
		t.addEntry("Barlin", "LOC2");

		// test removing
		int[] counts = t.countNodesEntries();
		System.out.println("Nodes " + counts[0] + "; Entries " + counts[1]);
		t.removeEntry("hallo");
		counts = t.countNodesEntries();
		System.out.println("Nodes " + counts[0] + "; Entries " + counts[1]);

		// destructively change content of trie node if index
		Trie<String> tt = t.getEntry("Peter");
		tt.setContent(tt.getContent()+"EXT");
		System.out.println(t.getEntry("Peter").getContent());
		System.out.println(t.getEntry("Peter Müller").getContent());

		// test get prefix entries and get substring

		System.out.println(t.getPrefixEntries("Minister Peter Müller wohn in Berlin.").toString());
		System.out.println(t.getSubstringLongestMatch(
				"Der ehemalige saarländische Minister Peter Müller wohn in Berlin und dann in Barlin, aber nicht in Dudweiler. "
						+ "Dann aber ist Peter doch nach Berlin gezogen. Der ehemalige saarländische Minister "
						+ "Peter Müller wohn in Berlin und dann in Barlin, aber nicht in Dudweiler.", false, false).toString());

	}

	// test non-overwriting
	public static void test2(String[] args) {
		// test string content
		Trie<String> t = new Trie<String>(false);
		t.addEntry("Peter Müller", "PER1");
		System.out.println(t.getEntry("Peter Müller").getContent());
		t.addEntry("Peter", "PER2");
		System.out.println(t.getEntry("Peter").getContent());
		t.addEntry("Peter Müller", "PER3");
		System.out.println(t.getEntry("Peter Müller").getContent());


		System.out.println(t.getPrefixEntries("Minister Peter Müller wohn in Berlin.").toString());
		System.out.println(t.getSubstringLongestMatch(
				"Der ehemalige saarländische Minister Peter Müller wohn in Berlin und dann in Barlin, aber nicht in Dudweiler. "
						+ "Dann aber ist Peter doch nach Berlin gezogen. Der ehemalige saarländische Minister "
						+ "Peter Müller wohn in Berlin und dann in Barlin, aber nicht in Dudweiler.", false, false).toString());

	}

	public static void test3(String[] args) {

		TrieDictionaryFileReader dict = new TrieDictionaryFileReader();

		dict.initDictWithFile(
				//"/Users/gune00/data/NE-Lists/wiktionaryFemaleFirstNames.txt", 
				"/Users/gune00/data/NE-Lists/wikiPedia/WikiPeople.lst",
				"PER", 
				DictType.SIMPLE,
				// tokenize MWL entry ?
				false
				);

		long time1 = System.currentTimeMillis();

		dict.readFromWordListDictionary();

		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println(dict.getTrie().toString());

		time1 = System.currentTimeMillis();

		dict.addDictWithFile(
				"/Users/gune00/data/NE-Lists/nemex/nemexTest.txt",
				"NO", 
				DictType.NEMEX,
				// tokenize MWL entry ?
				false
				);
		dict.readFromWordListDictionary();

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println(dict.getTrie().toString());

		time1 = System.currentTimeMillis();

		dict.getTrie().getSubstringLongestMatch(
				"Günter Neumann works at DFKI with Josef and all others.", 
				false,
				false);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
	}

	public static void test4(String[] args) {

		TrieDictionaryFileReader dict = new TrieDictionaryFileReader();

		dict.initDictWithFile(
				//"/Users/gune00/dfki/workspaceKepler/nemexa/src/main/webapp/resources/MedicalTerms-mwl-plain.txt",
				"/Users/gune00/dfki/workspaceKepler/nemex/src/main/webapp/resources/GeneLexicon-mwl-plain.txt",
				"NO", 
				DictType.NEMEX,
				false
				);

		long time1 = System.currentTimeMillis();

		dict.readFromWordListDictionary();

		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));


		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println(dict.getTrie().toString());

		time1 = System.currentTimeMillis();

		System.out.println(dict.getTrie().getSubstringLongestMatch(
				"The population of Sardinia is characterized by a relatively low level of genetic heterogeneity : therefore 'founder mutations ' "
						+ "can be expected to be found ."
						+ "We analysed 17 probands from families with high incidence of breast cancer or breast and ovarian cancer by sequencing the "
						+ "full-length coding regions of brca 1 and BRCA2 genes . A novel BRCA2 frameshift mutation, 3951del3insAT , which produces a "
						+ "protein truncated at codon 1258 , was observed in six patients with BC from the same village  The mutation was not found in unaffected females "
						+ "( matched on basis of ethnicity and age ) with no family history of cancer ."
						+ "Haplotype analysis strongly suggests that all affected persons had a common ancestor ."
						+ "The identification of this clinically significant founder mutation may facilitate screening/testing for inherited risk of breast cancer ."
						+ "Recent genomic projects reveal that about half of the gene repertoire in plant genomes is made up by multigene families ."
						+ "In this paper , a set of structural and phylogenetic analyses have been applied to compare the differently sized nicotianamine synthase "
						+ "( NAS ) gene families in barley and rice ."
						+ "Nicotianamine acts as a chelator of iron and other heavy metals and plays a key role in uptake , phloem transport and cytoplasmic "
						+ "distribution of iron , challenging efforts for the breeding of iron-efficient crop plants ."
						+ "Nine barley NAS genes have been mapped , and co-linearity of flanking genes in barley and rice was determined ."
						+ "The combined analyses reveal that the NAS multigene family members in barley originated through at least "
						+ "one duplication event that occurred before the divergence of rice and barley ."
						+ "Additional duplications appear to have occurred within each of the species .", 
						false,
						false)
				);

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
	}

	public static void test5(String[] args) {

		TrieDictionaryFileReader dict = new TrieDictionaryFileReader();

		dict.initDictWithFile(
				"/Users/gune00/data/aquaint/aquaint-nyt-plain.txt",
				"WORD", 
				DictType.SIMPLE,
				// tokenize MWL entry ?
				true
				);

		long time1 = System.currentTimeMillis();

		dict.readFromWordListDictionary();

		long time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));

		System.out.println(dict.getTrie().toString());

		time1 = System.currentTimeMillis();

		System.out.println(dict.getTrie().getSubstringLongestMatch(
				"It also helps to have a player like freshman LaToya Thomas, who can deliver points in bunches when needed.", 
				false,
				false));

		time2 = System.currentTimeMillis();
		System.out.println("System time (msec): " + (time2-time1));
	}

	public static void main(String[] args) {
		TrieRunner.test4(args);
	}
}
