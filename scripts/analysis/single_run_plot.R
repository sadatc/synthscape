library(ggplot2)

csvFile <- "/tmp/sample.csv"
pdfFile <- "/tmp/sample.pdf"

# read in the command line arguments
args <- commandArgs(TRUE)

if(length(args)==2) {
  csvFile = args[1]
  pdfFile = args[2]
} 

if(length(args)==1) {
  csvFile = args[1]
} 

row_data <- read.csv(file=csvFile)
pdf(pdfFile)

# restrict to the first 250 rows
# row_data <- head(row_data,250)

# only pick the columns we're interested in...
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","CAPTURES_TOTAL","CAPTURES_MEAN","TOT_FITNESS_MEAN","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS","TRANSPORTER_FITNESS_MEAN", "DETECTOR_FITNESS_MEAN","EXTRACTOR_FITNESS_MEAN")]



# do some data manipulation
row_data$CAPTURES_BEST_CASE_PERC <- row_data$CAPTURES_BEST_CASE/16
row_data$TOT_POP <- row_data$NUM_DETECTORS+ row_data$NUM_EXTRACTORS+ row_data$NUM_TRANSPORTERS
row_data$P1 <- row_data$NUM_DETECTORS/row_data$TOT_POP
row_data$P2 <- row_data$NUM_EXTRACTORS/row_data$TOT_POP
row_data$P3 <- row_data$NUM_TRANSPORTERS/row_data$TOT_POP
#row_data$H <- -((row_data$P1*log(row_data$P1))+(row_data$P3*log(row_data$P3)+(row_data$P2*log(row_data$P2)))
row_data$H <- -((row_data$P1*log(row_data$P1)) + (row_data$P2*log(row_data$P2)) + (row_data$P3*log(row_data$P3)) )
row_data$E <- row_data$H/log(3)

data <- row_data 
print(dim(data))
data <- subset(row_data,TOT_POP==36)
print(dim(data))

#data$gofp <- factor(data$gofp)

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



