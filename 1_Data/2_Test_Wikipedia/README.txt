Description for each of the file:
1_MovieNames - Contains names and Wikipedia URLs for movies used in test set.

2_TestData_for_Judgement - Contains crawled and isolated sentences from Wikipedia for test movies. This set was given further for crowd judgement.

3_CrowdJudged_Raw - Actual file we obtained from crowd sourcing platform. It contains judgement by each individual judge.

4_CrowdJudged_Processed_AllSentences - File obtained after parsing the raw file, to obtain the required values. In this file all the Wikipedia sentences are included - even the ones with missing context.

5_CrowdJudged_Processed_CandidateSelected - Contains the crowd judgement over the candidates selected using our candidate selection module.

6_Final_featurized_test_set - We extracted the features and put each of them as a column. NOTE that these columns are not directly put to ranker, but our ranking code (written in R), prepares the final columnized features as required by ranker.