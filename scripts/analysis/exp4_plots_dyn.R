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


#print("mann-whitney u test. if p<0.05, the samples are non-identical populationf...")
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
	dist.table <- data.frame()



#	print("========== ALIFE ========")
	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous"  & expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS=="none"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" &  expDataFrame$MODEL=="alife" & expDataFrame$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	data(dist.table)
	print(xtable(dist.table, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
}



doNormalityAnalysisFullPop <- function(expDataFrame) {
	dist.table <- data.frame()

	gdata <- expDataFrame[expDataFrame$SPECIES=="homogenous" & expDataFrame$QUALITY=="s"   ,]

	sdata <- expDataFrame[expDataFrame$SPECIES=="heterogenous" & expDataFrame$QUALITY=="s"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	

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
	
	if(colName == "NUM_DETECTORS") {
		result <- "Detectors"
	}

	if(colName == "NUM_TRANSPORTERS") {
		result <- "Transporters"
	}

	if(colName == "NUM_EXTRACTORS") {
		result <- "Extractors"
	}

	if(colName == "E") {
		result <- "Evenness"
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
	
	if(colName == "NUM_DETECTORS") {
		result <- "Detectors"
	}

	if(colName == "NUM_EXTRACTORS") {
		result <- "Extractors"
	}

	if(colName == "NUM_TRANSPORTERS") {
		result <- "Transporters"
	}


	if(colName == "E") {
		result <- "Evenness"
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


plotHistByIntResMix <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	xAxisLabel <- getMeasurePrettyName(colName)
	
	
	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="EXTRACTED_RESOURCES")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( INTERACTIONS ~ EXTRACTED_RESOURCES) +
			xlab(xAxisLabel) +
			scale_fill_manual(values=c("white","grey50")) +
			theme_bw() + theme(#text=element_text(family="CMUSerif-Roman"),
			legend.position="none", 
				axis.text.x = element_text(size=rel(0.7)))
		)
	} else {
		print(
			ggplot(dataFrame,aes_string(x=colName, fill="EXTRACTED_RESOURCES")) +
			geom_histogram(color="black", alpha = 0.85) +
			facet_grid( INTERACTIONS ~ EXTRACTED_RESOURCES) +
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
	plotHistByIntResMix( expDataFrame,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-e4c.pdf")
	plotHistByIntResMix( expDataFrame,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-rm.pdf")
	plotHistByIntResMix( expDataFrame,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-rc.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-dets.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-exts.pdf")
	plotHistByIntResMix( expDataFrame,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-trns.pdf")
	plotHistByIntResMix( expDataFrame,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4/e4-hist-intresmix-e.pdf")



}


plotBoxPlot_Even <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 4,height = 4, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="EXTRACTED_RESOURCES",y=colName)) +
			geom_boxplot(aes(fill=EXTRACTED_RESOURCES), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~ .) +
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
			ggplot(dataFrame, aes_string(x="EXTRACTED_RESOURCES", y=colName)) +
			geom_boxplot(aes(fill=EXTRACTED_RESOURCES), notch=globalNotchValue) +
			facet_grid(INTERACTIONS ~ .) +
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


	
	dataFrame$EXTRACTED_RESOURCES <- mapvalues(dataFrame$EXTRACTED_RESOURCES, 
		from=c("f","p"), 
		to=c("uniform resources", "mixed resources")
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
	expDataFrame$E_R <- expDataFrame$EXTRACTED_RESOURCES
	
	
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


plotBootHistPop_I <-function(popDataFrame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(popDataFrame, aes_string(colName, fill="EXTRACTED_RESOURCES")) 
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
			ggplot(popDataFrame, aes_string(colName, fill="EXTRACTED_RESOURCES"))
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
performTTest <-function(measure,p.v,f.v) {

	t.result <- t.test(p.v,f.v)
	
	tValue <- t.result$statistic[[1]]
	dFreedom <- t.result$parameter[[1]]
	pValue <- af.double(t.result$p.value)

	#print(measure)
	#print(t.result)
	#print(summary(t.result))
	#print(pValue)	

	
	result.frame <- data.frame(MEASURE=measure,
		T_VALUE=tValue, D_FREEDOM=dFreedom, P_VALUE=pValue)
	
	return(result.frame)
}


computeBootStats <-function(fDataFrame,pDataFrame) {
	bootSize <- 1000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
	f.CAPTURES_BEST_CASE <- fDataFrame$CAPTURES_BEST_CASE
	f.CAPTURES_MEAN <- fDataFrame$CAPTURES_MEAN
	f.RES_E2C_STEPS_MEAN <- fDataFrame$RES_E2C_STEPS_MEAN
	f.RATE_MOTION <- fDataFrame$RATE_MOTION
	f.RATE_COMMUNICATION <- fDataFrame$RATE_COMMUNICATION

	f.CAPTURES_BEST_CASE <- bootMean(f.CAPTURES_BEST_CASE,bootSize)
	f.CAPTURES_MEAN <- bootMean(f.CAPTURES_MEAN,bootSize)
	f.RES_E2C_STEPS_MEAN <- bootMean(f.RES_E2C_STEPS_MEAN,bootSize)
	f.RATE_MOTION <- bootMean(f.RATE_MOTION,bootSize)
	f.RATE_COMMUNICATION <- bootMean(f.RATE_COMMUNICATION,bootSize)

	p.CAPTURES_BEST_CASE <- pDataFrame$CAPTURES_BEST_CASE
	p.CAPTURES_MEAN <- pDataFrame$CAPTURES_MEAN
	p.RES_E2C_STEPS_MEAN <- pDataFrame$RES_E2C_STEPS_MEAN
	p.RATE_MOTION <- pDataFrame$RATE_MOTION
	p.RATE_COMMUNICATION <- pDataFrame$RATE_COMMUNICATION

	p.CAPTURES_BEST_CASE <- bootMean(p.CAPTURES_BEST_CASE,bootSize)
	p.CAPTURES_MEAN <- bootMean(p.CAPTURES_MEAN,bootSize)
	p.RES_E2C_STEPS_MEAN <- bootMean(p.RES_E2C_STEPS_MEAN,bootSize)
	p.RATE_MOTION <- bootMean(p.RATE_MOTION,bootSize)
	p.RATE_COMMUNICATION <- bootMean(p.RATE_COMMUNICATION,bootSize)

	
	f.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=f.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=f.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=f.RES_E2C_STEPS_MEAN,
		RATE_MOTION=f.RATE_MOTION,
		RATE_COMMUNICATION=f.RATE_COMMUNICATION,
		POPULATION="heterogenous"
	)


	p.popDataFrame <- data.frame(
		CAPTURES_BEST_CASE=p.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=p.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=p.RES_E2C_STEPS_MEAN,
		RATE_MOTION=p.RATE_MOTION,
		RATE_COMMUNICATION=p.RATE_COMMUNICATION,
		POPULATION="homogenous"
	)
	
	popDataFrame <- rbind(p.popDataFrame,f.popDataFrame)

	return(popDataFrame)

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
			f.EXTRACTED_RESOURCES <- fDataFrame$EXTRACTED_RESOURCES
			
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
			p.EXTRACTED_RESOURCES <- pDataFrame$EXTRACTED_RESOURCES

			p.NUM_DETECTORS <- pDataFrame$NUM_DETECTORS
			p.NUM_EXTRACTORS <- pDataFrame$NUM_EXTRACTORS
			p.NUM_TRANSPORTERS <- pDataFrame$NUM_TRANSPORTERS
			p.E <- pDataFrame$E
			p.EXTRACTED_RESOURCES <- pDataFrame$EXTRACTED_RESOURCES
				

			p.CAPTURES_BEST_CASE <- bootMean(p.CAPTURES_BEST_CASE,bootSize)
			p.CAPTURES_MEAN <- bootMean(p.CAPTURES_MEAN,bootSize)
			p.RES_E2C_STEPS_MEAN <- bootMean(p.RES_E2C_STEPS_MEAN,bootSize)
			p.RATE_MOTION <- bootMean(p.RATE_MOTION,bootSize)
			p.RATE_COMMUNICATION <- bootMean(p.RATE_COMMUNICATION,bootSize)


			p.NUM_DETECTORS <- bootMean(p.NUM_DETECTORS,bootSize)
			p.NUM_EXTRACTORS <- bootMean(p.NUM_EXTRACTORS,bootSize)
			p.NUM_TRANSPORTERS <- bootMean(p.NUM_TRANSPORTERS,bootSize)
			p.E <- bootMean(p.E,bootSize)	

			
			#print(f.EXTRACTED_RESOURCES)
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
				EXTRACTED_RESOURCES="f",
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
				EXTRACTED_RESOURCES="p",
				INTERACTIONS=varInteraction

			)

			

			popDataFrame <- rbind(popDataFrame,f.popDataFrame,p.popDataFrame)


		}
	
	
	return(popDataFrame)

}






plotBootedStats <- function(expDataFrame) {

	
	popDataFrame <- computeBootStatsI(expDataFrame[expDataFrame$E_R=="f",], expDataFrame[expDataFrame$E_R=="p",])

	print(names(popDataFrame))
	print("--here--")
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
#expDataFrame <- expDataFrame[expDataFrame$SPECIES=="heterogenous",]
#plotBoxPlots(expDataFrame) # boxplots to show difference

#expDataFrame <- orig
#plotBoxPlotsAllPop(expDataFrame)



plotBootedStats(expDataFrame)








#plot the totals

