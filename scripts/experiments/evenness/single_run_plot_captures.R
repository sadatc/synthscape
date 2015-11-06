library(ggplot2)

#random
row_data <- read.csv(file="~/unfittest.csv")
row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","CAPTURES_TOTAL","CAPTURES_MEAN","TOT_FITNESS_MEAN","NUM_DETECTORS","NUM_EXTRACTORS","NUM_TRANSPORTERS")]
row_data$CAPTURES_BEST_CASE <- row_data$CAPTURES_BEST_CASE/16
data <- row_data 

#data$gofp <- factor(data$gofp)

pdf("single_run_plot_captures.pdf")

print(

  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
  geom_point()
  #xlim(0,1000)+
  #geom_line(aes(color=resource_distribution), size=0.25)+
  #facet_grid( gofp ~ gosc, scales="free_y") +  
  #xlab("Generation")+ylab("Resources Capture %") +
  #ggtitle("Capture Trends (Algo-Params (gofp/gosc) vs. Resource Distribution)") +  
  #theme_bw() +
  #theme( text = element_text(size = 11)) +
  #theme(legend.position="bottom",legend.direction="horizontal") +
  #scale_color_discrete(name = "Resource Distribution")
)

dev.off()



