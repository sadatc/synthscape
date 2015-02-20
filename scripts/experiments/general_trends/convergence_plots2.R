# island model
library(ggplot2)
row_data <- read.csv(file="ehon10_convergence_stats.csv")
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="ehot10_convergence_stats.csv")
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="ehob10_convergence_stats.csv")
row_data <- cbind(type="homogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ehou10_convergence_stats.csv")
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)

pdf("embodied_convergence_plots.pdf")

print(ggplot(data,aes(x=generations))+geom_histogram(fill="white", color="black")+facet_wrap( ~ type, scale="free", ncol=2) + ggtitle("Embodied Model Population Convergence Distribution") +  theme(text = element_text(size = 9)))

dev.off()

# alife

row_data <- read.csv(file="ahon10_convergence_stats.csv")
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="ahot10_convergence_stats.csv")
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)


#row_data <- read.csv(file="ahob10_convergence_stats.csv")
#row_data <- cbind(type="homogenous, broadcast",row_data)
#data <- rbind(data,row_data)

row_data <- read.csv(file="ahou10_convergence_stats.csv")
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)

pdf("alife_convergence_plots.pdf")

print(ggplot(data,aes(x=generations))+geom_histogram(fill="white", color="black")+facet_wrap( ~ type, scale="free", ncol=2) + ggtitle("ALIFE Model Population Convergence Distribution") +  theme(text = element_text(size = 7)))

dev.off()






