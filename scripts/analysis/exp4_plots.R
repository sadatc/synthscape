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



doNormalityAnalysisSubPop <- function(expDataFrame) {
	distTable <- data.frame()



#	print("========== ALIFE ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous"  & expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" &  expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	data(distTable)
	print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
}



doNormalityAnalysisFullPop <- function(expDataFrame) {
	distTable <- data.frame()

	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous" & expDataFrame$QUALITY=="s"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$QUALITY=="s"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	

	p("**** Normality test for Pg vs Ps (Shapiro-Wilks Normality Test)  ****")
	data(distTable)
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


plotHistByPopulationQuality <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
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
			facet_grid( POPULATION ~ QUALITY) +
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
			facet_grid( POPULATION ~ QUALITY) +
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


plotHistByInteractionQuality <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="homogenous"] <-  "homogenous"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="heterogenous"] <-  "heterogenous"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName)) +
			geom_histogram(color="black", fill="grey80",alpha = 0.85) +
			facet_grid( INTERACTIONS ~ QUALITY) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("grey80")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.x = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame,aes_string(x=colName)) +
			geom_histogram(color="black", fill="grey80",alpha = 0.85) +
			facet_grid( INTERACTIONS ~ QUALITY) +
			xlab(xAxisLabel) +
			#scale_fill_manual(values=c("grey80")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 				
				#axis.title.x = element_text(family="cmsy10"),
				axis.text.x = element_text(size=rel(0.7)))
			+ scale_x_continuous(labels=percentFormatter)
		)
	}
	dev.off()

}




plotHistBySubPopulation <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="homogenous"] <-  "non-interacting homogenous"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="heterogenous"] <-  "interacting heterogenous"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ ., labeller=label_parsed) +
			xlab(xAxisLabel) +
			scale_fill_manual(values=c("white","grey50")) +
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
			scale_fill_manual(values=c("white","grey50")) +
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
	
	plotHistByPopulation("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-cm.pdf", TRUE)
	plotHistByPopulation("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-cb.pdf", TRUE)
	plotHistByPopulation("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-e4c.pdf")
	plotHistByPopulation("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-rm.pdf")
	plotHistByPopulation("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-rc.pdf")

	plotHistByPopulationQuality("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-q-cm.pdf", TRUE)
	plotHistByPopulationQuality("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-q-cb.pdf", TRUE)
	plotHistByPopulationQuality("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-q-e4c.pdf")
	plotHistByPopulationQuality("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-q-rm.pdf")
	plotHistByPopulationQuality("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-q-rc.pdf")


	plotHistByInteractionQuality("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-iq-cm.pdf", TRUE)
	plotHistByInteractionQuality("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-iq-cb.pdf", TRUE)
	plotHistByInteractionQuality("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-iq-e4c.pdf")
	plotHistByInteractionQuality("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-iq-rm.pdf")
	plotHistByInteractionQuality("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-alife-iq-rc.pdf")


	
	plotHistBySubPopulation("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-s-alife-cm.pdf", TRUE)
	plotHistBySubPopulation("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-s-alife-cb.pdf", TRUE)
	plotHistBySubPopulation("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-s-alife-e4c.pdf")
	plotHistBySubPopulation("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-s-alife-rm.pdf")
	plotHistBySubPopulation("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-s-alife-rc.pdf")

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
	  width = 4,height = 4, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~POPULATION_MIX) +
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
			facet_grid(INTERACTIONS ~ POPULATION_MIX) +
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
	  width = 4,height = 4, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~ RICHNESS) +
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
			facet_grid(INTERACTIONS ~ RICHNESS) +
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
	  width = 6,height = 4, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="POPULATION",y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~ T2SRATIO) +
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
			facet_grid(INTERACTIONS ~ T2SRATIO) +
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



plotBoxPlotsAllPop <- function(expDataFrame) {
	plotBoxPlot_PM(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-all-cb.pdf", TRUE)

	plotBoxPlot_PM(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-all-cm.pdf", TRUE)

	plotBoxPlot_PM(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-all-e2c.pdf", FALSE)

	plotBoxPlot_PM(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-all-rm.pdf", FALSE)

	plotBoxPlot_PM(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-all-rc.pdf", FALSE)
}



plotBoxPlots <- function(expDataFrame) {
	orig <- expDataFrame


	# compare richness type vs performance

	plotBoxPlot_PM(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-cb.pdf", TRUE)

	plotBoxPlot_PM(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-cm.pdf", TRUE)

	plotBoxPlot_PM(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-e2c.pdf", FALSE)

	plotBoxPlot_PM(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-rm.pdf", FALSE)

	plotBoxPlot_PM(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-pm-rc.pdf", FALSE)




	plotBoxPlot_R(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-cb.pdf", TRUE)

	plotBoxPlot_R(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-cm.pdf", TRUE)

	plotBoxPlot_R(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-e2c.pdf", FALSE)

	plotBoxPlot_R(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-rm.pdf", FALSE)

	plotBoxPlot_R(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-rc.pdf", FALSE)


	plotBoxPlot_T2S(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-t2s-cb.pdf", TRUE)

	plotBoxPlot_T2S(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-t2s-cm.pdf", TRUE)

	plotBoxPlot_T2S(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-t2s-e2c.pdf", FALSE)

	plotBoxPlot_T2S(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-t2s-rm.pdf", FALSE)

	plotBoxPlot_T2S(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-box-r-rc.pdf", FALSE)







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
		from=c("mo","bi","tr","po","ho"), 
		to=c("mono-","bi-","tri-","poly-","homogenous")
	)

	dataFrame$POPULATION_MIX <-factor(
		dataFrame$POPULATION_MIX,c("mono-","bi-","tri-","poly-","homogenous")
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


plotBootHistPop_I <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(. ~ INTERACTIONS) 
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
			+ facet_grid(. ~ INTERACTIONS) 
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


computeBootStats <-function(s.data.frame,g.data.frame) {
	bootSize <- 1000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
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
		POPULATION="heterogenous"
	)


	g.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION,
		RATE_COMMUNICATION=g.RATE_COMMUNICATION,
		POPULATION="homogenous"
	)
	
	popDataFrame <- rbind(g.popDataFrame,s.popDataFrame)

	return(popDataFrame)

}


computeBootStatsIQ <-function(s.orig.data.frame,g.orig.data.frame) {
	bootSize <- 2000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	
	for(varInteraction in levels(s.orig.data.frame$INTERACTIONS)) {
		for(var.quality in levels(s.orig.data.frame$QUALITY)) {
			s <- data.frame()
			g <- data.frame()
			
			s.data.frame <- s.orig.data.frame[s.orig.data.frame$INTERACTIONS==varInteraction & s.orig.data.frame$QUALITY==var.quality,]
			
			g.data.frame <- g.orig.data.frame[g.orig.data.frame$INTERACTIONS==varInteraction & g.orig.data.frame$QUALITY==var.quality,]
			
			
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
				POPULATION="heterogenous",
				QUALITY=var.quality,
				INTERACTIONS=varInteraction
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="homogenous",
				QUALITY=var.quality,
				INTERACTIONS=varInteraction

			)

			popDataFrame <- rbind(popDataFrame,g.popDataFrame,s.popDataFrame)
			print(paste("dealt with",varInteraction,var.quality))

		}
	}
	
	return(popDataFrame)

}



computeBootStatsI <-function(s.orig.data.frame,g.orig.data.frame) {
	bootSize <- 1000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	
	for(varInteraction in levels(s.orig.data.frame$INTERACTIONS)) {

			s <- data.frame()
			g <- data.frame()
			
			s.data.frame <- s.orig.data.frame[s.orig.data.frame$INTERACTIONS==varInteraction,]
			
			g.data.frame <- g.orig.data.frame[g.orig.data.frame$INTERACTIONS==varInteraction,]
			
			
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
				POPULATION="heterogenous",

				INTERACTIONS=varInteraction
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="homogenous",

				INTERACTIONS=varInteraction

			)

			popDataFrame <- rbind(popDataFrame,g.popDataFrame,s.popDataFrame)


		}
	
	
	return(popDataFrame)

}


computeBootStatsQ <-function(s.orig.data.frame,g.orig.data.frame) {
	bootSize <- 1000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
	popDataFrame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities
	

		for(var.quality in levels(s.orig.data.frame$QUALITY)) {
			s <- data.frame()
			g <- data.frame()
			
			s.data.frame <- s.orig.data.frame[ s.orig.data.frame$QUALITY==var.quality,]
			
			g.data.frame <- g.orig.data.frame[ g.orig.data.frame$QUALITY==var.quality,]
			
			
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
				POPULATION="heterogenous",
				QUALITY=var.quality
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="homogenous",
				QUALITY=var.quality

			)

			popDataFrame <- rbind(popDataFrame,g.popDataFrame,s.popDataFrame)
			#print(paste("dealt with",varInteraction,var.quality))

		
	}
	
	return(popDataFrame)

}



plotBootedStats <- function(expDataFrame) {


	popDataFrame <- computeBootStatsI(expDataFrame[expDataFrame$SPECIES=="heterogenous",], expDataFrame[expDataFrame$SPECIES=="homogenous",])

plotBootHistPop_I(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-cb.pdf", TRUE)


plotBootHistPop_I(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-cm.pdf", TRUE)

plotBootHistPop_I(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-e4c.pdf", FALSE)


plotBootHistPop_I(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-rm.pdf", FALSE)


plotBootHistPop_I(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-i-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsQ(expDataFrame[expDataFrame$SPECIES=="heterogenous",], expDataFrame[expDataFrame$SPECIES=="homogenous",])



plotBootHistPop_Q(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-q-cb.pdf", TRUE)

plotBootHistPop_Q(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-q-cm.pdf", TRUE)

plotBootHistPop_Q(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-q-e4c.pdf", FALSE)


plotBootHistPop_Q(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-q-rm.pdf", FALSE)


plotBootHistPop_Q(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-q-rc.pdf", FALSE)


popDataFrame <- computeBootStatsIQ(expDataFrame[expDataFrame$SPECIES=="heterogenous",], expDataFrame[expDataFrame$SPECIES=="homogenous",])


	plotBootHistPop_IQ(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-iq-cb.pdf", TRUE)

plotBootHistPop_IQ(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-iq-cm.pdf", TRUE)

plotBootHistPop_IQ(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-iq-e4c.pdf", FALSE)


plotBootHistPop_IQ(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-iq-rm.pdf", FALSE)


plotBootHistPop_IQ(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-boot-pop-iq-rc.pdf", FALSE)

if(1!=1) {

	t.table <- data.frame()


	t.table <- rbind(t.table, performTTest("CAPTURES_BEST_CASE",s.CAPTURES_BEST_CASE,g.CAPTURES_BEST_CASE))
	
	t.table <- rbind(t.table, performTTest("CAPTURES_MEAN",s.CAPTURES_MEAN,g.CAPTURES_MEAN))

	t.table <- rbind(t.table, performTTest("RES_E2C_STEPS_MEAN",s.RES_E2C_STEPS_MEAN,g.RES_E2C_STEPS_MEAN))

	t.table <- rbind(t.table, performTTest("RATE_MOTION",s.RATE_MOTION,g.RATE_MOTION))

	data(t.table)
	print("data table for pg vs ps")
	print(xtable(t.table, digits=c(0,0,2,2,-2), include.rownames=FALSE))
	#print(xtable(t.table, include.rownames=FALSE))
	}

}








##############################   MAIN PROCESS BEGINS ###############################


expDataFrame <- read.csv(file="~/synthscape/scripts/analysis/data/exp4/exp4_dyn_experiments_mean_300.csv")

expDataFrame <- preProcessData(expDataFrame)     # factorizes, as appropriate, adjusts E2C...
expDataFrame <- renameFactorValues(expDataFrame) # renames for nice plots

orig <- expDataFrame

##### not using these....plotGraphs(expDataFrame)

# Using these...

# Populations: 3_4 and 3_8
# Richness Types: mo, bi, po, tr, ho
# Richness
# Traitsum
# 

#plotHists(expDataFrame)    # plots histograms

#doNormalityAnalysisFullPop(expDataFrame)
#doNormalityAnalysisSubPop(expDataFrame)

# first we'll only look at the heterogenous population:
expDataFrame <- expDataFrame[expDataFrame$SPECIES=="heterogenous",]
#plotBoxPlots(expDataFrame) # boxplots to show difference

#expDataFrame <- orig
#plotBoxPlotsAllPop(expDataFrame)



plotBootedStats(expDataFrame)








#plot the totals

