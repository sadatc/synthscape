# island model
library(ggplot2)
library(plyr)

plotGraphs <-function(data) {
	data <- renameFactors(data)
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






analyzePair <-function(msg,g,s) {
	print("**** Analyzing Pair (G vs. S)****")
	print(msg)
	


	print("performing shapiro test. if p<0.05, sample likely non-normal")
	print(length(g))
	print(shapiro.test(g))

	print(length(s))
	print(shapiro.test(s))

	print("performing mann-whitney u test. if p<0.05, the samples are non-identical populations...")
	print(wilcox.test(g,s))

	print("******* Done Analyzing Pair ******")
}




doAnalytics <- function(exp1.df) {
	print("========== ISLAND ========")
	
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="i" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="i" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)

	analyzePair("RES_D2C_STEPS_MEAN",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)

	analyzePair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION)
	
	


	print("========== EMBODIED ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="e" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="e" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION)

	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="a" & exp1.df$INTERACTIONS=="n"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="a" & exp1.df$INTERACTIONS!="n"  ,]

	analyzePair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION)
	
	
		print("========== EMBODIED (both comm and non-comm) ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="e" ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="e" ,]

	analyzePair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION)

	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="g"  & exp1.df$MODEL=="a"    ,]

	sdata <- exp1.df[exp1.df$SPECIES=="s" &  exp1.df$MODEL=="a"  ,]

	analyzePair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN)
	analyzePair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE)
	analyzePair("RES_D2C_STEPS_MEAN",gdata$RES_D2C_STEPS_MEAN,sdata$RES_D2C_STEPS_MEAN)
	analyzePair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION)


	
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


plotBoxPlots2 <- function(exp1.df) {
	orig <- exp1.df
	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="g") |  (exp1.df$SPECIES=="s")  ,]
	
	
	
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/tmp/box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/tmp/box_mi_captures_mean.png")
	plotBoxPlot_M_I(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/tmp/box_mi_d2c.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/tmp/box_mi_rate_motion.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/tmp/box_mi_rate_comm.png")


	
	# filter out island model
	exp1.df <-  exp1.df[exp1.df$MODEL!="i",]
	
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/tmp/ea_box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/tmp/ea_box_mi_captures_mean_ea.png")
	plotBoxPlot_M_I(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/tmp/ea_box_mi_d2c_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/tmp/ea_box_mi_rate_motion_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/tmp/ea_box_mi_rate_comm_ea.png")

	
	plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/tmp/ea_box_m_captures_best.png")
	plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/tmp/tmp/ea_box_m_captures_mean_ea.png")
	plotBoxPlot_M(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/tmp/ea_box_m_d2c_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_MOTION", "/tmp/tmp/ea_box_m_rate_motion_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/tmp/tmp/ea_box_m_rate_comm_ea.png")
	

	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/tmp/tmp/gr_box_m_captures_best.png")
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/tmp/tmp/gr_box_m_captures_mean.png")
	plotBoxPlot(exp1.df,"RES_D2C_STEPS_MEAN", "/tmp/tmp/gr_ea_box_m_d2c.png")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/tmp/tmp/gr_box_m_rate_motion.png")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/tmp/tmp/gr_box_m_rate_comm.png")

	exp1.df <-  orig[orig$MODEL!="i",]

	plotBoxPlot(orig,"CAPTURES_BEST_CASE", "/tmp/tmp/ult_box_m_captures_best.png")
	plotBoxPlot(orig,"CAPTURES_MEAN", "/tmp/tmp/ult_box_m_captures_mean.png")
	plotBoxPlot(orig,"RES_D2C_STEPS_MEAN", "/tmp/tmp/ult_ea_box_m_d2c.png")
	plotBoxPlot(orig,"RATE_MOTION", "/tmp/tmp/ult_box_m_rate_motion.png")
	plotBoxPlot(orig,"RATE_COMMUNICATION", "/tmp/tmp/ult_box_m_rate_comm.png")


	
	
}




## main function

	# read main data
	exp1.df <- read.csv(file="~/synthscape/scripts/analysis/data/exp1/all_experiments_mean_300.csv")
	
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

	plotGraphs(exp1.df)

	# analyze, plot...
	#doTmpAnalytics(exp1.df)
	#doAnalytics(exp1.df)
	#plotHists(exp1.df)
	#plotBoxPlots2(exp1.df)
	
	#plot the totals

