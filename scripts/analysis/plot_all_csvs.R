library(ggplot2)
# Constants
MAX_GENERATIONS <-999

CSV_FIELDS_TO_GRAB <- c("GENERATION","CAPTURES_TOTAL","CAPTURES_BEST_CASE",
	"CAPTURES_MEAN","RES_D2C_STEPS_MEAN","TOT_TRAIL_SENT","TOT_TRAIL_RECEIVED",
	"TOT_BROADCAST_SENT","TOT_BROADCAST_RECEIVED",
	"TOT_UNICAST_SENT","TOT_UNICAST_RECEIVED")



###### PROGRAM STARTS HERE

processCSVS <-function(directory) {
	csvFiles <- list.files(directory,pattern="*perf_dat.csv")

	for(i in 1:length(csvFiles)){
		csvFileName <- csvFiles[i]
		csvFile <- paste(directory,csvFileName,sep="/")
		pdfFileName <- paste("/tmp",csvFileName,sep="/") 
		csvFileData <- read.csv(csvFile, header=TRUE)
 
		# only gather data if there is at least MAX_GENERATIONS amount of rows
		if(nrow(csvFileData) >= MAX_GENERATIONS) { 
			pdfFile <- paste(pdfFileName,"pdf", sep=".")
			print(paste("about to plot:",pdfFile))
			pdf(pdfFile)
			
			print(
# 			 ggplot(csvFileData,aes(x=GENERATION,y=CAPTURES_MEAN))+
# 			 ggplot(csvFileData,aes(x=GENERATION,y=RES_D2C_STEPS_MEAN))+
 			 ggplot(csvFileData,aes(x=GENERATION,y=CAPTURES_BEST_CASE/RESOURCES))+
		     geom_point() +
  			 ggtitle(csvFileName) 
			)
			dev.off()
			
		}
	}
	print("done")

}
	


### Main Program
directory <- "/Users/sadat/ExperimentResults/GeneralTrends/andy_3t/data/e1_agt_3_10"
print(processCSVS(directory))
