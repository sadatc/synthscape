# island model
library(ggplot2)
library(plyr)

plotGraphs <-function(data) {

	png("/tmp/isl_capture.png",  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(ggplot(data,aes(x=GENERATION,y=CAPTURES_TOTAL))+
	geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
	facet_wrap( INTERACTIONS ~ MODEL, scale="free", ncol=2) + 
	ggtitle("Island Model Capture Trends") +  
	theme_bw() +
	theme(text = element_text(size = 9)))

	dev.off()

}



plotHistBy_S_M <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)



	print(
		ggplot(dataFrame,aes_string(x=colName, fill="INTERACTIONS")) +
		geom_histogram(alpha=0.4) +
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

	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( ~ POPULATION) 
	)
	dev.off()
}






analyzePair <-function(model,measure,g,s) {
#	print("**** Analyzing Pair (G vs. S)****")
	rMeasure <- measure

#	print("performing shapiro test. if p<0.05, sample likely non-normal")
	rGSampleSize <- length(g)
	rGShapiro <- shapiro.test(g)
	rGShapiroW <- rGShapiro$statistic[[1]]
	rGShapiroP <- rGShapiro$p.value

#	print(rGShapiro)
#	print(rGShapiroW)
#	print(rGShaoroP)


	rSSampleSize <- length(s)
	rSShapiro <- shapiro.test(s)
	rSShapiroW <- rSShapiro$statistic[[1]]
	rSShapiroP <- rSShapiro$p.value

#	print(rSShapiro)
#	print(rSShapiroW)
#	print(rSShaoroP)


	#print("performing mann-whitney u test. if p<0.05, the samples are non-identical populations...")
	rWilcox <- wilcox.test(g,s)
	rWilcoxW <- rWilcox$statistic[[1]]
	rWilcoxP <- rWilcox$p.value
	
#	print(rWilcox)
#	print(rWilcoxW)
#	print(rWilcoxP)

#	print("******* Done Analyzing Pair ******")

	result.frame <- data.frame(MODEL=model,MEASURE=measure, G_SIZE=rGSampleSize, G_SHAP_W=rGShapiroW, G_SHAP_P=rGShapiroP, 
		S_SIZE=rSSampleSize, S_SHAP_W=rSShapiroW, S_SHAP_P=rSShapiroP, 
		WILCOX_W=rWilcoxW, WILCOX_P=rWilcoxP)
		
	return(result.frame)
}



doAnalytics <- function(exp1.df) {
	dist.table <- data.frame()

#	print("========== ISLAND ========")
	
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="island" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="island" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table, analyzePair("island","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzePair("island","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))

	dist.table <- rbind(dist.table,analyzePair("island","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))

	dist.table <- rbind(dist.table,analyzePair("island","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	


#	print("========== EMBODIED ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="embodied" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="embodied" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzePair("embodied","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzePair("embodied","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzePair("embodied","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzePair("embodied","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))

#	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="alife" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="alife" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzePair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzePair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzePair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzePair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	
#		print("========== EMBODIED (both comm and non-comm) ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="embodied" ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="embodied" ,]

	dist.table <- rbind(dist.table,analyzePair("embodied_general","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzePair("embodied_general","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzePair("embodied_general","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	
	dist.table <- rbind(dist.table,analyzePair("embodied_general","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))

#	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="alife"    ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="alife"  ,]

	dist.table <- rbind(dist.table,analyzePair("alife_general","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzePair("alife_general","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	
	dist.table <- rbind(dist.table,analyzePair("alife_general","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	
	dist.table <- rbind(dist.table,	analyzePair("alife_general","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))

	print(dist.table)

	
}


plotHists <- function(exp1.df) {
	# == plot general distributions ===

	plotHistBy_S_M(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/hist_sm_captures_mean.png")
	plotHistBy_S_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/hist_sm_captures_best.png")
	plotHistBy_S_M(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/hist_sm_d2c_steps_mean.png")
	plotHistBy_S_M(exp1.df,"RATE_MOTION", "/tmp/exp1/hist_sm_rate_motion.png")
	plotHistBy_S_M(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/hist_sm_rate_comm.png")

	
	
	plotHistBy_S(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/hist_s_rate_comm.png")
	plotHistBy_S(exp1.df,"RATE_MOTION", "/tmp/exp1/hist_sm_rate_motion_single.png")

	# == plot  distributions minus lower values ===

	exp1.df2 <-  exp1.df[exp1.df$CAPTURES_MEAN >0.1,]

	plotHistBy_S_M(exp1.df2,"CAPTURES_MEAN", "/tmp/exp1/x_hist_sm_captures_mean.png")
	plotHistBy_S_M(exp1.df2,"CAPTURES_BEST_CASE", "/tmp/exp1/x_hist_sm_captures_best.png")
	plotHistBy_S_M(exp1.df2,"RES_E2C_STEPS_MEAN", "/tmp/exp1/x_hist_sm_d2c_steps_mean.png")
	plotHistBy_S_M(exp1.df2,"RATE_MOTION", "/tmp/exp1/x_hist_sm_rate_motion.png")
	plotHistBy_S(exp1.df2,"RATE_MOTION", "/tmp/exp1/x_hist_sm_rate_motion_single.png")
	plotHistBy_S_M(exp1.df2,"RATE_COMMUNICATION", "/tmp/exp1/x_hist_sm_rate_comm.png")
	plotHistBy_S(exp1.df2,"RATE_COMMUNICATION", "/tmp/exp1/x_hist_s_rate_comm.png")


	testData <- exp1.df[exp1.df$MODEL=="island",]
	print(summary(testData$RATE_MOTION))
}




plotBoxPlot_M_I <-function(dataFrame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)


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
	  

	print(
		ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
		geom_boxplot(aes(fill=POPULATION), notch=TRUE) 
	)
	dev.off()
}



plotBoxPlots <- function(exp1.df) {
	orig <- exp1.df
	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |  (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none")  ,]
	
	
	
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/box_mi_captures_mean.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/box_mi_d2c.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/box_mi_rate_motion.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/box_mi_rate_comm.png")

	
	# filter out island model
	exp1.df <-  exp1.df[exp1.df$MODEL!="i",]
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_mi_captures_mean_ea.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_mi_d2c_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_mi_rate_motion_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_mi_rate_comm_ea.png")

	
	plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_m_captures_best.png")
	plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_m_captures_mean_ea.png")
	plotBoxPlot_M(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_m_d2c_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_m_rate_motion_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_m_rate_comm_ea.png")
	

	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/gr_box_m_captures_best.png")
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/gr_box_m_captures_mean.png")
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/gr_ea_box_m_d2c.png")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/tmp/exp1/gr_box_m_rate_motion.png")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/gr_box_m_rate_comm.png")

	exp1.df <-  orig[orig$MODEL!="i",]

	plotBoxPlot(orig,"CAPTURES_BEST_CASE", "/tmp/exp1/ult_box_m_captures_best.png")
	plotBoxPlot(orig,"CAPTURES_MEAN", "/tmp/exp1/ult_box_m_captures_mean.png")
	plotBoxPlot(orig,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ult_ea_box_m_d2c.png")
	plotBoxPlot(orig,"RATE_MOTION", "/tmp/exp1/ult_box_m_rate_motion.png")
	plotBoxPlot(orig,"RATE_COMMUNICATION", "/tmp/exp1/ult_box_m_rate_comm.png")


	
	
}


plotBoxPlots2 <- function(exp1.df) {
	orig <- exp1.df
	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="homogenous") |  (exp1.df$SPECIES=="heterogenous")  ,]
	
	
	
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/box_mi_captures_mean.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/box_mi_d2c.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/box_mi_rate_motion.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/box_mi_rate_comm.png")


	
	# filter out island model
	exp1.df <-  exp1.df[exp1.df$MODEL!="i",]
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_mi_captures_mean_ea.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_mi_d2c_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_mi_rate_motion_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_mi_rate_comm_ea.png")

	
	plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_m_captures_best.png")
	plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_m_captures_mean_ea.png")
	plotBoxPlot_M(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_m_d2c_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_m_rate_motion_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_m_rate_comm_ea.png")
	

	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/gr_box_m_captures_best.png")
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/gr_box_m_captures_mean.png")
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/gr_ea_box_m_d2c.png")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/tmp/exp1/gr_box_m_rate_motion.png")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/gr_box_m_rate_comm.png")

	exp1.df <-  orig[orig$MODEL!="i",]

	plotBoxPlot(orig,"CAPTURES_BEST_CASE", "/tmp/exp1/ult_box_m_captures_best.png")
	plotBoxPlot(orig,"CAPTURES_MEAN", "/tmp/exp1/ult_box_m_captures_mean.png")
	plotBoxPlot(orig,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ult_ea_box_m_d2c.png")
	plotBoxPlot(orig,"RATE_MOTION", "/tmp/exp1/ult_box_m_rate_motion.png")
	plotBoxPlot(orig,"RATE_COMMUNICATION", "/tmp/exp1/ult_box_m_rate_comm.png")
}


# Function turns the internal abbreviated values to full expansions
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a","e","i"), to=c("alife","embodied","island"))
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("n","b","u","t"), to=c("none","broadcast","unicast","trail"))
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("homogenous","heterogenous"))
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("homogenous","heterogenous"))
	return(dataFrame)
}

# this function:
# 1. makes POPULATION a synonym for species
# 2. sets RES_E2C_STEPS_MEAN = RES_E2C_STEPS_MEAN * CAPTURES_MEAN
preProcessData <- function(exp1.df) {
	# set all factors
	exp1.df$MODEL <- factor(exp1.df$MODEL)
	exp1.df$SPECIES <- factor(exp1.df$SPECIES)
	exp1.df$POPULATION <- exp1.df$SPECIES
	exp1.df$POPULATION <- factor(exp1.df$POPULATION)	
	exp1.df$INTERACTIONS <- factor(exp1.df$INTERACTIONS)
	exp1.df$COMPLEXITY <- factor(exp1.df$COMPLEXITY)
	exp1.df$CLONES <- factor(exp1.df$CLONES)
	exp1.df$GRIDS <- factor(exp1.df$GRIDS)
	exp1.df$RESOURCES <- factor(exp1.df$RESOURCES)
	exp1.df$SITES <- factor(exp1.df$SITES)
	exp1.df$OBSTACLES <- factor(exp1.df$OBSTACLES)
	exp1.df$DIFFICULTY <- factor(exp1.df$DIFFICULTY)
	
	exp1.df$RES_E2C_STEPS_MEAN <- (exp1.df$RES_E2C_STEPS_MEAN*exp1.df$CAPTURES_MEAN)

	return(exp1.df)
}
##############################   MAIN PROCESS BEGINS ###############################
exp1.df <- read.csv(file="~/synthscape/scripts/analysis/data/exp1/all_experiments_mean_300.csv")

exp1.df <- preProcessData(exp1.df)
exp1.df <- renameFactorValues(exp1.df)
#plotGraphs(exp1.df)

# analyze, plot...

doAnalytics(exp1.df)
plotHists(exp1.df)
#plotBoxPlots2(exp1.df)

#plot the totals

