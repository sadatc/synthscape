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
# given sample.data, computes mean based on r replicates 
#
boot.mean = function(sample.data, r) {
	sample.data = na.omit(sample.data)
	n = length(sample.data)
	boot.samples = matrix(sample(sample.data,size=n*r, replace=TRUE),r,n)
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
	
			
	
	result.frame <- data.frame(MODEL=model,MEASURE=measure,
		G_SHAP_W=rGShapiroW, G_SHAP_P=pValueString(rGShapiroP), S_SHAP_W=rSShapiroW, S_SHAP_P=pValueString(rSShapiroP))
	
	
	
	
	
	return(result.frame)
}



doNormalityAnalysisSubPop <- function(exp1.df) {
	dist.table <- data.frame()

#	print("========== ISLAND ========")
	
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="island" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="island" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table, analyzeNormailtyPair("island","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))

	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))

	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	


#	print("========== EMBODIED ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="embodied" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="embodied" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))

#	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="alife" & exp1.df$INTERACTIONS=="none"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="alife" & exp1.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	data(dist.table)
	print(xtable(dist.table, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
}



doNormalityAnalysisFullPop <- function(exp1.df) {
	dist.table <- data.frame()

#	print("========== ISLAND ========")
	
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="island"   ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="island"  ,]

	dist.table <- rbind(dist.table, analyzeNormailtyPair("island","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))

	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))

	dist.table <- rbind(dist.table,analyzeNormailtyPair("island","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	


#	print("========== EMBODIED ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="embodied"  ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="embodied" ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("embodied","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))

#	print("========== ALIFE ========")
	gdata <- exp1.df[exp1.df$SPECIES=="homogenous"  & exp1.df$MODEL=="alife"  ,]

	sdata <- exp1.df[exp1.df$SPECIES=="heterogenous" &  exp1.df$MODEL=="alife"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("alife","RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	

	p("**** Normality test for Pg vs Ps (Shapiro-Wilks Normality Test)  ****")
	data(dist.table)
	print(xtable(dist.table, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
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





plotHistByModel <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
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

plotHistByModelSpecific <-function(model, dataFrame, colName, fileName, showPercent=FALSE) {
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





plotHists <- function(exp1.df) {
	# == plot general distributions ===
	
	
	plotHistByModel("island", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-island-cm.pdf", TRUE)
	plotHistByModel("island", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-island-cb.pdf", TRUE)
	plotHistByModel("island", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-island-e2c.pdf")
	plotHistByModel("island", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-island-rm.pdf")
	plotHistByModel("island", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-island-rc.pdf")
	
	
	plotHistByModel("embodied", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-embodied-cm.pdf", TRUE)
	plotHistByModel("embodied", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-embodied-cb.pdf", TRUE)
	plotHistByModel("embodied", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-embodied-e2c.pdf")
	plotHistByModel("embodied", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-embodied-rm.pdf")
	plotHistByModel("embodied", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-embodied-rc.pdf")
	
	
	plotHistByModel("alife", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-alife-cm.pdf", TRUE)
	plotHistByModel("alife", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-alife-cb.pdf", TRUE)
	plotHistByModel("alife", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-alife-e2c.pdf")
	plotHistByModel("alife", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-alife-rm.pdf")
	plotHistByModel("alife", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-alife-rc.pdf")



	exp1.df <-  exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |  (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none")  ,]


	plotHistByModelSpecific("island", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-island-cm.pdf", TRUE)
	plotHistByModelSpecific("island", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-island-cb.pdf", TRUE)
	plotHistByModelSpecific("island", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-island-e2c.pdf")
	plotHistByModelSpecific("island", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-island-rm.pdf")
	plotHistByModelSpecific("island", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-island-rc.pdf")
	
	
	plotHistByModelSpecific("embodied", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-embodied-cm.pdf", TRUE)
	plotHistByModelSpecific("embodied", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-embodied-cb.pdf", TRUE)
	plotHistByModelSpecific("embodied", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-embodied-e2c.pdf")
	plotHistByModelSpecific("embodied", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-embodied-rm.pdf")
	plotHistByModelSpecific("embodied", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-embodied-rc.pdf")
	
	
	plotHistByModelSpecific("alife", exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-alife-cm.pdf", TRUE)
	plotHistByModelSpecific("alife", exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-alife-cb.pdf", TRUE)
	plotHistByModelSpecific("alife", exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-alife-e2c.pdf")
	plotHistByModelSpecific("alife", exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-alife-rm.pdf")
	plotHistByModelSpecific("alife", exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-hist-s-alife-rc.pdf")






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
			facet_grid( MODEL ~ INTERACTIONS, labeller=label_parsed) +
			ylab(yAxisLabel) +
			scale_fill_discrete("Population") +
			scale_fill_manual(values=c("white","grey50")) +
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

plotBoxPlot_M <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string(x="POPULATION", y=colName)) +
			geom_boxplot(aes(fill=POPULATION), notch=globalNotchValue) +
			facet_grid(  ~ MODEL , labeller=label_parsed) +
			scale_fill_manual(values=c("white","grey50")) +
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
			scale_fill_manual(values=c("white","grey50")) +
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



plotBoxPlots <- function(exp1.df) {
	orig <- exp1.df

	# only look at non-communicating homogenous and communicating heterogenous	
	exp1.df <-  exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |  (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none")  ,]
	

	
	# do box plots by model and interaction
	plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-cb.pdf", TRUE)
	plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-cm.pdf", TRUE)
	plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-e2c.pdf")
	plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-rm.pdf")
	plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-box-mi-rc.pdf")

	
	# filter out island model

	exp1.df <-  exp1.df[exp1.df$MODEL!="island",]
	
	exp1.df <- exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |
	   (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none") ,]

	
	# do box plots by model and interaction of just E and A
	#plotBoxPlot_M_I(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-mi-cb.pdf", TRUE)
	#plotBoxPlot_M_I(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-mi-cm-ea.pdf", TRUE)
	#plotBoxPlot_M_I(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-mi-e2c-ea.pdf")
	#plotBoxPlot_M_I(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-mi-rm-ea.pdf")
	#plotBoxPlot_M_I(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-mi-rc-ea.pdf")


	# do box plots by model 
	#plotBoxPlot_M(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-m-cb.pdf", TRUE)
	#plotBoxPlot_M(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-m-cm-ea.pdf", TRUE)
	#plotBoxPlot_M(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-m-e2c-ea.pdf")
	#plotBoxPlot_M(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-m-rm-ea.pdf")
	#plotBoxPlot_M(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-m-rc-ea.pdf")
	

	# do box plots of overall (including interactions)
	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-cb.pdf", TRUE)
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-cm.pdf", TRUE)
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-e2c.pdf")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-rm.pdf")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-ea-box-rc.pdf")
	
	# do only island
	exp1.df <-  orig[orig$MODEL=="island",]
	exp1.df <- exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |
	   (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none") ,]

	# do box plots by model 
	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-box-cb.pdf", TRUE)
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-box-cm.pdf", TRUE)
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-box-e2c.pdf")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-box-rm.pdf")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-i-box-rc.pdf")
	
	# consider all models... just pdf vs pis
	exp1.df <-  orig

	# show a comparison of the means...
	print("comparing pg and ps...(non-parametric)")
	compareMeans(exp1.df,"CAPTURES_BEST_CASE")
	compareMeans(exp1.df,"CAPTURES_MEAN")
	compareMeans(exp1.df,"RES_E2C_STEPS_MEAN")
	compareMeans(exp1.df,"RATE_MOTION")
	compareMeans(exp1.df,"RATE_COMMUNICATION")
	print("DONE comparing pg and ps...(non-parametric)")
	
	# do box plots of overall (including interactions)
	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-cb.pdf", TRUE)
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-cm.pdf", TRUE)
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-e2c.pdf")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-rm.pdf")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-full-box-rc.pdf")
	
	


	# compare  just pdf vs pis in non-island
	exp1.df <-  exp1.df[exp1.df$MODEL!="island",]
	
	exp1.df <- exp1.df[(exp1.df$SPECIES=="homogenous" & exp1.df$INTERACTIONS=="none") |
	   (exp1.df$SPECIES=="heterogenous" & exp1.df$INTERACTIONS!="none") ,]


	# show a comparison of the means...
	print("comparing pdf and pis...(non-parametric)")
	compareMeans(exp1.df,"CAPTURES_BEST_CASE")
	compareMeans(exp1.df,"CAPTURES_MEAN")
	compareMeans(exp1.df,"RES_E2C_STEPS_MEAN")
	compareMeans(exp1.df,"RATE_MOTION")
	compareMeans(exp1.df,"RATE_COMMUNICATION")
	print("DONE comparing pg and ps...(non-parametric)")


	
	# do box plots of overall (including interactions)
	plotBoxPlot(exp1.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-part-box-cb.pdf", TRUE)
	plotBoxPlot(exp1.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-part-box-cm.pdf", TRUE)
	plotBoxPlot(exp1.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-part-box-e2c.pdf")
	plotBoxPlot(exp1.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-part-box-rm.pdf")
	plotBoxPlot(exp1.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-part-box-rc.pdf")




}


# Function turns the internal abbreviated values to full expansions and re-ordered
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a","e","i"), to=c("alife","embodied","island"))
	
	dataFrame$MODEL <- factor(dataFrame$MODEL,c("island","embodied","alife"))
	
	
	dataFrame$INTERACTIONS <- mapvalues(dataFrame$INTERACTIONS, from=c("n","b","u","t"), to=c("none","broadcast","unicast","trail"))
	
	dataFrame$INTERACTIONS <- factor(dataFrame$INTERACTIONS, c("none","trail","broadcast","unicast"))
	
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("homogenous","heterogenous"))

	dataFrame$SPECIES <- factor(dataFrame$SPECIES, c("homogenous","heterogenous"))
	
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("homogenous","heterogenous"))
	
	dataFrame$POPULATION <- factor(dataFrame$POPULATION, c("homogenous","heterogenous"))
	
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
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	print(
		ggplot(data.frame(x=bootSample), aes(x=x)) 
		+ geom_histogram(aes(y=..density..), binwidth=binWidth) 
		+ geom_density(color="red")
		
	)
	dev.off()
}


plotBootHist2Pop <-function(pop.data.frame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 2.5,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION")) 
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
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION"))
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




plotBootedStatsFull <- function(exp1.df) {

	# now we will bootstrap the measures
	bootSize <- 1000

	ea.data.frame <-  exp1.df

	sg.data.frame <-  ea.data.frame

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
		RATE_COMMUNICATION=s.RATE_COMMUNICATION,
		POPULATION="heterogenous"
	)


	g.pop.data.frame <- data.frame(
		CAPTURES_BEST_CASE=g.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=g.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=g.RES_E2C_STEPS_MEAN,
		RATE_MOTION=g.RATE_MOTION,
		RATE_COMMUNICATION=g.RATE_COMMUNICATION,
		POPULATION="homogenous"
	)
	
	pop.data.frame <- rbind(g.pop.data.frame,s.pop.data.frame)
	
	
	plotBootHist2Pop(pop.data.frame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-cb.pdf", TRUE)
	plotBootHist2Pop(pop.data.frame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-cm.pdf", TRUE)
	plotBootHist2Pop(pop.data.frame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-e2c.pdf")
	plotBootHist2Pop(pop.data.frame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-rm.pdf")
	

plotBootHist2Pop(pop.data.frame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-rc.pdf")


	
	
	plotBoxPlot(pop.data.frame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-cb.pdf", TRUE, FALSE)

	plotBoxPlot(pop.data.frame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-cm .pdf", TRUE, FALSE)

	plotBoxPlot(pop.data.frame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-e2c.pdf", FALSE, FALSE)

	plotBoxPlot(pop.data.frame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-full-box-rm.pdf", FALSE, FALSE)

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



plotBootedStatsPartial <- function(exp1.df) {

	# now we will bootstrap the measures
	bootSize <- 1000

	ea.data.frame <-  exp1.df[exp1.df$MODEL!="island",]

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
	
	pop.data.frame <- rbind(g.pop.data.frame,s.pop.data.frame)
	
	
	plotBootHist2Pop(pop.data.frame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-cb.pdf", TRUE)
	plotBootHist2Pop(pop.data.frame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-cm.pdf", TRUE)
	plotBootHist2Pop(pop.data.frame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-e2c.pdf")
	plotBootHist2Pop(pop.data.frame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-rm.pdf")
	
	
	
	plotBoxPlot(pop.data.frame,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-cb.pdf", TRUE, FALSE)

	plotBoxPlot(pop.data.frame,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-cm .pdf", TRUE, FALSE)

	plotBoxPlot(pop.data.frame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-e2c.pdf", FALSE, FALSE)

	plotBoxPlot(pop.data.frame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp1/e1-boot-partial-box-rm.pdf", FALSE, FALSE)

	t.table <- data.frame()


	t.table <- rbind(t.table, performTTest("CAPTURES_BEST_CASE",s.CAPTURES_BEST_CASE,g.CAPTURES_BEST_CASE))
	
	t.table <- rbind(t.table, performTTest("CAPTURES_MEAN",s.CAPTURES_MEAN,g.CAPTURES_MEAN))

	t.table <- rbind(t.table, performTTest("RES_E2C_STEPS_MEAN",s.RES_E2C_STEPS_MEAN,g.RES_E2C_STEPS_MEAN))

	t.table <- rbind(t.table, performTTest("RATE_MOTION",s.RATE_MOTION,g.RATE_MOTION))

	data(t.table)
	print("data table for pdf vs pis")
	print(xtable(t.table, digits=c(0,0,2,2,-2), include.rownames=FALSE))
	#print(xtable(t.table, include.rownames=FALSE))




}



##############################   MAIN PROCESS BEGINS ###############################


exp1.df <- read.csv(file="~/synthscape/scripts/analysis/data/exp1/exp1_experiments_mean_300.csv")

exp1.df <- preProcessData(exp1.df)     # factorizes, as appropriate, adjusts E2C...
exp1.df <- renameFactorValues(exp1.df) # renames for nice plots

##### not using these....plotGraphs(exp1.df)

# Using these...
plotHists(exp1.df)    # plots histograms

doNormalityAnalysisFullPop(exp1.df)
doNormalityAnalysisSubPop(exp1.df)
plotBoxPlots(exp1.df) # boxplots to show difference
plotBootedStatsFull(exp1.df)
plotBootedStatsPartial(exp1.df)







#plot the totals

