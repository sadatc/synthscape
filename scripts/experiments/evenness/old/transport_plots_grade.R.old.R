# island model
library(ggplot2)

#heterogenous
#trail
row_data <- read.csv(file="het_broad_1s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(signals="1",row_data)
data <- row_data

row_data <- read.csv(file="het_broad_2s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(signals="2",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_broad_3s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(signals="3",row_data)
data <- rbind(data,row_data)




row_data <- read.csv(file="het_uni_1s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(signals="1",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_uni_2s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(signals="2",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_uni_3s.csv")
row_data <- row_data[,c("GENERATION","INTERVAL_TRANSPORTATION")]

row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(signals="3",row_data)
data <- rbind(data,row_data)





data$model <- factor(data$model)
data$interaction <- factor(data$interaction)
data$signal <- factor(data$signal)

pdf("transport_plots_signal.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=INTERVAL_TRANSPORTATION))+
xlim(0,500)+
#sscale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=signal), size=0.75)+
facet_grid( interaction ~ model, scales="free_y") +  
xlab("Generation")+ylab("Transport Rate ") +
ggtitle("Transport Rate Trends for Signals 1,2,3") +  
theme_bw() +
theme( text = element_text(size = 9)))

dev.off()



