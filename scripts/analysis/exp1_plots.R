# island model
library(ggplot2)
library(plyr)

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

renameFactors <- function(dataFrame) {
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a","e","i"), to=c("alife","embodied","island"))
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("n","b","u","t"), to=c("none","broadcast","unicast","trail"))
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("homogenous","heterogenous"))
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("homogenous","heterogenous"))
	return(dataFrame)
}

plotHistBy_S_M <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	dataFrame <- renameFactors(dataFrame)

	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( POPULATION ~ MODEL) 
	)
	dev.off()
}

plotHistBy_S <-function(dataFrame, colName, fileName) {
	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	dataFrame <- renameFactors(dataFrame)
	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( ~ POPULATION) 
	)
	dev.off()
}






analyzePair <-function(msg,x,y) {
	print(msg)
	
	print(length(x))
	print(summary(x))
	
	print(length(y))
	print(summary(y))
	
	t.test(x,y)
}


doAnalytics <- function(exp1.df) {
	print("========== ISLAND ========")
	
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="i" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="i" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)



	print("========== EMBODIED ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="e" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="e" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)


	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="a" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="a" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN: g vs. s",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE: g vs. s",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN: g vs. s",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION: g vs. s",gdata$RATE_MOTION,sdata$RATE_MOTION)



	# generalists vs. communicating specalists
	#gdata <- exp1.df[exp1.df$SPECIES=="g" & exp1.df$INTERACTION=="n",]
	#sdata <- exp1.df[exp1.df$SPECIES=="s" & ,]

	#analyzePair("CAPTURES_MEAN: g (no comm) vs. s (comm) ",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)

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
}


plotHists <- function(exp1.df) {
	# == plot general distributions ===

	plotHistBy_S_M(exp1.df,"CAPTURES_MEAN", "/tmp/hist_sm_captures_mean.png")
	plotHistBy_S_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/hist_sm_captures_best.png")
	plotHistBy_S_M(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/hist_sm_d2c_steps_mean.png")
	plotHistBy_S_M(exp1.df,"RATE_MOTION", "/tmp/hist_sm_rate_motion.png")
	plotHistBy_S_M(exp1.df,"RATE_COMMUNICATION", "/tmp/hist_sm_rate_comm.png")

	
	
	plotHistBy_S(exp1.df,"RATE_COMMUNICATION", "/tmp/hist_s_rate_comm.png")
	plotHistBy_S(exp1.df,"RATE_MOTION", "/tmp/hist_sm_rate_motion_single.png")

	# == plot  distributions minus lower values ===

	exp1.df2 <-  exp1.df[exp1.df$CAPTURES_MEAN >0.1,]

	plotHistBy_S_M(exp1.df2,"CAPTURES_MEAN", "/tmp/x_hist_sm_captures_mean.png")
	plotHistBy_S_M(exp1.df2,"CAPTURES_BEST_CASE", "/tmp/x_hist_sm_captures_best.png")
	plotHistBy_S_M(exp1.df2,"RES_D2C_STEPS_MEAN", "/tmp/x_hist_sm_d2c_steps_mean.png")
	plotHistBy_S_M(exp1.df2,"RATE_MOTION", "/tmp/x_hist_sm_rate_motion.png")
	plotHistBy_S(exp1.df2,"RATE_MOTION", "/tmp/x_hist_sm_rate_motion_single.png")
	plotHistBy_S_M(exp1.df2,"RATE_COMMUNICATION", "/tmp/x_hist_sm_rate_comm.png")
	plotHistBy_S(exp1.df2,"RATE_COMMUNICATION", "/tmp/x_hist_s_rate_comm.png")


	testData <- exp1.df[exp1.df$MODEL=="i",]
	print(summary(testData$RATE_MOTION))
}




plotBoxPlot_M_I <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	dataFrame <- renameFactors(dataFrame)

	print(
		ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
		geom_boxplot(aes(fill=POPULATION), notch=TRUE) +
		facet_grid( MODEL ~ INTERACTIONS ) 
	)
	dev.off()
}

plotBoxPlot_M <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)
	  
	dataFrame <- renameFactors(dataFrame)

	print(
		ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
		geom_boxplot(aes(fill=POPULATION), notch=TRUE) +
		facet_grid( ~ MODEL ) 
	)
	dev.off()
}

plotBoxPlot <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)
	  
	dataFrame <- renameFactors(dataFrame)

	print(
		ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
		geom_boxplot(aes(fill=POPULATION), notch=TRUE) 
	)
	dev.off()
}



plotBoxPlots <- function(exp1.df) {
	orig <- exp1.df
	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="g" & exp1.df$INTERACTIONS=="n") |  (exp1.df$SPECIES=="s" & exp1.df$INTERACTIONS!="n")  ,]
	
	
	
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/box_mi_captures_mean.png")
	plotBoxPlot_M_I(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/box_mi_d2c.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/box_mi_rate_motion.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/box_mi_rate_comm.png")

	
	# filter out island model
	exp1.df <-  exp1.df[exp1.df$MODEL!="i",]
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/ea_box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/ea_box_mi_captures_mean_ea.png")
	plotBoxPlot_M_I(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/ea_box_mi_d2c_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/ea_box_mi_rate_motion_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/ea_box_mi_rate_comm_ea.png")

	
	plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/ea_box_m_captures_best.png")
	plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/tmp/ea_box_m_captures_mean_ea.png")
	plotBoxPlot_M(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/ea_box_m_d2c_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_MOTION", "/tmp/ea_box_m_rate_motion_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/tmp/ea_box_m_rate_comm_ea.png")
	

	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/tmp/gr_box_m_captures_best.png")
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/tmp/gr_box_m_captures_mean.png")
	plotBoxPlot(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/gr_ea_box_m_d2c.png")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/tmp/gr_box_m_rate_motion.png")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/tmp/gr_box_m_rate_comm.png")

	exp1.df <-  orig[orig$MODEL!="i",]

	plotBoxPlot(orig,"CAPTURES_BEST_CASE", "/tmp/ult_box_m_captures_best.png")
	plotBoxPlot(orig,"CAPTURES_MEAN", "/tmp/ult_box_m_captures_mean.png")
	plotBoxPlot(orig,"RES_D2C_STEPS_MEAN", "/tmp/ult_ea_box_m_d2c.png")
	plotBoxPlot(orig,"RATE_MOTION", "/tmp/ult_box_m_rate_motion.png")
	plotBoxPlot(orig,"RATE_COMMUNICATION", "/tmp/ult_box_m_rate_comm.png")


	
	
}

## main function

	# read main data
	exp1.df <- read.csv(file="/Users/sadat/ExperimentResults/GeneralTrends/all_experiments_mean_300.csv")
	
	exp1.df$POPULATION <- exp1.df$SPECIES


	# set all factors
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
	
	exp1.df$RES_D2C_STEPS_MEAN <- (exp1.df$RES_D2C_STEPS_MEAN*exp1.df$CAPTURES_MEAN)
	

	# analyze, plot...
	doAnalytics(exp1.df)
	#plotHists(exp1.df)
	#plotBoxPlots(exp1.df)
	
	#plot the totals

