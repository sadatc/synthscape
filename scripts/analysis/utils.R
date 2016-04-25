pValueString <- function(val) {
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
	x <- sub("CAPTURES[\\]_MEAN","$M_{mean}$",x) 
	x <- sub("CAPTURES[\\]_BEST[\\]_CASE","$M_{best}$",x) 
	x <- sub("RES[\\]_E2C[\\]_STEPS[\\]_MEAN","$M_{effort}$",x) 
	x <- sub("RATE[\\]_COMMUNICATION","$M_{comm}$",x) 
	x <- sub("RATE[\\]_MOTION","$M_{move}$",x) 
	x <- sub("NUM[\\]_DETECTORS","$N_{d}$",x) 
	x <- sub("NUM[\\]_EXTRACTORS","$N_{e}$",x) 
	x <- sub("NUM[\\]_TRANSPORTERS","$N_{t}$",x) 
	x <- sub("NUM[\\]_PROCESSORS","$N_{p}$",x) 
	x <- sub("[[]ht[]]","[H]",x) 
	cat(x,sep="\n")
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

percentFormatter <- function(x) {
	y <- paste(round(x*100),"%",sep="")
	return(y)
}