All the experiments have been done in Windows 7. Version of R is 3.1.2
Folder 'rankTemp' is empty initially, but required to store intermediate files produced dureing execution.

Dependency binaries (already present in folder):
1. svm_rank_learn.exe
2. svm_rank_classify.exe
3. svmLight_FormatWriter.class

Please follow the given steps to replicate the results
1. Install R.
2. Within R install two packages i) tm ii) RTextTools using the commands install.packages("tm") and install.packages("RTextTools") respectively.
3. Execute code.R

Output:
1. P@10 will be shown on screen.
2. Actual result files "result_all.txt" and "result_top10.txt" will be created in same folder.
3. "rankTemp" will contain all the intermediate files and the model file.