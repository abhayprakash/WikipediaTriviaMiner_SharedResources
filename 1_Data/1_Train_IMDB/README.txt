Description for each of the file:
1_MovieNames - Contains list of names and IMDB URLs for the movies crawled from IMDB.

2_CrawledTrivia - Contains all the trivia crawled for the movies in above file.

3_Filtered_and_Graded - Contains only the filtered samples which have been selected for training set. Each of them contains a grade along with it.

4_Final_featurized_train_set - Extracted features for each of the sample have been put as a column. NOTE that these columns are not directly provided to ranker, but our ranking code (written in R) prepares the actual columnized features for train set.