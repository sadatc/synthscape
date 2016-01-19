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



plotGeneralHistograms <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( SPECIES ~ MODEL) 
	)
	dev.off()


}


plotGeneralHistogramsSingleModel <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( ~ SPECIES) 
	)
	dev.off()


}






compareTTest <-function(msg,x,y) {
	print(msg)
	
	print(length(x))
	print(summary(x))
	
	print(length(y))
	print(summary(y))
	
	t.test(x,y)
}


## main function

exp1.df <- read.csv(file="/Users/sadat/ExperimentResults/GeneralTrends/all_experiments_mean_300.csv")

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









print("========== ISLAND ========")
gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="i" & exp1.df$INTERACTIONS=="n"   ,]

sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="i" & exp1.df$INTERACTIONS!="n"  ,]

compareTTest("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
compareTTest("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
compareTTest("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
compareTTest("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)



print("========== EMBODIED ========")
gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="e" & exp1.df$INTERACTIONS=="n"   ,]

sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="e" & exp1.df$INTERACTIONS!="n"  ,]

compareTTest("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
compareTTest("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
compareTTest("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
compareTTest("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)


print("========== ALIFE ========")
gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="a" & exp1.df$INTERACTIONS=="n"   ,]

sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="a" & exp1.df$INTERACTIONS!="n"  ,]

compareTTest("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
compareTTest("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
compareTTest("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
compareTTest("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)



# generalists vs. communicating specalists
#gdata <- exp1.df[exp1.df$SPECIES=="g" & exp1.df$INTERACTION=="n",]
#sdata <- exp1.df[exp1.df$SPECIES=="s" & ,]

#compareTTest("CAPTURES_MEAN: g (no comm) vs. s (comm) ",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)

#print("========= NORMALITY TEST =========")

#dist_s <- exp1.df[exp1.df$SPECIES=="s" & exp1.df$DIFFICULTY=="r" & exp1.df$CAPTURES_MEAN >0,]
#dist_g <- exp1.df[exp1.df$SPECIES=="g" & exp1.df$DIFFICULTY=="r" & exp1.df$CAPTURES_MEAN >0,]

#print(length(gmeansf$CAPTURES_MEAN))
#ks.test(dist_s$CAPTURES_BEST_CASE,dist_g$CAPTURES_BEST_CASE)



#pdf("/tmp/exp1_plots.pdf")



#print(
#	qplot(dist_s$CAPTURES_BEST_CASE, geom="histogram") 
#)

#print(
#	qplot(dist_g$CAPTURES_BEST_CASE, geom="histogram") 
#)

#dev.off()





#shapiro.test(smeans)

# == plot general distributions ===

plotGeneralHistograms(exp1.df,"CAPTURES_MEAN", "/tmp/gen_dist_captures_mean.png")
plotGeneralHistograms(exp1.df,"CAPTURES_BEST_CASE", "/tmp/gen_dist_captures_best.png")
plotGeneralHistograms(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/gen_dist_d2c_steps_mean.png")
plotGeneralHistograms(exp1.df,"RATE_MOTION", "/tmp/gen_dist_rate_motion.png")
plotGeneralHistogramsSingleModel(exp1.df,"RATE_MOTION", "/tmp/gen_dist_rate_motion_single.png")
plotGeneralHistograms(exp1.df,"RATE_COMMUNICATION", "/tmp/gen_dist_rate_comm.png")
plotGeneralHistogramsSingleModel(exp1.df,"RATE_COMMUNICATION", "/tmp/gen_dist_rate_comm_single.png")


# == plot  distributions minus lower values ===

exp1.df2 <-  exp1.df[exp1.df$CAPTURES_MEAN >0.1,]

plotGeneralHistograms(exp1.df2,"CAPTURES_MEAN", "/tmp/n_dist_captures_mean.png")
plotGeneralHistograms(exp1.df2,"CAPTURES_BEST_CASE", "/tmp/n_dist_captures_best.png")
plotGeneralHistograms(exp1.df2,"RES_D2C_STEPS_MEAN", "/tmp/n_dist_d2c_steps_mean.png")
plotGeneralHistograms(exp1.df2,"RATE_MOTION", "/tmp/n_dist_rate_motion.png")
plotGeneralHistogramsSingleModel(exp1.df2,"RATE_MOTION", "/tmp/n_dist_rate_motion_single.png")
plotGeneralHistograms(exp1.df2,"RATE_COMMUNICATION", "/tmp/n_dist_rate_comm.png")
plotGeneralHistogramsSingleModel(exp1.df2,"RATE_COMMUNICATION", "/tmp/n_dist_rate_comm_single.png")

testData <- exp1.df[exp1.df$MODEL=="i",]
print(summary(testData$RATE_MOTION))


