# Constants
library(data.table)
library(boot)

MAX_GENERATIONS <-299

CSV_FIELDS_TO_GRAB <- c("MODEL","RESOURCES","INTERACTIONS","SPECIES",
	"GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","RATE_MOTION",
	"RES_E2C_STEPS_MEAN")

CSV_FIELDS_TO_GRAB_S <- c("EXPERIMENT",CSV_FIELDS_TO_GRAB,"SIGNAL_SENT","SIGNAL_RECEIVED")

EXPERIMENT_NUMBER <- 0

MEAN_FILE_NAME <- "exp1_mean_trends"

sample_mean = function(data, indices) {
	return(mean(data[indices]))
}




hashTable <- new.env(hash=TRUE)
accumulateMean <- function(key, val) {
	if(!is.nan(val)) {
		
				
		if(is.null(hashTable[[key]])) {
			# first entry				
			hashTable[[key]] <- val
		} else {
			# update mean
		
			currentMean <- hashTable[[key]]
			#newCount <- count+1
			newMean <- paste0(currentMean,":",val)
		
			hashTable[[key]] <- newMean
		}	
	}
}

getAccumulatedMean <- function(key) {	
	
	if(!is.null(hashTable[[key]])) {
		return(hashTable[[key]])
	} else {
		return(NA)
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
	SPECIES <- ""
	INTERACTIONS <- ""
	COMPLEXITY <- 0
	CLONES <- 0

	
	#for(i in 1:length(csvFiles)) {
	for(i in 1:2) {
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
			
			
			partialKey <- paste(correctedModel,correctedSpecies,interactionType,sep=":")
			

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
	print("done computing averages...executing mapply and writing to files....")
	
	theNames <<- c("MODEL","SPECIES","INTERACTIONS", "GENERATION","MEASURE","VALUE")	
	write(paste(theNames,collapse=", "),file=meanFile)


	mapply(function(rowKey) {
		rowData <- hashTable[[rowKey]]
		if(!is.null(rowData)) {

			rowData <- as.numeric(strsplit(as.character(rowData),":")[[1]])
			
			bMean = boot(rowData,sample_mean,R=1000)
			rowData <- bMean$t0
			
			theValues <- unlist(strsplit(rowKey,":"))
			theValues <- c(theValues,rowData)
			df <- data.frame(rbind(theValues))
			
			colnames(df) <- theNames					
			
			write.table(df,file=meanFile,,append=T,col.names=F,sep=",",row.names=F)
		}
		
	},ls(hashTable))
	print("finished writing bootstrap into file...")


	dfLongFormat <- read.csv(file=meanFile)
	print("read bootstrap data from file for long to wide conversion....")	
	
	library(tidyr)
	modifiedDf <- spread(dfLongFormat,MEASURE,VALUE)
	print("converted to wide format...")

	if(file.exists(meanFile)) {
		unlink(meanFile)
		print(paste("previous meanFile was removed:",meanFile))
	} else {
		print("will create new meanFile")
	}

	write.table(modifiedDf,file=meanFile,,append=F,col.names=T,sep=",",row.names=F)
	print("finished writing wide format into file...ready to plot")

	
	print("done")

}






##############################   MAIN PROCESS BEGINS ###############################

meanifyExp1Data("/Users/sadat/ExperimentResults/GeneralTrends/final_data/","/tmp/", 300)








