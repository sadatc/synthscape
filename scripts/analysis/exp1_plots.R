# island model
library(ggplot2)
library(plyr)
library(xtable)
library(extrafont)
library(scales)
library(extrafont)
source("utils.R")

options(width=150)

globalNotchValue <- FALSE


## Summarizes data.
## Gives count, mean, standard deviation, standard error of the mean, and confidence interval (default 95%).
##   data: a data frame.
##   measurevar: the name of a column that contains the variable to be summariezed
##   groupvars: a vector containing names of columns that contain grouping variables
##   na.rm: a boolean that indicates whether to ignore NA's
##   conf.interval: the percent range of the confidence interval (default is 95%)
summarySE <- function(data=NULL, measurevar, groupvars=NULL, na.rm=FALSE,
                      conf.interval=.95, .drop=TRUE) {
    library(plyr)

    # New version of length which can handle NA's: if na.rm==T, don't count them
    length2 <- function (x, na.rm=FALSE) {
        if (na.rm) sum(!is.na(x))
        else       length(x)
    }

    # This does the summary. For each group's data frame, return a vector with
    # N, mean, and sd
    datac <- ddply(data, groupvars, .drop=.drop,
      .fun = function(xx, col) {
        c(N    = length2(xx[[col]], na.rm=na.rm),
          mean = mean   (xx[[col]], na.rm=na.rm),
          sd   = sd     (xx[[col]], na.rm=na.rm)
        )
      },
      measurevar
    )

    # Rename the "mean" column    
    datac <- rename(datac, c("mean" = measurevar))

    datac$se <- datac$sd / sqrt(datac$N)  # Calculate standard error of the mean

    # Confidence interval multiplier for standard error
    # Calculate t-statistic for confidence interval: 
    # e.g., if conf.interval is .95, use .975 (above/below), and use df=N-1
    ciMult <- qt(conf.interval/2 + .5, datac$N-1)
    datac$ci <- datac$se * ciMult

    return(datac)
}


plotGraphs <-function(data) {

	pdf("/tmp/isl_capture.pdf",  
	  width = 6,height = 3, family="CMU Serif")
	print(ggplot(data,aes(x=GENERATION,y=CAPTURES_TOTAL))+
	geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
	facet_wrap( INTERACTIONS ~ MODEL, scale="free", ncol=2) + 
	ggtitle("Island Model Capture Trends") +  
	theme_bw() +
	theme(text = element_text(size = 9)))

	dev.off()

}

plotHistBy_S <-function(dataFrame, colName, fileName) {
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram() +
		facet_grid( ~ POPULATION) 
	)
	dev.off()
}


#
# given sampleData, computes mean based on r replicates 
#
bootMean = function(sampleData, r) {
	sampleData = na.omit(sampleData)
	n = length(sampleData)
	boot.samples = matrix(sample(sampleData,size=n*r, replace=TRUE),r,n)
	boot.statistics = apply(boot.samples,1,mean)
	return(boot.statistics)
}


#
# performs shapiro-wilk test and mann-whitney test
# reports back a data.frame
#
analyzeNormailtyPair <-function(interaction,measure,g,s) {
	
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

	#result.frame <- data.frame(MODEL=model,MEASURE=measure, G_SIZE=rGSampleSize,
	#	G_SHAP_W=rGShapiroW, G_SHAP_P=rGShapiroP, S_SIZE=rSSampleSize,
	#	S_SHAP_W=rSShapiroW, S_SHAP_P=rSShapiroP, WILCOX_W=rWilcoxW, 
	#	WILCOX_P=rWilcoxP)
	

	if(rGSampleSize!=rSSampleSize) {
		print(rGSampleSize)
		print(rSSampleSize)

		print("no match")
		
	}
			
	
	result.frame <- data.frame(INTERACTIONS=interaction,MEASURE=measure,
		G_SHAP_W=rGShapiroW, G_SHAP_P=pValueString(rGShapiroP), S_SHAP_W=rSShapiroW, S_SHAP_P=pValueString(rSShapiroP))
	
	
	
	
	
	return(result.frame)
}



doNormalityAnalysisSubPop <- function(expDataFrame) {
	

	print("========== ISLAND ========")
	distTable <- data.frame()
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="island" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="island" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable, analyzeNormailtyPair("island","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("island","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	


	print("========== EMBODIED ========")
	#distTable <- data.frame()
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="embodied" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="embodied" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))

	print("========== ALIFE ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,3,-2,3,-2)), include.rownames=FALSE))

	
}



doNormalityAnalysisFullPop1 <- function(expDataFrame) {
	distTable <- data.frame()

#	print("========== ISLAND ========")
	
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="island"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="island"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("island","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("island","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	


#	print("========== EMBODIED ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="embodied"  ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="embodied" ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("embodied","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))

#	print("========== ALIFE ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="alife"  ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="alife"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	

	p("**** Normality test for Pg vs Ps (Shapiro-Wilks Normality Test)  ****")
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE))

	
}


doNormalityAnalysisFullPop <- function(expDataFrame) {
	

	print("========== island ========")
	distTable <- data.frame()
	gdataOrig <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="island"   ,]
	sdataOrig <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="island"  ,]

	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="none",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="none",]
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="trail",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="trail",]
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="broadcast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="broadcast",]	
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="unicast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="unicast",]		
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,3,-2,3,-2)), include.rownames=FALSE))


	print("========== embodied ========")
	distTable <- data.frame()
	gdataOrig <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="embodied"   ,]
	sdataOrig <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="embodied"  ,]

	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="none",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="none",]
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="trail",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="trail",]
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="broadcast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="broadcast",]	
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="unicast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="unicast",]		
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,3,-2,3,-2)), include.rownames=FALSE))



	print("========== alife ========")
	distTable <- data.frame()
	gdataOrig <- expDataFrame[expDataFrame$SPECIES=="Hm"  & expDataFrame$MODEL=="alife"   ,]
	sdataOrig <- expDataFrame[expDataFrame$SPECIES=="Ht" &  expDataFrame$MODEL=="alife"  ,]

	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="none",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="none",]
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	#distTable <- rbind(distTable,analyzeNormailtyPair("none","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="trail",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="trail",]
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("trail","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="broadcast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="broadcast",]	
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("broadcast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	gdata <- gdataOrig[gdataOrig$INTERACTIONS=="unicast",]
	sdata <- sdataOrig[sdataOrig$INTERACTIONS=="unicast",]		
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyPair("unicast","RATE_COMMUNICATION",gdata$RATE_COMMUNICATION,sdata$RATE_COMMUNICATION))
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,3,-2,3,-2)), include.rownames=FALSE))
	
}




plotHistByPM <-function(pInteraction, dataFrame, colName, fileName) {

	dataFrame <- dataFrame[dataFrame$INTERACTION==pInteraction,]
	#dataFrame <- dataFrame[dataFrame$CAPTURES_MEAN>0.05,]

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	print(
		ggplot(dataFrame,aes_string(x=colName)) +
		geom_histogram(fill="lightblue", color="black") +
		facet_grid( POPULATION ~ MODEL) +
		theme_bw()
	
	)
	dev.off()
}



plotHistBy_S_M <-function(dataFrame, colName, fileName) {

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	print(
		ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
		geom_histogram(color="black", alpha = 0.85) +
		facet_grid( MODEL ~ INTERACTIONS) +
		theme_bw()
	)
	dev.off()
}

plotHistByModel <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="Hm"] <-  "Hm"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="Ht"] <-  "Ht"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ INTERACTIONS, labeller=label_parsed) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.x = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ INTERACTIONS, labeller=label_parsed) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				#axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()

}

plotHistByModelSpecific <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="Hm"] <-  "non-interacting homogenous"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="Ht"] <-  "interacting heterogenous"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ ., labeller=label_parsed) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
#			axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ ., labeller=label_parsed) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
 # 			axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()

}





plotHists <- function(expDataFrame) {
	# == plot general distributions ===
	
	
	plotHistByModel("island", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-island-cm.pdf", TRUE)
	plotHistByModel("island", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-island-cb.pdf", TRUE)
	plotHistByModel("island", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-island-e2c.pdf")
	plotHistByModel("island", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-island-rm.pdf")
	plotHistByModel("island", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-island-rc.pdf")
	
	
	plotHistByModel("embodied", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-embodied-cm.pdf", TRUE)
	plotHistByModel("embodied", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-embodied-cb.pdf", TRUE)
	plotHistByModel("embodied", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-embodied-e2c.pdf")
	plotHistByModel("embodied", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-embodied-rm.pdf")
	plotHistByModel("embodied", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-embodied-rc.pdf")
	
	
	plotHistByModel("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-alife-cm.pdf", TRUE)
	plotHistByModel("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-alife-cb.pdf", TRUE)
	plotHistByModel("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-alife-e2c.pdf")
	plotHistByModel("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-alife-rm.pdf")
	plotHistByModel("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-full-alife-rc.pdf")



	expDataFrame <-  expDataFrame[(expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none") |  (expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none")  ,]


	plotHistByModelSpecific("island", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-island-cm.pdf", TRUE)
	plotHistByModelSpecific("island", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-island-cb.pdf", TRUE)
	plotHistByModelSpecific("island", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-island-e2c.pdf")
	plotHistByModelSpecific("island", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-island-rm.pdf")
	plotHistByModelSpecific("island", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-island-rc.pdf")
	
	
	plotHistByModelSpecific("embodied", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-embodied-cm.pdf", TRUE)
	plotHistByModelSpecific("embodied", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-embodied-cb.pdf", TRUE)
	plotHistByModelSpecific("embodied", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-embodied-e2c.pdf")
	plotHistByModelSpecific("embodied", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-embodied-rm.pdf")
	plotHistByModelSpecific("embodied", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-embodied-rc.pdf")
	
	
	plotHistByModelSpecific("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-alife-cm.pdf", TRUE)
	plotHistByModelSpecific("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-alife-cb.pdf", TRUE)
	plotHistByModelSpecific("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-alife-e2c.pdf")
	plotHistByModelSpecific("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-alife-rm.pdf")
	plotHistByModelSpecific("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-part-s-alife-rc.pdf")






}






plotBoxPlot_M_I <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( MODEL ~ INTERACTIONS, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() +
			geom_line(size=0.1) +
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="bottom", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()
				)
		)
	} else {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( MODEL ~ INTERACTIONS, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			geom_line(size=0.1) +
			theme_bw() + 			
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="bottom", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()

				)
			+ scale_y_continuous(labels=percentFormatter)
		)
	}

	dev.off()
}

plotBoxPlot_M <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid(  ~ MODEL , labeller=label_parsed) +
			#scale_fill_manual(values=c("white","grey50")) +
			ylab(yAxisLabel) +
			#xlab("Population") +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( ~ MODEL, labeller=label_parsed) +
			#scale_fill_manual(values=c("white","grey50")) +
			ylab(yAxisLabel) +
			#xlab("Population") +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				axis.text.y = element_text(size=rel(0.7)))
			+ scale_y_continuous(labels=percentFormatter)
		)
	}

	dev.off()
}

plotBoxPlot <-function(dataFrame, colName, fileName, showPercent=FALSE, showNotches=FALSE) {

#	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION,from=c("Hm","Ht"),to=c("hom","het"))


	pdf(fileName,  
	  width = 2.3,height = 1.75, family="CMU Serif")	  
	  
	  
	yAxisLabel <- getMeasureShortName(colName)
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=showNotches) +
			#facet_grid(  ~ MODEL , labeller=label_parsed) +
			#ylab(yAxisLabel) +
			#xlab("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			axis.title.x = element_blank(),
			axis.title.y = element_blank(),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_text(size=rel(0.9))
								)
		)
	} else {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=showNotches) +
			#facet_grid( ~ MODEL, labeller=label_parsed) +
			#ylab(yAxisLabel) +
			#xlab("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			axis.title.x = element_blank(),
			axis.title.y = element_blank(),
			legend.position="none", 				
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_text(size=rel(0.9)))
			+ scale_y_continuous(labels=percentFormatter)
		)
	}
	  

	dev.off()
}




compareMeans <-function(d.f,measure) {
	r.f <- data.frame()

	g <- d.f[(d.f$SPECIES=="Hm"),]
	g <- g[[measure]]

	g.mean <- mean(g, na.rm=TRUE)
	g.var <- var(g, na.rm=TRUE)
	g.median <- median(g, na.rm=TRUE)
	g.t <- t.test(g)
	g.ci1 <- g.t$conf.int[[1]]
	g.ci2 <- g.t$conf.int[[2]]
	
	

	
	s <- d.f[(d.f$SPECIES=="Ht"),]
	s <- s[[measure]]

	s.mean <- mean(s, na.rm=TRUE)
	s.var <- var(s, na.rm=TRUE)
	s.median <- median(s, na.rm=TRUE)
	s.t <- t.test(s)
	s.ci1 <- s.t$conf.int[[1]]
	s.ci2 <- s.t$conf.int[[2]]


	rWilcox <- wilcox.test(g,s)
	rWilcoxW <- rWilcox$statistic[[1]]
	rWilcoxP <- rWilcox$p.value

	g.f <- data.frame( POPULATION="Hm", MEAUSURE=measure, MEAN=g.mean,
		VAR = g.var, MEDIAN = g.median, CI1 = g.ci1, CI2 = g.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)
	s.f <- data.frame( POPULATION="Ht", MEAUSURE=measure, MEAN=s.mean,
		VAR = s.var, MEDIAN = s.median, CI1 = s.ci1, CI2 = s.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)


	r.f <- rbind(g.f,s.f)

	print(r.f)

}



plotBoxPlots <- function(expDataFrame) {
	orig <- expDataFrame

	# only look at non-communicating homogenous and communicating heterogenous	
	#expDataFrame <-  expDataFrame[(expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none") |  (expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none")  ,]
	

	
	# do box plots by model and interaction
	plotBoxPlot_M_I(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-cb.pdf", TRUE)
	plotBoxPlot_M_I(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-cm.pdf", TRUE)
	plotBoxPlot_M_I(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-e2c.pdf")
	plotBoxPlot_M_I(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-rm.pdf")
	plotBoxPlot_M_I(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-rc.pdf")

	
	
	# filter out island model

	expDataFrame <-  expDataFrame[expDataFrame$MODEL!="island",]
	
	expDataFrame <- expDataFrame[(expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none") |
	   (expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none") ,]

	
	

	# do box plots of overall (including interactions)
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-part-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-part-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-part-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-part-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-part-box-rc.pdf")
	
	# do only island
	expDataFrame <-  orig[orig$MODEL=="island",]
	expDataFrame <- expDataFrame[(expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none") |
	   (expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none") ,]

	# do box plots by model 
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-part-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-part-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-part-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-part-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-part-box-rc.pdf")
	
	# consider all models... just pdf vs pis
	expDataFrame <-  orig

	# show a comparison of the means...
	print("comparing pg and ps...(non-parametric)")
	compareMeans(expDataFrame,"CAPTURES_BEST_CASE")
	compareMeans(expDataFrame,"CAPTURES_MEAN")
	compareMeans(expDataFrame,"RES_E2C_STEPS_MEAN")
	compareMeans(expDataFrame,"RATE_MOTION")
	compareMeans(expDataFrame,"RATE_COMMUNICATION")
	print("DONE comparing pg and ps...(non-parametric)")
	
	# do box plots of overall (including interactions)
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-rc.pdf")
	


}


# Function turns the internal abbreviated values to full expansions and re-ordered
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a","e","i"), to=c("alife","embodied","island"))
	
	dataFrame$MODEL <- factor(dataFrame$MODEL,c("island","embodied","alife"))
	
	
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("n","b","u","t"), to=c("none","broadcast","unicast","trail"))
	
	dataFrame$INTERACTIONS <- factor(dataFrame$INTERACTIONS, c("none","trail","broadcast","unicast"))
	
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("Hm","Ht"))

	dataFrame$SPECIES <- factor(dataFrame$SPECIES, c("Hm","Ht"))
	
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("Hm","Ht"))
	
	dataFrame$POPULATION <- factor(dataFrame$POPULATION, c("Hm","Ht"))
	
	return(dataFrame)
}

# this function:
# 1. makes POPULATION a synonym for species
# 2. sets RES_E2C_STEPS_MEAN = RES_E2C_STEPS_MEAN * CAPTURES_MEAN
preProcessData <- function(expDataFrame) {
	# set all factors
	expDataFrame$MODEL <- factor(expDataFrame$MODEL)
	expDataFrame$SPECIES <- factor(expDataFrame$SPECIES)
	expDataFrame$POPULATION <- expDataFrame$SPECIES
	expDataFrame$POPULATION <- factor(expDataFrame$POPULATION)	
	expDataFrame$INTERACTIONS <- factor(expDataFrame$INTERACTIONS)
	expDataFrame$COMPLEXITY <- factor(expDataFrame$COMPLEXITY)
	expDataFrame$CLONES <- factor(expDataFrame$CLONES)
	expDataFrame$GRIDS <- factor(expDataFrame$GRIDS)
	expDataFrame$RESOURCES <- factor(expDataFrame$RESOURCES)
	expDataFrame$SITES <- factor(expDataFrame$SITES)
	expDataFrame$OBSTACLES <- factor(expDataFrame$OBSTACLES)
	expDataFrame$DIFFICULTY <- factor(expDataFrame$DIFFICULTY)
	
	expDataFrame$RES_E2C_STEPS_MEAN <- (expDataFrame$RES_E2C_STEPS_MEAN*expDataFrame$CAPTURES_MEAN)

	return(expDataFrame)
}



plotBootHist <-function(bootSample, fileName) {

	se <- sd(bootSample)
	binWidth <- diff(range(bootSample))/30
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	print(
		ggplot(data.frame(x=bootSample), aes(x=x)) 
		+ geom_histogram(aes(y=..density..), binwidth=binWidth) 
		+ geom_density(color="red")
		
	)
	dev.off()
}



plotBootHist2PopNew <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	tst <- summarySE(popDataFrame,measurevar=colName,groupvars=c("POPULATION"))
	tst$POPULATION <- factor(tst$POPULATION)


	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.5,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {

		print(
			ggplot(tst, aes_string(y=colName, x="POPULATION"))
			+ geom_bar(position=position_dodge(), stat="identity", color="black", fill="white")
			+ geom_errorbar(aes_string(ymin=paste(colName,"-ci",sep=""), ymax=paste(colName,"+ci",sep="")),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9))
			#+ facet_grid(. ~ INTERACTIONS ) 
			+ ylab(xAxisLabel) 
			+ theme_bw()
			+ theme( legend.position="none", 				
				axis.title.x = element_blank(),
				axis.text.x = element_text(size=rel(0.7)),
				axis.text.y = element_text(size=rel(0.7)))
			#+ scale_y_continuous(labels=percentFormatter)
		)
		

	} else {
		
		print(
			ggplot(tst, aes_string(y=colName, x="POPULATION"))
			+ geom_bar(position=position_dodge(), stat="identity", color="black", fill="POPULATION")
			+ geom_errorbar(aes_string(ymin=paste(colName,"-ci",sep=""), ymax=paste(colName,"+ci",sep="")),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9))
			#+ facet_grid(. ~ INTERACTIONS ) 
			+ ylab(xAxisLabel) 
			+ theme_bw()
			+ theme( legend.position="none", 				
				axis.title.x = element_blank(),
				axis.text.x = element_text(size=rel(0.7)),
				axis.text.y = element_text(size=rel(0.7)))
			+ scale_y_continuous(labels=percentFormatter)
		)

	}
	dev.off()
}








plotBootHist2Pop <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.3,height = 1.75, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			# + scale_fill_manual(values=c("white","grey50")) 
			+ xlab(xAxisLabel) 
			+ theme_bw()
			+ theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.title.y = element_blank(),
				axis.text.x = element_text(size=rel(0.7)),
				axis.text.y = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION"))
			+ geom_density(alpha = 0.9)
			# + scale_fill_manual(values=c("white","grey50")) 
			+ xlab(xAxisLabel) 
			+ theme_bw()
			+ theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				axis.title.y = element_blank(),
				axis.text.x = element_text(size=rel(0.7)),
				axis.text.y = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()
}


#
# performs t-tests
# reports back a data.frame
#
performTTest <-function(measure,s.v,g.v) {

	#print(t.test(g.v))
	#stop()

	t.result <- t.test(g.v,s.v)
	
	tValue <- t.result$statistic[[1]]
	dFreedom <- t.result$parameter[[1]]
	pValue <- as.double(t.result$p.value)

	#print(measure)
	gMean <- mean(g.v)
	gT <- t.test(g.v)
	gCI1 <- gT$conf.int[[1]]
	gCI2 <- gT$conf.int[[2]]
	gStdErr <- var(g.v)/length(g.v)



	sMean <- mean(s.v)
	sT <- t.test(s.v)
	sCI1 <- sT$conf.int[[1]]
	sCI2 <- sT$conf.int[[2]]
	sStdErr <- var(s.v)/length(s.v)	

	delta <- round(((sMean - gMean)/gMean)*100,digits=0)


	#print(t.result)
	#print(summary(t.result))
	
	#print(pValue)	

	
	result.frame <- data.frame(
		MEASURE=measure,
		T_VALUE=tValue, 
		D_FREEDOM=dFreedom, 
		P_VALUE=pValueString(pValue),
		GM = gMean,
		GErr = gStdErr, 
		#GM = paste0("( ",gCI1,", ",gCI2," )"),
		#GCI1 = gCI1,
		#GCI2 = gCI2,
		SM = sMean,
		SErr = sStdErr,
		delta = paste0(delta,"%")
		#SM = paste0("( ",sCI1,", ",sCI2," )")
		#SCI1 = sCI1,
		#SCI2 = sCI2
		)
	
	return(result.frame)
}




plotBootedStatsFull <- function(expDataFrame) {

	# now we will bootstrap the measures
bootSize <- 1000

	expDataFrame <- expDataFrame[expDataFrame$MODEL!="island",]

	ea.data.frame <-  expDataFrame

	sg.data.frame <-  ea.data.frame

	# extract data for s and g
	s.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="Ht",]
	g.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="Hm",]

	# extract the measures
	s.CAPTURES_BEST_CASE <- s.data.frame$CAPTURES_BEST_CASE
	s.CAPTURES_MEAN <- s.data.frame$CAPTURES_MEAN
	s.RES_E2C_STEPS_MEAN <- s.data.frame$RES_E2C_STEPS_MEAN
	s.RATE_MOTION <- s.data.frame$RATE_MOTION
	s.RATE_COMMUNICATION <- s.data.frame$RATE_COMMUNICATION

	s.CAPTURES_BEST_CASE <- bootMean(s.CAPTURES_BEST_CASE,bootSize)
	s.CAPTURES_MEAN <- bootMean(s.CAPTURES_MEAN,bootSize)
	s.RES_E2C_STEPS_MEAN <- bootMean(s.RES_E2C_STEPS_MEAN,bootSize)
	s.RATE_MOTION <- bootMean(s.RATE_MOTION,bootSize)
	s.RATE_COMMUNICATION <- bootMean(s.RATE_COMMUNICATION,bootSize)

	g.CAPTURES_BEST_CASE <- g.data.frame$CAPTURES_BEST_CASE
	g.CAPTURES_MEAN <- g.data.frame$CAPTURES_MEAN
	g.RES_E2C_STEPS_MEAN <- g.data.frame$RES_E2C_STEPS_MEAN
	g.RATE_MOTION <- g.data.frame$RATE_MOTION
	g.RATE_COMMUNICATION <- g.data.frame$RATE_COMMUNICATION

	g.CAPTURES_BEST_CASE <- bootMean(g.CAPTURES_BEST_CASE,bootSize)
	g.CAPTURES_MEAN <- bootMean(g.CAPTURES_MEAN,bootSize)
	g.RES_E2C_STEPS_MEAN <- bootMean(g.RES_E2C_STEPS_MEAN,bootSize)
	g.RATE_MOTION <- bootMean(g.RATE_MOTION,bootSize)
	g.RATE_COMMUNICATION <- bootMean(g.RATE_COMMUNICATION,bootSize)

	
	s.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=s.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
		RATE_MOTION=s.RATE_MOTION,
		RATE_COMMUNICATION=s.RATE_COMMUNICATION,
		POPULATION="Ht"
	)


	g.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION,
		RATE_COMMUNICATION=g.RATE_COMMUNICATION,
		POPULATION="Hm"
	)
	
	popDataFrame <- rbind(g.popDataFrame,s.popDataFrame)
	
	
	plotBootHist2Pop(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-cb.pdf", TRUE)
	plotBootHist2Pop(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-cm.pdf", TRUE)
	plotBootHist2Pop(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-e2c.pdf")
	plotBootHist2Pop(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-rm.pdf")
	plotBootHist2Pop(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-rc.pdf")


	
	
	#plotBoxPlot(popDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-cb.pdf", TRUE, FALSE)
	#plotBoxPlot(popDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-cm .pdf", TRUE, FALSE)
	#plotBoxPlot(popDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-e2c.pdf", FALSE, FALSE)
	#plotBoxPlot(popDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-rm.pdf", FALSE, FALSE)

	t.table <- data.frame()


	t.table <- rbind(t.table, performTTest("CAPTURES_BEST_CASE",s.CAPTURES_BEST_CASE,g.CAPTURES_BEST_CASE))
	t.table <- rbind(t.table, performTTest("CAPTURES_MEAN",s.CAPTURES_MEAN,g.CAPTURES_MEAN))
	t.table <- rbind(t.table, performTTest("RES_E2C_STEPS_MEAN",s.RES_E2C_STEPS_MEAN,g.RES_E2C_STEPS_MEAN))
	t.table <- rbind(t.table, performTTest("RATE_MOTION",s.RATE_MOTION,g.RATE_MOTION))
	t.table <- rbind(t.table, performTTest("RATE_COMMUNICATION",s.RATE_COMMUNICATION,g.RATE_COMMUNICATION))

	#data(t.table)
	p("boot data table for pg vs ps")
	displayLatex(print(xtable(t.table, digits=c(0,0,0,0,-2,-2,-1,-2,-1,1)), include.rownames=FALSE))
	#displayLatex(print(xtable(t.table, include.rownames=FALSE)))

}



plotBootedStatsPartial <- function(expDataFrame) {

	# now we will bootstrap the measures
bootSize <- 1000

	ea.data.frame <-  expDataFrame[expDataFrame$MODEL!="island",]

	sg.data.frame <-  ea.data.frame[(ea.data.frame$SPECIES=="Hm" & ea.data.frame$INTERACTIONS=="none") |  (ea.data.frame$SPECIES=="Ht" & ea.data.frame$INTERACTIONS!="none")  ,]

	# extract data for s and g
	s.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="Ht" & sg.data.frame$INTERACTIONS!="none",]
	g.data.frame <- sg.data.frame[sg.data.frame$SPECIES=="Hm" & sg.data.frame$INTERACTIONS=="none",]

	# extract the measures
	s.CAPTURES_BEST_CASE <- s.data.frame$CAPTURES_BEST_CASE
	s.CAPTURES_MEAN <- s.data.frame$CAPTURES_MEAN
	s.RES_E2C_STEPS_MEAN <- s.data.frame$RES_E2C_STEPS_MEAN
	s.RATE_MOTION <- s.data.frame$RATE_MOTION
	s.RATE_COMMUNICATION <- s.data.frame$RATE_COMMUNICATION

	s.CAPTURES_BEST_CASE <- bootMean(s.CAPTURES_BEST_CASE,bootSize)
	s.CAPTURES_MEAN <- bootMean(s.CAPTURES_MEAN,bootSize)
	s.RES_E2C_STEPS_MEAN <- bootMean(s.RES_E2C_STEPS_MEAN,bootSize)
	s.RATE_MOTION <- bootMean(s.RATE_MOTION,bootSize)
	s.RATE_COMMUNICATION <- bootMean(s.RATE_COMMUNICATION,bootSize)

	g.CAPTURES_BEST_CASE <- g.data.frame$CAPTURES_BEST_CASE
	g.CAPTURES_MEAN <- g.data.frame$CAPTURES_MEAN
	g.RES_E2C_STEPS_MEAN <- g.data.frame$RES_E2C_STEPS_MEAN
	g.RATE_MOTION <- g.data.frame$RATE_MOTION
	g.RATE_COMMUNICATION <- g.data.frame$RATE_COMMUNICATION

	g.CAPTURES_BEST_CASE <- bootMean(g.CAPTURES_BEST_CASE,bootSize)
	g.CAPTURES_MEAN <- bootMean(g.CAPTURES_MEAN,bootSize)
	g.RES_E2C_STEPS_MEAN <- bootMean(g.RES_E2C_STEPS_MEAN,bootSize)
	g.RATE_MOTION <- bootMean(g.RATE_MOTION,bootSize)
	g.RATE_COMMUNICATION <- bootMean(g.RATE_COMMUNICATION,bootSize)

	
	s.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=s.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
		RATE_MOTION=s.RATE_MOTION,
		POPULATION="Ht"
	)


	g.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION, 
		POPULATION="Hm"
	)
	
	popDataFrame <- rbind(g.popDataFrame,s.popDataFrame)
	
	
	plotBootHist2Pop(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-cb.pdf", TRUE)
	plotBootHist2Pop(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-cm.pdf", TRUE)
	plotBootHist2Pop(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-e2c.pdf")
	plotBootHist2Pop(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-rm.pdf")
	
	
	
	#plotBoxPlot(popDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-cb.pdf", TRUE, FALSE)
	#plotBoxPlot(popDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-cm .pdf", TRUE, FALSE)
	#plotBoxPlot(popDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-e2c.pdf", FALSE, FALSE)
	#plotBoxPlot(popDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-rm.pdf", FALSE, FALSE)

	t.table <- data.frame()


	t.table <- rbind(t.table, performTTest("CAPTURES_BEST_CASE",s.CAPTURES_BEST_CASE,g.CAPTURES_BEST_CASE))
	t.table <- rbind(t.table, performTTest("CAPTURES_MEAN",s.CAPTURES_MEAN,g.CAPTURES_MEAN))
	t.table <- rbind(t.table, performTTest("RES_E2C_STEPS_MEAN",s.RES_E2C_STEPS_MEAN,g.RES_E2C_STEPS_MEAN))
	t.table <- rbind(t.table, performTTest("RATE_MOTION",s.RATE_MOTION,g.RATE_MOTION))

	#data(t.table)
	p("boot data table for png vs pis")
	displayLatex(print(xtable(t.table, digits=c(0,0,0,0,-2,-2,-1,-2,-1,0)), include.rownames=FALSE))
	#displayLatex(print(xtable(t.table, include.rownames=FALSE)))




}


nonParametricCompare <-function(measureName,dataFrame1, dataFrame2) {

	result <- data.frame()
	vecA <- dataFrame1[[measureName]]
	vecB <- dataFrame2[[measureName]]
	wilcox <- wilcox.test(vecA,vecB, conf.int=TRUE)


	g.mean <- mean(vecA, na.rm=TRUE)
	g.var <- var(vecA, na.rm=TRUE)
	g.median <- median(vecA, na.rm=TRUE)
	g.t <- t.test(vecA)
	g.ci1 <- g.t$conf.int[[1]]
	g.ci2 <- g.t$conf.int[[2]]


	s.mean <- mean(vecB, na.rm=TRUE)
	s.var <- var(vecB, na.rm=TRUE)
	s.median <- median(vecB, na.rm=TRUE)
	s.t <- t.test(vecB)
	s.ci1 <- s.t$conf.int[[1]]
	s.ci2 <- s.t$conf.int[[2]]



	
	W <- wilcox$statistic[[1]]
	pValue <- wilcox$p.value
	
	z<- qnorm(pValue/2)
	r<- z/ sqrt(length(vecA))
	
	rowData <- data.frame(				
		MEASURE=measureName,						
		W=W,
		P=pValueString(pValue),
		G_MEDIAN=g.median,
		#G_MEAN=g.mean,
		#G_CI1=g.ci1,
		#G_CI2=g.ci2,
		S_MEDIAN=s.median
		#S_MEAN=s.mean,
		#S_CI1=s.ci1,
		#S_CI2=s.ci2
		#EFFECT=r
	)
	result <- rbind(result,rowData)

	return(result)
}



doNonParametricComparisons <- function(expDataFrame) {

	distTable <- data.frame()
	p("PG vs PS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" ,]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht" ,]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< PG vs PS: NON-PARAMETRIC COMPARE")

	#stop()


	distTable <- data.frame()
	p("ISLAND PG vs PS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" & expDataFrame$MODEL=="island",]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht"  & expDataFrame$MODEL=="island",]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< ISLAND PG vs PS: NON-PARAMETRIC COMPARE")


	distTable <- data.frame()
	p("NON-ISLAND PG vs PS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" & expDataFrame$MODEL!="island",]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht"  & expDataFrame$MODEL!="island",]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< NON-ISLAND PG vs PS: NON-PARAMETRIC COMPARE")

	distTable <- data.frame()
	p("PNG vs PIS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none",]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none",]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< PNG vs PIS: NON-PARAMETRIC COMPARE")


	distTable <- data.frame()
	p("ISLAND PNG vs PIS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none" & expDataFrame$MODEL=="island",]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none" & expDataFrame$MODEL=="island",]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< ISLAND PNG vs PIS: NON-PARAMETRIC COMPARE")


	distTable <- data.frame()
	p("NON-ISLAND PNG vs PIS: NON-PARAMETRIC COMPARE >>>>>>")

	data1 <- expDataFrame[expDataFrame$SPECIES=="Hm" & expDataFrame$INTERACTIONS=="none" & expDataFrame$MODEL!="island",]
	data2 <- expDataFrame[expDataFrame$SPECIES=="Ht" & expDataFrame$INTERACTIONS!="none" & expDataFrame$MODEL!="island",]	
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",data1, data2))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",data1, data2))
	
	#distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-3,-3)), include.rownames=FALSE))
	p("<<<<<<<<< NON-ISLAND PNG vs PIS: NON-PARAMETRIC COMPARE")
	
}


##############################   MAIN PROCESS BEGINS ###############################


expDataFrame <- read.csv(file="~/synthscape/scripts/analysis/data/exp1/exp1_experiments_mean_300.csv")

expDataFrame <- preProcessData(expDataFrame)     # factorizes, as appropriate, adjusts E2C...
expDataFrame <- renameFactorValues(expDataFrame) # renames for nice plots

##### not using these....plotGraphs(expDataFrame)

# Using these...
#plotHists(expDataFrame)    # plots histograms

#doNormalityAnalysisFullPop(expDataFrame)
#doNormalityAnalysisSubPop(expDataFrame)
#plotBoxPlots(expDataFrame) # boxplots to show difference
#doNonParametricComparisons(expDataFrame)

#plotBootedStatsFull(expDataFrame)
plotBootedStatsPartial(expDataFrame)

#plot the totals

