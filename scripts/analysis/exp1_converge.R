# Constants
library(data.table)


MAX_GENERATIONS <-999

CSV_FIELDS_TO_GRAB <- c("GRIDS","RESOURCES","SITES","OBSTACLES","DIFFICULTY",
	"COMPLEXITY","CLONES","MODEL","INTERACTIONS","SPECIES",
	"GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","RATE_MOTION",
	"RES_D2C_STEPS_MEAN")

CSV_FIELDS_TO_GRAB_S <- c("EXPERIMENT",CSV_FIELDS_TO_GRAB,"SIGNAL_SENT","SIGNAL_RECEIVED")

EXPERIMENT_NUMBER <- 0




###### PROGRAM STARTS HERE

processCSVS <-function(directory, msiccSummaryFile) {

	csvFiles <- list.files(directory,pattern="*perf_dat.csv")

	#aggregateData will contain the data from ALL csv files.
	aggregateData <- data.frame()

	aggregatedFiles <- 0

	MODEL <- ""	
	SPECIES <- ""
	INTERACTIONS <- ""
	COMPLEXITY <- 0
	CLONES <- 0

	
	for(i in 1:length(csvFiles)) {
		correctedSpecies <- substr(csvFiles[i],5,5)
		interactionType <- substr(csvFiles[i],6,6)
		correctedModel <-substr(csvFiles[i],4,4)

		
		csvFile <- paste(directory,csvFiles[i],sep="/") # concats
		csvFileData <- read.csv(csvFile, header=TRUE)
		
	
		
 
		# only gather data if there is at least MAX_GENERATIONS amount of rows
		if(nrow(csvFileData) >= MAX_GENERATIONS) { 

			if(interactionType == "b") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_BROADCAST_SENT",
					"TOT_BROADCAST_RECEIVED")
			}
			
			if(interactionType == "t") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_TRAIL_SENT",
					"TOT_TRAIL_RECEIVED")
			}

			if(interactionType == "u") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_UNICAST_SENT",
					"TOT_UNICAST_RECEIVED")
			}

			if(interactionType == "n") {
				# nothing to do...
			}




			csvFileData <- csvFileData[1:MAX_GENERATIONS,] # we trim off excess rows
			fileData <- data.frame(EXPERIMENT=i,csvFileData[CSV_FIELDS_TO_GRAB])
			
			## the data has some issues that needs to be fixed
			
			## problem 1: species column is always "s", we fix this by using 
			# speciesType
			fileData$SPECIES <- correctedSpecies
			
			## problem 2: difficulty and complexity was swapped!
			if(fileData$DIFFICULTY == 3 || fileData$DIFFICULTY == 4) {
				tmp <- fileData$DIFFICULTY
				fileData$DIFFICULTY <- fileData$COMPLEXITY
				fileData$COMPLEXITY <- tmp
			}
			
			
			
			if(interactionType == "b") {
				fileData$SIGNAL_SENT <- fileData$TOT_BROADCAST_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_BROADCAST_RECEIVED
				fileData$TOT_BROADCAST_SENT  <- NULL
				fileData$TOT_BROADCAST_RECEIVED <- NULL
			}
			
			if(interactionType == "t") {
				fileData$SIGNAL_SENT <- fileData$TOT_TRAIL_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_TRAIL_RECEIVED
				fileData$TOT_TRAIL_SENT  <- NULL
				fileData$TOT_TRAIL_RECEIVED <- NULL
			}

			if(interactionType == "u") {
				fileData$SIGNAL_SENT <- fileData$TOT_UNICAST_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_UNICAST_RECEIVED
				fileData$TOT_UNICAST_SENT  <- NULL
				fileData$TOT_UNICAST_RECEIVED <- NULL
			}

			if(interactionType == "n") {
				fileData$SIGNAL_SENT <- 0
				fileData$SIGNAL_RECEIVED <- 0
			}
			
			

			fileDataS <- data.frame(fileData[CSV_FIELDS_TO_GRAB_S])
			
			# fix the captures best case and mean to percentages
			
			fileDataS$CAPTURES_BEST_CASE <- 
				fileDataS$CAPTURES_BEST_CASE/fileDataS$RESOURCES
				
			fileDataS$CAPTURES_MEAN <- 
				fileDataS$CAPTURES_MEAN/fileDataS$RESOURCES

			fileDataS$MODEL <- correctedModel

			MODEL <- correctedModel
			SPECIES <- fileDataS$SPECIES
			INTERACTIONS <- fileDataS$INTERACTIONS
			COMPLEXITY <- fileDataS$COMPLEXITY
			CLONES <- fileDataS$CLONES
			
			
			aggregateData <- rbind(aggregateData,fileDataS)    
			
			rm(fileDataS)
			rm(fileData)
			rm(csvFileData)
			
			print(paste("done aggregating data from:",csvFiles[i]))
			aggregatedFiles <- aggregatedFiles + 1 
			

		}
	}
	print(paste("number of experiments aggregated:",aggregatedFiles))

	# write.csv(aggregateData,file=aggregateFile,row.names=F)


	# aggregateData is a giant table with data like:
	# grids, resources, ... generation, measure, measure...
	# we need to average out all those measures...across the various
	# environs...

	summaryData <- data.frame()

	for(generation in 1:MAX_GENERATIONS) {
		# print(paste("averaging generation=",generation))
		numDataCols <- ncol(aggregateData)

		# pick the observations: they are from col 12 onwards...
		# the first 12 are particulars of the experiment..
		observations <- aggregateData[aggregateData$GENERATION==generation,
			13:numDataCols]
			
		#print(observations)
		#exit
		

		# find the means of these values
		meanObservations <-lapply(observations,mean, na.rm=TRUE)
	
		generationSummary <- data.frame(GENERATION=generation,meanObservations)
		summaryData <-rbind(summaryData,generationSummary)
	}
	
	summaryData$MODEL <- correctedModel
	summaryData$SPECIES <- SPECIES
	summaryData$INTERACTIONS <- INTERACTIONS
	summaryData$COMPLEXITY <- COMPLEXITY
	summaryData$CLONES <- CLONES


	write.csv(summaryData,file=msiccSummaryFile,row.names=F)
	rm(aggregateData)

}
	
	
summarizeExp1Data <-function(dataDir, outputDir) {	
	
	dataSubDirs <- list.dirs(dataDir,  full.names = FALSE)
	print(dataSubDirs)

	for(directoryIndex in 1:length(dataSubDirs)) {
		dataSubDir <- dataSubDirs[directoryIndex]

		if( dataSubDir != "") {

			summaryFile <- paste(outputDir,"/",dataSubDir,"_msicc_summ.csv",sep="")

			print(dataSubDir)
			dataSubDirFullPath <- paste(dataDir,dataSubDir,sep="/")
				

			print("processing ...")
			print(dataSubDirFullPath)	
			print(summaryFile)
			processCSVS(dataSubDirFullPath,summaryFile)
			print("======")
		}
	
	}

}



meanifyCSVS <-function(directory, aggregateData) {

	csvFiles <- list.files(directory,pattern="*perf_dat.csv")
	aggregatedFiles <- 0

	MODEL <- ""	
	SPECIES <- ""
	INTERACTIONS <- ""
	COMPLEXITY <- 0
	CLONES <- 0

	
	for(i in 1:length(csvFiles)) {
	#for(i in 1:2) {
		assign("EXPERIMENT_NUMBER",EXPERIMENT_NUMBER + 1,.GlobalEnv)
		csvFileName <- csvFiles[i]
		correctedSpecies <- substr(csvFileName,5,5)
		interactionType <- substr(csvFileName,6,6)
		correctedModel <-substr(csvFileName,4,4)

		
		csvFile <- paste(directory,csvFileName,sep="/") # concats
		print(csvFile)
		csvFileData <- read.csv(csvFile, header=TRUE)
	
		# only gather data if there is at least MAX_GENERATIONS amount of rows
		if(nrow(csvFileData) >= MAX_GENERATIONS) { 

			if(interactionType == "b") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_BROADCAST_SENT",
					"TOT_BROADCAST_RECEIVED")
			}
			
			if(interactionType == "t") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_TRAIL_SENT",
					"TOT_TRAIL_RECEIVED")
			}

			if(interactionType == "u") {
				CSV_FIELDS_TO_GRAB <- c(CSV_FIELDS_TO_GRAB,"TOT_UNICAST_SENT",
					"TOT_UNICAST_RECEIVED")
			}

			if(interactionType == "n") {
				# nothing to do...
			}

			csvFileData <- csvFileData[1:MAX_GENERATIONS,] # we trim off excess rows
			fileData <- data.frame(EXPERIMENT=i,csvFileData[CSV_FIELDS_TO_GRAB])
			
			## the data has some issues that needs to be fixed
			
			## problem 1: species column is always "s", we fix this by using 
			# speciesType
			fileData$SPECIES <- correctedSpecies
			
			## problem 2: difficulty and complexity was swapped!
			if(fileData$DIFFICULTY == 3 || fileData$DIFFICULTY == 4) {
				tmp <- fileData$DIFFICULTY
				fileData$DIFFICULTY <- fileData$COMPLEXITY
				fileData$COMPLEXITY <- tmp
			}
			
			if(interactionType == "b") {
				fileData$SIGNAL_SENT <- fileData$TOT_BROADCAST_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_BROADCAST_RECEIVED
				fileData$TOT_BROADCAST_SENT  <- NULL
				fileData$TOT_BROADCAST_RECEIVED <- NULL
			}
			
			if(interactionType == "t") {
				fileData$SIGNAL_SENT <- fileData$TOT_TRAIL_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_TRAIL_RECEIVED
				fileData$TOT_TRAIL_SENT  <- NULL
				fileData$TOT_TRAIL_RECEIVED <- NULL
			}

			if(interactionType == "u") {
				fileData$SIGNAL_SENT <- fileData$TOT_UNICAST_SENT 
				fileData$SIGNAL_RECEIVED <- fileData$TOT_UNICAST_RECEIVED
				fileData$TOT_UNICAST_SENT  <- NULL
				fileData$TOT_UNICAST_RECEIVED <- NULL
			}

			if(interactionType == "n") {
				fileData$SIGNAL_SENT <- 0
				fileData$SIGNAL_RECEIVED <- 0
			}

			fileDataS <- data.frame(fileData[CSV_FIELDS_TO_GRAB_S])
			
			fileDataS <- head(fileDataS,300)
			
			COMPLEXITY <- fileDataS$COMPLEXITY[[1]]
			CLONES <- fileDataS$CLONES[[1]]

			
			# fix the captures best case and mean to percentages


			fileDataS$CAPTURES_BEST_CASE <- 
				fileDataS$CAPTURES_BEST_CASE/fileDataS$RESOURCES
				
			fileDataS$CAPTURES_MEAN <- 
				fileDataS$CAPTURES_MEAN/fileDataS$RESOURCES
			
			
			
			numDataCols <- ncol(fileDataS)
			
			observations <- fileDataS[,13:numDataCols]
			meanObservations <-lapply(observations,mean, na.rm=TRUE)
			
			meanData <- data.frame(EXPERIMENT=EXPERIMENT_NUMBER,
				MODEL=correctedModel,
				SPECIES = correctedSpecies,
				INTERACTIONS = interactionType,
				COMPLEXITY=COMPLEXITY,
				CLONES = CLONES,
				GRIDS = fileDataS$GRIDS[[1]],
				RESOURCES = fileDataS$RESOURCES[[1]],			
				SITES = fileDataS$SITES[[1]],
				OBSTACLES = fileDataS$OBSTACLES[[1]],
				DIFFICULTY = fileDataS$DIFFICULTY[[1]],
				meanObservations)
		
			
			aggregateData <- rbind(aggregateData,meanData)    
			
			rm(fileDataS)
			rm(fileData)
			rm(csvFileData)
			
			print(paste("done aggregating data from:",csvFiles[i]))
			aggregatedFiles <- aggregatedFiles + 1 
		}
	}
	return(aggregateData)
}

meanifyExp1Data <-function(dataDir, meanFile) {	

#	N <- 4000
#	aggregateData <- data.frame( EXPERIMENT=numeric(N), MODEL=character(N),SPECIES=character(N),INTERACTIONS=character(N),COMPLEXITY=numeric(N),CLONES=numeric(N),GRIDS=numeric(N),RESOURCES=numeric(N),SITES=numeric(N),OBSTACLES=numeric(N),DIFFICULTY=character(N),CAPTURES_TOTAL=numeric(N),CAPTURES_BEST_CASE=numeric(N),CAPTURES_MEAN=numeric(N),TOT_FITNESS_MEAN=numeric(N),RATE_COMMUNICATION=numeric(N),RATE_MOTION=numeric(N),RES_D2C_STEPS_MEAN=numeric(N),SIGNAL_SENT=numeric(N),SIGNAL_RECEIVED=numeric(N))
	
	aggregateData <- data.frame()
	
	dataSubDirs <- list.dirs(dataDir,  full.names = FALSE)
	print(dataSubDirs)

	for(directoryIndex in 1:length(dataSubDirs)) {
		dataSubDir <- dataSubDirs[directoryIndex]

		if( dataSubDir != "") {

			print(dataSubDir)
			dataSubDirFullPath <- paste(dataDir,dataSubDir,sep="/")
				

			print("processing ...")
			print(dataSubDirFullPath)	
			print(meanFile)
			
			if(file.exists(meanFile)) {
				aggregateData <- read.csv(file=meanFile)
			} else {
				aggregateData <- data.frame()
			}
			
			aggregateData <- meanifyCSVS(dataSubDirFullPath,aggregateData)
			write.csv(aggregateData,file=meanFile,row.names=F)
			print("======")
		}
	}
	print("done")


	

}



### Main Program


#summarizeExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/penzias_4t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/msiccSummaries")

#summarizeExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/msiccSummaries")

#summarizeExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/post_processed")

#processCSVS("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/e1_ist_3_10",
#	"/tmp/summ.csv")



#meanifyExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/penzias_4t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/msiccSummaries/4t_mean.csv")
meanifyExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/combined/",
	"/Users/sadat/ExperimentResults/GeneralTrends/all_experiments_mean_300.csv")	








