# gnTextUtils
a useful set of text-oriented java utils

## SimpleSegmentizer

- performs already quite good

- it checks whether a token is a candidate abbreviation

- if so sets a flag

- then if a sentence boundary is recognized checks whether above flag is not set

- does not work always, but already usefull

- can be improved with help of a language model

### Evaluation

- currently it is reasonable fast:

		running a DE cluebweb file: 33974 WarcRecords; resulting text file: 12M tokens, 700K sentences 
		without sentence boundary: 							  110921 msec
		and with tokenization and sentence boundary: 		  119478 msec			 
		
		

- for some text, run it with MorphAdorner

- measure time 

- measure difference

## Deployment

- eventually use it in NEMEX

- use in GNT

## Testing TrieDirectoryRunner with Amplexor data:

- takes about 17 seconds to run whole corpus

- next write out found entries per file

- Abgleichen mit W. Daten

- more tests with trie

- check isValidEntry also numbers

- handle prober treatment of lower case 

- add edit distance function

## Implement TASTE like approach:

-	store entries in form of partitions taking into account ED threshold as parameter

-	store each entry which shares a partition (similar to ngram index in Faerie)

-	for document, get all possible matches

-	now find optimal chains of matches that are similar under given ED

-	this would be done by extending the found segments to find similar entries