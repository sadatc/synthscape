# island model
library(ggplot2)

#heterogenous
#trail
row_data <- read.csv(file="het_trail_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="25",row_data)
data <- row_data

row_data <- read.csv(file="het_trail_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_trail_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="het_trail_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)



#broad
row_data <- read.csv(file="het_broad_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="25",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="het_broad_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_broad_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="het_broad_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)


#unicast
row_data <- read.csv(file="het_uni_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="25",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="het_uni_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="het_uni_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="het_uni_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="heterogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)



#homogenous
#trail
row_data <- read.csv(file="hom_trail_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="25",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_trail_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="hom_trail_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_trail_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="trail",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)



#broad
row_data <- read.csv(file="hom_broad_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="25",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_broad_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="hom_broad_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_broad_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="broadcast",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)


#unicast
row_data <- read.csv(file="hom_uni_25.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="25",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_uni_50.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="50",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="hom_uni_75.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="75",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="hom_uni_100.csv")
row_data <- row_data[,c("GENERATION","RATE_COMMUNICATION")]
row_data <- cbind(model="homogenous",row_data)
row_data <- cbind(interaction="unicast",row_data)
row_data <- cbind(level="100",row_data)
data <- rbind(data,row_data)


data$model <- factor(data$model)
data$interaction <- factor(data$interaction)
data$level <- factor(data$level)

pdf("comm_plots.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=RATE_COMMUNICATION))+
xlim(0,500)+
#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=level), size=0.75)+
facet_grid( interaction ~ model, scales="free_y") +  
xlab("Generation")+ylab("Interaction Rate ") +
ggtitle("Interaction Rate Trends vs. Interaction Quality") +  
theme_bw() +
theme( text = element_text(size = 9)))

dev.off()



