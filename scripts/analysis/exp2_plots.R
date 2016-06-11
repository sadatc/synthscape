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
	gdata <- expDataFrame[expDataFrame$SPECIES=="HmI"  & expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="HtI" &  expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS!="none"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE))

	
}



doNormalityAnalysisFullPop <- function(expDataFrame) {
	distTable <- data.frame()

	gdata <- expDataFrame[expDataFrame$SPECIES=="HmI" & expDataFrame$QUALITY=="s"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="HtI" & expDataFrame$QUALITY=="s"  ,]

	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	distTable <- rbind(distTable,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	

	p("**** Normality test for Pg vs Ps (Shapiro-Wilks Normality Test)  ****")
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE))

	
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
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HmI"] <-  "HmI"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HtI"] <-  "HtI"

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


plotHistByPopulationQuality <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HmI"] <-  "HmI"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HtI"] <-  "HtI"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="POPULATION")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( POPULATION ~ QUALITY) +
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
			facet_grid( POPULATION ~ QUALITY) +
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


plotHistByInteractionQuality <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
	dataFrame <- dataFrame[dataFrame$MODEL==model,]	
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HmI"] <-  "HmI"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HtI"] <-  "HtI"

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName)) +
			geom_histogram(color="black",aes_string(  fill="INTERACTIONS")) +
			#geom_histogram() +
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
			geom_histogram(color="black",aes_string(fill="INTERACTIONS")) +
			#geom_histogram() +
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
	
	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HmI"] <-  "non-interacting homogenous"

	#levels(dataFrame$POPULATION)[levels(dataFrame$POPULATION)=="HtI"] <-  "interacting heterogenous"

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
	
	
	plotHistByInteractionQuality("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-iq-cm.pdf", TRUE)
	plotHistByInteractionQuality("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-iq-cb.pdf", TRUE)
	plotHistByInteractionQuality("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-iq-e2c.pdf")
	plotHistByInteractionQuality("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-iq-rm.pdf")
	plotHistByInteractionQuality("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-iq-rc.pdf")
	
	

	plotHistByPopulation("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pi-cm.pdf", TRUE)
	plotHistByPopulation("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pi-cb.pdf", TRUE)
	plotHistByPopulation("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pi-e2c.pdf")
	plotHistByPopulation("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pi-rm.pdf")
	plotHistByPopulation("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pi-rc.pdf")

	plotHistByPopulationQuality("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pq-cm.pdf", TRUE)
	plotHistByPopulationQuality("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pq-cb.pdf", TRUE)
	plotHistByPopulationQuality("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pq-e2c.pdf")
	plotHistByPopulationQuality("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pq-rm.pdf")
	plotHistByPopulationQuality("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pq-rc.pdf")

	
	plotHistBySubPopulation("alife", expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pop-cb-cm.pdf", TRUE)
	plotHistBySubPopulation("alife", expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pop-cb-cb.pdf", TRUE)
	plotHistBySubPopulation("alife", expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pop-cb-e2c.pdf")
	plotHistBySubPopulation("alife", expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pop-cb-rm.pdf")
	plotHistBySubPopulation("alife", expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-hist-pop-cb-rc.pdf")

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
			facet_grid( INTERACTIONS ~ QUALITY ) + # labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
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


plotBoxPlot_I <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3.5, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid( . ~ INTERACTIONS, labeller=label_parsed) +
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
			facet_grid(. ~ INTERACTIONS, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_y_continuous(labels=percentFormatter)+
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
		)
	}

	dev.off()
}



plotBoxPlot_P <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 2.25,height = 2, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			#facet_grid( . ~ POPULATION, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			theme_bw() +
			geom_line(size=0.1) +
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.title.y = element_blank(),
				axis.text.x = element_blank(),
				axis.title.x = element_blank()
				)
		)
	} else {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			#facet_grid(. ~ POPULATION, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_y_continuous(labels=percentFormatter)+
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
			geom_line(size=0.1) +
			theme_bw() + 			
			theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.y = element_text(size=rel(0.7)),
				axis.title.y = element_blank(),
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
			facet_grid(. ~ QUALITY) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			#scale_fill_manual(values=c("white","grey50")) +
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

#	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION,from=c("HmI","HtI"),to=c("hom","het"))


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

	g <- d.f[(d.f$SPECIES=="HmI"),]
	g <- g[[measure]]

	g.mean <- mean(g, na.rm=TRUE)
	g.var <- var(g, na.rm=TRUE)
	g.median <- median(g, na.rm=TRUE)
	g.t <- t.test(g)
	g.ci1 <- g.t$conf.int[[1]]
	g.ci2 <- g.t$conf.int[[2]]
	
	

	
	s <- d.f[(d.f$SPECIES=="HtI"),]
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

	g.f <- data.frame( POPULATION="HmI", MEAUSURE=measure, MEAN=g.mean,
		VAR = g.var, MEDIAN = g.median, CI1 = g.ci1, CI2 = g.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)
	s.f <- data.frame( POPULATION="HtI", MEAUSURE=measure, MEAN=s.mean,
		VAR = s.var, MEDIAN = s.median, CI1 = s.ci1, CI2 = s.ci2, 
		WILCOX_W=rWilcoxW, WILCOX_P = rWilcoxP)


	r.f <- rbind(g.f,s.f)

	print(r.f)

}



plotBoxPlots <- function(expDataFrame) {
	orig <- expDataFrame


	# compare interaction vs performance
	plotBoxPlot_I(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-i-cb.pdf", TRUE)
	plotBoxPlot_I(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-i-cm.pdf", TRUE)
	plotBoxPlot_I(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-i-e2c.pdf")
	plotBoxPlot_I(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-i-rm.pdf")
	plotBoxPlot_I(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-i-rc.pdf")

	# compare quality vs performance

	plotBoxPlot_Q(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-q-cb.pdf", TRUE)
	plotBoxPlot_Q(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-q-cm.pdf", TRUE)
	plotBoxPlot_Q(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-q-e2c.pdf")
	plotBoxPlot_Q(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-q-rm.pdf")
	plotBoxPlot_Q(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-q-rc.pdf")


	# compare quality vs interaction vs performance

	plotBoxPlot_I_Q(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-iq-cb.pdf", TRUE)
	plotBoxPlot_I_Q(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-iq-cm.pdf", TRUE)
	plotBoxPlot_I_Q(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-iq-e2c.pdf")
	plotBoxPlot_I_Q(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-iq-rm.pdf")
	plotBoxPlot_I_Q(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-iq-rc.pdf")

	# compare interaction vs performance
	plotBoxPlot_P(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-p-cb.pdf", TRUE)
	plotBoxPlot_P(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-p-cm.pdf", TRUE)
	plotBoxPlot_P(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-p-e2c.pdf")
	plotBoxPlot_P(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-p-rm.pdf")
	plotBoxPlot_P(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-p-rc.pdf")


}



plotBoxPlotsOld <- function(expDataFrame) {
	orig <- expDataFrame


	

	
	# do box plots by model and interaction
	plotBoxPlot_I_Q(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-mi-cb.pdf", TRUE)
	plotBoxPlot_I_Q(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-mi-cm.pdf", TRUE)
	plotBoxPlot_I_Q(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-mi-e2c.pdf")
	plotBoxPlot_I_Q(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-mi-rm.pdf")
	plotBoxPlot_I_Q(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-box-mi-rc.pdf")

	
	# filter out island model

	expDataFrame <-  expDataFrame[expDataFrame$MODEL!="island",]
	
	expDataFrame <- expDataFrame[(expDataFrame$SPECIES=="HmI" & expDataFrame$INTERACTIONS=="none") |
	   (expDataFrame$SPECIES=="HtI" & expDataFrame$INTERACTIONS!="none") ,]

	
	# do box plots by model and interaction of just E and A
	#plotBoxPlot_I_Q(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-mi-cb.pdf", TRUE)
	#plotBoxPlot_I_Q(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-mi-cm-ea.pdf", TRUE)
	#plotBoxPlot_I_Q(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-mi-e2c-ea.pdf")
	#plotBoxPlot_I_Q(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-mi-rm-ea.pdf")
	#plotBoxPlot_I_Q(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-mi-rc-ea.pdf")


	# do box plots by model 
	#plotBoxPlot_M(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-m-cb.pdf", TRUE)
	#plotBoxPlot_M(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-m-cm-ea.pdf", TRUE)
	#plotBoxPlot_M(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-m-e2c-ea.pdf")
	#plotBoxPlot_M(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-m-rm-ea.pdf")
	#plotBoxPlot_M(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-m-rc-ea.pdf")
	

	# do box plots of overall (including interactions)
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-ea-box-rc.pdf")
	
	# do only island
	expDataFrame <-  orig[orig$MODEL=="island",]
	expDataFrame <- expDataFrame[(expDataFrame$SPECIES=="HmI" & expDataFrame$INTERACTIONS=="none") |
	   (expDataFrame$SPECIES=="HtI" & expDataFrame$INTERACTIONS!="none") ,]

	# do box plots by model 
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-i-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-i-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-i-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-i-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-i-box-rc.pdf")
	
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
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-full-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-full-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-full-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-full-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-full-box-rc.pdf")
	
	


	# compare  just pdf vs pis in non-island
	expDataFrame <-  expDataFrame[expDataFrame$MODEL!="island",]
	
	expDataFrame <- expDataFrame[(expDataFrame$SPECIES=="HmI" & expDataFrame$INTERACTIONS=="none") |
	   (expDataFrame$SPECIES=="HtI" & expDataFrame$INTERACTIONS!="none") ,]


	# show a comparison of the means...
	print("comparing pdf and pis...(non-parametric)")
	compareMeans(expDataFrame,"CAPTURES_BEST_CASE")
	compareMeans(expDataFrame,"CAPTURES_MEAN")
	compareMeans(expDataFrame,"RES_E2C_STEPS_MEAN")
	compareMeans(expDataFrame,"RATE_MOTION")
	compareMeans(expDataFrame,"RATE_COMMUNICATION")
	print("DONE comparing pg and ps...(non-parametric)")


	
	# do box plots of overall (including interactions)
	plotBoxPlot(expDataFrame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-part-box-cb.pdf", TRUE)
	plotBoxPlot(expDataFrame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-part-box-cm.pdf", TRUE)
	plotBoxPlot(expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-part-box-e2c.pdf")
	plotBoxPlot(expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-part-box-rm.pdf")
	plotBoxPlot(expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-part-box-rc.pdf")




}



# Function turns the internal abbreviated values to full expansions and re-ordered
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a"), to=c("alife"))
	
	dataFrame$MODEL <- factor(dataFrame$MODEL,c("alife"))
	
	
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("b","u","t"), to=c("broadcast","unicast","trail"))
	
	dataFrame$INTERACTIONS <- factor(dataFrame$INTERACTIONS, c("trail","broadcast","unicast"))
	
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("HmI","HtI"))

	dataFrame$SPECIES <- factor(dataFrame$SPECIES, c("HmI","HtI"))
	
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("HmI","HtI"))
	
	dataFrame$POPULATION <- factor(dataFrame$POPULATION, c("HmI","HtI"))


	dataFrame$QUALITY <- mapvalues(dataFrame$QUALITY, from=c("s","h","m","l"), to=c("no-loss","25% loss","50% loss","75% loss"))
	
	dataFrame$QUALITY <- factor(dataFrame$QUALITY, c("75% loss","50% loss","25% loss","no-loss"))

	
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
	expDataFrame$QUALITY <- factor(expDataFrame$QUALITY)
	
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


plotBootHist2Pop <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.5,height = 2, family="CMU Serif")
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


plotBootHistPop_I <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="POPULATION")) 
			+ geom_density(alpha = 0.9)
			# + scale_fill_manual(values=c("white","grey50")) 
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
			# + scale_fill_manual(values=c("white","grey50")) 
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
			# + scale_fill_manual(values=c("white","grey50")) 
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
			# + scale_fill_manual(values=c("white","grey50")) 
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
			# + scale_fill_manual(values=c("white","grey50")) 
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
			# + scale_fill_manual(values=c("white","grey50")) 
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
		POPULATION="HtI"
	)


	g.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION,
		RATE_COMMUNICATION=g.RATE_COMMUNICATION,
		POPULATION="HmI"
	)
	
	popDataFrame <- rbind(g.popDataFrame,s.popDataFrame)

	return(popDataFrame)

}


computeBootStatsIQ <-function(s.orig.data.frame,g.orig.data.frame) {
bootSize <- 1000

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
				POPULATION="HtI",
				QUALITY=var.quality,
				INTERACTIONS=varInteraction
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="HmI",
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
				POPULATION="HtI",

				INTERACTIONS=varInteraction
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="HmI",

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
				POPULATION="HtI",
				QUALITY=var.quality
			)


			g.popDataFrame <- data.frame(
				CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
				CAPTURES_MEAN=g.CAPTURES_MEAN,
				RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
				RATE_MOTION=g.RATE_MOTION,
				RATE_COMMUNICATION=g.RATE_COMMUNICATION,
				POPULATION="HmI",
				QUALITY=var.quality

			)

			popDataFrame <- rbind(popDataFrame,g.popDataFrame,s.popDataFrame)
			#print(paste("dealt with",varInteraction,var.quality))

		
	}
	
	return(popDataFrame)

}



plotBootedStats <- function(expDataFrame) {


	popDataFrame <- computeBootStatsI(expDataFrame[expDataFrame$SPECIES=="HtI",], expDataFrame[expDataFrame$SPECIES=="HmI",])

	plotBootHistPop_I(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-i-cb.pdf", TRUE)


	plotBootHistPop_I(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-i-cm.pdf", TRUE)

	plotBootHistPop_I(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-i-e2c.pdf", FALSE)


	plotBootHistPop_I(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-i-rm.pdf", FALSE)


	plotBootHistPop_I(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-i-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsQ(expDataFrame[expDataFrame$SPECIES=="HtI",], expDataFrame[expDataFrame$SPECIES=="HmI",])



	plotBootHistPop_Q(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-q-cb.pdf", TRUE)

	plotBootHistPop_Q(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-q-cm.pdf", TRUE)

	plotBootHistPop_Q(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-q-e2c.pdf", FALSE)


	plotBootHistPop_Q(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-q-rm.pdf", FALSE)


	plotBootHistPop_Q(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-q-rc.pdf", FALSE)


	popDataFrame <- computeBootStatsIQ(expDataFrame[expDataFrame$SPECIES=="HtI",], expDataFrame[expDataFrame$SPECIES=="HmI",])


	plotBootHistPop_IQ(popDataFrame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-iq-cb.pdf", TRUE)

	plotBootHistPop_IQ(popDataFrame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-iq-cm.pdf", TRUE)

	plotBootHistPop_IQ(popDataFrame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-iq-e2c.pdf", FALSE)


	plotBootHistPop_IQ(popDataFrame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-iq-rm.pdf", FALSE)


	plotBootHistPop_IQ(popDataFrame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp2/e2-boot-pop-iq-rc.pdf", FALSE)



}


selectFromDf <- function(dataFrame,columnName, columnValue) {
	filterExpression <- paste0("dataFrame$",columnName," == '",columnValue, "'")	
	
	result <-data.frame()
	result <- rbind(result,dataFrame[eval(parse(text=filterExpression),dataFrame),])
	return(result)
}


analyzeNormailtyMeasureByGroups <-function(primaryGroup,secondaryGroup,measureName,dataFrame) {

	
	result <- data.frame()

	for(primary in unique(dataFrame[[primaryGroup]])) {
		for(secondary in unique(dataFrame[[secondaryGroup]])) {
			data = selectFromDf(dataFrame,primaryGroup,primary)
			data = selectFromDf(data,secondaryGroup,secondary)
			
			
			shap <- shapiro.test(data[[measureName]])
			W <- shap$statistic[[1]]
			pValue <- pValueString(shap$p.value)
			
			rowData <- data.frame(
				primaryGroup=primary,
				secondaryGroup=secondary,				
				MEASURE=measureName,				
				W=W,
				pValue=pValue
			)

			result <- rbind(result,rowData)
		}
	}
	
	
	return(result)



}


analyzeNormailtyMeasureByGroup <-function(primaryGroup,measureName,dataFrame) {

	
	result <- data.frame()

	for(primary in unique(dataFrame[[primaryGroup]])) {
		
			data = selectFromDf(dataFrame,primaryGroup,primary)
		
			d <- data[[measureName]]
			if(length(d)>5000) {
				d <- d[1:5000]
			}
			
			shap <- shapiro.test(d)
			W <- shap$statistic[[1]]
			pValue <- pValueString(shap$p.value)
			
			rowData <- data.frame(
				primaryGroup=primary,				
				MEASURE=measureName,				
				W=W,
				pValue=pValue
			)

			result <- rbind(result,rowData)
		
	}
	
	
	return(result)



}






doNormalityAnalysis <- function(expDataFrame) {

	distTable <- data.frame()
	p("POP-INTERACTION: NORMALITY TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","INTERACTIONS","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","INTERACTIONS","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","INTERACTIONS","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","INTERACTIONS","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","INTERACTIONS","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$primaryGroup,distTable$secondaryGroup,distTable$MEASURE),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,2,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION: NORMALITY TEST")


	distTable <- data.frame()
	p("POP-QUALITY: NORMALITY TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("SPECIES","QUALITY","RATE_COMMUNICATION",data))
	distTable <- distTable[order(distTable$primaryGroup,distTable$secondaryGroup,distTable$MEASURE),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,2,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-QUALITY: NORMALITY TEST")



	distTable <- data.frame()
	p("INTERACTIONS-QUALITY: NORMALITY TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroups("INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))
	distTable <- distTable[order(distTable$primaryGroup,distTable$secondaryGroup,distTable$MEASURE),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,2,0)), include.rownames=FALSE))
	p("<<<<<<<<< INTERACTIONS-QUALITY: NORMALITY TEST")



	distTable <- data.frame()
	p("POPULATION: NORMALITY TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroup("SPECIES","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroup("SPECIES","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroup("SPECIES","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroup("SPECIES","RATE_MOTION",data))
	distTable <- rbind(distTable,analyzeNormailtyMeasureByGroup("SPECIES","RATE_COMMUNICATION",data))
	distTable <- distTable[order(distTable$primaryGroup,distTable$MEASURE),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,0)), include.rownames=FALSE))
	p("<<<<<<<<< POPULATION: NORMALITY TEST")
	
	
}



kruskalWallisTest <-function(groupByColParam,primaryGroup,measureName,dataFrame) {
	#library(pgirmess)

	result <- data.frame()
	
	
	data <- dataFrame[dataFrame$SPECIES=="HmI",]
	measureData <- data[[measureName]]
	groupByCol <- data[[primaryGroup]]
	kResult <- kruskal.test(measureData ~ groupByCol)

	#print(summary(measureData))
	#print(summary(groupByCol))

	
	kValueG <- kResult$statistic[[1]]
	dFreedomG <- kResult$parameter[[1]]
	pValueG <- as.double(kResult$p.value)
	
	data <- dataFrame[dataFrame$SPECIES=="HtI",]
	measureData <- data[[measureName]]
	groupByCol <- data[[primaryGroup]]
	kResult <- kruskal.test(measureData ~ groupByCol)
	kValueS <- kResult$statistic[[1]]
	dFreedomS <- kResult$parameter[[1]]
	pValueS <- as.double(kResult$p.value)
	

	rowData <- data.frame(
		MEASURE=measureName,
		H_VALUE_G=kValueG, 
		D_FREEDOM_G=dFreedomG, 
		P_VALUE_G=pValueString(pValueG),
		H_VALUE_S=kValueS, 
		D_FREEDOM_S=dFreedomS, 
		P_VALUE_S=pValueString(pValueS)
	)
	

	return(rowData)
}


nonParametricCompare <-function(groupByColParam,primaryGroup,measureName,dataFrame) {
	#library(pgirmess)

	result <- data.frame()
	
	for(primary in unique(dataFrame[[primaryGroup]])) {

		data = selectFromDf(dataFrame,primaryGroup,primary)

		vecA <- data[data$SPECIES=="HmI",]
		vecA <- vecA[[measureName]]
		medianA = median(vecA, na.rm=TRUE)



		vecB <- data[data$SPECIES=="HtI",]
		vecB <- vecB[[measureName]]
		medianB = median(vecB, na.rm=TRUE)


		
		#print(head(vecA))
		#print(head(vecB))
		#stop()

		wilcox <- wilcox.test(vecA,vecB, conf.int=TRUE)
		
		W <- wilcox$statistic[[1]]
		pValue <- wilcox$p.value
		
		z<- qnorm(pValue/2)
		r<- z/ sqrt(length(vecA))
		
		rowData <- data.frame(	
			PRIMARY=primary,
			MEASURE=measureName,						
			MED_A=medianA,
			MED_B=medianB,
			W=W,
			P=pValueString(pValue)
			#EFFECT=r
		)
		result <- rbind(result,rowData)

	}
	
	
	return(result)
}


kruskalWallisTest1 <-function(groupByColParam,measureName,data) {
	return(nonParametricCompare1(groupByColParam,measureName,data))
}


nonParametricCompare1 <-function(groupByColParam,measureName,data) {
	#library(pgirmess)

	result <- data.frame()
	
	

	vecA <- data[data$SPECIES=="HmI",]
	vecA <- vecA[[measureName]]
	medianA = median(vecA, na.rm=TRUE)


	vecB <- data[data$SPECIES=="HtI",]
	vecB <- vecB[[measureName]]
	medianB = median(vecB, na.rm=TRUE)
	print(vecB)
	print(medianB)


	#print(head(vecA))
	#print(head(vecB))
	#stop()

	wilcox <- wilcox.test(vecA,vecB, conf.int=TRUE)
	
	W <- wilcox$statistic[[1]]
	pValue <- wilcox$p.value
	
	z<- qnorm(pValue/2)
	r<- z/ sqrt(length(vecA))
	
	rowData <- data.frame(	
		MEASURE=measureName,
		MEDIAN_A=medianA,
		MEDIAN_B=medianB,
		W=W,
		P=pValueString(pValue)
	)
	result <- rbind(result,rowData)

	
	
	
	return(result)
}


kruskalWallisTest2 <-function(groupByColParam,primaryGroup,secondaryGroup,measureName,mainData) {
	#library(pgirmess)

	result <- data.frame()
	
	

		for(primary in unique(mainData[[primaryGroup]])) {
			dataFrame = selectFromDf(mainData,primaryGroup,primary)
		
		
			
			data <- dataFrame[dataFrame$SPECIES=="HmI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]
			kResult <- kruskal.test(measureData ~ groupByCol)
			kValueG <- kResult$statistic[[1]]
			dFreedomG <- kResult$parameter[[1]]
			pValueG <- as.double(kResult$p.value)
			
			data <- dataFrame[dataFrame$SPECIES=="HtI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]
			kResult <- kruskal.test(measureData ~ groupByCol)
			kValueS <- kResult$statistic[[1]]
			dFreedomS <- kResult$parameter[[1]]
			pValueS <- as.double(kResult$p.value)
			

			rowData <- data.frame(				
				PRIMARY=primary,
				MEASURE=measureName,
				
				H_VALUE_G=kValueG, 
				D_FREEDOM_G=dFreedomG, 
				P_VALUE_G=pValueString(pValueG),
				H_VALUE_S=kValueS, 
				D_FREEDOM_S=dFreedomS, 
				P_VALUE_S=pValueString(pValueS)
			)


			result <- rbind(result,rowData)
		}
	



	return(result)
}


nonParametricCompare2 <-function(groupByColParam,primaryGroup,secondaryGroup,measureName,dataFrame) {
	#library(pgirmess)

	result <- data.frame()
	
	for(primary in unique(dataFrame[[primaryGroup]])) {

		for(secondary in unique(dataFrame[[secondaryGroup]])) {


			data = selectFromDf(dataFrame,primaryGroup,primary)
			data = selectFromDf(data,secondaryGroup,secondary)

			vecA <- data[data$SPECIES=="HmI",]
			vecA <- vecA[[measureName]]
			medianA = median(vecA, na.rm=TRUE)


			vecB <- data[data$SPECIES=="HtI",]
			vecB <- vecB[[measureName]]
			medianB = median(vecB, na.rm=TRUE)

			
			wilcox <- wilcox.test(vecA,vecB, conf.int=TRUE)
			
			W <- wilcox$statistic[[1]]
			pValue <- wilcox$p.value
			
			z<- qnorm(pValue/2)
			r<- z/ sqrt(length(vecA))
			
			rowData <- data.frame(	
				PRIMARY=primary,
				SECONDARY=secondary,
				MEASURE=measureName,
				MED_A=medianA,
				MED_B=medianB,											
				W=W,
				P=pValueString(pValue)
			)
			result <- rbind(result,rowData)
		}
	}



	return(result)
}


doNonParametricComparisons <- function(expDataFrame) {

	distTable <- data.frame()
	p("QUALITY-INTERACTION: NON-PARAMETRIC COMPARE >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,nonParametricCompare2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,nonParametricCompare2("SPECIES","INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare2("SPECIES","INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,nonParametricCompare2("SPECIES","INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-2,-2,0,2)), include.rownames=FALSE))
	p("<<<<<<<<< QUALITY-INTERACTION: NON-PARAMETRIC COMPARE")

	stop()



	distTable <- data.frame()
	p("POP-INTERACTION: NON-PARAMETRIC COMPARE >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","INTERACTIONS","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","INTERACTIONS","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","INTERACTIONS","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","INTERACTIONS","RATE_MOTION",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","INTERACTIONS","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,-2,-2,0,2)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION: NON-PARAMETRIC COMPARE")
	


	distTable <- data.frame()
	p("POP-QUALITY: NON-PARAMETRIC COMPARE >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,nonParametricCompare("SPECIES","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,-2,-2,0,2)), include.rownames=FALSE))
	p("<<<<<<<<< POP-QUALITY: NON-PARAMETRIC COMPARE")
	

	



	distTable <- data.frame()
	p("POPULATION: NON-PARAMETRIC COMPARE >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RATE_MOTION",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RATE_COMMUNICATION",data))

	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,-2,-2,0,2)), include.rownames=FALSE))
	p("<<<<<<<<< POPULATION: NON-PARAMETRIC COMPARE")
	
}


doKruskalWallis <- function(expDataFrame) {

	distTable <- data.frame()
	p("POPULATION: KRUSKAL-WALLIS >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RATE_MOTION",data))
	distTable <- rbind(distTable,nonParametricCompare1("SPECIES","RATE_COMMUNICATION",data))

	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,-2,-2,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POPULATION: KRUSKAL-WALLIS")
	stop()


	distTable <- data.frame()
	p("POP-INTERACTION: KRUSKAL-WALLIS >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","INTERACTIONS","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","INTERACTIONS","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","INTERACTIONS","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","INTERACTIONS","RATE_MOTION",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","INTERACTIONS","RATE_COMMUNICATION",data))

	print(distTable)
	
	displayLatex(print(xtable(distTable, digits=c(0,0,2,0,0,2,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION: KRUSKAL-WALLIS")

	stop()


	distTable <- data.frame()
	p("POP-QUALITY: KRUSKAL-WALLIS >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,kruskalWallisTest("SPECIES","QUALITY","RATE_COMMUNICATION",data))

	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,2,0,0,2,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-QUALITY: KRUSKAL-WALLIS")
	







	


	distTable <- data.frame()
	p("POP-INTERACTION-QUALITY: KRUSKAL-WALLIS >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,kruskalWallisTest2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,kruskalWallisTest2("SPECIES","INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,kruskalWallisTest2("SPECIES","INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,kruskalWallisTest2("SPECIES","INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	#stop()
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,0,0,2,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION-QUALITY: KRUSKAL-WALLIS")



	
	
}


as.numeric.factor <- function(x) {seq_along(levels(x))[x]}

jonkheereTest <-function(groupByColParam,primaryGroup,measureName,dataFrame) {
	library(clinfun)
	options(warn=-1)

	result <- data.frame()
	
	
	data <- dataFrame[dataFrame$SPECIES=="HmI",]
	measureData <- data[[measureName]]
	groupByCol <- data[[primaryGroup]]
	groupByCol = as.numeric.factor(groupByCol)
	jResult <- jonckheere.test(measureData,groupByCol,alternative="increasing")
	print(summary(jResult))
	print(jResult)
	stop()

	
	jValueG <-jResult$statistic[[1]]
	jAltG <- jResult$alternative[[1]]
	pValueG <- as.double(jResult$p.value)
	

	data <- dataFrame[dataFrame$SPECIES=="HtI",]
	measureData <- data[[measureName]]
	groupByCol <- data[[primaryGroup]]
	groupByCol = as.numeric.factor(groupByCol)
	jResult <- jonckheere.test(measureData,groupByCol,alternative="increasing")
	
	jValueS <-jResult$statistic[[1]]
	jAltS <- jResult$alternative[[1]]
	pValueS <- as.double(jResult$p.value)

	

	rowData <- data.frame(
		MEASURE=measureName,
		JT_G=jValueG, 
		#J_ALT_G=jAltG, 
		P_VALUE_G=pValueString(pValueG),
		JT_S=jValueS, 
		#J_ALT_S=jAltS, 
		P_VALUE_S=pValueString(pValueS)
	)
	
	options(warn=1)
	return(rowData)
}




jonkheereTest2 <-function(groupByColParam,primaryGroup,secondaryGroup,measureName,mainData) {
	library(clinfun)
	options(warn=-1)


	result <- data.frame()
	
		for(primary in unique(mainData[[primaryGroup]])) {
			dataFrame = selectFromDf(mainData,primaryGroup,primary)
		
		
			
			data <- dataFrame[dataFrame$SPECIES=="HmI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]
			groupByCol = as.numeric.factor(groupByCol)
			

			jResult <- jonckheere.test(measureData,groupByCol,alternative="increasing")
			
			jValueG <-jResult$statistic[[1]]
			jAltG <- jResult$alternative[[1]]
			pValueG <- as.double(jResult$p.value)
			

			data <- dataFrame[dataFrame$SPECIES=="HtI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]
			groupByCol = as.numeric.factor(groupByCol)
			jResult <- jonckheere.test(measureData,groupByCol,alternative="increasing")
			
			jValueS <-jResult$statistic[[1]]
			jAltS <- jResult$alternative[[1]]
			pValueS <- as.double(jResult$p.value)

			rowData <- data.frame(
				PRIMARY=primary,
				MEASURE=measureName,
				JT_G=jValueG, 
				#J_ALT_G=jAltG, 
				P_VALUE_G=pValueString(pValueG),
				JT_S=jValueS, 
				#J_ALT_S=jAltS, 
				P_VALUE_S=pValueString(pValueS)
			)
			#print(rowData)
			#stop()

			result <- rbind(result,rowData)
		}
	


	options(warn=1)
	return(result)
}


doTrendTest <- function(expDataFrame) {

	distTable <- data.frame()
	p("POP-QUALITY:TREND TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,jonkheereTest("SPECIES","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,jonkheereTest("SPECIES","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,jonkheereTest("SPECIES","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,jonkheereTest("SPECIES","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,jonkheereTest("SPECIES","QUALITY","RATE_COMMUNICATION",data))

	print(distTable)
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-QUALITY:TREND TEST")
	

	


	distTable <- data.frame()
	p("POP-INTERACTION-QUALITY:TREND TEST >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,jonkheereTest2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,jonkheereTest2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,jonkheereTest2("SPECIES","INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,jonkheereTest2("SPECIES","INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,jonkheereTest2("SPECIES","INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	#stop()
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,0,0,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION-QUALITY:TREND TEST")



	
}





anova2 <-function(groupByColParam,primaryGroup,secondaryGroup,measureName,mainData) {
	#library(pgirmess)

	result <- data.frame()
	
		for(primary in unique(mainData[[primaryGroup]])) {
			print(primary)

			dataFrame = selectFromDf(mainData,primaryGroup,primary)
		
		
			
			data <- dataFrame[dataFrame$SPECIES=="HmI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]

			
			anova <- oneway.test(measureData ~ groupByCol)
			
			fValueG <- anova$statistic[[1]]
			numDFreedomG <- anova$parameter[[1]]
			denDFreedomG <- anova$parameter[[2]]
			pValueG <- 	anova$p.value
			



			data <- dataFrame[dataFrame$SPECIES=="HtI",]
			measureData <- data[[measureName]]
			groupByCol <- data[[secondaryGroup]]
		

			anova <- oneway.test(measureData ~ groupByCol)
			
			fValueS <- anova$statistic[[1]]
			numDFreedomS <- anova$parameter[[1]]
			denDFreedomS <- anova$parameter[[2]]
			pValueS <- 	anova$p.value
			


			rowData <- data.frame(				
				PRIMARY=primary,
				MEASURE=measureName,
				
				F_VALUE_G=fValueG, 
				N_D_FREEDOM_G=numDFreedomG, 
				D_D_FREEDOM_G=denDFreedomG, 
				P_VALUE_G=pValueString(pValueG),

				F_VALUE_S=fValueS, 
				N_D_FREEDOM_S=numDFreedomS, 
				D_D_FREEDOM_S=denDFreedomS, 
				P_VALUE_S=pValueString(pValueS)
			)


			result <- rbind(result,rowData)
		}
	



	return(result)
}



meansErrs2 <-function(groupByColParam,primaryGroup,secondaryGroup,measureName,mainData) {
	#library(pgirmess)

	result <- data.frame()
	
	for(primary in unique(mainData[[primaryGroup]])) {

		for(secondary in unique(mainData[[secondaryGroup]])) {
			dataFrame = selectFromDf(mainData,primaryGroup,primary)
			dataFrame = selectFromDf(dataFrame,secondaryGroup,secondary)

			data <- dataFrame[dataFrame$SPECIES=="HmI",]
			g.v <- data[[measureName]]
			g.v <- g.v[!is.na(g.v)]
			gMean <- mean(g.v)
			gStdErr <- var(g.v)/length(g.v)

			data <- dataFrame[dataFrame$SPECIES=="HtI",]
			s.v <- data[[measureName]]
			s.v <- s.v[!is.na(s.v)]
			sMean <- mean(s.v)
			sStdErr <- var(s.v)/length(s.v)


			rowData <- data.frame(				
				PRIMARY=primary,
				SECONDARY=secondary,
				MEASURE=measureName,
				
				GM = gMean,
				GErr = gStdErr, 
				SM = sMean,
				SErr = sStdErr
			)
			result <- rbind(result,rowData)
		}
		
	}
	



	return(result)
}




doANOVA <- function(expDataFrame) {

	data <- computeBootStatsIQ(expDataFrame[expDataFrame$SPECIES=="HtI",], expDataFrame[expDataFrame$SPECIES=="HmI",])
	
	distTable <- data.frame()
	p("POP-INTERACTION-QUALITY: ANOVA >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,meansErrs2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,meansErrs2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,meansErrs2("SPECIES","INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,meansErrs2("SPECIES","INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,meansErrs2("SPECIES","INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	#stop()
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,0,-2,-2,-2,-2)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION-QUALITY: ANOVA")
	
	stop()



	data <- computeBootStatsIQ(expDataFrame[expDataFrame$SPECIES=="HtI",], expDataFrame[expDataFrame$SPECIES=="HmI",])
	
	distTable <- data.frame()
	p("POP-INTERACTION-QUALITY: ANOVA >>>>>>")
	data <- expDataFrame
	distTable <- rbind(distTable,anova2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_MEAN",data))
	distTable <- rbind(distTable,anova2("SPECIES","INTERACTIONS","QUALITY","CAPTURES_BEST_CASE",data))
	distTable <- rbind(distTable,anova2("SPECIES","INTERACTIONS","QUALITY","RES_E2C_STEPS_MEAN",data))
	distTable <- rbind(distTable,anova2("SPECIES","INTERACTIONS","QUALITY","RATE_MOTION",data))
	distTable <- rbind(distTable,anova2("SPECIES","INTERACTIONS","QUALITY","RATE_COMMUNICATION",data))

	distTable <- distTable[order(distTable$PRIMARY),]
	print(distTable)
	#stop()
	
	displayLatex(print(xtable(distTable, digits=c(0,0,0,2,2,2,0,2,2,2,0)), include.rownames=FALSE))
	p("<<<<<<<<< POP-INTERACTION-QUALITY: ANOVA")



}








##############################   MAIN PROCESS BEGINS ###############################


expDataFrame <- read.csv(file="~/synthscape/scripts/analysis/data/exp2/exp2_experiments_mean_300.csv")

expDataFrame <- preProcessData(expDataFrame)     # factorizes, as appropriate, adjusts E2C...
expDataFrame <- renameFactorValues(expDataFrame) # renames for nice plots

#print(summary(expDataFrame))

##### not using these....plotGraphs(expDataFrame)


# Using these...
#plotHists(expDataFrame)    # plots histograms
#doNormalityAnalysis(expDataFrame)

#plotBoxPlots(expDataFrame) # boxplots to show difference

#doKruskalWallis(expDataFrame)
#doTrendTest(expDataFrame)
#doNonParametricComparisons(expDataFrame)
#plotBootedStats(expDataFrame)
doANOVA(expDataFrame)







#plot the totals

