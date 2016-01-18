library(ggplot2)

csvFile <- "/tmp/sample.csv"
pdfFile <- "/tmp/sample.pdf"

# read in the command line arguments
args <- commandArgs(TRUE)

if(length(args)==2) {
  csvFile = args[1]
  pdfFile = args[2]
} 

if(length(args)==1) {
  csvFile = args[1]
} 

row_data <- read.csv(file=csvFile)
pdf(pdfFile)

# restrict to the first 250 rows
# row_data <- head(row_data,250)

# only pick the columns we're interested in...
row_data <- row_data[,c("GENERATION","RESOURCES","CAPTURES_BEST_CASE","CAPTURES_TOTAL","CAPTURES_MEAN","TOT_FITNESS_MEAN")]



# do some data manipulation
row_data$CAPTURES_BEST_CASE_PERC <- row_data$CAPTURES_BEST_CASE/row_data$RESOURCES

data <- row_data 


print(
  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
  geom_point(alpha=0.3)
)


print(
  ggplot(data,aes(x=GENERATION,y=CAPTURES_MEAN))+
  geom_point(alpha=0.3) +
  ggtitle("Mean Captures") 
)

print(
  ggplot(data,aes(x=GENERATION,y=TOT_FITNESS_MEAN))+
  geom_line() +
  ggtitle("Overall Mean Fitness") 
)

dev.off()



