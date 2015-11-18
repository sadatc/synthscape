library(ggplot2)

plotSummary <-function(csvFile,pdfFile, numExperiments) {
	theData <- read.csv(file=csvFile)
	pdf(pdfFile)
	numExperiments <- paste("Number of Experiments:",	numExperiments)
	print(numExperiments)


	print(
	  ggplot(theData) +
	  geom_line(aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN, color="Detectors")) +
	  geom_line(aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN, color="Extractors")) +
	  geom_line(aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN, color="Transporters")) + 
	  #ggtitle(parse("Species Fitness Growth", " () +
	  ggtitle(bquote(atop(.("Species Fitness Growth"), atop(.(numExperiments))))) + 
	  scale_color_discrete(name="Species") +
	  ylab("Fitness") +
	  xlab("Generation")
	) 

	print(
	  ggplot(theData,aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN))+
	  geom_line(color="red") +
	  ggtitle("Detector Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")

	)

	print(
	  ggplot(theData,aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN))+
	  geom_line(color="green") +
	  ggtitle("Extractor Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")

	)

	print(
	  ggplot(theData,aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN))+
	  geom_line(color="blue") +
	  ggtitle("Transporter Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")

	)


	#print(

	  #ggplot(theData,aes(x=GENERATION,y=CAPTURES_BEST_CASE_PERC))+
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
	  ggplot(theData,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
	  geom_point(alpha=0.3) +
	  ggtitle("Captures (Best Case)") +
	  ylab("Captures (Best Case)") +
	  xlab("Generation")
	)


	print(
	  ggplot(theData,aes(x=GENERATION,y=CAPTURES_MEAN))+
	  geom_point(alpha=0.3) +
	  ggtitle("Captures (Mean)") +
	  ylab("Captures (Mean)") +
	  xlab("Generation")

	)



	print(
	  ggplot(theData) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Generation") + 
	  scale_color_discrete(name="Species") +
	  ylab("Number of Agents")+
	  xlab("Generation")
	) 

	print(
	  ggplot(theData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Best Captures") + 
	  scale_color_discrete("Species") +
	  ylab("Number of Agents") +
	  xlab("Capture (Best)")
	) 
	
	print(
	  ggplot(theData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Mean Captures") + 
	  scale_color_discrete("Species") +
	  ylab("Number of Agents") +
	  xlab("Capture (Mean)")
	) 
	



	print(
	  ggplot(theData) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=E)) +
	  geom_smooth(method=lm, aes(x=GENERATION,y=E)) +
	  ggtitle("Evenness vs. Generation") + 
	  ylab("Evenness (E)") + 
	  xlab("Generation")
	)

	orderedData <-theData[order(theData$CAPTURES_MEAN),]

	print(
	  ggplot(orderedData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=E)) +
	  geom_smooth(method=lm, aes(x=CAPTURES_BEST_CASE,y=E)) +

	  ggtitle("Evenness vs. Best Capture") + 
	  ylab("Evenness (E)") +
	  xlab("Capture (Best)")
	  
	)

	print(
	  ggplot(orderedData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=E)) +
	  geom_smooth(method=lm, aes(x=CAPTURES_MEAN,y=E)) +

	  ggtitle("Evenness vs. Mean Capture") + 
	  ylab("Evenness (E)") +
	  xlab("Capture (Mean)")
	  
	)



	dev.off()
}


### Main Program

plotSummary("/Users/sadat/Dropbox/summ_env_reg_24.csv","/tmp/summ_env_reg_24.pdf",339)
plotSummary("/Users/sadat/Dropbox/summ_env_diff_24.csv","/tmp/summ_env_diff_24.pdf",392)
plotSummary("/Users/sadat/Dropbox/summ_env_vdiff_24.csv","/tmp/summ_env_vdiff_24.pdf",396)

plotSummary("/Users/sadat/Dropbox/summ_env_reg_36.csv","/tmp/summ_env_reg_36.pdf",222)
plotSummary("/Users/sadat/Dropbox/summ_env_diff_36.csv","/tmp/summ_env_diff_36.pdf",251)
plotSummary("/Users/sadat/Dropbox/summ_env_vdiff_36.csv","/tmp/summ_env_vdiff_36.pdf",257)


