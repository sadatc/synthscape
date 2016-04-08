library(ggplot2)
library(plyr)
library(xtable)
library(extrafont)
library(scales)
library(extrafont)


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



doNormalityAnalysisSubPop <- function(exp4.df) {
	dist.table <- data.frame()



#	print("========== ALIFE ========")
	gdata <- exp4.df[exp4.df$SPECIES=="homogenous"  & exp4.df$MODEL=="alife" & exp4.df$INTERACTIONS=="none"   ,]

	sdata <- exp4.df[exp4.df$SPECIES=="heterogenous" &  exp4.df$MODEL=="alife" & exp4.df$INTERACTIONS!="none"  ,]

	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_MEAN",gdata$CAPTURES_MEAN,sdata$CAPTURES_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("CAPTURES_BEST_CASE",gdata$CAPTURES_BEST_CASE,sdata$CAPTURES_BEST_CASE))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RES_E2C_STEPS_MEAN",gdata$RES_E2C_STEPS_MEAN,sdata$RES_E2C_STEPS_MEAN))
	dist.table <- rbind(dist.table,analyzeNormailtyPair("RATE_MOTION",gdata$RATE_MOTION,sdata$RATE_MOTION))
	
	

	p("**** Normality test for Png vs Pis data (Shapiro-Wilks Normality Test)  ****")
	data(dist.table)
	print(xtable(dist.table, digits=c(0,0,0,2,-2,2,-2)), include.rownames=FALSE)

	
}



doNormalityAnalysisFullPop <- function(exp4.df) {
	dist.table <- data.frame()

	gdata <- exp4.df[exp4.df$SPECIES=="homogenous" & exp4.df$QUALITY=="s"   ,]

	sdata <- exp4.df[exp4.df$SPECIES=="heterogenous" & exp4.df$QUALITY=="s"  ,]

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















plotHists <- function(exp4.df) {

	plotHistByIntResMix( exp4.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-cm.pdf", TRUE)
	plotHistByIntResMix( exp4.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-cb.pdf", TRUE)
	plotHistByIntResMix( exp4.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-e4c.pdf")
	plotHistByIntResMix( exp4.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-rm.pdf")
	plotHistByIntResMix( exp4.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-rc.pdf")
	plotHistByIntResMix( exp4.df,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-dets.pdf")
	plotHistByIntResMix( exp4.df,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-exts.pdf")
	plotHistByIntResMix( exp4.df,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-trns.pdf")
	plotHistByIntResMix( exp4.df,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-hist-intresmix-e.pdf")



}


plotBoxPlot_EvenM <-function(dataFrame, colName, fileName, showPercent=FALSE) {

	pdf(fileName,  
	  width = 7,height = 4, family="CMU Serif")	  
	yAxisLabel <- getMeasureShortName(colName)
	
	
	if( showPercent==FALSE ) {
		print(
			ggplot(dataFrame, aes_string( x="EXTRACTED_RESOURCES",y=colName)) +
			geom_boxplot(aes(fill=EXTRACTED_RESOURCES), notch=globalNotchValue) +
			facet_grid( INTERACTIONS ~ RATIO) +
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
			facet_grid(INTERACTIONS ~ RATIO) +
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


plotBoxPlotsManual <- function(exp4.df) {
	orig <- exp4.df


	# compare richness type vs performance

	plotBoxPlot_EvenM(exp4.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-cb.pdf", TRUE)

	plotBoxPlot_EvenM(exp4.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-cm.pdf", TRUE)

	plotBoxPlot_EvenM(exp4.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-e2c.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-rm.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-rc.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-ds.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-es.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-ts.pdf", FALSE)

	plotBoxPlot_EvenM(exp4.df,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e.pdf", FALSE)

}


plotBoxPlots <- function(exp4.df) {
	orig <- exp4.df


	# compare richness type vs performance

	plotBoxPlot_Even(exp4.df,"CAPTURES_BEST_CASE", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-cb.pdf", TRUE)

	plotBoxPlot_Even(exp4.df,"CAPTURES_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-cm.pdf", TRUE)

	plotBoxPlot_Even(exp4.df,"RES_E2C_STEPS_MEAN", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-e2c.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"RATE_MOTION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-rm.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"RATE_COMMUNICATION", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e-rc.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"NUM_DETECTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-ds.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"NUM_EXTRACTORS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-es.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"NUM_TRANSPORTERS", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-ts.pdf", FALSE)

	plotBoxPlot_Even(exp4.df,"E", "/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-box-e.pdf", FALSE)

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
preProcessData <- function(exp4.df) {
	# set all factors
	exp4.df$MODEL <- factor(exp4.df$MODEL) 
	exp4.df$SPECIES <- factor(exp4.df$SPECIES)
	exp4.df$POPULATION <- exp4.df$SPECIES
	
	
	exp4.df$POPULATION <- factor(exp4.df$POPULATION)	
	exp4.df$INTERACTIONS <- factor(exp4.df$INTERACTIONS)
	exp4.df$COMPLEXITY <- factor(exp4.df$COMPLEXITY)
	exp4.df$CLONES <- factor(exp4.df$CLONES)
	exp4.df$EVENNESS_CONTROL <- factor(exp4.df$EVENNESS_CONTROL)
	exp4.df$EXTRACTED_RESOURCES <- factor(exp4.df$EXTRACTED_RESOURCES)
	exp4.df$E_R <- exp4.df$EXTRACTED_RESOURCES
	
	
	exp4.df$GRIDS <- factor(exp4.df$GRIDS)
	exp4.df$RESOURCES <- factor(exp4.df$RESOURCES)
	exp4.df$SITES <- factor(exp4.df$SITES)
	exp4.df$OBSTACLES <- factor(exp4.df$OBSTACLES)
	exp4.df$DIFFICULTY <- factor(exp4.df$DIFFICULTY)
	exp4.df$RATIO <- factor(exp4.df$RATIO)

	
	exp4.df$RES_E2C_STEPS_MEAN <- (exp4.df$RES_E2C_STEPS_MEAN*exp4.df$CAPTURES_MEAN)

	## compute evenness
	exp4.df$TOT_POP <- 24
	numSpecies <- 3

	P1 <- exp4.df$NUM_DETECTORS/exp4.df$TOT_POP
	P2 <- exp4.df$NUM_EXTRACTORS/exp4.df$TOT_POP
	P3 <- exp4.df$NUM_TRANSPORTERS/exp4.df$TOT_POP

	H <- -((P1*log(P1)) + (P2*log(P2)) + (P3*log(P3)) )
	exp4.df$E <- H/log(numSpecies)


	return(exp4.df)
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


plotBootHistPop_I <-function(pop.data.frame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(pop.data.frame, aes_string(colName, fill="EXTRACTED_RESOURCES")) 
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(INTERACTIONS ~ RATIO) 
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
			ggplot(pop.data.frame, aes_string(colName, fill="EXTRACTED_RESOURCES"))
			+ geom_density(alpha = 0.9)
			+ scale_fill_manual(values=c("white","grey50")) 
			+ facet_grid(INTERACTIONS ~ RATIO) 
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



plotBootHistPop_Q <-function(pop.data.frame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 2, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION")) 
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
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION"))
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


plotBootHistPop_IQ <-function(pop.data.frame, colName, fileName, showPercent = FALSE) {

	xAxisLabel <- getMeasureShortName(colName)

	pdf(fileName,  
	  width = 6,height = 3, family="CMU Serif")
	if( showPercent==FALSE ) {
		print(
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION")) 
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
			ggplot(pop.data.frame, aes_string(colName, fill="POPULATION"))
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


computeBootStats <-function(f.data.frame,p.data.frame) {
	bootSize <- 1000

	# extract the measures
	s <- data.frame()
	g <- data.frame()
	f.CAPTURES_BEST_CASE <- f.data.frame$CAPTURES_BEST_CASE
	f.CAPTURES_MEAN <- f.data.frame$CAPTURES_MEAN
	f.RES_E2C_STEPS_MEAN <- f.data.frame$RES_E2C_STEPS_MEAN
	f.RATE_MOTION <- f.data.frame$RATE_MOTION
	f.RATE_COMMUNICATION <- f.data.frame$RATE_COMMUNICATION

	f.CAPTURES_BEST_CASE <- boot.mean(f.CAPTURES_BEST_CASE,bootSize)
	f.CAPTURES_MEAN <- boot.mean(f.CAPTURES_MEAN,bootSize)
	f.RES_E2C_STEPS_MEAN <- boot.mean(f.RES_E2C_STEPS_MEAN,bootSize)
	f.RATE_MOTION <- boot.mean(f.RATE_MOTION,bootSize)
	f.RATE_COMMUNICATION <- boot.mean(f.RATE_COMMUNICATION,bootSize)

	p.CAPTURES_BEST_CASE <- p.data.frame$CAPTURES_BEST_CASE
	p.CAPTURES_MEAN <- p.data.frame$CAPTURES_MEAN
	p.RES_E2C_STEPS_MEAN <- p.data.frame$RES_E2C_STEPS_MEAN
	p.RATE_MOTION <- p.data.frame$RATE_MOTION
	p.RATE_COMMUNICATION <- p.data.frame$RATE_COMMUNICATION

	p.CAPTURES_BEST_CASE <- boot.mean(p.CAPTURES_BEST_CASE,bootSize)
	p.CAPTURES_MEAN <- boot.mean(p.CAPTURES_MEAN,bootSize)
	p.RES_E2C_STEPS_MEAN <- boot.mean(p.RES_E2C_STEPS_MEAN,bootSize)
	p.RATE_MOTION <- boot.mean(p.RATE_MOTION,bootSize)
	p.RATE_COMMUNICATION <- boot.mean(p.RATE_COMMUNICATION,bootSize)

	
	f.pop.data.frame <- data.frame(
		CAPTURES_BEST_CASE=f.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=f.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=f.RES_E2C_STEPS_MEAN,
		RATE_MOTION=f.RATE_MOTION,
		RATE_COMMUNICATION=f.RATE_COMMUNICATION,
		POPULATION="heterogenous"
	)


	p.pop.data.frame <- data.frame(
		CAPTURES_BEST_CASE=p.CAPTURES_BEST_CASE,
		CAPTURES_MEAN=p.CAPTURES_MEAN,
		RES_E2C_STEPS_MEAN=p.RES_E2C_STEPS_MEAN,
		RATE_MOTION=p.RATE_MOTION,
		RATE_COMMUNICATION=p.RATE_COMMUNICATION,
		POPULATION="homogenous"
	)
	
	pop.data.frame <- rbind(p.pop.data.frame,f.pop.data.frame)

	return(pop.data.frame)

}






computeBootStatsI <-function(f.orig.data.frame, p.orig.data.frame) {
	bootSize <- 1000



	# extract the measures
	f <- data.frame()
	p <- data.frame()
	pop.data.frame <- data.frame()
	
	## loop through all interactions
	## loop through all qualities


	for(var.ratio in unique(f.orig.data.frame$RATIO)) {

	
		for(var.interaction in unique(f.orig.data.frame$INTERACTIONS)) {
				

				f <- data.frame()
				p <- data.frame()
				
				f.data.frame <- f.orig.data.frame[f.orig.data.frame$INTERACTIONS==var.interaction & f.orig.data.frame$RATIO == var.ratio,]
				
				p.data.frame <- p.orig.data.frame[p.orig.data.frame$INTERACTIONS==var.interaction & p.orig.data.frame$RATIO == var.ratio,]
				
				
				f.CAPTURES_BEST_CASE <- f.data.frame$CAPTURES_BEST_CASE
				f.CAPTURES_MEAN <- f.data.frame$CAPTURES_MEAN
				f.RES_E2C_STEPS_MEAN <- f.data.frame$RES_E2C_STEPS_MEAN
				f.RATE_MOTION <- f.data.frame$RATE_MOTION
				f.RATE_COMMUNICATION <- f.data.frame$RATE_COMMUNICATION
				f.EXTRACTED_RESOURCES <- f.data.frame$EXTRACTED_RESOURCES
				
				f.NUM_DETECTORS <- f.data.frame$NUM_DETECTORS
				f.NUM_EXTRACTORS <- f.data.frame$NUM_EXTRACTORS
				f.NUM_TRANSPORTERS <- f.data.frame$NUM_TRANSPORTERS
				f.E <- f.data.frame$E
						

				f.CAPTURES_BEST_CASE <- boot.mean(f.CAPTURES_BEST_CASE,bootSize)
				
				f.CAPTURES_MEAN <- boot.mean(f.CAPTURES_MEAN,bootSize)
				f.RES_E2C_STEPS_MEAN <- boot.mean(f.RES_E2C_STEPS_MEAN,bootSize)
				f.RATE_MOTION <- boot.mean(f.RATE_MOTION,bootSize)
				f.RATE_COMMUNICATION <- boot.mean(f.RATE_COMMUNICATION,bootSize)

				f.NUM_DETECTORS <- boot.mean(f.NUM_DETECTORS,bootSize)
				f.NUM_EXTRACTORS <- boot.mean(f.NUM_EXTRACTORS,bootSize)
				f.NUM_TRANSPORTERS <- boot.mean(f.NUM_TRANSPORTERS,bootSize)
				f.E <- boot.mean(f.E,bootSize)	


				p.CAPTURES_BEST_CASE <- p.data.frame$CAPTURES_BEST_CASE
				p.CAPTURES_MEAN <- p.data.frame$CAPTURES_MEAN
				p.RES_E2C_STEPS_MEAN <- p.data.frame$RES_E2C_STEPS_MEAN
				p.RATE_MOTION <- p.data.frame$RATE_MOTION
				p.RATE_COMMUNICATION <- p.data.frame$RATE_COMMUNICATION
				p.EXTRACTED_RESOURCES <- p.data.frame$EXTRACTED_RESOURCES

				p.NUM_DETECTORS <- p.data.frame$NUM_DETECTORS
				p.NUM_EXTRACTORS <- p.data.frame$NUM_EXTRACTORS
				p.NUM_TRANSPORTERS <- p.data.frame$NUM_TRANSPORTERS
				p.E <- p.data.frame$E
				p.EXTRACTED_RESOURCES <- p.data.frame$EXTRACTED_RESOURCES
					

				p.CAPTURES_BEST_CASE <- boot.mean(p.CAPTURES_BEST_CASE,bootSize)
				p.CAPTURES_MEAN <- boot.mean(p.CAPTURES_MEAN,bootSize)
				p.RES_E2C_STEPS_MEAN <- boot.mean(p.RES_E2C_STEPS_MEAN,bootSize)
				p.RATE_MOTION <- boot.mean(p.RATE_MOTION,bootSize)
				p.RATE_COMMUNICATION <- boot.mean(p.RATE_COMMUNICATION,bootSize)


				p.NUM_DETECTORS <- boot.mean(p.NUM_DETECTORS,bootSize)
				p.NUM_EXTRACTORS <- boot.mean(p.NUM_EXTRACTORS,bootSize)
				p.NUM_TRANSPORTERS <- boot.mean(p.NUM_TRANSPORTERS,bootSize)
				p.E <- boot.mean(p.E,bootSize)	

				
				#print(f.EXTRACTED_RESOURCES)
				#stop()
				

				f.pop.data.frame <- data.frame(
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
					INTERACTIONS=var.interaction,
					RATIO = var.ratio
				)

				
				

				p.pop.data.frame <- data.frame(
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
					INTERACTIONS=var.interaction,
					RATIO = var.ratio

				)

				

				pop.data.frame <- rbind(pop.data.frame,f.pop.data.frame,p.pop.data.frame)


			}
		}
	
	return(pop.data.frame)

}






plotBootedStats <- function(exp4.df) {

	
	pop.data.frame <- computeBootStatsI(exp4.df[exp4.df$E_R=="f",], exp4.df[exp4.df$E_R=="p",])
	

	print(names(pop.data.frame))
	print("--here--")
	plotBootHistPop_I(pop.data.frame,"CAPTURES_BEST_CASE","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-cb.pdf", TRUE)
	plotBootHistPop_I(pop.data.frame,"CAPTURES_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-cm.pdf", TRUE)
	plotBootHistPop_I(pop.data.frame,"RES_E2C_STEPS_MEAN","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-e2c.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"RATE_MOTION","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-rm.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"RATE_COMMUNICATION","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-rc.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"NUM_DETECTORS","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-dets.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"NUM_EXTRACTORS","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-exts.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"NUM_TRANSPORTERS","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-trns.pdf", FALSE)
	plotBootHistPop_I(pop.data.frame,"E","/Users/sadat/Dropbox/research/dissertation/images/exp4.m/e4-boot-pop-i-e.pdf", FALSE)

}



##############################   MAIN PROCESS BEGINS ###############################


exp4.df <- read.csv(file="~/synthscape/scripts/analysis/data/exp4/exp4_man_experiments_mean_300.csv")

exp4.df <- preProcessData(exp4.df)     # factorizes, as appropriate, adjusts E2C...
exp4.df <- renameFactorValues(exp4.df) # renames for nice plots

orig <- exp4.df


##### not using these....plotGraphs(exp4.df)

# Using these...

# Populations: 3_4 and 3_8
# Richness Types: mo, bi, po, tr, ho
# Richness
# Traitsum
# 

#plotHists(exp4.df)    # plots histograms

#doNormalityAnalysisFullPop(exp4.df)
#doNormalityAnalysisSubPop(exp4.df)

# first we'll only look at the heterogenous population:
#exp4.df <- exp4.df[exp4.df$SPECIES=="heterogenous",]
#plotBoxPlots(exp4.df) # boxplots to show difference

plotBoxPlotsManual(exp4.df) # boxplots to show difference

#exp4.df <- orig
#plotBoxPlotsAllPop(exp4.df)



plotBootedStats(exp4.df)
#plot the totals

