library(ggplot2)

#random
row_data <- read.csv(file="~/evenness_data/converged/evn_50_50_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="50",row_data)
row_data <- cbind(gosc="50",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
# data is the final data container
# the first time, we just dump what was read in row_data
# next time around we use rbind to concatenate
data <- row_data 

row_data <- read.csv(file="~/evenness_data/converged/evn_50_5_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="50",row_data)
row_data <- cbind(gosc="5",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="~/evenness_data/converged/evn_5_50_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="5",row_data)
row_data <- cbind(gosc="50",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_5_5_pln_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="5",row_data)
row_data <- cbind(gosc="5",row_data)
row_data <- cbind(resource_distribution="evenly distributed",row_data)
data <- rbind(data,row_data)



#barricaded
row_data <- read.csv(file="~/evenness_data/converged/evn_50_50_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="50",row_data)
row_data <- cbind(gosc="50",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_50_5_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="50",row_data)
row_data <- cbind(gosc="5",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="~/evenness_data/converged/evn_5_50_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="5",row_data)
row_data <- cbind(gosc="50",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="~/evenness_data/converged/evn_5_5_dff_c.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
row_data <- cbind(gofp="5",row_data)
row_data <- cbind(gosc="5",row_data)
row_data <- cbind(resource_distribution="barricaded",row_data)
data <- rbind(data,row_data)





data$gofp <- factor(data$gofp)
data$gosc <- factor(data$gosc)
data$resource_distribution <- factor(data$resource_distribution)

pdf("de_capture_plots.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
xlim(0,1000)+
#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=resource_distribution), size=0.25)+
facet_grid( gofp ~ gosc, scales="free_y") +  
xlab("Generation")+ylab("Resources Capture %") +
ggtitle("Capture Trends (Algo-Params (gofp/gosc) vs. Resource Distribution)") +  
theme_bw() +
theme( text = element_text(size = 11))
+theme(legend.position="bottom",legend.direction="horizontal")
+scale_color_discrete(name = "Resource Distribution")
)
dev.off()



