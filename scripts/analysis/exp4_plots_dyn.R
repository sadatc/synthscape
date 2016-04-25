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



nonParametricCompare <-function(measureName,vecA,vecB) {

	wilcox <- wilcox.test(vecA,vecB, conf.int=TRUE)
	
	W <- wilcox$statistic[[1]]
	pValue <- wilcox$p.value
	
	z<- qnorm(pValue/2)
	r<- z/ sqrt(length(vecA))
	
	result.frame <- data.frame(
		
		MEASURE=measureName,
		W=W,
		P=pValueString(pValue)
	)
	
	return(result.frame)
}

doNonParametricAnalysis <- function(expDataFrame) {

	distTable <- data.frame()

	p("NON-PARAMETRIC TEST >>>>>>")

	aDat <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="uniform",]
	bDat <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="mixed",]

	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_MEAN",aDat$CAPTURES_MEAN,bDat$CAPTURES_MEAN))
	distTable <- rbind(distTable,nonParametricCompare("CAPTURES_BEST_CASE",aDat$CAPTURES_BEST_CASE,bDat$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,nonParametricCompare("RES_E2C_STEPS_MEAN",aDat$RES_E2C_STEPS_MEAN,bDat$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,nonParametricCompare("RATE_COMMUNICATION",aDat$RATE_COMMUNICATION,bDat$RATE_COMMUNICATION))
	distTable <- rbind(distTable,nonParametricCompare("RATE_MOTION",aDat$RATE_MOTION,bDat$RATE_MOTION))
	distTable <- rbind(distTable,nonParametricCompare("NUM_DETECTORS",aDat$NUM_DETECTORS,bDat$NUM_DETECTORS))
	distTable <- rbind(distTable,nonParametricCompare("NUM_EXTRACTORS",aDat$NUM_EXTRACTORS,bDat$NUM_EXTRACTORS))
	distTable <- rbind(distTable,nonParametricCompare("NUM_TRANSPORTERS",aDat$NUM_TRANSPORTERS,bDat$NUM_TRANSPORTERS))
	distTable <- rbind(distTable,nonParametricCompare("E",aDat$E,bDat$E))

	print(distTable)

	displayLatex(print(xtable(distTable, digits=c(0,0,2,-2)), include.rownames=FALSE))

	p("<<<<<<<<< NON-PARAMETRIC TEST")
	
}












#
# performs shapiro-wilk test and mann-whitney test
# reports back a data.frame
#
analyzeNormailtyMeasure <-function(groupName,measureName,vec) {

	shap <- shapiro.test(vec)

	W <- shap$statistic[[1]]
	pValue <- shap$p.value

	result.frame <- data.frame(
		RESOURCE_UNIFORMITY=groupName,
		MEASURE=measureName,
		W=W,
		P=pValueString(pValue)
		#P=pValue
	)
	
	return(result.frame)
}



doNormalityAnalysis <- function(expDataFrame) {

	distTable <- data.frame()

	p("NORMALITY TEST >>>>>>")

	data <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="uniform",]

	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","CAPTURES_MEAN",data$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","CAPTURES_BEST_CASE",data$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","RES_E2C_STEPS_MEAN",data$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","RATE_COMMUNICATION",data$RATE_COMMUNICATION))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","RATE_MOTION",data$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","NUM_DETECTORS",data$NUM_DETECTORS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","NUM_EXTRACTORS",data$NUM_EXTRACTORS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","NUM_TRANSPORTERS",data$NUM_TRANSPORTERS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("uniform","E",data$E))

	data <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="mixed",]

	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","CAPTURES_MEAN",data$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","CAPTURES_BEST_CASE",data$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","RES_E2C_STEPS_MEAN",data$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","RATE_COMMUNICATION",data$RATE_COMMUNICATION))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","RATE_MOTION",data$RATE_MOTION))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","NUM_DETECTORS",data$NUM_DETECTORS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","NUM_EXTRACTORS",data$NUM_EXTRACTORS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","NUM_TRANSPORTERS",data$NUM_TRANSPORTERS))
	distTable <- rbind(distTable,analyzeNormailtyMeasure("mixed","E",data$E))

	print(distTable)
	
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,0)), include.rownames=FALSE))

	p("<<<<<<<<< NORMALITY TEST")
	
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

plotHistByPopulation <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="homogenous"] <-  "homogenous"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="heterogenous"] <-  "heterogenous"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ INTERACTIONS, labeller=label_parsed) +
			xlab(xAxisLabel) +
			scale_fill_manual(values=c("white","grey50")) +
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
			scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				#axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()

}


plotHistByIntResMixNew <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	xAxisLabel <- getMeasurePrettyName(colName)
	

	df2 <- ddply(.data = dataFrame, .variables = .(RESOURCE_UNIFORMITY), function(dat){
	             q <- qqnorm(dat[[colName]], plot = FALSE)
	             dat$xq <- q$x
	             dat
		}
	)

	print("hi")


	
	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	print(
	ggplot(data = df2, aes_string(x = "xq", y = colName)) +
  	geom_point(size=1) +
  	geom_smooth(method = "lm", se = FALSE, color="black" ) +
  	xlab("Theoretical") +
  	ylab("Sample") +
  	facet_grid(. ~ RESOURCE_UNIFORMITY)
	)
	dev.off()
	


}


plotHistByIntResMix <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="RESOURCE_UNIFORMITY")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( . ~ RESOURCE_UNIFORMITY) +
			xlab(xAxisLabel) +
			scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.x = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="RESOURCE_UNIFORMITY")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( . ~ RESOURCE_UNIFORMITY) +
			xlab(xAxisLabel) +
			scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				#axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()

}

plotHists <- function(expDataFrame) {

	plotHistByIntResMix( expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-cm.pdf", TRUE)
	plotHistByIntResMix( expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-cb.pdf", TRUE)
	plotHistByIntResMix( expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-e2c.pdf")
	plotHistByIntResMix( expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-rm.pdf")
	plotHistByIntResMix( expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-rc.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-dets.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-exts.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-trns.pdf")
	plotHistByIntResMix( expDataFrame,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-e.pdf")



}


plotBoxPlot_Even <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 2.1,height = 1.5 , family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="RESOURCE_UNIFORMITY",y=colName)) +
			geom_boxplot(aes(fill=RESOURCE_UNIFORMITY), notch=globalNotchValue) +
			#facet_grid( INTERACTIONS ~ .) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
			theme_bw() +
			geom_line(size=0.1) +
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()
				)
		)
	} else {
		print(
			ggplot(dataFrame, aes_string(x="RESOURCE_UNIFORMITY", y=colName)) +
			geom_boxplot(aes(fill=RESOURCE_UNIFORMITY), notch=globalNotchValue) +
			#facet_grid(INTERACTIONS ~ .) +
			ylab(yAxisLabel) +
			scale_y_continuous(labels=percentFormatter)+
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
			geom_line(size=0.1) +
			theme_bw() + 			
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()

				)
		)
	}

	dev.off()
}







plotBoxPlot <-function(dataFrame, colName, fileName, showPercent=FALSE, showNotches=FALSE) {

#	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION,from=c("homogenous","heterogenous"),to=c("hom","het"))


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
			scale_fill_manual(values=c("white","grey50")) +
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
			scale_fill_manual(values=c("white","grey50")) +
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

	g <- d.f[(d.f$SPECIES=="homogenous"),]
	g <- g[[measure]]

	p.mean <- mean(g, na.rm=TRUE)
	p.var <- var(g, na.rm=TRUE)
	p.median <- median(g, na.rm=TRUE)
	p.t <- t.test(g)
	p.ci1 <- p.t$conf.int[[1]]
	p.ci2 <- p.t$conf.int[[2]]
	
	

	
	s <- d.f[(d.f$SPECIES=="heterogenous"),]
	s <- s[[measure]]

	f.mean <- mean(s, na.rm=TRUE)
	f.var <- var(s, na.rm=TRUE)
	f.median <- median(s, na.rm=TRUE)
	f.t <- t.test(s)
	f.ci1 <- f.t$conf.int[[1]]
	f.ci2 <- f.t$conf.int[[2]]


	rWilcox <- wilcox.test(g,s)
	rWilcoxW <- rWilcox$statistic[[1]]
	rWilcoxP <- rWilcox$p.value

	p.f <- data.frame( POPULATION="homogenous", MEAUSURE=measure, MEAN=p.mean,
		VAR = p.var, MEDIAN = p.median, CI1 = p.ci1, CI2 = p.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)
	f.f <- data.frame( POPULATION="heterogenous", MEAUSURE=measure, MEAN=f.mean,
		VAR = f.var, MEDIAN = f.median, CI1 = f.ci1, CI2 = f.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)


	r.f <- rbind(p.f,f.f)

	print(r.f)

}





plotBoxPlots <- function(expDataFrame) {
	orig <- expDataFrame


	# compare richness type vs performance

	plotBoxPlot_Even(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e-cb.pdf", TRUE)

	plotBoxPlot_Even(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e-cm.pdf", TRUE)

	plotBoxPlot_Even(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e-e2c.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e-rm.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e-rc.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-ds.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-es.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-ts.pdf", FALSE)

	plotBoxPlot_Even(expDataFrame,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-e.pdf", FALSE)

}







# Function turns the internal abbreviated values to full expansions and re-ordered
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a"), to=c("alife"))

	
	dataFrame$MODEL <- factor(dataFrame$MODEL,c("alife"))
	
	
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("b","u","t"), to=c("broadcast","unicast","trail"))
	
	dataFrame$INTERACTIONS <- factor(dataFrame$INTERACTIONS, c("trail","broadcast","unicast"))
	
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("homogenous","heterogenous"))

	dataFrame$SPECIES <- factor(dataFrame$SPECIES, c("homogenous","heterogenous"))
	
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("homogenous","heterogenous"))
	
	dataFrame$POPULATION <- factor(dataFrame$POPULATION, c("homogenous","heterogenous"))


	dataFrame$EVENNESS_CONTROL <- mapvalues(dataFrame$EVENNESS_CONTROL, 
		from=c("d","m"), 
		to=c("dynamic", "manual")
	)


	
	dataFrame$RESOURCE_UNIFORMITY <- mapvalues(dataFrame$RESOURCE_UNIFORMITY, 
		from=c("f","p"), 
		to=c("uniform", "mixed")
	)
	
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
	expDataFrame$EVENNESS_CONTROL <- factor(expDataFrame$EVENNESS_CONTROL)
	expDataFrame$EXTRACTED_RESOURCES <- factor(expDataFrame$EXTRACTED_RESOURCES)
	expDataFrame$RESOURCE_UNIFORMITY <- factor(expDataFrame$EXTRACTED_RESOURCES)
	expDataFrame$RU <- expDataFrame$RESOURCE_UNIFORMITY
	
	
	expDataFrame$GRIDS <- factor(expDataFrame$GRIDS)
	expDataFrame$RESOURCES <- factor(expDataFrame$RESOURCES)
	expDataFrame$SITES <- factor(expDataFrame$SITES)
	expDataFrame$OBSTACLES <- factor(expDataFrame$OBSTACLES)
	expDataFrame$DIFFICULTY <- factor(expDataFrame$DIFFICULTY)

	
	expDataFrame$RES_E2C_STEPS_MEAN <- (expDataFrame$RES_E2C_STEPS_MEAN*expDataFrame$CAPTURES_MEAN)

	## compute evenness
	expDataFrame$TOT_POP <- 24
	numSpecies <- 3

	P1 <- expDataFrame$NUM_DETECTORS/expDataFrame$TOT_POP
	P2 <- expDataFrame$NUM_EXTRACTORS/expDataFrame$TOT_POP
	P3 <- expDataFrame$NUM_TRANSPORTERS/expDataFrame$TOT_POP

	H <- -((P1*log(P1)) + (P2*log(P2)) + (P3*log(P3)) )
	expDataFrame$E <- H/log(numSpecies)


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


plotBootHist2Pop <-function(popDataFrame, colName, fileName, showPercent = FALSE) {



	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.5,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
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
			+ scale_fill_manual(values=c("white","grey50")) 
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

plotBootHistPop_tst <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	tst <- summarySE(popDataFrame,measurevar=colName,groupvars=c("INTERACTIONS","POPULATION_MIX"))

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 3,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {

		print(
			ggplot(tst, aes_string(y=colName, x="POPULATION_MIX"))
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
			ggplot(tst, aes_string(y=colName, x="POPULATION_MIX"))
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
			+ scale_y_continuous(labels=percentFormatter)
		)

	}
	dev.off()
}





plotBootHistPop_Itmp<-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	tst <- summarySE(popDataFrame,measurevar=colName,groupvars=c("RESOURCE_UNIFORMITY"))
	tst$RESOURCE_UNIFORMITY <- factor(tst$RESOURCE_UNIFORMITY)

	print(tst)

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 3,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {

		print(
			ggplot(tst, aes_string(y=colName, x="RESOURCE_UNIFORMITY"))
			+ geom_bar(position=position_dodge(), stat="identity", color="black", fill="white")
			+ geom_errorbar(aes_string(ymin=paste(colName,"-ci",sep=""), ymax=paste(colName,"+ci",sep="")),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9))
			#+ facet_grid(. ~ RESOURCE_UNIFORMITY ) 
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
			ggplot(tst, aes_string(y=colName, x="RESOURCE_UNIFORMITY"))
			+ geom_bar(position=position_dodge(), stat="identity", color="black", fill="white")
			+ geom_errorbar(aes_string(ymin=paste(colName,"-ci",sep=""), ymax=paste(colName,"+ci",sep="")),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9))
			#+ facet_grid(. ~ RESOURCE_UNIFORMITY ) 
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





plotBootHistPop_I <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	#t1 <- popDataFrame[popDataFrame$RESOURCE_UNIFORMITY == "p",]
	#t1 <- t1$CAPTURES_BEST_CASE
	#print(t.test(t1))
	
	
	#print(summary(popDataFrame$CAPTURES_BEST_CASE))
	#t#st <- summarySE(popDataFrame,measurevar=colName,groupvars=c("RESOURCE_UNIFORMITY"))
	#tst$RESOURCE_UNIFORMITY <- factor(tst$RESOURCE_UNIFORMITY)
	#print(tst$CAPTURES_BEST_CASE-tst$ci)
	#print(tst$CAPTURES_BEST_CASE)
	#print(tst$CAPTURES_BEST_CASE+tst$ci)
	


	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.5,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="RESOURCE_UNIFORMITY")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			#+ facet_grid(. ~ INTERACTIONS) 
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
			ggplot(popDataFrame, aes_string(colName, fill="RESOURCE_UNIFORMITY"))
			+ geom_density(alpha = 0.9)
			#+ geom_vline(xintercept=tst$CAPTURES_BEST_CASE, size=0.001)
			#+ geom_vline(xintercept=(tst$CAPTURES_BEST_CASE+tst$ci), size=0.001)
			#+ geom_vline(xintercept=(tst$CAPTURES_BEST_CASE-tst$ci), size=0.001)
			+ scale_fill_manual(values=c("white","grey50")) 
			#+ facet_grid(. ~ INTERACTIONS) 
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
#stop()
	
}



plotBootHistPop_Q <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(. ~ QUALITY) 
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
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(. ~ QUALITY) 
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


plotBootHistPop_IQ <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(INTERACTIONS ~ QUALITY) 
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
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(INTERACTIONS ~ QUALITY) 
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


bootedTTest <-function(measureName,sampleA, sampleB, bootSize) {
	

	sampleA <- bootMean(sampleA,bootSize)
	sampleB <- bootMean(sampleB,bootSize)



	tResult <- t.test(sampleA,sampleB)
	
	#print(tResult)
	tValue <- tResult$statistic[[1]]
	dFreedom <- tResult$parameter[[1]]
	pValue <- as.double(tResult$p.value)


	
	resultFrame <- data.frame(MEASURE=measureName,
		T_VALUE=tValue, D_FREEDOM=dFreedom, P_VALUE=pValueString(pValue))

	
	return(resultFrame)
}


doBootAnalysis <-function(expDataFrame) {

	bootSize <- 1000	

	distTable <- data.frame()

	p("BOOT ANALYSIS >>>>>>")

	dataA <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="uniform",]
	dataB <- expDataFrame[expDataFrame$RESOURCE_UNIFORMITY=="mixed",]

	distTable <- rbind(distTable,bootedTTest("CAPTURES_MEAN",dataA$CAPTURES_MEAN,dataB$CAPTURES_MEAN,bootSize))
	distTable <- rbind(distTable,bootedTTest("CAPTURES_BEST_CASE",dataA$CAPTURES_BEST_CASE,dataB$CAPTURES_BEST_CASE,bootSize))
	distTable <- rbind(distTable,bootedTTest("RES_E2C_STEPS_MEAN",dataA$RES_E2C_STEPS_MEAN,dataB$RES_E2C_STEPS_MEAN,bootSize))
	distTable <- rbind(distTable,bootedTTest("RATE_COMMUNICATION",dataA$RATE_COMMUNICATION,dataB$RATE_COMMUNICATION,bootSize))
	distTable <- rbind(distTable,bootedTTest("RATE_MOTION",dataA$RATE_MOTION,dataB$RATE_MOTION,bootSize))
	distTable <- rbind(distTable,bootedTTest("NUM_DETECTORS",dataA$NUM_DETECTORS,dataB$NUM_DETECTORS,bootSize))
	distTable <- rbind(distTable,bootedTTest("NUM_EXTRACTORS",dataA$NUM_EXTRACTORS,dataB$NUM_EXTRACTORS,bootSize))
	distTable <- rbind(distTable,bootedTTest("NUM_TRANSPORTERS",dataA$NUM_TRANSPORTERS,dataB$NUM_TRANSPORTERS,bootSize))
	distTable <- rbind(distTable,bootedTTest("E",dataA$E,dataB$E,bootSize))


	print(distTable)	
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,-2)), include.rownames=FALSE))

	p("<<<<<<<<< BOOT ANALYSIS TEST")
	
}








computeBootStatsI <-function(fOrigDataFrame, pOrigDataFrame) {
bootSize <- 1000



	# extract the measures
	f <- data.frame()
	p <- data.frame()
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities

	
	for(varInteraction in unique(fOrigDataFrame$INTERACTIONS)) {
			print(varInteraction)

			f <- data.frame()
			p <- data.frame()
			
			fDataFrame <- fOrigDataFrame[fOrigDataFrame$INTERACTIONS==varInteraction,]
			
			pDataFrame <- pOrigDataFrame[pOrigDataFrame$INTERACTIONS==varInteraction,]
			
			
			f.CAPTURES_BEST_CASE <- fDataFrame$CAPTURES_BEST_CASE
			f.CAPTURES_MEAN <- fDataFrame$CAPTURES_MEAN
			f.RES_E2C_STEPS_MEAN <- fDataFrame$RES_E2C_STEPS_MEAN
			f.RATE_MOTION <- fDataFrame$RATE_MOTION
			f.RATE_COMMUNICATION <- fDataFrame$RATE_COMMUNICATION
			f.RESOURCE_UNIFORMITY <- fDataFrame$RESOURCE_UNIFORMITY
			
			f.NUM_DETECTORS <- fDataFrame$NUM_DETECTORS
			f.NUM_EXTRACTORS <- fDataFrame$NUM_EXTRACTORS
			f.NUM_TRANSPORTERS <- fDataFrame$NUM_TRANSPORTERS
			f.E <- fDataFrame$E
					

			f.CAPTURES_BEST_CASE <- bootMean(f.CAPTURES_BEST_CASE,bootSize)
			
			f.CAPTURES_MEAN <- bootMean(f.CAPTURES_MEAN,bootSize)
			f.RES_E2C_STEPS_MEAN <- bootMean(f.RES_E2C_STEPS_MEAN,bootSize)
			f.RATE_MOTION <- bootMean(f.RATE_MOTION,bootSize)
			f.RATE_COMMUNICATION <- bootMean(f.RATE_COMMUNICATION,bootSize)

			f.NUM_DETECTORS <- bootMean(f.NUM_DETECTORS,bootSize)
			f.NUM_EXTRACTORS <- bootMean(f.NUM_EXTRACTORS,bootSize)
			f.NUM_TRANSPORTERS <- bootMean(f.NUM_TRANSPORTERS,bootSize)
			f.E <- bootMean(f.E,bootSize)	


			p.CAPTURES_BEST_CASE <- pDataFrame$CAPTURES_BEST_CASE
			p.CAPTURES_MEAN <- pDataFrame$CAPTURES_MEAN
			p.RES_E2C_STEPS_MEAN <- pDataFrame$RES_E2C_STEPS_MEAN
			p.RATE_MOTION <- pDataFrame$RATE_MOTION
			p.RATE_COMMUNICATION <- pDataFrame$RATE_COMMUNICATION
			p.RESOURCE_UNIFORMITY <- pDataFrame$RESOURCE_UNIFORMITY

			p.NUM_DETECTORS <- pDataFrame$NUM_DETECTORS
			p.NUM_EXTRACTORS <- pDataFrame$NUM_EXTRACTORS
			p.NUM_TRANSPORTERS <- pDataFrame$NUM_TRANSPORTERS
			p.E <- pDataFrame$E
			p.RESOURCE_UNIFORMITY <- pDataFrame$RESOURCE_UNIFORMITY
				

			p.CAPTURES_BEST_CASE <- bootMean(p.CAPTURES_BEST_CASE,bootSize)
			p.CAPTURES_MEAN <- bootMean(p.CAPTURES_MEAN,bootSize)
			p.RES_E2C_STEPS_MEAN <- bootMean(p.RES_E2C_STEPS_MEAN,bootSize)
			p.RATE_MOTION <- bootMean(p.RATE_MOTION,bootSize)
			p.RATE_COMMUNICATION <- bootMean(p.RATE_COMMUNICATION,bootSize)


			p.NUM_DETECTORS <- bootMean(p.NUM_DETECTORS,bootSize)
			p.NUM_EXTRACTORS <- bootMean(p.NUM_EXTRACTORS,bootSize)
			p.NUM_TRANSPORTERS <- bootMean(p.NUM_TRANSPORTERS,bootSize)
			p.E <- bootMean(p.E,bootSize)	

			
			#print(f.RESOURCE_UNIFORMITY)
			#stop()
			

			f.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=f.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=f.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=f.RES_E2C_STEPS_MEAN,
				RATE_MOTION=f.RATE_MOTION,
				RATE_COMMUNICATION=f.RATE_COMMUNICATION,
				NUM_DETECTORS=f.NUM_DETECTORS,
				NUM_EXTRACTORS=f.NUM_EXTRACTORS,
				NUM_TRANSPORTERS=f.NUM_TRANSPORTERS,
				E=f.E,
				RESOURCE_UNIFORMITY="f",
				INTERACTIONS=varInteraction
			)

			
			

			p.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=p.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=p.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=p.RES_E2C_STEPS_MEAN,
				RATE_MOTION=p.RATE_MOTION,
				RATE_COMMUNICATION=p.RATE_COMMUNICATION,
				NUM_DETECTORS=p.NUM_DETECTORS,
				NUM_EXTRACTORS=p.NUM_EXTRACTORS,
				NUM_TRANSPORTERS=p.NUM_TRANSPORTERS,
				E=p.E,
				RESOURCE_UNIFORMITY="p",
				INTERACTIONS=varInteraction

			)

			

			popDataFrame <- rbind(popDataFrame,f.popDataFrame,p.popDataFrame)


		}
	
	
	return(popDataFrame)

}






plotBootedStats <- function(expDataFrame) {
	
	popDataFrame <- computeBootStatsI(expDataFrame[expDataFrame$RU=="f",], expDataFrame[expDataFrame$RU=="p",])
	
	
	plotBootHistPop_I(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-cb.pdf", TRUE)
	plotBootHistPop_I(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-cm.pdf", TRUE)
	plotBootHistPop_I(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-e2c.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-rm.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-rc.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"NUM_DETECTORS","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-dets.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"NUM_EXTRACTORS","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-exts.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"NUM_TRANSPORTERS","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-trns.pdf", FALSE)
	plotBootHistPop_I(popDataFrame,"E","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-e.pdf", FALSE)

}

##############################   MAIN PROCESS BEGINS ###############################



expDataFrame <- read.csv(file="~/synthscape/scripts/analysis/data/exp4/exp4_dyn_experiments_mean_300.csv")

expDataFrame <- preProcessData(expDataFrame)     # factorizes, as appropriate, adjusts E2C...
expDataFrame <- renameFactorValues(expDataFrame) # renames for nice plots

orig <- expDataFrame

expDataFrame <- expDataFrame[expDataFrame$INTERACTIONS=="trail",]




plotHists(expDataFrame)    # plots histograms
doNormalityAnalysis(expDataFrame)


plotBoxPlots(expDataFrame) 
doNonParametricAnalysis(expDataFrame)

plotBootedStats(expDataFrame)
doBootAnalysis(expDataFrame)






#plot the totals

