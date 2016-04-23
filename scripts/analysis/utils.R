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
	cat(x,sep="\n")
}