# some "constants"
MAX_GENERATIONS <-999

CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION")

FIELDS_TO_CHECK_FOR_NORMALITY <- CSV_FIELDS_TO_GRAB[-1] # just skip generations


###### PROGRAM STARTS HERE

directory <- "/Users/sadat/richness_data/data/rich_broad_bi"
csvFiles <- list.files(directory,pattern="*perf_dat.csv")

#aggregateData will contain the data from ALL csv files.
aggregateData <- data.frame()

#for(i in 1:length(csvFiles)){
for(i in 1:10){
	csvFile <- paste(directory,csvFiles[i],sep="/") # concats
	csvFileData <- read.csv(csvFile, header=TRUE)
 
  	# only gather data if there is at least MAX_GENERATIONS amount of rows
  	if(nrow(csvFileData) >= MAX_GENERATIONS) { 
  		csvFileData <- csvFileData[1:MAX_GENERATIONS,] # we trim off excess rows
	 	fileData <- data.frame(EXPERIMENT=i,csvFileData[CSV_FIELDS_TO_GRAB])
    	aggregateData <- rbind(aggregateData,fileData)    
		print(paste("done appending...",csvFile))

  	}
}
# aggregateData now contains a gigantic table of experiment x generation values
# we can check for the normality of the fields we're interested in

summaryData <- data.frame()
for(i in 1:1) {
	numDataCols <- ncol(aggregateData)
	generationData <- aggregateData[aggregateData$GENERATION==i,3:numDataCols]
	meanData <- mean(generationData$TOT_FITNESS_MEAN)
	print(generationData$TOT_FITNESS_MEAN)
	print(summary(generationData$TOT_FITNESS_MEAN))
}

