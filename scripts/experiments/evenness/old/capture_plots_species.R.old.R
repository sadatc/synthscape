library(ggplot2)

#random
row_data <- read.csv(file="~/evenness_data/converged/evn_50_50_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="50_50",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
# data is the final data container
# the first time, we just dump what was read in row_data
# next time around we use rbind to concatenate
data <- row_data 

row_data <- read.csv(file="~/evenness_data/converged/evn_50_5_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="50_5",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="~/evenness_data/converged/evn_5_50_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="5_50",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_5_5_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="5_5",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)



#barricaded
row_data <- read.csv(file="~/evenness_data/converged/evn_50_50_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="50_50",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_50_5_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="50_5",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="~/evenness_data/converged/evn_5_50_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="5_50",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_5_5_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(params="5_5",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)





data$params <- factor(data$params)
data$resource_distribution <- factor(data$resource_distribution)

pdf("de_species_plot.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(
ggplot(data,aes(x=GENERATION,y= NUM_DETECTORS))+
xlim(0,1000)+
geom_line(aes(y=NUM_DETECTORS, color="Detector"), size=0.25)+
geom_line(aes(y=NUM_EXTRACTORS, color="Extractor"), size=0.25)+
geom_line(aes(y=NUM_TRANSPORTERS, color="Transporter"), size=0.25)+
facet_grid( params ~ resource_distribution, scales="free_y") +  
xlab("Generation")+ylab("Number of Agents") +
ggtitle("Number of Agents (Resource Distribution vs. Algo-Params vs. Species)") +  
theme_bw() +
theme( text = element_text(size = 11))
+theme(legend.position="bottom",legend.direction="horizontal")
+scale_color_discrete(name = "Species")
)
dev.off()



