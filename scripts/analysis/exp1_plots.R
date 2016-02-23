# island model
library(ggplot2)
library(plyr)
library(xtable)

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


#
# given sample.data, computes mean based on r replicates 
#
boot.mean = function(sample.data, r) {
	sample.data = na.omit(sample.data)
	n = length(sample.data)
	boot.samples = matrix(sample(sample.data,size=n*r, replace=TRUE),r,n)
	boot.statistics = apply(boot.samples,1,mean)
	return(boot.statistics)
}


#
# performs shapiro-wilk test and mann-whitney test
# reports back a data.frame
#
analyzePair <-function(model,measure,g,s) {
	rMeasure <- measure

#	print("performing shapiro test. if p<0.05, sample likely non-normal")
	rGSampleSize <- length(g)
	rGShapiro <- shapiro.test(g)

	rGShapiroW <- rGShapiro$statistic[[1]]
	rGShapiroP <- rGShapiro$p.value

	rSSampleSize <- length(s)
	rSShapiro <- shapiro.test(s)
	rSShapiroW <- rSShapiro$statistic[[1]]
	rSShapiroP <- rSShapiro$p.value


#print("mann-whitney u test. if p<0.05, the samples are non-identical populations...")
	rWilcox <- wilcox.test(g,s)
	rWilcoxW <- rWilcox$statistic[[1]]
	rWilcoxP <- rWilcox$p.value

	result.frame <- data.frame(MODEL=model,MEASURE=measure, G_SIZE=rGSampleSize,
		G_SHAP_W=rGShapiroW, G_SHAP_P=rGShapiroP, S_SIZE=rSSampleSize,
		S_SHAP_W=rSShapiroW, S_SHAP_P=rSShapiroP, WILCOX_W=rWilcoxW, 
		WILCOX_P=rWilcoxP)
		
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
	data(dist.table)
	xtable(dist.table)

	
}




plotHists <- function(exp1.df) {
	# == plot general distributions ===

	plotHistBy_S_M(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/hist_sm_captures_mean.png")
	plotHistBy_S_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/hist_sm_captures_best.png")
	plotHistBy_S_M(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/hist_sm_e2c_steps_mean.png")
	plotHistBy_S_M(exp1.df,"RATE_MOTION", "/tmp/exp1/hist_sm_rate_motion.png")
	plotHistBy_S_M(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/hist_sm_rate_comm.png")

	plotHistBy_S(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/hist_s_rate_comm.png")
	plotHistBy_S(exp1.df,"RATE_MOTION", "/tmp/exp1/hist_sm_rate_motion_single.png")

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

	# only look at non-communicating homogenous and communicating heterogenous	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |  (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none")  ,]
	

	
	# do box plots by model and interaction
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/box_mi_captures_mean.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/box_mi_e2c.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/box_mi_rate_motion.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/box_mi_rate_comm.png")

	
	# filter out island model
	exp1.df <-  exp1.df[exp1.df$MODEL!="i",]
	
	# do box plots by model and interaction of just E and A
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_mi_captures_best.png")
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_mi_captures_mean_ea.png")
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_mi_e2c_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_mi_rate_motion_ea.png")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_mi_rate_comm_ea.png")


	# do box plots by model 
	plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/ea_box_m_captures_best.png")
	plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/ea_box_m_captures_mean_ea.png")
	plotBoxPlot_M(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/ea_box_m_e2c_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_MOTION", "/tmp/exp1/ea_box_m_rate_motion_ea.png")
	plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/ea_box_m_rate_comm_ea.png")
	

	# do box plots of overall (including interactions)
	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/tmp/exp1/gr_box_m_captures_best.png")
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/tmp/exp1/gr_box_m_captures_mean.png")
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/tmp/exp1/gr_ea_box_m_e2c.png")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/tmp/exp1/gr_box_m_rate_motion.png")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/tmp/exp1/gr_box_m_rate_comm.png")

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



plotBootHist <-function(bootSample, fileName) {

	se <- sd(bootSample)
	binWidth <- diff(range(bootSample))/30
	
	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(
		ggplot(data.frame(x=bootSample), aes(x=x)) 
		+ geom_histogram(aes(y=..density..), binwidth=binWidth) 
		+ geom_density(color="red")
		
	)
	dev.off()
}


plotBootHist2Pop <-function(pop.data.frame, colName, fileName) {

	png(fileName,  
	  width     = 6,
	  height    = 6,
	  units     = "in",
	  res=360)

	print(
		ggplot(pop.data.frame, aes_string(colName, fill="POPULATION")) 
		+ geom_density(alpha = 0.2)
		
	)
	dev.off()
}



plotBootedStats <- function(exp1.df) {

	# now we will bootstrap the measures
	bootSize <- 1000


	ea.data.frame <-  exp1.df[exp1.df$MODEL!="i",]

	sg.data.frame <-  ea.data.frame[(ea.data.frame$SPECIES=="homogenous" & ea.data.frame$INTERACTIONS=="none") |  (ea.data.frame$SPECIES=="heterogenous" & ea.data.frame$INTERACTIONS!="none")  ,]

	# extract data for s and g
	s.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="heterogenous",]
	g.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="homogenous",]

	# extract the measures
	s.CAPTURES_BEST_CASE <- s.data.frame$CAPTURES_BEST_CASE
	s.CAPTURES_MEAN <- s.data.frame$CAPTURES_MEAN
	s.RES_E2C_STEPS_MEAN <- s.data.frame$RES_E2C_STEPS_MEAN
	s.RATE_MOTION <- s.data.frame$RATE_MOTION
	s.RATE_COMMUNICATION <- s.data.frame$RATE_COMMUNICATION

	s.CAPTURES_BEST_CASE <- boot.mean(s.CAPTURES_BEST_CASE,bootSize)
	s.CAPTURES_MEAN <- boot.mean(s.CAPTURES_MEAN,bootSize)
	s.RES_E2C_STEPS_MEAN <- boot.mean(s.RES_E2C_STEPS_MEAN,bootSize)
	s.RATE_MOTION <- boot.mean(s.RATE_MOTION,bootSize)
	s.RATE_COMMUNICATION <- boot.mean(s.RATE_COMMUNICATION,bootSize)

	g.CAPTURES_BEST_CASE <- g.data.frame$CAPTURES_BEST_CASE
	g.CAPTURES_MEAN <- g.data.frame$CAPTURES_MEAN
	g.RES_E2C_STEPS_MEAN <- g.data.frame$RES_E2C_STEPS_MEAN
	g.RATE_MOTION <- g.data.frame$RATE_MOTION
	g.RATE_COMMUNICATION <- g.data.frame$RATE_COMMUNICATION

	g.CAPTURES_BEST_CASE <- boot.mean(g.CAPTURES_BEST_CASE,bootSize)
	g.CAPTURES_MEAN <- boot.mean(g.CAPTURES_MEAN,bootSize)
	g.RES_E2C_STEPS_MEAN <- boot.mean(g.RES_E2C_STEPS_MEAN,bootSize)
	g.RATE_MOTION <- boot.mean(g.RATE_MOTION,bootSize)
	g.RATE_COMMUNICATION <- boot.mean(g.RATE_COMMUNICATION,bootSize)

	
	s.pop.data.frame <- data.frame(
		CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=s.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
		RATE_MOTION=s.RATE_MOTION,
		POPULATION="heterogenous"
	)


	g.pop.data.frame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION,
		POPULATION="homogenous"
	)
	
	pop.data.frame <- rbind(s.pop.data.frame,g.pop.data.frame)
	
	
	
	print(shapiro.test(s.CAPTURES_BEST_CASE))
	print(shapiro.test(g.CAPTURES_BEST_CASE))
	
	print(shapiro.test(s.CAPTURES_MEAN))
	print(shapiro.test(g.CAPTURES_MEAN))

	print(shapiro.test(s.RES_E2C_STEPS_MEAN))
	print(shapiro.test(g.RES_E2C_STEPS_MEAN))

	print(shapiro.test(s.RATE_MOTION))
	print(shapiro.test(g.RATE_MOTION))

	plotBootHist2Pop(pop.data.frame,"CAPTURES_BEST_CASE","/tmp/exp1/boot_cbc.png")
	plotBootHist2Pop(pop.data.frame,"CAPTURES_MEAN","/tmp/exp1/boot_cm.png")
	plotBootHist2Pop(pop.data.frame,"RES_E2C_STEPS_MEAN","/tmp/exp1/boot_e2c.png")
	plotBootHist2Pop(pop.data.frame,"RATE_MOTION","/tmp/exp1/boot_rm.png")

	if(1!=1) {
		plotBootHist(s.CAPTURES_BEST_CASE, "/tmp/exp1/boot_s_cbc.png")
		plotBootHist(g.CAPTURES_BEST_CASE, "/tmp/exp1/boot_g_cbc.png")
	


		plotBootHist(s.CAPTURES_MEAN, "/tmp/exp1/boot_s_cm.png")
		plotBootHist(g.CAPTURES_MEAN, "/tmp/exp1/boot_g_cm.png")

		plotBootHist(s.RES_E2C_STEPS_MEAN, "/tmp/exp1/boot_s_e2.png")
		plotBootHist(g.RES_E2C_STEPS_MEAN, "/tmp/exp1/boot_g_e2.png")

		plotBootHist(s.RATE_MOTION, "/tmp/exp1/boot_s_mo.png")
		plotBootHist(g.RATE_MOTION, "/tmp/exp1/boot_g_mo.png")
	}





}


##############################   MAIN PROCESS BEGINS ###############################
exp1.df <- read.csv(file="~/synthscape/scripts/analysis/data/exp1/all_experiments_mean_300.csv")

exp1.df <- preProcessData(exp1.df)     # factorizes, as appropriate, adjusts E2C...
exp1.df <- renameFactorValues(exp1.df) # renames for nice plots
#plotGraphs(exp1.df)

# analyze, plot...

plotHists(exp1.df)    # plots histograms

#doAnalytics(exp1.df)  #does shapiro test for normality and wilcox test for diff
#plotBoxPlots(exp1.df) # boxplots to show difference
#plotBootedStats(exp1.df)






#plot the totals

