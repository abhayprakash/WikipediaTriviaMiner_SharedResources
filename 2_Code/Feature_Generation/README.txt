Description for each of the file:
contradictory_words - Counts number of contradictory words.

EntityLinker - Parses each of the sentence to identify any resolvable entity and links it.

NLPFeatures - Extracts all the linguistic features.

readabilityIndex - calculates the FOG index as a measure of readability.

Procedure to execute:
- Make a single column for all the text, you want to featurize.
- First run NLPFeatures.java It will produce some intermediate files, each of which will have name starting with INT_
- Now, run EntityLinker.java It requires a dictionary of entity:attribute:value triplets as given in entiyLinks.txt
- Run the contradictory_words.java and readabilityIndex.java, which are independent of any other file.
- Augment all the columns, and use the file for ranking purpose.