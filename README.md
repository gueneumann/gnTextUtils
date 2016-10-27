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

- for some text, run it with MorphAdorner

- measure time 

- measure difference

- eventually use it in NEMEX