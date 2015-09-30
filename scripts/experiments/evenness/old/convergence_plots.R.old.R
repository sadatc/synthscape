# island model
library(ggplot2)
row_data <- read.csv(file="ihn_convergence_stats.csv")
row_data <- cbind(type="heterogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="iht_convergence_stats.csv")
row_data <- cbind(type="heterogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="ihb_convergence_stats.csv")
row_data <- cbind(type="heterogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ihu_convergence_stats.csv")
row_data <- cbind(type="heterogenous, unicast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ihon_convergence_stats.csv")
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ihot_convergence_stats.csv")
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ihob_convergence_stats.csv")
row_data <- cbind(type="homogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ihou_convergence_stats.csv")
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)

pdf("island_convergence_plots.pdf")

print(ggplot(data,aes(x=generations))+geom_histogram(fill="white", color="black")+facet_wrap( ~ type, scale="free", ncol=2) + ggtitle("Island Model Population Convergence Distribution") +  theme(text = element_text(size = 9)))

dev.off()

# embodied

row_data <- read.csv(file="ehen_convergence_stats.csv")
row_data <- cbind(type="heterogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="ehet_convergence_stats.csv")
row_data <- cbind(type="heterogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="eheb_convergence_stats.csv")
row_data <- cbind(type="heterogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="eheu_convergence_stats.csv")
row_data <- cbind(type="heterogenous, unicast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ehon_convergence_stats.csv")
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ehot_convergence_stats.csv")
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)

#row_data <- read.csv(file="ehob_convergence_stats.csv")
#row_data <- cbind(type="homogenous, broadcast",row_data)
#data <- rbind(data,row_data)

#row_data <- read.csv(file="ehou_convergence_stats.csv")
#row_data <- cbind(type="homogenous, unicast",row_data)
#data <- rbind(data,row_data)

data$type <- factor(data$type)

pdf("embodied_convergence_plots.pdf")

print(ggplot(data,aes(x=generations))+geom_histogram(fill="white", color="black")+facet_wrap( ~ type, scale="free", ncol=2) + ggtitle("Embodied Model Population Convergence Distribution") +  theme(text = element_text(size = 7)))

dev.off()






