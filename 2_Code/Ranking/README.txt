Description for each of the files:
code.R - It does the following tasks:
       - extracts the unigram features
       - columnizes the extracted linguistic and entity features in columnizes it in the format required by ranker.

Get_featureWeights.java - Extracts the weights assigned to each of the feature from the model file(generated in 'rankTemp' folder) and the feature to id map (File name 'featureMap.txt' generated in 'rankTemp' folder). Feature to id map is created and stored in a temporary file by the below mentioned svmLight_FormatWriter.

svm_rank_classify - Taken from http://svmlight.joachims.org/ See details at given link.

svm_rank_learn - Taken from http://svmlight.joachims.org/ See details at given link.

svmLight_FormatWriter.java - converts the columnized feature matrix in svmLight format. Refer: http://svmlight.joachims.org/ for more details.

