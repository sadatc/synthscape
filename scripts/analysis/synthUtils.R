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



convergeEvennessData_DE <-function() {
	
	pCsvFieldsToGrab <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
		"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
		"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
		"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN","CAPTURES_BEST_CASE_PERC","TOT_POP",
		"E")

	pDirectory <- "~/ExperimentResults/Evenness/DE/aggregated_data"
	pMaxGenerations <-999
	pNumResources <- 16
	pMaxFiles <- 30
	pShouldComputeE <- TRUE
	pPopulationSizeFilter <- 24
 	pNumSpecies <-3
	

	summarizeCSVFiles(pDirectory,"env_reg_24*", "/tmp/summ_env_reg_24.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"env_diff_24*", "/tmp/summ_env_diff_24.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"env_vdiff_24*", "/tmp/summ_env_vdiff_24.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)


	pPopulationSizeFilter <- 36
	summarizeCSVFiles(pDirectory,"env_reg_36*", "/tmp/summ_env_reg_36.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"env_diff_36*", "/tmp/summ_env_diff_36.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"env_vdiff_36*", "/tmp/summ_env_vdiff_36.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)

}


analyzeEvennessData_DE <-function() {
	
	pCsvFieldsToGrab <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
		"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
		"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
		"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN")

	pDirectory <- "~/ExperimentResults/Evenness/DE/aggregated_data"
	pMaxFiles <- 30
	pMaxGenerations <-999
	
	analyzeCSVFiles(pDirectory,"env_reg_24*", pCsvFieldsToGrab, pMaxFiles,pMaxGenerations)
}

convergeEvennessData_ME <-function() {
	
	pCsvFieldsToGrab <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
		"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
		"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
		"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN","CAPTURES_BEST_CASE_PERC","TOT_POP",
		"E")

	pDirectory <- "~/ExperimentResults/Evenness/ME/aggregated_data"
	pMaxGenerations <-1999
	pNumResources <- 16
	pMaxFiles <- 30
	pShouldComputeE <- TRUE
	pPopulationSizeFilter <- 24
 	pNumSpecies <-3
	
	# the random population cannot be averaged like this
	# instead it needs to be averaged according to
	# ratio: generation: rate of growth of metrices
	# summarizeCSVFiles(pDirectory,"me_reg_24_r*", "/tmp/me_reg_24_r.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)


	#summarizeCSVFiles(pDirectory,"me_reg_24_m111*", "/tmp/sum_me_reg_24_m111.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	#summarizeCSVFiles(pDirectory,"me_reg_24_m112*", "/tmp/sum_me_reg_24_m112.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	#summarizeCSVFiles(pDirectory,"me_reg_24_m121*", "/tmp/sum_me_reg_24_m121.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	#summarizeCSVFiles(pDirectory,"me_reg_24_m211*", "/tmp/sum_me_reg_24_m211.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)


	summarizeCSVFiles(pDirectory,"me_reg_24_m123*", "/tmp/sum_me_reg_24_m123.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"me_reg_24_m132*", "/tmp/sum_me_reg_24_m132.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"me_reg_24_m213*", "/tmp/sum_me_reg_24_m213.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"me_reg_24_m231*", "/tmp/sum_me_reg_24_m231.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"me_reg_24_m312*", "/tmp/sum_me_reg_24_m312.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)
	summarizeCSVFiles(pDirectory,"me_reg_24_m321*", "/tmp/sum_me_reg_24_m321.csv", pCsvFieldsToGrab,pMaxGenerations,pNumResources,pMaxFiles,pShouldComputeE,pPopulationSizeFilter,pNumSpecies)





}
	
	
###
### Main Program
###

#convergeEvennessData_DE()
#analyzeEvennessData_DE()

convergeEvennessData_ME()
#analyzeEvennessData_DE()




































