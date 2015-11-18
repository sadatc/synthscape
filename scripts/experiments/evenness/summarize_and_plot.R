library(ggplot2)


# Constants
MAX_GENERATIONS <-999
NUM_RESOURCES <- 16
NUM_SPECIES <- 3
REPORT_PROGRESS_ITEMS <- 25

CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","TOT_FITNESS_MEAN","RATE_COMMUNICATION","INTERVAL_TRANSPORTATION",
	"NUM_DETECTORS", "NUM_EXTRACTORS","NUM_TRANSPORTERS", "TRANSPORTER_FITNESS_MEAN", 
	"DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN","CAPTURES_BEST_CASE_PERC","TOT_POP",
	"E")

FIELDS_TO_CHECK_FOR_NORMALITY <- CSV_FIELDS_TO_GRAB[-1] # just skip generations

plotSummary <-function(csvFile,pdfFile) {
	row_data <- read.csv(file=csvFile)
	pdf(pdfFile)

	# only pick the columns we're interested in...
	# row_data <- row_data[row_data$TOT_POP!="NA",]
	# do some data manipulation

	data <- row_data 
	print(dim(data))

	print(
	  ggplot(data,aes(x=GENERATION,y=TOT_POP))+
	  geom_point() +
	  ggtitle("Total Population") 
	)

	print(
	  ggplot(data) +
	  geom_line(aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN, color="Detectors")) +
	  geom_line(aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN, color="Extractors")) +
	  geom_line(aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN, color="Transporters")) + 
	  ggtitle("Species Fitness Growth") + 
	  scale_color_discrete(name="Species") +
	  ylab("Fitness")
	) 

	print(
	  ggplot(data,aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN))+
	  geom_line() +
	  ggtitle("Detector Fitness") 
	)

	print(
	  ggplot(data,aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN))+
	  geom_line() +
	  ggtitle("Extractor Fitness") 
	)

	print(
	  ggplot(data,aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN))+
	  geom_line() +
	  ggtitle("Transporter Fitness") 
	)

	#print(
	  #ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE_PERC))+
	  #geom_point()

	  #xlim(0,1000)+
	  #geom_line(aes(color=resource_distribution), size=0.25)+
	  #facet_grid( gofp ~ gosc, scales="free_y") +  
	  #xlab("Generation")+ylab("Resources Capture %") +
	  #ggtitle("Capture Trends (Algo-Params (gofp/gosc) vs. Resource Distribution)") +  
	  #theme_bw() +
	  #theme( text = element_text(size = 11)) +
	  #theme(legend.position="bottom",legend.direction="horizontal") +
	  #scale_color_discrete(name = "Resource Distribution")
	#)

	print(
	  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
	  geom_point(alpha=0.3)
	)


	print(
	  ggplot(data,aes(x=GENERATION,y=CAPTURES_MEAN))+
	  geom_point(alpha=0.3) +
	  ggtitle("Mean Captures") 
	)

	#print(
	#  ggplot(data,aes(x=GENERATION,y=TOT_FITNESS_MEAN))+
	#  geom_line() +
	#  ggtitle("Overall Mean Fitness") 
	#)

	print(
	  ggplot(data) +
	  geom_line(aes(x=GENERATION,y=NUM_DETECTORS, color="Detectors")) +
	  geom_line(aes(x=GENERATION,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_line(aes(x=GENERATION,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth as generation progresses...") + 
	  scale_color_discrete(name="Species") +
	  ylab("Number of Agents")
	) 

	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(aes(alpha=0.3,x=CAPTURES_BEST_CASE,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth as generation progresses...") + 
	  scale_color_discrete(name="Species") +
	  ylab("Number of Agents")
	) 

	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=E)) +
	  geom_smooth(method=lm, aes(x=GENERATION,y=E)) +
	  ggtitle("Evenness Change (by Generations)") + 
	  ylab("Evenness Value")
	)

	ordered_data <- row_data[order(row_data$CAPTURES_MEAN),]

	print(
	  ggplot(ordered_data) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=E)) +
	  geom_smooth(method=lm, aes(x=CAPTURES_BEST_CASE,y=E)) +

	  ggtitle("Evenness Change (by Best Captures)") + 
	  ylab("Evenness Value")
	)
	dev.off()
}


###### PROGRAM STARTS HERE

summarizeEvennessData <-function(directory, filePattern, summaryFile, populationSizeFilter) {
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
		# MAX_GENERATIONS, we do some transformations:
		# a. trim off excess rows and rows with low populations
		# b. add in some computed columns regarding evenness measures
		# c. only grab the fields we are really interested in
		
		# only gather data if there is at least MAX_GENERATIONS amount of rows
		if(nrow(csvFileData) >= MAX_GENERATIONS) { 
			# a)  we trim off excess rows
			csvFileData <- csvFileData[1:MAX_GENERATIONS,] 

			csvFileData$TOT_POP <- csvFileData$NUM_DETECTORS+ csvFileData$NUM_EXTRACTORS+ csvFileData$NUM_TRANSPORTERS
			csvFileData <- csvFileData[csvFileData$TOT_POP==populationSizeFilter,] 

			
			# b) add in some computed columns
			csvFileData$CAPTURES_BEST_CASE_PERC <- csvFileData$CAPTURES_BEST_CASE/NUM_RESOURCES

			#csvFileData$P1 <- csvFileData$NUM_DETECTORS/csvFileData$TOT_POP
			#csvFileData$P2 <- csvFileData$NUM_EXTRACTORS/csvFileData$TOT_POP
			#csvFileData$P3 <- csvFileData$NUM_TRANSPORTERS/csvFileData$TOT_POP

			#csvFileData$H <- -((csvFileData$P1*log(csvFileData$P1)) + (csvFileData$P2*log(csvFileData$P2)) + (csvFileData$P3*log(csvFileData$P3)) )
			#csvFileData$E <- csvFileData$H/log(NUM_SPECIES)

			P1 <- csvFileData$NUM_DETECTORS/csvFileData$TOT_POP
			P2 <- csvFileData$NUM_EXTRACTORS/csvFileData$TOT_POP
			P3 <- csvFileData$NUM_TRANSPORTERS/csvFileData$TOT_POP

			H <- -((P1*log(P1)) + (P2*log(P2)) + (P3*log(P3)) )
			csvFileData$E <- H/log(NUM_SPECIES)

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

	for(generation in 1:MAX_GENERATIONS) {
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