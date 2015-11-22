# Constants

REPORT_PROGRESS_ITEMS <- 25

#CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
#	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
#	"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
#	"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN","CAPTURES_BEST_CASE_PERC","TOT_POP",
#	"E")

#FIELDS_TO_CHECK_FOR_NORMALITY <- CSV_FIELDS_TO_GRAB[-1] # just skip generations


###### PROGRAM STARTS HERE

summarizeEvennessData <-function(directory, filePattern, summaryFile, 
	populationSizeFilter, maxGenerations, numSpecies, numResources) {
	print(paste("directory:",directory))
	print(paste("filePattern:",filePattern))
	print(paste("populationSizeFilter:",populationSizeFilter))
	print(paste("summaryFile:",summaryFile))

	csvFiles <- list.files(directory,pattern=filePattern)
	#print(csvFiles)

	#aggregateData will contain the data from ALL csv files.
	aggregateData <- data.frame()

	aggregatedFiles <- 0
	print(paste("Number of CSV files to aggregate:",length(csvFiles)))
	for(i in 1:length(csvFiles)){
	#for(i in 1:100){
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

			csvFileData$TOT_POP <- csvFileData$NUM_DETECTORS+ csvFileData$NUM_EXTRACTORS+ csvFileData$NUM_TRANSPORTERS
			csvFileData <- csvFileData[csvFileData$TOT_POP==populationSizeFilter,] 

			
			# b) add in some computed columns
			csvFileData$CAPTURES_BEST_CASE_PERC <- csvFileData$CAPTURES_BEST_CASE/numResources

			#csvFileData$P1 <- csvFileData$NUM_DETECTORS/csvFileData$TOT_POP
			#csvFileData$P2 <- csvFileData$NUM_EXTRACTORS/csvFileData$TOT_POP
			#csvFileData$P3 <- csvFileData$NUM_TRANSPORTERS/csvFileData$TOT_POP

			#csvFileData$H <- -((csvFileData$P1*log(csvFileData$P1)) + (csvFileData$P2*log(csvFileData$P2)) + (csvFileData$P3*log(csvFileData$P3)) )
			#csvFileData$E <- csvFileData$H/log(numSpecies)

			P1 <- csvFileData$NUM_DETECTORS/csvFileData$TOT_POP
			P2 <- csvFileData$NUM_EXTRACTORS/csvFileData$TOT_POP
			P3 <- csvFileData$NUM_TRANSPORTERS/csvFileData$TOT_POP

			H <- -((P1*log(P1)) + (P2*log(P2)) + (P3*log(P3)) )
			csvFileData$E <- H/log(numSpecies)




			#print(dim(csvFileData))
			#print(csvFileData)
			#stop()
			
			fileData <- data.frame(EXPERIMENT=i,csvFileData[CSV_FIELDS_TO_GRAB])
			aggregateData <- rbind(aggregateData,fileData)    
			if( i%% REPORT_PROGRESS_ITEMS == 0 ) {
				print(paste("done aggregating data from: ", i, " files..."))
			}
			aggregatedFiles <- aggregatedFiles + 1 
		}
	}
	print(paste("*** number of experiments aggregated:",aggregatedFiles))

	# aggregateData now contains a gigantic table of experiment x generation values
	# at this point we can do two things: 1) check normality and 2) create summary table



	summaryData <- data.frame()

	for(generation in 1:maxGenerations) {
		print(paste("averaging generation=",generation))
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
	write.csv(summaryData,file=paste(directory,summaryFile,sep="/"),row.names=F)
}
	

### Main Program

#print(processCSVS("/Users/sadat/aggregated_data","env_reg_24*perf_dat.csv"))
summarizeEvennessData("/Users/sadat/aggregated_data","env_reg_24*", "summ_env_reg_24.csv",24)
summarizeEvennessData("/Users/sadat/aggregated_data","env_diff_24*", "summ_env_diff_24.csv",24)
summarizeEvennessData("/Users/sadat/aggregated_data","env_vdiff_24*", "summ_env_vdiff_24.csv",24)
summarizeEvennessData("/Users/sadat/aggregated_data","env_reg_36*", "summ_env_reg_36.csv",36)
summarizeEvennessData("/Users/sadat/aggregated_data","env_diff_36*", "summ_env_diff_36.csv",36)
summarizeEvennessData("/Users/sadat/aggregated_data","env_vdiff_36*", "summ_env_vdiff_36.csv",36)