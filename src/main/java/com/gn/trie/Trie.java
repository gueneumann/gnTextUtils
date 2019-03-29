package com.gn.trie;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

//Define:
// node-count; OK

// deleteEntry: OK
// - delete not just the entry but also unnecessary branches
// - should be able to use parent slot for this to go up a single path if its entry has been deleted

// getSuffixEntry(index trie) : returns the tree of the last element of the specified index -> NOT YET NECESSARY

// getPrefixEntries(index trie): -> OK
// - return entries for all found prefixes of index up to the longest matching prefix 
// - in form of (length(prefix), entry(prefix))

// tries.getSubstringEntries(index): return entries for all substrings; -> OK
// - works already in robust mode, which means that prefixes need not be adjacent
// - THUS: also provide strategies for robust match, which might be similar to edit distance ...

// edit distance search

// updateContent
// assuming an entry can have multiple readings, I need to update
// but I will clarify this later



/**
 * The class implements the Trie data type. Code is based on my dtree.lisp implementation
 */
public class Trie<E> extends AbstractSet<CharSequence> {

	/** Holds the children */
	private TreeMap<Character, Trie<E>> children = new TreeMap<Character, Trie<E>>();

	/** true if this is a word */
	private boolean isWord = false;

	/** number of elements */
	private int size=0;

	/** maps to parent*/
	/** realizes a bidirectional pointer from bottom to top */
	/** so far only used in removeEntry(); uses additional memory */
	private Trie<E> parent;

	/** an object carrying the content of a word */
	private E content;

	/** true if existing word's content should be overwritten with new content*/
	private boolean overwrite = true;

	// Getters and setters

	/**
	 * @return the children
	 */
	public TreeMap<Character, Trie<E>> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(TreeMap<Character, Trie<E>> children) {
		this.children = children;
	}
	/**
	 * @return the isWord
	 */
	public boolean isWord() {
		return isWord;
	}
	/**
	 * @param isWord the isWord to set
	 */
	public void setWord(boolean isWord) {
		this.isWord = isWord;
	}

	/**
	 * @return the parent
	 */
	public Trie<E> getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Trie<E> parent) {
		this.parent = parent;
	}
	/**
	 * @return the content
	 */
	public E getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(E content) {
		this.content = content;
	}

	/**
	 * @return the overwrite
	 */
	public boolean isOverwrite() {
		return overwrite;
	}
	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite (boolean overwrite) {
		this.overwrite = overwrite;
	}

	// Initialization

	/** Constructs a Trie*/
	public Trie() {

	}

	public Trie(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/** Constructs a Trie*/
	public Trie(Trie<E> p) {
		parent=p;
	}

	// Methods

	@Override
	public void clear() {
		children.clear();
		isWord=false;
		size=0;
	}

	@Override
	public boolean isEmpty() {
		return(size==0);
	}

	@Override
	public Iterator<CharSequence> iterator() {
		return null;
	}

	@Override
	public String toString() {
		return "Trie with "+size()+" elements and "+children.size()+" children";
	}
	@Override
	public int size() {
		return (size);
	}

	public void updateContent (E newContent) {
		this.setContent(newContent);
	}

	/** Main caller for adding an entry into the trie. Index is a char sequence and 
	 * content is an instance of the specified class of type E
	 * @param s
	 * @param content
	 * @return
	 */
	public boolean addEntry(CharSequence s, E content) {
		boolean a=add(s, 0, content);
		// if entry could be added then increase the trie's size
		if (a) size++;
		return (a);
	}

	/** Adds a sequence starting from start position */
	private boolean add(CharSequence s, int start, E content) {
		if (s.length() == start) {
			// path exists
			if (isWord) {
				// overwrite content if required
				if (overwrite) {updateContent(content);}
				// signal that no new entry/path has been added
				return (false);}
			// else add new entry and content
			isWord = true;
			setContent(content);
			return (true);
		}
		// traverse or build new path
		Character c = s.charAt(start);
		if (children.get(c) == null)
			// else create new trie node for current child with THIS as parent
			children.put(c, new Trie<E>(this));
		return (children.get(c).add(s, start + 1, content));
	}

	@Override
	public boolean contains(Object s) {
		return (s instanceof CharSequence && containsCS((CharSequence) s, 0));
	}

	/** TRUE if the trie contains the sequence from start position on */
	protected boolean containsCS(CharSequence cs, int start) {
		if (cs.length() == start)
			return (isWord);
		Character c = cs.charAt(start);
		if (children.get(c) == null)
			return (false);
		return (children.get(c).containsCS(cs, start + 1));
	}

	/** look up entry and if successful return content; if not return NULL */
	public Trie<E> getEntry(CharSequence s){
		return getCS(s, 0);
	}

	protected Trie<E> getCS(CharSequence cs, int start) {
		//System.err.println(start);
		if (cs.length() == start) {
			// If path exists with value return trie node, else NULL
			if (isWord) {return this;} else {return null;}
		}		
		Character c = cs.charAt(start);
		if (children.get(c) == null)
			return (null);
		return (children.get(c).getCS(cs, start + 1));
	}

	/**
	 * Returns the length of the longest contained subsequence, starting from
	 * start position
	 */
	public int containedLength(CharSequence s, int startPos) {
		if (isWord)
			return (0);
		if(s.length()<=startPos) return(-1);
		Character c = s.charAt(startPos);
		if (children.get(c) == null)
			return (-1);
		int subtreelength = children.get(c).containedLength(s, startPos + 1);
		if (subtreelength == -1)
			return (-1);
		return (subtreelength + 1);
	}

	/** Counts all nodes and entries of trie and store the values in a 2-element integer vector count */
	public int[] countNodesEntries (){
		// I do not count the root node
		// just its child nodes recursively
		return countNodesEntriesCS(this.getChildren(), new int[]{0,0});
	}

	/** Recursively traverse the children of a node and count the nodes and entries */
	private int[] countNodesEntriesCS (TreeMap<Character, Trie<E>> children, int[] counts){
		Iterator<Entry<Character, Trie<E>>> it = children.entrySet().iterator();
		while (it.hasNext()) {
			// For each child node (if there is any)
			// Bound the entry, so that it can be used several times
			Map.Entry<Character, Trie<E>> entry = it.next();
			// Count the node
			counts[0]++;
			// If node has an entry count this as well 
			if (entry.getValue().isWord) counts[1]++;
			// Recursion over children of child node
			countNodesEntriesCS(entry.getValue().getChildren(),counts);
		};
		return counts;
	}

	/** print path from bottom to root
	 * get entry and then traverse its parent link */
	public void printChildParents(CharSequence s){
		Trie<E> lowestNode = getEntry(s);
		if (lowestNode != null) traverseParents(lowestNode);
		else System.out.println("No entry for " + s);
	}

	public void traverseParents (Trie<E> trie){
		System.out.println("get up");
		if (trie.getChildren() != null) System.out.println(trie.getChildren().toString());
		if (trie.getParent()==null) {System.out.println("reached root!");}
		else {
			System.out.println(trie.isWord());
			traverseParents(trie.getParent());
		}
	}

	/** removing of entries. It uses the parent pointer to be able to upward the trie from the 
	 * position of the found entry that is to be to removed. returns true if entry is removed and
	 * false if not.*/
	public boolean removeEntry(CharSequence s){
		// No entry exists that could be removed
		if (!this.contains(s)) return false;
		else
			// get the entry and remove it and eventually its parents
		{
			Trie<E> lowestNode = getEntry(s);
			// reset its content and isWord boolean feature
			lowestNode.setWord(false);
			lowestNode.setContent(null);
			// then check whether its complete node and its parents should be deleted
			if (lowestNode.getChildren().isEmpty()) removeFromParent(lowestNode);
			return true;
		}
	}

	/** This is the current place where only parent is actually is used.*/
	//TODO check whether this parent is really needed, because it needs more space
	private void removeFromParent(Trie<E> childNode) {
		// get parent node
		Trie<E> parentNode = childNode.getParent();
		Iterator<Entry<Character, Trie<E>>> it = parentNode.getChildren().entrySet().iterator();
		outerloop:
			while (it.hasNext()) {
				Map.Entry<Character, Trie<E>> entry = it.next();
				// find entry whose value is a reference to childNode
				// it is guaranteed to exist but I do not know the position/field
				if (entry.getValue() == childNode) 
					// if entry is found then delete it and break loop
				{parentNode.getChildren().remove(entry.getKey()); break outerloop;}
			};
			// if parent node has no more children delete it from its parent
			// but only if it is not a word
			if ((parentNode.getChildren().isEmpty()) && !parentNode.isWord()) 
				removeFromParent(parentNode);

	}

	/** getPrefixEntries(index): traverses an index from left starting by 0 and determines the longest matching prefix p for which it finds
	 * and entry, as well as for all matching sub-prefixes of p. It returns a sorted tree map of pairs of <end position,content>
	 */
	public TreeMap<Integer,E> getPrefixEntries(CharSequence s){
		return getPrefixEntriesIter(s, this, new TreeMap<Integer,E>());
	}

	/** The working horse: it processes the characters of index from left to right 
	 * until a character is found that breaks the prefix. In the meanwhile it collects all entry and the position they were found,
	 * @param cs:  the index
	 * @param end:  current position in trie
	 * @param prefixEntries: a sorted map of end+content pairs
	 * @return
	 */

	protected TreeMap<Integer,E> getPrefixEntriesIter(CharSequence cs, Trie<E> node, TreeMap<Integer,E> prefixEntries) {
		for (int i = 0; i < cs.length(); i++)
			// Loop through input string
		{
			// if current node is a word, then add it
			if (node.isWord) prefixEntries.put(i, node.getContent());

			Character c = cs.charAt(i);
			// get next char
			// if there is no, simply stop
			if ((node.children.get(c) == null)) break;
			// else traverse to next node
			node = node.children.get(c);
		}
		return prefixEntries;
	}



	/** <p>Call getPrefixEntries on suffixes.
	 * It returns pairs <startPos, content> of each found  prefix in index. 
	 * This means: concatenation of all resulting entries in the given order leads to a substring of index</p>
	 * <p>Implemented Strategy: longest match adjacent prefixes </p>
	 * <p>Other possibilities:</p>
	 * <p>exhaustive</p>
	 * <p>N-longest</p>
	 * <p>N-random</p>
	 * <p> The idea behind the parameter chain is: If set to true, then we require that found matching entries are adjacent that is not
	 * interrupted by some non matching character sequence. 
	 * So, using this strategy might be useful for finding THE highest ranked  matching prefix of s. 
	 * NOTE: currently the highest ranked entry is the longest matching entry, BUT we could also return the best one based on some
	 * dictionary statistics or other tests.
	 * So it means we segmentize s into prefix and suffix, where prefix is a matching entry.
	 * (Actually this method is more usefull for morphological processing.)
	 * If it is set to false, we actually segmentize s into affixes based on matching substrings, where a matching substring is a 
	 * best matching prefix. an entry.
	 * @param s: the index string
	 * @param chain: if true consider only chain of adjacent prefixes else ignore non-matching substrings;
	 * For example if string is abcd, and entries are ab, b, d, then if chain=true, return ab, else return ab and d
	 * * @param exhaustive: if true consider all matching substrings;
	 * For example if string is abcd, and entries are ab, b then if exhaustive=true, return ab, b, else ab (if chain=false)
	 * NOTE: exhaustive just makes sure we get all matching SUFFIXES; to get all matching PREFIXES, we need to process all
	 * prefixes found by getPrefixEntries()
	 * @return a dynamic tupe structure of (start,end) with entry content as value
	 */

	public TreeMap<Integer,TreeMap<Integer,E>> getSubstringLongestMatch(CharSequence s, boolean chain, boolean exhaustive){
		
		return getSubstringBestMatchIter(s, new TreeMap<Integer,TreeMap<Integer,E>>(), chain, exhaustive);
	}

	public TreeMap<Integer,TreeMap<Integer,E>> getSubstringBestMatchIter(CharSequence s, TreeMap<Integer,TreeMap<Integer,E>> prefixEntries, 
			boolean chain, boolean exhaustive){
		outer:
			for (int i=0; i < s.length(); i++) {
				// last element as longest span
				Map.Entry<Integer, E> longestEntry = getPrefixEntries(s.subSequence(i, s.length())).lastEntry();

				//TODO
				// if exhaustive is set to true, I also should check all found prefixes, which introduces an additional iteration

				// stop if no prefix was found
				if (longestEntry == null) 
				{
					// if not robust then do not allow non-matching substrings, and return with found adjacent list of prefixes
					if (chain) break outer;
					else {
						// else do not consider character as reliable indices, and return found eventually non-chained prefixes
						if (s.length() < 2) break outer;
					}
				}
				else{
					int end = i+longestEntry.getKey();
					//					System.out.println(s.subSequence(i, end)+":["+i+","+end+"]" + ", val: " + longestEntry.getValue() + 
					//							"; isValid: " + this.isValidEntry(s, i, end));

					if (this.isValidEntry(s, i, end)){
						// System.out.println(s.subSequence(i, end)+":["+i+","+end+"]" + ", val: " + longestEntry.getValue());
						TreeMap<Integer,E> value = new TreeMap<Integer,E>();
						value.put(end, longestEntry.getValue());
						prefixEntries.put(i, value);
						// jump to position after found prefix
						// if this is not done, then I will just try the next substring after +i -> exhaustive search
						if (!exhaustive) i = end-1;
					}
				}
			}
	return prefixEntries;
	}

	// TODO:
	// consider begin of sentence and end of sentence !!
	// What about numbers etc. ?
	
	private boolean isValidEntry(CharSequence s, int start, int end){
		boolean isValid = false;
		if (start > 0 && end < s.length()) {
			if ((!Character.isAlphabetic(s.charAt(start-1)))
					&& 
					(!Character.isAlphabetic(s.charAt(end))))
				isValid = true;
		}
		else
			if (end < s.length()) {
				if ((!Character.isAlphabetic(s.charAt(end))))
					isValid = true;
			}
		return isValid;
	}
}
