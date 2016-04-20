# island model
library(ggplot2)
library(plyr)
library(xtable)
library(extrafont)
library(scales)
library(extrafont)
#library(tikzDevice)

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



#library(scale)
p <- function(msg) {
	print("**************************************************************************", quote=FALSE)
	print(msg, quote=FALSE)
	print("**************************************************************************", quote=FALSE)

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


pValueString <- function(val) {
	result <- val

	if(val < 0.05) {
		result <- "p < 0.05"
	} 

	if(val < 0.01) {
		result <- "p < 0.01"
	} 

	if(val > 0.05) {
		result <- paste("p = ",round(val,digits=2),sep="")
	}
	
	return(result)
}
#
# performs shapiro-wilk test and mann-whitney test
# reports back a data.frame
#
analyzeNormailtyPair <-function(model,measure,g,s) {
	rMeasure <- measure

#	print("performing shapiro test. if p<0.05, sample likely non-normal")
	rGSampleSize <- length(g)
	rGShapiro <- shapiro.test(g)

p(rGShapiro)
	rGShapiroW <- rGShapiro$statistic[[1]]
	rGShapiroP <- rGShapiro$p.value
	


	rSSampleSize <- length(s)
	rSShapiro <- shapiro.test(s)

p(rSShapiro)

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
	
			
	
	result.frame <- data.frame(MODEL=model,MEASURE=measure,
		G_SHAP_W=rGShapiroW, G_SHAP_P=pValueString(rGShapiroP), S_SHAP_W=rSShapiroW, S_SHAP_P=pValueString(rSShapiroP))
	
	
	
	
	
	return(result.frame)
}



doNormalityAnalysis_PMSubPop <- function(expDataFrame) {
	distTable <- data.frame()



#	print("========== ALIFE ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous"  & expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" &  expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	
	print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
}



doNormalityAnalysis_PMFullPop <- function(expDataFrame) {
	distTable <- data.frame()

	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous" & expDataFrame$QUALITY=="s"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$QUALITY=="s"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	

	p("**** Normality test for Pg vs Ps (Shapiro-Wilks Normality Test)  ****")
	
	print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
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

percentFormatter <- function(x) {
	y <- paste(round(x*100),"%",sep="")
	return(y)
}


getMeasurePrettyName <- function(colName) {
	result <- colName
	
	if(colName == "CAPTURES_MEAN") {
		result <- expression(italic(M[mean]) ~ ":  Mean Captures (% of total)")
	}

	if(colName == "CAPTURES_BEST_CASE") {
		result <- expression(italic(M[best]) ~ ":  Best Captures (% of total)")
	}

	if(colName == "RES_E2C_STEPS_MEAN") {
		result <- expression(italic(M[effort]) ~ ":  Mean Steps between Extraction & Capture")
	}

	if(colName == "RATE_COMMUNICATION") {
		result <- expression(italic(M[comm]) ~ ":  Communication Instructions")
	}

	if(colName == "RATE_MOTION") {
		result <- expression(italic(M[move]) ~ ":  Movement Instructions")
	}
	return(result)
}



getMeasureShortName <- function(colName) {
	result <- colName
	
	if(colName == "CAPTURES_MEAN") {
		result <- expression(italic(M[mean]) )
	}

	if(colName == "CAPTURES_BEST_CASE") {
		result <- expression(italic(M[best]))
	}

	if(colName == "RES_E2C_STEPS_MEAN") {
		result <- expression(italic(M[effort]))
	}

	if(colName == "RATE_COMMUNICATION") {
		result <- expression(italic(M[comm]) )
	}

	if(colName == "RATE_MOTION") {
		result <- expression(italic(M[move]) )
	}
	return(result)
}





plotHistByPopulationMix <-function(dataFrame, colName, fileName, showPercent=FALSE) {
	
	
	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 7,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( . ~ POPULATION_MIX, labeller=label_parsed) +
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
			facet_grid( . ~ POPULATION_MIX, labeller=label_parsed) +
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

	orig <- expDataFrame
	
	expDataFrame <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="3_8",]
	plotHistByPopulationMix( expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-hist-cm.pdf", TRUE)
	plotHistByPopulationMix( expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-hist-cb.pdf", TRUE)
	plotHistByPopulationMix( expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-hist-e2c.pdf")
	plotHistByPopulationMix( expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-hist-rm.pdf")
	plotHistByPopulationMix( expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-hist-rc.pdf")
	expDataFrame <- orig
	
	expDataFrame <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="4_24",]
	plotHistByPopulationMix( expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-hist-cm.pdf", TRUE)
	plotHistByPopulationMix( expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-hist-cb.pdf", TRUE)
	plotHistByPopulationMix( expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-hist-e2c.pdf")
	plotHistByPopulationMix( expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-hist-rm.pdf")
	plotHistByPopulationMix( expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-hist-rc.pdf")
	expDataFrame <- orig

}



plotBoxPlot_I_Q <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~ QUALITY) + #, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
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
			facet_grid( INTERACTIONS ~ QUALITY ) + # labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
			scale_y_continuous(labels=percentFormatter)+
			geom_line(size=0.1) +
			theme_bw() + 			
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="bottom", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()

				)
		)
	}

	dev.off()
}


plotBoxPlot_PM <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 3,height = 2.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( . ~POPULATION_MIX) +
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
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid(. ~ POPULATION_MIX) +
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


plotBoxPlot_R <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 3,height = 2.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( . ~ RICHNESS) +
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
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid(. ~ RICHNESS) +
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




plotBoxPlot_T2S <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 3,height = 2.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( . ~ T2SRATIO) +
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
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid(. ~ T2SRATIO) +
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




plotBoxPlot_Q <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( . ~ QUALITY) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
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
			facet_grid(. ~ QUALITY) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
			#scale_x_continuous(labels=percentFormatter)+
			scale_y_continuous(labels=percentFormatter)+
			geom_line(size=0.1) +
			theme_bw() + 			
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="bottom", 
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

	g.mean <- mean(g, na.rm=TRUE)
	g.var <- var(g, na.rm=TRUE)
	g.median <- median(g, na.rm=TRUE)
	g.t <- t.test(g)
	g.ci1 <- g.t$conf.int[[1]]
	g.ci2 <- g.t$conf.int[[2]]
	
	

	
	s <- d.f[(d.f$SPECIES=="heterogenous"),]
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

	g.f <- data.frame( POPULATION="homogenous", MEAUSURE=measure, MEAN=g.mean,
		VAR = g.var, MEDIAN = g.median, CI1 = g.ci1, CI2 = g.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)
	s.f <- data.frame( POPULATION="heterogenous", MEAUSURE=measure, MEAN=s.mean,
		VAR = s.var, MEDIAN = s.median, CI1 = s.ci1, CI2 = s.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)


	r.f <- rbind(g.f,s.f)

	print(r.f)

}







plotBoxPlots <- function(expDataFrame) {
	orig <- expDataFrame

	

	expDataFrame <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="3_8",]	
	plotBoxPlot_PM(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-pm-cb.pdf", TRUE)
	plotBoxPlot_PM(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-pm-cm.pdf", TRUE)
	plotBoxPlot_PM(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-pm-e2c.pdf", FALSE)
	plotBoxPlot_PM(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-pm-rm.pdf", FALSE)
	plotBoxPlot_PM(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-pm-rc.pdf", FALSE)

	plotBoxPlot_R(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-r-cb.pdf", TRUE)
	plotBoxPlot_R(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-r-cm.pdf", TRUE)
	plotBoxPlot_R(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-r-e2c.pdf", FALSE)
	plotBoxPlot_R(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-r-rm.pdf", FALSE)
	plotBoxPlot_R(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-r-rc.pdf", FALSE)


	plotBoxPlot_T2S(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-t2s-cb.pdf", TRUE)
	plotBoxPlot_T2S(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-t2s-cm.pdf", TRUE)
	plotBoxPlot_T2S(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-t2s-e2c.pdf", FALSE)
	plotBoxPlot_T2S(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-t2s-rm.pdf", FALSE)
	plotBoxPlot_T2S(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-box-t2s-rc.pdf", FALSE)
	expDataFrame <- orig



	expDataFrame <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="4_24",]	
	plotBoxPlot_PM(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-pm-cb.pdf", TRUE)
	plotBoxPlot_PM(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-pm-cm.pdf", TRUE)
	plotBoxPlot_PM(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-pm-e2c.pdf", FALSE)
	plotBoxPlot_PM(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-pm-rm.pdf", FALSE)
	plotBoxPlot_PM(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-pm-rc.pdf", FALSE)

	plotBoxPlot_R(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-r-cb.pdf", TRUE)
	plotBoxPlot_R(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-r-cm.pdf", TRUE)
	plotBoxPlot_R(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-r-e2c.pdf", FALSE)
	plotBoxPlot_R(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-r-rm.pdf", FALSE)
	plotBoxPlot_R(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-r-rc.pdf", FALSE)


	plotBoxPlot_T2S(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-t2s-cb.pdf", TRUE)
	plotBoxPlot_T2S(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-t2s-cm.pdf", TRUE)
	plotBoxPlot_T2S(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-t2s-e2c.pdf", FALSE)
	plotBoxPlot_T2S(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-t2s-rm.pdf", FALSE)
	plotBoxPlot_T2S(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-box-t2s-rc.pdf", FALSE)
	expDataFrame <- orig


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


	dataFrame$POPULATION_MIX <- mapvalues(dataFrame$POPULATION_MIX, 
		from=c("mo","bi","tr","qu","po","ho"), 
		to=c("mono","bi","tri","qua","poly","homogenous")
	)

	dataFrame$POPULATION_MIX <-factor(
		dataFrame$POPULATION_MIX,c("mono","bi","tri","qua","poly","homogenous")
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
	expDataFrame$RICHNESS_VARIATION <- factor(expDataFrame$RICHNESS_VARIATION)
	expDataFrame$POPULATION <- expDataFrame$SPECIES
	expDataFrame$POPULATION_MIX <- expDataFrame$RICHNESS_TYPE
	
	expDataFrame$POPULATION <- factor(expDataFrame$POPULATION)	
	expDataFrame$INTERACTIONS <- factor(expDataFrame$INTERACTIONS)
	expDataFrame$COMPLEXITY <- factor(expDataFrame$COMPLEXITY)
	expDataFrame$CLONES <- factor(expDataFrame$CLONES)
	expDataFrame$RICHNESS_TYPE <- factor(expDataFrame$RICHNESS_TYPE)
	expDataFrame$POPULATION_MIX <- factor(expDataFrame$POPULATION_MIX)
	
	
	expDataFrame$GRIDS <- factor(expDataFrame$GRIDS)
	expDataFrame$RESOURCES <- factor(expDataFrame$RESOURCES)
	expDataFrame$SITES <- factor(expDataFrame$SITES)
	expDataFrame$OBSTACLES <- factor(expDataFrame$OBSTACLES)
	expDataFrame$DIFFICULTY <- factor(expDataFrame$DIFFICULTY)

	
	expDataFrame$RES_E2C_STEPS_MEAN <- (expDataFrame$RES_E2C_STEPS_MEAN*expDataFrame$CAPTURES_MEAN)
	
	# round the T2SRATIO
	expDataFrame$T2SRATIO <- round(expDataFrame$T2SRATIO,3)

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


plotBootHistPop_PM <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

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

plotBootHistPop_R <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	tst <- summarySE(popDataFrame,measurevar=colName,groupvars=c("INTERACTIONS","RICHNESS"))
	tst$RICHNESS <- factor(tst$RICHNESS)


	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 3,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {

		print(
			ggplot(tst, aes_string(y=colName, x="RICHNESS"))
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
			ggplot(tst, aes_string(y=colName, x="RICHNESS"))
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



plotBootHistPop_T2S <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	tst <- summarySE(popDataFrame,measurevar=colName,groupvars=c("INTERACTIONS","T2SRATIO"))
	tst$T2SRATIO <- factor(tst$T2SRATIO)


	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 3,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {

		print(
			ggplot(tst, aes_string(y=colName, x="T2SRATIO"))
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
			ggplot(tst, aes_string(y=colName, x="T2SRATIO"))
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




#
# performs t-tests
# reports back a data.frame
#
performTTest <-function(measure,g.v,s.v) {

	t.result <- t.test(g.v,s.v)
	
	tValue <- t.result$statistic[[1]]
	dFreedom <- t.result$parameter[[1]]
	pValue <- as.double(t.result$p.value)

	#print(measure)
	#print(t.result)
	#print(summary(t.result))
	#print(pValue)	

	
	result.frame <- data.frame(MEASURE=measure,
		T_VALUE=tValue, D_FREEDOM=dFreedom, P_VALUE=pValue)
	
	return(result.frame)
}


computeBootStatsPM <-function(s.orig.data.frame) {
bootSize <- 1000

	# extract the measures
	s <- data.frame()
	
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	
	for(varInteractions in levels(s.orig.data.frame$INTERACTIONS)) {
		for(var.populationMix in unique(s.orig.data.frame$POPULATION_MIX)) {

				s <- data.frame()
				
				
				s.data.frame <- s.orig.data.frame[s.orig.data.frame$POPULATION_MIX==var.populationMix & s.orig.data.frame$INTERACTIONS ==varInteractions,]
				
				
				
				
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




				s.popDataFrame <- data.frame(
					CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
					CAPTURES_MEAN=s.CAPTURES_MEAN,
					RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
					RATE_MOTION=s.RATE_MOTION,
					RATE_COMMUNICATION=s.RATE_COMMUNICATION,
					POPULATION="heterogenous",
					INTERACTIONS = varInteractions,

					POPULATION_MIX=var.populationMix
				)


				

				popDataFrame <- rbind(popDataFrame,s.popDataFrame)


			}
		}
	
	
	return(popDataFrame)

}








computeBootStatsR <-function(s.orig.data.frame) {
bootSize <- 1000

	# extract the measures
	s <- data.frame()
	
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	
	for(varInteractions in levels(s.orig.data.frame$INTERACTIONS)) {
		for(var.Richness in unique(s.orig.data.frame$RICHNESS)) {

				s <- data.frame()
				
				
				s.data.frame <- s.orig.data.frame[s.orig.data.frame$RICHNESS==var.Richness & s.orig.data.frame$INTERACTIONS ==varInteractions,]
				
				
				
				
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




				s.popDataFrame <- data.frame(
					CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
					CAPTURES_MEAN=s.CAPTURES_MEAN,
					RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
					RATE_MOTION=s.RATE_MOTION,
					RATE_COMMUNICATION=s.RATE_COMMUNICATION,
					POPULATION="heterogenous",
					INTERACTIONS = varInteractions,

					RICHNESS=var.Richness
				)


				

				popDataFrame <- rbind(popDataFrame,s.popDataFrame)


			}
		}
	
	
	return(popDataFrame)

}




computeBootStatsT2S <-function(s.orig.data.frame) {
bootSize <- 1000

	# extract the measures
	s <- data.frame()
	
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	
	for(varInteractions in levels(s.orig.data.frame$INTERACTIONS)) {
		for(var.T2SRATIO in unique(s.orig.data.frame$T2SRATIO)) {

				s <- data.frame()
				
				
				s.data.frame <- s.orig.data.frame[s.orig.data.frame$T2SRATIO==var.T2SRATIO & s.orig.data.frame$INTERACTIONS ==varInteractions,]
				
				
				
				
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




				s.popDataFrame <- data.frame(
					CAPTURES_BEST_CASE=s.CAPTURES_BEST_CASE,
					CAPTURES_MEAN=s.CAPTURES_MEAN,
					RES_E2C_STEPS_MEAN=s.RES_E2C_STEPS_MEAN,
					RATE_MOTION=s.RATE_MOTION,
					RATE_COMMUNICATION=s.RATE_COMMUNICATION,
					POPULATION="heterogenous",
					INTERACTIONS = varInteractions,

					T2SRATIO=var.T2SRATIO
				)


				

				popDataFrame <- rbind(popDataFrame,s.popDataFrame)


			}
		}
	
	
	return(popDataFrame)

}






plotBootedStats <- function(expDataFrame) {

	popDataFrame <- computeBootStatsPM(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="3_8",])

	plotBootHistPop_PM(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-cb.pdf", TRUE)
	plotBootHistPop_PM(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-cm.pdf", TRUE)
	plotBootHistPop_PM(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-e2c.pdf", FALSE)
	plotBootHistPop_PM(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-rm.pdf", FALSE)
	plotBootHistPop_PM(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsPM(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="4_24",])

	plotBootHistPop_PM(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-cb.pdf", TRUE)
	plotBootHistPop_PM(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-cm.pdf", TRUE)
	plotBootHistPop_PM(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-e2c.pdf", FALSE)
	plotBootHistPop_PM(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-rm.pdf", FALSE)
	plotBootHistPop_PM(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsR(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="3_8",])

	plotBootHistPop_R(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-cb.pdf", TRUE)
	plotBootHistPop_R(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-cm.pdf", TRUE)
	plotBootHistPop_R(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-e2c.pdf", FALSE)
	plotBootHistPop_R(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-rm.pdf", FALSE)
	plotBootHistPop_R(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-r-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsR(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="4_24",])

	plotBootHistPop_R(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-cb.pdf", TRUE)
	plotBootHistPop_R(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-cm.pdf", TRUE)
	plotBootHistPop_R(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-e2c.pdf", FALSE)
	plotBootHistPop_R(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-rm.pdf", FALSE)
	plotBootHistPop_R(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-r-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsT2S(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="3_8",])

	plotBootHistPop_T2S(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-t2s-cb.pdf", TRUE)
	plotBootHistPop_T2S(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-t2s-cm.pdf", TRUE)
	plotBootHistPop_T2S(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-t2se-2c.pdf", FALSE)
	plotBootHistPop_T2S(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-t2s-rm.pdf", FALSE)
	plotBootHistPop_T2S(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-3_8-boot-pop-t2s-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsT2S(expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$RICHNESS_VARIATION=="4_24",])

	plotBootHistPop_T2S(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-t2s-cb.pdf", TRUE)
	plotBootHistPop_T2S(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-t2s-cm.pdf", TRUE)
	plotBootHistPop_T2S(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-t2s-e2c.pdf", FALSE)
	plotBootHistPop_T2S(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-t2s-rm.pdf", FALSE)
	plotBootHistPop_T2S(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp3/e3-4_24-boot-pop-t2s-rc.pdf", FALSE)
	
}




analyzeNormailtyMeasure_PM <-function(groupName,colName,dataFrame) {
	
	
	#mono
	data <- dataFrame[dataFrame$POPULATION_MIX=="mono",]	
	shap <- shapiro.test(data[[colName]])
	WMono <- shap$statistic[[1]]
	pValueMono <- pValueString(shap$p.value)

	#bi
	data <- dataFrame[dataFrame$POPULATION_MIX=="bi",]
	shap <- shapiro.test(data[[colName]])
	WBi <- shap$statistic[[1]]
	pValueBi <- pValueString(shap$p.value)
	
	#tri
	data <- dataFrame[dataFrame$POPULATION_MIX=="tri",]
	shap <- shapiro.test(data[[colName]])
	WTri <- shap$statistic[[1]]
	pValueTri <- pValueString(shap$p.value)
	#poly
	data <- dataFrame[dataFrame$POPULATION_MIX=="poly",]
	shap <- shapiro.test(data[[colName]])
	WPoly <- shap$statistic[[1]]
	pValuePoly <- pValueString(shap$p.value)
	
	
	result.frame <- data.frame(
		EXTRACTED_RESOURCES=groupName,
		MEASURE=colName,
		WMono=WMono,
		pValueMono=pValueMono,
		WBi=WBi,
		pValueBi=pValueBi,
		WTri=WTri,
		pValueTri=pValueTri,
		WPoly=WPoly,
		pValuePoly=pValuePoly		
	)
	

	if(groupName == "4_24") {
		#qua
		data <- dataFrame[dataFrame$POPULATION_MIX=="qua",]
		shap <- shapiro.test(data[[colName]])
		WQua <- shap$statistic[[1]]
		pValueQua <- pValueString(shap$p.value)

		result.frame <- data.frame(
			EXTRACTED_RESOURCES=groupName,
			MEASURE=colName,
			WMono=WMono,
			pValueMono=pValueMono,
			WBi=WBi,
			pValueBi=pValueBi,
			WTri=WTri,
			pValueTri=pValueTri,
			WQua=WQua,
			pValueQua=pValueQua,	
			WPoly=WPoly,
			pValuePoly=pValuePoly
				
		)

	} 



	return(result.frame)
}










analyzeNormailtyMeasure_R <-function(groupName,colName,dataFrame) {
	
	
	if(groupName == "3_8") {
	
		data <- dataFrame[dataFrame$RICHNESS==1,]	
		shap <- shapiro.test(data[[colName]])
		WR1 <- shap$statistic[[1]]
		pValueR1 <- pValueString(shap$p.value)

		data <- dataFrame[dataFrame$RICHNESS==3,]	
		shap <- shapiro.test(data[[colName]])
		WR3 <- shap$statistic[[1]]
		pValueR3 <- pValueString(shap$p.value)
		

		data <- dataFrame[dataFrame$RICHNESS==7,]	
		shap <- shapiro.test(data[[colName]])
		WR7 <- shap$statistic[[1]]
		pValueR7 <- pValueString(shap$p.value)
		
		result.frame <- data.frame(
			EXTRACTED_RESOURCES=groupName,
			MEASURE=colName,
			WR1=WR1,
			pValueR1=pValueR1,
			WR3=WR3,
			pValueR3=pValueR3,
			WR7=WR7,
			pValueR7=pValueR7
		)

		return(result.frame)
	}
	

	if(groupName == "4_24") {

		data <- dataFrame[dataFrame$RICHNESS==1,]	
		shap <- shapiro.test(data[[colName]])
		WR1 <- shap$statistic[[1]]
		pValueR1 <- pValueString(shap$p.value)

		data <- dataFrame[dataFrame$RICHNESS==4,]	
		shap <- shapiro.test(data[[colName]])
		WR4 <- shap$statistic[[1]]
		pValueR4 <- pValueString(shap$p.value)
		

		data <- dataFrame[dataFrame$RICHNESS==6,]	
		shap <- shapiro.test(data[[colName]])
		WR6 <- shap$statistic[[1]]
		pValueR6 <- pValueString(shap$p.value)
		
		data <- dataFrame[dataFrame$RICHNESS==15,]	
		shap <- shapiro.test(data[[colName]])
		WR15 <- shap$statistic[[1]]
		pValueR15 <- pValueString(shap$p.value)
		
		
		

		result.frame <- data.frame(
			EXTRACTED_RESOURCES=groupName,
			MEASURE=colName,
			WR1=WR1,
			pValueR1=pValueR1,
			WR4=WR4,
			pValueR4=pValueR4,
			WR6=WR6,
			pValueR6=pValueR6,
			WR15=WR15,
			pValueR15=pValueR15
		)
		return(result.frame)

	} 

}




analyzeNormailtyMeasure_T2S <-function(groupName,colName,dataFrame) {
	
	
	if(groupName == "3_8") {

	
		data <- dataFrame[dataFrame$T2SRATIO==1,]	
		shap <- shapiro.test(data[[colName]])
		WR1 <- shap$statistic[[1]]
		pValueR1 <- pValueString(shap$p.value)

		data <- dataFrame[dataFrame$T2SRATIO==1.714,]			
		shap <- shapiro.test(data[[colName]])
		WR1_714 <- shap$statistic[[1]]
		pValueR1_714 <- pValueString(shap$p.value)
		


		data <- dataFrame[dataFrame$T2SRATIO==2,]				
		shap <- shapiro.test(data[[colName]])
		WR2 <- shap$statistic[[1]]
		pValueR2 <- pValueString(shap$p.value)

		
		
		data <- dataFrame[dataFrame$T2SRATIO==3,]
		shap <- shapiro.test(data[[colName]])
		WR3 <- shap$statistic[[1]]
		pValueR3 <- pValueString(shap$p.value)


		result.frame <- data.frame(
			EXTRACTED_RESOURCES=groupName,
			MEASURE=colName,
			WR1=WR1,
			pValueR1=pValueR1,
			WR1_714=WR1_714,
			pValueR1_714=pValueR1_714,
			WR2=WR2,
			pValueR2=pValueR2,
			WR3=WR3,
			pValueR3=pValueR3
		)


		
		return(result.frame)
	}
	

	if(groupName == "4_24") {

		data <- dataFrame[dataFrame$T2SRATIO==1,]	
		shap <- shapiro.test(data[[colName]])
		WR1 <- shap$statistic[[1]]
		pValueR1 <- pValueString(shap$p.value)
		



		data <- dataFrame[dataFrame$T2SRATIO==2,]	
		shap <- shapiro.test(data[[colName]])
		WR2 <- shap$statistic[[1]]
		pValueR2 <- pValueString(shap$p.value)


		data <- dataFrame[dataFrame$T2SRATIO==2.133,]	
		shap <- shapiro.test(data[[colName]])
		WR2_133 <- shap$statistic[[1]]
		pValueR2_133 <- pValueString(shap$p.value)
		

		data <- dataFrame[dataFrame$T2SRATIO==3,]	
		shap <- shapiro.test(data[[colName]])
		WR3 <- shap$statistic[[1]]
		pValueR3 <- pValueString(shap$p.value)
		
		data <- dataFrame[dataFrame$T2SRATIO==4,]	
		shap <- shapiro.test(data[[colName]])
		WR4 <- shap$statistic[[1]]
		pValueR4 <- pValueString(shap$p.value)
		
		

		result.frame <- data.frame(
			EXTRACTED_RESOURCES=groupName,
			MEASURE=colName,
			WR1=WR1,
			pValueR1=pValueR1,
			WR2=WR2,
			pValueR2=pValueR2,
			WR2_133=WR2_133,
			pValueR2_133=pValueR2_133,
			WR3=WR3,
			pValueR3=pValueR3,
			WR4=WR4,
			pValueR4=pValueR4
		
		)
		return(result.frame)

	} 

}


doNormalityAnalysis <- function(expDataFrame) {

	distTable <- data.frame()

	p("POP-MIX: NORMALITY TEST >>>>>>")
	
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("3_8","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("3_8","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("3_8","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("3_8","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("3_8","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0,2,0)), include.rownames=FALSE)
	
	

	distTable <- data.frame()
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("4_24","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("4_24","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("4_24","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("4_24","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_PM("4_24","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0,2,0,2,0)), include.rownames=FALSE)
	p("<<<<<<<<< POP-MIX: NORMALITY TEST")

	distTable <- data.frame()

	p("RICHNESS: NORMALITY TEST >>>>>>")
	
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("3_8","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("3_8","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("3_8","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("3_8","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("3_8","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0)), include.rownames=FALSE)
	
	

	distTable <- data.frame()
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("4_24","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("4_24","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("4_24","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("4_24","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_R("4_24","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0,2,0)), include.rownames=FALSE)
	p("<<<<<<<<< RICHNESS: NORMALITY TEST")

	distTable <- data.frame()

	p("T2S: NORMALITY TEST >>>>>>")
	
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("3_8","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("3_8","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("3_8","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("3_8","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("3_8","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0,2,0)), include.rownames=FALSE)
	
	

	distTable <- data.frame()
	data <- expDataFrame[expDataFrame$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("4_24","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("4_24","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("4_24","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("4_24","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasure_T2S("4_24","RATE_COMMUNICATION",data))

	print(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,0,2,0,2,0,2,0,2,0)), include.rownames=FALSE)
	p("<<<<<<<<< T2S: NORMALITY TEST")
	
}


kruskalWallisTest <-function(groupName,measureName,groupByColParam,dataFrame) {

	library(pgirmess)

	measureData <- dataFrame[[measureName]]
	groupByCol <- dataFrame[[groupByColParam]]
	N <- length(measureData)
	
	
	
	#p(measureName)
	kResult <- kruskal.test(measureData ~ groupByCol)
	kResult2 <- kruskalmc(measureData ~ groupByCol, cont="two-tailed")
	
	
	#print(tResult)
	kValue <- kResult$statistic[[1]]
	dFreedom <- kResult$parameter[[1]]
	pValue <- as.double(kResult$p.value)

	
	
	resultFrame <- data.frame(
		EXTRACTED_RESOURCES = groupName,
		MEASURE=measureName,
		H_VALUE=kValue, 
		D_FREEDOM=dFreedom, 
		P_VALUE=pValueString(pValue))

	
	return(resultFrame)
}


doKruskalWallis <-function(expDataFrameOrig) {


	p("KRUSKAL-WALLIS 3_8 ANALYSIS >>>>>>")
	p("POPULATION_MIX")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_MEAN","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_BEST_CASE","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RES_E2C_STEPS_MEAN","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_COMMUNICATION","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_MOTION","POPULATION_MIX",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)

	p("KRUSKAL-WALLIS 4_8 ANALYSIS >>>>>>")
	p("POPULATION_MIX")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_MEAN","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_BEST_CASE","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RES_E2C_STEPS_MEAN","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_COMMUNICATION","POPULATION_MIX",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_MOTION","POPULATION_MIX",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)


	p("KRUSKAL-WALLIS 3_8 ANALYSIS >>>>>>")
	p("RICHNESS")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_MEAN","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_BEST_CASE","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RES_E2C_STEPS_MEAN","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_COMMUNICATION","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_MOTION","RICHNESS",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)

	p("KRUSKAL-WALLIS 4_8 ANALYSIS >>>>>>")
	p("RICHNESS")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_MEAN","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_BEST_CASE","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RES_E2C_STEPS_MEAN","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_COMMUNICATION","RICHNESS",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_MOTION","RICHNESS",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)


	p("KRUSKAL-WALLIS 3_8 ANALYSIS >>>>>>")
	p("T2SRATIO")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="3_8",]
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_MEAN","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","CAPTURES_BEST_CASE","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RES_E2C_STEPS_MEAN","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_COMMUNICATION","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("3_8","RATE_MOTION","T2SRATIO",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)

	p("KRUSKAL-WALLIS 4_8 ANALYSIS >>>>>>")
	p("T2SRATIO")

	distTable <- data.frame()
	expDataFrame <- expDataFrameOrig[expDataFrameOrig$RICHNESS_VARIATION=="4_24",]
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_MEAN","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","CAPTURES_BEST_CASE","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RES_E2C_STEPS_MEAN","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_COMMUNICATION","T2SRATIO",expDataFrame))
	distTable <- rbind(distTable,kruskalWallisTest("4_24","RATE_MOTION","T2SRATIO",expDataFrame))

	print(distTable)	
	print(xtable(distTable, digits=c(0,0,0,2,0,0)), include.rownames=FALSE)





	p("<<<<<<<<< KRUSKAL-WALLIS TEST")
	
}


##############################   MAIN PROCESS BEGINS ###############################


expDataFrame <- read.csv(file="~/synthscape/scripts/analysis/data/exp3/exp3_experiments_mean_300.csv")



expDataFrame <- preProcessData(expDataFrame)     # factorizes, as appropriate, adjusts E2C...
expDataFrame <- renameFactorValues(expDataFrame) # renames for nice plots

orig <- expDataFrame

# only keep the heterogenous ones...
expDataFrame <- expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$INTERACTIONS=="trail",]


plotHists(expDataFrame)    # plots histograms
doNormalityAnalysis(expDataFrame)

plotBoxPlots(expDataFrame) # boxplots to show difference
doKruskalWallis(expDataFrame)



#plotBootedStats(expDataFrame)


