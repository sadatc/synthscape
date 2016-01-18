# island model
library(ggplot2)

plotGraphs <-function() {
	row_data <- read.csv(file="isl_het_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, no interaction",row_data)
	data <- row_data

	row_data <- read.csv(file="isl_het_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="isl_het_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="isl_het_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, unicast",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="isl_hom_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, no interaction",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="isl_hom_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="isl_hom_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="isl_hom_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, unicast",row_data)
	data <- rbind(data,row_data)

	data$type <- factor(data$type)

	# pdf("isl_capture.pdf")
	png("isl_capture.png",  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(ggplot(data,aes(x=GENERATION,y=CAPTURES_TOTAL))+
	geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
	facet_wrap( ~ type, scale="free", ncol=2) + 
	ggtitle("Island Model Capture Trends") +  
	theme_bw() +
	theme(text = element_text(size = 9)))

	dev.off()



	row_data <- read.csv(file="emb_het_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, no interaction",row_data)
	data <- row_data

	row_data <- read.csv(file="emb_het_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="emb_het_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="emb_het_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, unicast",row_data)
	data <- rbind(data,row_data)



	row_data <- read.csv(file="emb_hom_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, no interaction",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="emb_hom_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="emb_hom_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="emb_hom_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, unicast",row_data)
	data <- rbind(data,row_data)

	data$type <- factor(data$type)


	png("emb_capture.png",  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(ggplot(data,aes(x=GENERATION,y=CAPTURES_TOTAL))+
	geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
	facet_wrap( ~ type, scale="free", ncol=2) + 
	ggtitle("Embodied Model Capture Trends") +  
	theme_bw() +
	theme(text = element_text(size = 9)))

	dev.off()


	row_data <- read.csv(file="ali_het_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, no interaction",row_data)
	data <- row_data

	row_data <- read.csv(file="ali_het_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="ali_het_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="ali_het_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="heterogenous, unicast",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="ali_hom_none.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, no interaction",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="ali_hom_trail.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, trail",row_data)
	data <- rbind(data,row_data)


	row_data <- read.csv(file="ali_hom_broad.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, broadcast",row_data)
	data <- rbind(data,row_data)

	row_data <- read.csv(file="ali_hom_uni.csv")
	row_data <- row_data[,c("GENERATION","CAPTURES_TOTAL")]
	row_data <- cbind(type="homogenous, unicast",row_data)
	data <- rbind(data,row_data)

	data$type <- factor(data$type)


	png("ali_capture.png",  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(ggplot(data,aes(x=GENERATION,y=CAPTURES_TOTAL))+
	geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
	facet_wrap( ~ type, scale="free", ncol=2) + 
	ggtitle("ALife Model Capture Tends") +  
	theme_bw() +
	theme(text = element_text(size = 9)))

	dev.off()
}


## main function

exp1.df <- read.csv(file="/Users/sadat/ExperimentResults/GeneralTrends/all_experiments_mean.csv")

exp1.df$MODEL <- factor(exp1.df$MODEL)
exp1.df$SPECIES <- factor(exp1.df$SPECIES)
exp1.df$INTERACTIONS <- factor(exp1.df$INTERACTIONS)
exp1.df$COMPLEXITY <- factor(exp1.df$COMPLEXITY)
exp1.df$CLONES <- factor(exp1.df$CLONES)
exp1.df$GRIDS <- factor(exp1.df$GRIDS)
exp1.df$RESOURCES <- factor(exp1.df$RESOURCES)
exp1.df$SITES <- factor(exp1.df$SITES)
exp1.df$OBSTACLES <- factor(exp1.df$OBSTACLES)
exp1.df$DIFFICULTY <- factor(exp1.df$DIFFICULTY)


# some analysis
#gmeans <- subset(exp1.df, select=CAPTURES_MEAN, subset=(SPECIES=="g"))
#gmeans <- as.numeric(exp1.df[exp1.df$SPECIES=="g",exp1.df$CAPTURES_MEAN])

#gmeans <- exp1.df[exp1.df$SPECIES=="g",]
gmeans <- exp1.df[exp1.df$SPECIES=="g" & ,]

print(summary(gmeans$CAPTURES_MEAN))
print(class(gmeans$CAPTURES_MEAN))
print(length(gmeans$CAPTURES_MEAN))
shapiro.test(gmeans$CAPTURES_MEAN)
#smeans <- subset(exp1.df, select=CAPTURES_MEAN, subset=(SPECIES=="s" && CAPTURES_MEAN!=NA))
#print(head(gmeans))
#
#shapiro.test(smeans)






