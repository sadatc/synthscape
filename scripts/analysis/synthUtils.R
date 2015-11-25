###
### GLOBAL VARS
###
SYNTH_REPORT_PROGRESS_ITEMS <- 25


### 
### FUNCTIONS
####

main <- function() {
	

}


summarizeCSVFiles <-function(directory, filePattern, summaryFile, csvFieldsToGrab,
 	maxGenerations, numResources, maxFiles, shouldComputeE, populationSizeFilter, 
 	numSpecies ) {
 	
	print(paste("directory:",directory))
	print(paste("filePattern:",filePattern))
	print(paste("populationSizeFilter:",populationSizeFilter))
	print(paste("summaryFile:",summaryFile))

	csvFiles <- list.files(directory,pattern=filePattern)
	#print(csvFiles)

	#aggregateData will contain the data from ALL csv files.
	aggregateData <- data.frame()

	aggregatedFiles <- 0
	numOfActualFiles <- length(csvFiles)
	
	if(maxFiles == -1) {
		maxFiles = numOfActualFiles
	} else {
		if ( numOfActualFiles < maxFiles ) {
			maxFiles = numOfActualFiles
		}
	}



	print(paste("Number of CSV files to aggregate:",maxFiles))
	for(i in 1:maxFiles){
		csvFile <- paste(directory,csvFiles[i],sep="/") # concats
		csvFileData <- read.csv(csvFile, header=TRUE)
 
		# after reading in the raw data, and checking that we have records for at least
		# maxGenerations, we do some transformations:
		# a. trim off excess rows and rows with low populations
		# b. add in some computed columns regarding evenness measures
		# c. only grab the fields we are really interested in
		
		# only gather data if there is at least maxGenerations amount of rows
		if(nrow(csvFileData) >= maxGenerations) { 
			# a)  we trim off excess rows
			csvFileData <- csvFileData[1:maxGenerations,] 

			if( shouldComputeE ) {
				csvFileData$TOT_POP <- csvFileData$NUM_DETECTORS+ csvFileData$NUM_EXTRACTORS+ csvFileData$NUM_TRANSPORTERS
				csvFileData <- csvFileData[csvFileData$TOT_POP==populationSizeFilter,] 
			}

			
			# b) add in some computed columns
			csvFileData$CAPTURES_BEST_CASE_PERC <- csvFileData$CAPTURES_BEST_CASE/numResources

			if( shouldComputeE ) {
				P1 <- csvFileData$NUM_DETECTORS/csvFileData$TOT_POP
				P2 <- csvFileData$NUM_EXTRACTORS/csvFileData$TOT_POP
				P3 <- csvFileData$NUM_TRANSPORTERS/csvFileData$TOT_POP
				H <- -((P1*log(P1)) + (P2*log(P2)) + (P3*log(P3)) )
				csvFileData$E <- H/log(numSpecies)
			}

			
			fileData <- data.frame(EXPERIMENT=i,csvFileData[csvFieldsToGrab])
			aggregateData <- rbind(aggregateData,fileData)    
			if( i%% SYNTH_REPORT_PROGRESS_ITEMS == 0 ) {
				print(paste("done aggregating data from: ", i, " files..."))
			}
			aggregatedFiles <- aggregatedFiles + 1 
		}
	}
	print(paste("*** number of experiments aggregated:",aggregatedFiles))

	summaryData <- data.frame()

	print("averaging generational data...")
	for(generation in 1:maxGenerations) {
		
		numDataCols <- ncol(aggregateData)

		# pick the observations: they are from col 3 onwards
		# col 1 = experiment#
		# col 2 = generation# 
		# no point in averaging these!
		observations <- aggregateData[aggregateData$GENERATION==generation,3:numDataCols]
		
		# 1) check normality 
		# print(shapiro.test(observations$E))


		# find the means of these values
		meanObservations <-lapply(observations,mean, na.rm=TRUE)
	
		generationSummary <- data.frame(GENERATION=generation,meanObservations)
		summaryData <-rbind(summaryData,generationSummary)
	}
	#return(summaryData)
	write.csv(summaryData,file=summaryFile,row.names=F)
}


analyzeCSVFiles <-function(directory, filePattern, csvFieldsToGrab, maxFiles, 
	maxGenerations) {
 	
	print(paste("directory:",directory))
	print(paste("filePattern:",filePattern))

	csvFiles <- list.files(directory,pattern=filePattern)

	aggregateData <- data.frame()

	aggregatedFiles <- 0
	numOfActualFiles <- length(csvFiles)
	
	if(maxFiles == -1) {
		maxFiles = numOfActualFiles
	} else {
		if ( numOfActualFiles < maxFiles ) {
			maxFiles = numOfActualFiles
		}
	}

	print(paste("Number of CSV files to aggregate:",maxFiles))
	for(i in 1:maxFiles){
		csvFile <- paste(directory,csvFiles[i],sep="/") # concats
		csvFileData <- read.csv(csvFile, header=TRUE)
 
		# after reading in the raw data, and checking that we have records for at least
		# maxGenerations, we do some transformations:
		# a. trim off excess rows and rows with low populations
		# b. add in some computed columns regarding evenness measures
		# c. only grab the fields we are really interested in
		
		# only gather data if there is at least maxGenerations amount of rows
		if(nrow(csvFileData) >= maxGenerations) { 
			# a)  we trim off excess rows
			csvFileData <- csvFileData[1:maxGenerations,] 

			fileData <- data.frame(EXPERIMENT=i,csvFileData[csvFieldsToGrab])

			aggregateData <- rbind(aggregateData,fileData)    
			if( i%% SYNTH_REPORT_PROGRESS_ITEMS == 0 ) {
				print(paste("done aggregating data from: ", i, " files..."))
			}
			aggregatedFiles <- aggregatedFiles + 1 

		}
	}
	print(paste("*** number of experiments aggregated:",aggregatedFiles))

	summaryData <- data.frame()

	print("analyzing generational data...")

	numDataCols <- ncol(aggregateData)
	
	generation <- 800
#	observations <- aggregateData[aggregateData$GENERATION==generation,3:numDataCols]

	observations <- aggregateData[aggregateData$CAPTURES_TOTAL>=10,3:numDataCols]


	pdf("/tmp/histograms.pdf")
	print(
		hist(observations$CAPTURES_TOTAL)
		
				
	)
	print(
		hist(observations$NUM_DETECTORS)
	)

	print(
		hist(observations$NUM_EXTRACTORS)
	)
	
	print(
		hist(observations$NUM_TRANSPORTERS)
	)
	
	print(
		hist(observations$DETECTOR_FITNESS_MEAN)
	)

	print(
		hist(observations$EXTRACTOR_FITNESS_MEAN)
	)

	print(
		hist(observations$TRANSPORTER_FITNESS_MEAN)
	)

	
	#hist(observations$NUM_DETECTORS)
	#hist(observations$NUM_EXTRACTORS)
	#hist(observations$NUM_TRANSPORTERS)
	#hist(observations$DETECTOR_FITNESS_MEAN)
	#hist(observations$EXTRACTOR_FITNESS_MEAN)
	#hist(observations$TRANSPORTER_FITNESS_MEAN)
	
	dev.off()
		
	
}



convergeEvennessData <-function() {
	
	CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
		"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
		"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
		"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN","CAPTURES_BEST_CASE_PERC","TOT_POP",
		"E")

	summarizeCSVFiles("/Users/sadat/aggregated_data","env_reg_24*", "/tmp/summ_env_reg_24.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)
	summarizeCSVFiles("/Users/sadat/aggregated_data","env_diff_24*", "/tmp/summ_env_diff_24.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)
	summarizeCSVFiles("/Users/sadat/aggregated_data","env_vdiff_24*", "/tmp/summ_env_vdiff_24.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)

	summarizeCSVFiles("/Users/sadat/aggregated_data","env_reg_36*", "/tmp/summ_env_reg_36.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)
	summarizeCSVFiles("/Users/sadat/aggregated_data","env_diff_36*", "/tmp/summ_env_diff_36.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)
	summarizeCSVFiles("/Users/sadat/aggregated_data","env_vdiff_36*", "/tmp/summ_env_vdiff_36.csv", CSV_FIELDS_TO_GRAB,999,16,30,TRUE,24,3)	
}

analyzeEvennessData <-function() {
	
	CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
		"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
		"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
		"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN")

	analyzeCSVFiles("/Users/sadat/aggregated_data","env_reg_24*", CSV_FIELDS_TO_GRAB, 60,999)
}
	
	
###
### Main Program
###

#convergeEvennessData()
analyzeEvennessData()




































