# Constants
library(data.table)


MAX_GENERATIONS <-299

CSV_FIELDS_TO_GRAB <- c("GRIDS","RESOURCES","SITES","OBSTACLES","DIFFICULTY",
	"COMPLEXITY","CLONES","MODEL","INTERACTIONS","SPECIES",
	"GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","RATE_MOTION",
	"RES_E2C_STEPS_MEAN")


CSV_FIELDS_TO_GRAB_S <- c("EXPERIMENT",CSV_FIELDS_TO_GRAB,"SIGNAL_SENT","SIGNAL_RECEIVED")

EXPERIMENT_NUMBER <- 0

MEAN_FILE_NAME <- "exp3_experiments_mean_"


###### PROGRAM STARTS HERE

meanifyCSVS <-function(directory, aggregateData, howManyGenerations) {

	csvFiles <- list.files(directory,pattern="*perf_dat.csv")
	aggregatedFiles <- 0


	RICHNESS_TYPE <- ""
	RICHNESS <- 0	# number of species
	TRAITSUM <- 0	# sum of all traits represented * clones
	T2SRATIO <- 0	# traitsum/(clones*richness)
	extractedSpecies <- "s" #specialist by default, override, if needed

		
	for(i in 1:length(csvFiles)) {
	#for(i in 1:2) {
		assign("EXPERIMENT_NUMBER",EXPERIMENT_NUMBER + 1,.GlobalEnv)
		csvFileName <- csvFiles[i]

		extractedModel <-"a"

		richnessType <- substr(csvFileName,5,6)
		interactionType <- substr(csvFileName,4,4)
		extractedClones <- substr(csvFileName,10,10)
		richnessVariation <- substr(csvFileName,8,10)

		if(richnessVariation == "4_2") {
			richnessVariation <- "4_24"			
		}

		
		if(richnessType == "ho") {
			extractedSpecies <- "g"
		}
		
		


		
		csvFile <- paste(directory,csvFileName,sep="/") # concats
		print("reading data from: ")
		print(csvFile)
		csvFileData <- read.csv(csvFile, header=TRUE)
		print("done reading...")		
	
	
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

			csvFileData <- csvFileData[MAX_GENERATIONS:MAX_GENERATIONS,] # we trim off excess rows
			fileData <- data.frame(EXPERIMENT=i,csvFileData[CSV_FIELDS_TO_GRAB])
			
			## the data has some issues that needs to be fixed
			
			## problem 1: species column is always "s", we fix this by using 
			# speciesType
			fileData$SPECIES <- extractedSpecies
			
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

      #if(richnessType == "ho") {
      #  print("I am here!!!")
      #  print(CSV_FIELDS_TO_GRAB_S)
      #}
			
			
			fileDataS <- data.frame(fileData[CSV_FIELDS_TO_GRAB_S])
			
			fileDataS <- head(fileDataS,howManyGenerations)
			
			MODEL <- fileDataS$MODEL[[1]]
			SPECIES <- fileDataS$SPECIES[[1]]
			COMPLEXITY <- fileDataS$COMPLEXITY[[1]]
			CLONES <- fileDataS$CLONES[[1]]

			RICHNESS_TYPE <- richnessType

			# 3_4 cases			
			if(RICHNESS_TYPE == "mo" & COMPLEXITY==3 & CLONES == 4) {
				RICHNESS <- 3	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "bi" & COMPLEXITY==3 & CLONES == 2) {
				RICHNESS <- 3	
				TRAITS_PER_SET <- 6
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "po" & COMPLEXITY==3 & CLONES == 1) {
				RICHNESS <- 7	
				TRAITS_PER_SET <- 12
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "tr" & COMPLEXITY==3 & CLONES == 4) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "ho" & COMPLEXITY==3 & CLONES == 4) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			
			# 3_8 cases			
			if(RICHNESS_TYPE == "mo" & COMPLEXITY==3 & CLONES == 8) {
				RICHNESS <- 3	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "bi" & COMPLEXITY==3 & CLONES == 4) {
				RICHNESS <- 3	
				TRAITS_PER_SET <- 6
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "po" & COMPLEXITY==3 & CLONES == 2) {
				RICHNESS <- 7	
				TRAITS_PER_SET <- 12
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "tr" & COMPLEXITY==3 & CLONES == 8) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "ho" & COMPLEXITY==3 & CLONES == 8) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 3
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 

			# 4_24 cases			
			if(RICHNESS_TYPE == "mo" & COMPLEXITY==4 & CLONES == 24) {
				RICHNESS <- 4	
				TRAITS_PER_SET <- 4
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "bi" & COMPLEXITY==4 & CLONES == 8) {
				RICHNESS <- 6
				TRAITS_PER_SET <- 12
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "tr" & COMPLEXITY==4 & CLONES == 8) {
				RICHNESS <- 4	
				TRAITS_PER_SET <- 12
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "po" & COMPLEXITY==4 & CLONES == 3) {
				RICHNESS <- 15	
				TRAITS_PER_SET <- 32
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else
			if(RICHNESS_TYPE == "qu" & COMPLEXITY==4 & CLONES == 24) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 4
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else 
			if(RICHNESS_TYPE == "ho" & COMPLEXITY==4 & CLONES == 24) {
				RICHNESS <- 1	
				TRAITS_PER_SET <- 4
				TRAITSUM <- TRAITS_PER_SET*CLONES
				T2SRATIO <- TRAITSUM/(CLONES*RICHNESS)
			} else {

				print("unknown pop")
			} 
			
	#		print(RICHNESS_TYPE)
	#		print(RICHNESS)
	#		print(TRAITSUM)
	#		print(T2SRATIO)

			
			
			
	#		stop()
			
			# fix the captures best case and mean to percentages
			fileDataS$CAPTURES_BEST_CASE <- 
				fileDataS$CAPTURES_BEST_CASE/fileDataS$RESOURCES
				
			fileDataS$CAPTURES_MEAN <- 
				fileDataS$CAPTURES_MEAN/fileDataS$RESOURCES
			
			
			
			numDataCols <- ncol(fileDataS)
			
			observations <- fileDataS[,13:numDataCols]
			meanObservations <-lapply(observations,mean, na.rm=TRUE)
			
			meanData <- data.frame(EXPERIMENT=EXPERIMENT_NUMBER,
				MODEL=MODEL,		
				RICHNESS_TYPE = RICHNESS_TYPE,
				RICHNESS_VARIATION = richnessVariation,
				RICHNESS = RICHNESS,
				TRAITSUM = 	TRAITSUM,
				T2SRATIO = T2SRATIO,
				SPECIES = SPECIES,
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

meanifyExp2Data <-function(dataDir, meanDir, howManyGenerations) {	

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
	print(dataSubDirs)
	
	aggregateData <- data.frame()

	for(directoryIndex in 1:length(dataSubDirs)) {
		dataSubDir <- dataSubDirs[directoryIndex]

		if( dataSubDir != "") {

			print(dataSubDir)
			dataSubDirFullPath <- paste(dataDir,dataSubDir,sep="/")
				

			print("processing ...")
			print(dataSubDirFullPath)	

			
			aggregateData <- meanifyCSVS(dataSubDirFullPath,aggregateData, howManyGenerations)
			
			#print(length(aggregateData))

			print("======")
		}
	}
	print("done")
	write.csv(aggregateData,file=meanFile,row.names=F)

	

}



##############################   MAIN PROCESS BEGINS ###############################


#summarizeExp2Data("/Users/sadat/ExperimentResults/GeneralTrends/penzias_4t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/msiccSummaries")

#summarizeExp2Data("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/msiccSummaries")

#summarizeExp2Data("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/",
#	"/Users/sadat/ExperimentResults/GeneralTrends/post_processed")

#processCSVS("/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/e1_ist_3_10",
#	"/tmp/summ.csv")



meanifyExp2Data("/Users/sadat/ExperimentResults/Richness/final_data/",
	"/Users/sadat/analysis/data/exp3/", 300)	








