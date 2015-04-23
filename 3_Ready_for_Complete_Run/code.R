library(tm)
library(RTextTools)

TRAIN_DATA_FILE_NAME <- "train_IMDB.txt";
TEST_DATA_FILE_NAME <- "test_Wikipedia.txt";

#reading and selecting columns in train set
train_validate_data <- read.csv(TRAIN_DATA_FILE_NAME, sep='\t', header=T)

# removing non required columns
train_validate_data$INTERESTED <- NULL
train_validate_data$VOTED <- NULL
train_validate_data$LIKENESS_RATIO <- NULL

# in case of name difference, name change of the movie column
names(train_validate_data)[names(train_validate_data) == 'MOVIE_NAME_IMDB'] <- 'MOVIE'

# tracking train_validate_data
train_validate_rows <- nrow(train_validate_data)

# HACK PART: add the unseen test part also
test_data <- read.csv(TEST_DATA_FILE_NAME, header = T, sep = '\t')
test_data$count_boring <- NULL
test_data$count_interesting <- NULL
test_data$count_veryInteresting <- NULL
test_data$BASE_superPOS <- NULL

combined_data <- rbind(train_validate_data[,!(colnames(train_validate_data) == "Movie.Roll.Num")], test_data)

combined_trivia <- combined_data["TRIVIA"]
combined_codes <- combined_data["GRADE"]

# Unigram words: combined for train, validate and test
combined_matrix <- as.matrix(create_matrix(combined_trivia, language = "english", stripWhitespace = TRUE, removeNumbers=FALSE, stemWords=TRUE, toLower = TRUE, removePunctuation=TRUE, removeStopwords = TRUE, weighting=weightTfIdf))

# parse tree features: combined for train, validate and test
root_matrix <- create_matrix(combined_data["ROOT_WORDS"], removePunctuation = FALSE, removeStopwords = FALSE, weighting = weightTf)
subject_matrix <- create_matrix(combined_data["SUBJECT_WORDS"], removePunctuation = FALSE, removeStopwords = FALSE, weighting = weightTf)
under_root_matrix <- create_matrix(combined_data["UNDER_ROOT_WORDS"], removePunctuation = FALSE, removeStopwords = FALSE, weighting = weightTf)
all_linked_entities_matrix <- create_matrix(combined_data["ALL_LINKABLE_ENTITIES_PRESENT"], removePunctuation = FALSE, removeStopwords = FALSE, weighting = weightTf)
parse_features_matrix <- cbind(as.matrix(all_linked_entities_matrix), as.matrix(root_matrix), as.matrix(subject_matrix), as.matrix(under_root_matrix))

combined_matrix <- cbind(as.matrix(combined_matrix), as.matrix(parse_features_matrix))

# + frequency of superlative POS and comparative POS as features
combined_matrix <- cbind(combined_matrix, as.matrix(combined_data["superPOS"]))

# + frequency of different NERs
combined_matrix <- cbind(combined_matrix, as.matrix(combined_data[,c("MOVIE","PERSON","ORGANIZATION","DATE","LOCATION","MONEY","TIME", "FOG", "Contradict")]))

addedFeatures <- c("PERSON","ORGANIZATION","DATE","LOCATION","MONEY","TIME","superPOS", "Contradict")

# converting frequencies to boolean presence
for(col in addedFeatures)
{
  index <- combined_matrix[,col] > 0
  combined_matrix[index,col] <- 1
}

index <- (combined_matrix[,"FOG"] < 7)
combined_matrix[index, "FOG"] <- as.factor(1)

index <- (combined_matrix[,"FOG"] >= 7)
combined_matrix[index,"FOG"] <- as.factor(2)

index <- (combined_matrix[,"FOG"] >= 15)
combined_matrix[index,"FOG"] <- as.factor(3)

rm(col, index)

# tracking breakpoint
test_start <- train_validate_rows+1
combined_rows <- nrow(combined_matrix)

# splitting combined_matrix
train_validate_matrix <- data.frame(combined_matrix[1:train_validate_rows,])
test_matrix <- data.frame(combined_matrix[test_start:combined_rows,])

# splitting combined_codes
train_validate_codes <- combined_codes[1:train_validate_rows,]
test_codes <- combined_codes[test_start:combined_rows,]

# matrix for all known result rows
comMAT <- data.frame(cbind(train_validate_matrix, train_validate_codes))
#rm(combined_data, combined_codes, combined_matrix, combined_rows, test_codes, test_start, train_validate_matrix)

# Cross validating within known result set -----------------
num_times = 5;
num_validate = length(unique(train_validate_data$Movie.Roll.Num))/num_times

total_ndcg_5_over_all_movies_all_validation_set <- 0
total_ndcg_10_over_all_movies_all_validation_set <- 0
total_precision_10_all_movies_all_validation_set <- 0

n5 <- NULL
n10 <- NULL
p10 <- NULL

# Final prediction on unseen test -------------------------
#writing features in table format
write.table(comMAT, "rankTemp/all_train_features.txt", sep = '\t', quote = F, row.names=F)
write.table(test_matrix, "rankTemp/test_features.txt", sep = '\t', quote = F, row.names=F)

# converting to svm light format
system('java svmLight_FormatWriter rankTemp/test_features.txt rankTemp/test_features_svmLight.txt rankTemp/f1.txt');
system('java svmLight_FormatWriter rankTemp/all_train_features.txt rankTemp/all_train_features_svmLight.txt rankTemp/featureMap.txt');

# creating model with all available data
system('./svm_rank_learn.exe -c 17 -e 0.21 rankTemp/all_train_features_svmLight.txt rankTemp/model_all_train_rank_0_4_IMDb')

# predict on test set
system('./svm_rank_classify.exe rankTemp/test_features_svmLight.txt rankTemp/model_all_train_rank_0_4_IMDb rankTemp/test_predicted_rank_0_4.txt')

# generate result file for test set
test_file <- read.csv(TEST_DATA_FILE_NAME, sep = '\t', header = TRUE)
predicted_test <- read.csv("rankTemp/test_predicted_rank_0_4.txt", sep = '\t', header = FALSE)
result_all <- cbind(test_file, predicted_test)

# writing all the sentences in test set
write.table(result_all, "result_all.txt", sep = '\t', row.names = F, quote = F)

# getting top 10 from each
sorted_result <- result_all[order(-result_all$V1),]
movie_result <- split(sorted_result, sorted_result$MOVIE)

top10Result <- NULL
total_correct_in_10 <- 0
for(i in 1:length(movie_result))
{
  top10Result <- rbind(data.frame(top10Result), data.frame(head(movie_result[[i]], 10)))
  thisMovie <- data.frame(head(movie_result[[i]], 10))
  correct_in_10 <- sum(thisMovie$CLASS)
  total_correct_in_10 <- total_correct_in_10 + correct_in_10
}
precision_in_10 <- total_correct_in_10/length(unique(sorted_result$MOVIE))
cat("TEST p@10 : ", precision_in_10);

# writing result file
write.table(top10Result, "result_top10.txt", sep='\t',row.names=F)