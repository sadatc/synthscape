# Constants
library(data.table)

MAX_GENERATIONS <-299

CSV_FIELDS_TO_GRAB <- c("MODEL","RESOURCES","INTERACTIONS","SPECIES",
	"GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","RATE_MOTION",
	"RES_E2C_STEPS_MEAN")

CSV_FIELDS_TO_GRAB_S <- c("EXPERIMENT",CSV_FIELDS_TO_GRAB,"SIGNAL_SENT","SIGNAL_RECEIVED")

EXPERIMENT_NUMBER <- 0

MEAN_FILE_NAME <- "exp2_mean_trends"




hashTable <- new.env(hash=TRUE)
accumulateMean <- function(key, val) {
	if(!is.nan(val)) {
		countKey <- paste0(key,"_count")
		meanKey <- paste0(key,"_mean")

		if(is.null(hashTable[[countKey]])) {
			# first entry		
			hashTable[[countKey]] <- 1
			hashTable[[meanKey]] <- val
		} else {
			# update mean
			count <- hashTable[[countKey]]
			currentMean <- hashTable[[meanKey]]
			newCount <- count+1
			newMean <- ((currentMean*count) + val)/newCount
			hashTable[[countKey]] <- newCount
			hashTable[[meanKey]] <- newMean
		}	
	}
}

getAccumulatedMean <- function(key) {	
	meanKey <- paste0(key,"_mean")
	if(!is.null(hashTable[[meanKey]])) {
		return(hashTable[[meanKey]])
	} else {
		return(NA)
	}
}

addSet <- function(setName, element) {
	
	setKey <- paste0(setName,"_set")

	if(is.null(hashTable[[setKey]])) {
		# first entry		
		hashTable[[setKey]] <- element		
	} else {
		# update mean
		elementStr <- hashTable[[setKey]]
		elements <- unlist(strsplit(hashTable[[setKey]],":"))
		found <- 0
		for(e in elements) {
			#print("iterating over")
			#print(e)
			if(element == e) {
				found <- 1
			}
		}
		if(found==0) {
			newElement <- paste(elementStr,element,sep=":")
			hashTable[[setKey]] <- newElement	
		}	
	}	
	
}


getElements <- function(setName) {	
	setKey <- paste0(setName,"_set")
	if(!is.null(hashTable[[setKey]])) {
		elements <- unlist(strsplit(hashTable[[setKey]],":"))
		return(elements)
	} 
}

accumulatorFunc <- function(partialKey,
	GENERATION,CAPTURES_MEAN,CAPTURES_BEST_CASE,
	RATE_COMMUNICATION,RATE_MOTION,RES_E2C_STEPS_MEAN) {

	partialKey <- paste(partialKey,GENERATION ,sep=":")

	accumulateMean(paste(partialKey,"CAPTURES_MEAN",sep=":"),CAPTURES_MEAN)
	accumulateMean(paste(partialKey,"CAPTURES_BEST_CASE",sep=":"),CAPTURES_BEST_CASE)
	accumulateMean(paste(partialKey,"RATE_COMMUNICATION",sep=":"),RATE_COMMUNICATION)
	accumulateMean(paste(partialKey,"RATE_MOTION",sep=":"),RATE_MOTION)
	accumulateMean(paste(partialKey,"RES_E2C_STEPS_MEAN",sep=":"),RES_E2C_STEPS_MEAN)
	
	
	
}


###### PROGRAM STARTS HERE

meanifyCSVS <-function(directory, aggregateData, howManyGenerations) {

	csvFiles <- list.files(directory,pattern="*perf_dat.csv")
	aggregatedFiles <- 0

	MODEL <- ""
	QUALITY <- ""	
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
		quality <- substr(csvFileName,7,7)
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
			fileDataS <- head(fileDataS,howManyGenerations)
			
			
			
			# fix the captures best case and mean to percentages

			fileDataS$CAPTURES_BEST_CASE <- 
				fileDataS$CAPTURES_BEST_CASE/fileDataS$RESOURCES

				
			fileDataS$CAPTURES_MEAN <- 
				fileDataS$CAPTURES_MEAN/fileDataS$RESOURCES
			
			
			
			numDataCols <- ncol(fileDataS)
			
			observations <- fileDataS[,6:numDataCols]
			
			addSet("QUALITY",quality)
			addSet("SPECIES",correctedSpecies)
			addSet("INTERACTIONS",interactionType)

			partialKey <- paste(quality,correctedSpecies,interactionType,sep=":")

			# apply accumulatorFunc to every row of observations
			with(observations,{mapply(accumulatorFunc,partialKey,
			 						  GENERATION,CAPTURES_MEAN,CAPTURES_BEST_CASE,
			 						  RATE_COMMUNICATION,RATE_MOTION,RES_E2C_STEPS_MEAN			 						  
			 						  )
							   }
			    )
			

			rm(fileDataS)
			rm(fileData)
			rm(csvFileData)
			
			#print(paste("done aggregating data from:",csvFiles[i]))
			
		}
	}
	
}

meanifyExp1Data <-function(dataDir, meanDir, howManyGenerations) {	

	meanFile <- paste(meanDir,MEAN_FILE_NAME,sep="")
	meanFile <- paste(meanFile,howManyGenerations,".csv",sep="")

	if(file.exists(meanFile)) {
		unlink(meanFile)
		print(paste("previous meanFile was removed:",meanFile))
	} else {
		print("will create new meanFile")
	}

	
	aggregateData <- data.frame()
	
	dataSubDirs <- list.dirs(dataDir,  full.names = FALSE)
	
	
	aggregateData <- data.frame()

	for(directoryIndex in 1:length(dataSubDirs)) {
		dataSubDir <- dataSubDirs[directoryIndex]

		if( dataSubDir != "") {
			dataSubDirFullPath <- paste(dataDir,dataSubDir,sep="/")
			
			meanifyCSVS(dataSubDirFullPath,aggregateData, howManyGenerations)
			
		}
	}
	print("done computing averages...now dumping them in the aggregate file")



	# Now accumulate everything back to a data structure, and write that one out...

	aggregateDF <- data.frame()
	for(quality in getElements("QUALITY")) {
		for(species in getElements("SPECIES")) {
			for(interactions in getElements("INTERACTIONS")) {
				for(generations in 1:MAX_GENERATIONS) {
					CAPTURES_MEAN <- paste(quality,species,interactions,generations,"CAPTURES_MEAN",sep=":")
					CAPTURES_MEAN <- getAccumulatedMean(CAPTURES_MEAN)

					CAPTURES_BEST_CASE <- paste(quality,species,interactions,generations,"CAPTURES_BEST_CASE",sep=":")
					CAPTURES_BEST_CASE <- getAccumulatedMean(CAPTURES_BEST_CASE)

					RES_E2C_STEPS_MEAN <- paste(quality,species,interactions,generations,"RES_E2C_STEPS_MEAN",sep=":")
					RES_E2C_STEPS_MEAN <- getAccumulatedMean(RES_E2C_STEPS_MEAN)

					RATE_COMMUNICATION <- paste(quality,species,interactions,generations,"RATE_COMMUNICATION",sep=":")
					RATE_COMMUNICATION <- getAccumulatedMean(RATE_COMMUNICATION)

					RATE_MOTION <- paste(quality,species,interactions,generations,"RATE_MOTION",sep=":")
					RATE_MOTION <- getAccumulatedMean(RATE_MOTION)

	
					row <- data.frame(
						QUALITY=quality,
						SPECIES=species,
						INTERACTIONS=interactions,
						GENERATION=generations,
						CAPTURES_BEST_CASE=CAPTURES_BEST_CASE,
						CAPTURES_MEAN=CAPTURES_MEAN,
						RES_E2C_STEPS_MEAN=RES_E2C_STEPS_MEAN,
						RATE_MOTION=RATE_MOTION,
						RATE_COMMUNICATION=RATE_COMMUNICATION
					)					
					aggregateDF <- rbind(aggregateDF,row)
					
				}
			}
		}
	}
	
	write.csv(aggregateDF,file=meanFile,row.names=F)

}






##############################   MAIN PROCESS BEGINS ###############################

meanifyExp1Data("/Users/sadat/ExperimentResults/Interactions/final_data/","/tmp/", 300)	





