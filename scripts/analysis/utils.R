pValueString <- function(val) {
	if(is.na(val)) {
		return("NA")
	}

	result <- val

	if(val < 0.05) {
		result <- "< 0.05"
	} 

	if(val < 0.01) {
		result <- "< 0.01"
	} 

	if(val > 0.05) {
		result <- paste("= ",round(val,digits=2),sep="")
	}
	
	return(result)
}

p <- function(msg) {
	print("**************************************************************************", quote=FALSE)
	print(msg, quote=FALSE)
	print("**************************************************************************", quote=FALSE)

}

displayLatex <- function(xtableString) {
	x <- capture.output(xtableString)
	x <- sub("CAPTURES[\\]_MEAN","$M_{captures}$",x) 
	x <- sub("CAPTURES[\\]_BEST[\\]_CASE","$M_{best}$",x) 
	x <- sub("RES[\\]_E2C[\\]_STEPS[\\]_MEAN","$M_{time}$",x) 
	x <- sub("RATE[\\]_COMMUNICATION","$M_{messages}$",x) 
	x <- sub("RATE[\\]_MOTION","$M_{distance}$",x) 
	x <- sub("NUM[\\]_DETECTORS","$N_{d}$",x) 
	x <- sub("NUM[\\]_EXTRACTORS","$N_{e}$",x) 
	x <- sub("NUM[\\]_TRANSPORTERS","$N_{t}$",x) 
	x <- sub("NUM[\\]_PROCESSORS","$N_{p}$",x) 
	x <- sub("[[]ht[]]","[H]",x)
	x <- gsub("[^[:digit:]]0[.]",".",x)
	x <- gsub("0.000000E[+]00","0",x)
	x <- gsub("(((\\S)+)E[+]((\\S)+))","\\\\num{\\1}",x)
	x <- gsub("(((\\S)+)E[-]((\\S)+))","\\\\num{\\1}",x)

	x <- gsub("\\num{&.00E+00}"," & 0",x,fixed=TRUE)

	
	cat(x,sep="\n")
}



getMeasurePrettyName <- function(colName) {
	result <- colName
	
	if(colName == "CAPTURES_MEAN") {
		result <- expression(italic(M[captures]) ~ "(% of total)")
	}

	if(colName == "CAPTURES_BEST_CASE") {
		result <- expression(italic(M[best]) ~ ":  Best Captures (% of total)")
	}

	if(colName == "RES_E2C_STEPS_MEAN") {
		result <- expression(italic(M[time]) )
	}

	if(colName == "RATE_COMMUNICATION") {
		result <- expression(italic(M[messages]))
	}

	if(colName == "RATE_MOTION") {
		result <- expression(italic(M[distance]) )
	}
	
	if(colName == "NUM_DETECTORS") {
		result <- expression(italic(N[d]) ~ ":  Detectors")		
	}

	if(colName == "NUM_TRANSPORTERS") {
		result <- expression(italic(N[t]) ~ ":  Transporters")
	}

	if(colName == "NUM_EXTRACTORS") {
		result <- expression(italic(N[e]) ~ ":  Extractors")		
	}

	if(colName == "E") {
		result <- expression(italic(E) ~ ":  Evenness")
	}

	return(result)
}



getMeasureShortName <- function(colName) {
	result <- colName
	
	if(colName == "CAPTURES_MEAN") {
		result <- expression(italic(M[captures]) )
	}

	if(colName == "CAPTURES_BEST_CASE") {
		result <- expression(italic(M[best]))
	}

	if(colName == "RES_E2C_STEPS_MEAN") {
		result <- expression(italic(M[time]))
	}

	if(colName == "RATE_COMMUNICATION") {
		result <- expression(italic(M[messages]) )
	}

	if(colName == "RATE_MOTION") {
		result <- expression(italic(M[distance]) )
	}
	
	if(colName == "NUM_DETECTORS") {
		result <- expression(italic(N[d]) )
	}

	if(colName == "NUM_EXTRACTORS") {
		result <- expression(italic(N[e]) )
	}

	if(colName == "NUM_TRANSPORTERS") {
		result <- expression(italic(N[t]) )
	}


	if(colName == "E") {
		result <- expression(italic(E) )
	}
	return(result)
}

percentFormatter <- function(x) {
	y <- paste(round(x*100),"%",sep="")
	return(y)
}

# Function turns the internal abbreviated values to full expansions and re-ordered
# the values were abbreviated in data files to save space
renameFactorValues <- function(dataFrame) {
	
	dataFrame$MODEL <- mapvalues(dataFrame$MODEL, from=c("a","e","i"), to=c("alife","embodied","island"))
	
	dataFrame$MODEL <- factor(dataFrame$MODEL,c("island","embodied","alife"))
	
	
	dataFrame$INTERACTIONS_REAL <- mapvalues(dataFrame$INTERACTIONS, from=c("n","b","u","t"), to=c("none","broadcast","unicast","trail"))
	
	dataFrame$INTERACTIONS_REAL <- factor(dataFrame$INTERACTIONS_REAL, c("none","trail","broadcast","unicast"))

	
	dataFrame$INTERACTIONS <- ifelse(dataFrame$INTERACTIONS_REAL=="none","none","interacting")
	
	dataFrame$INTERACTIONS <- factor(dataFrame$INTERACTIONS,c("none","interacting"))	
	
	dataFrame$SPECIES <- mapvalues(dataFrame$SPECIES, from=c("g","s"), to=c("Hm","Ht"))

	dataFrame$SPECIES <- factor(dataFrame$SPECIES, c("Hm","Ht"))
	
	dataFrame$POPULATION <- mapvalues(dataFrame$POPULATION, from=c("g","s"), to=c("Hm","Ht"))
	
	dataFrame$POPULATION <- factor(dataFrame$POPULATION, c("Hm","Ht"))
	
	return(dataFrame)
}