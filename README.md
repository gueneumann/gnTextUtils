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

- eventually use it in NEMEX