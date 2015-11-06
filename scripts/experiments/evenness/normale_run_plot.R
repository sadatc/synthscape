library(ggplot2)


#row_data <- read.csv(file="~/all_prolif.csv")
#row_data <- read.csv(file="~/dynamics1.csv)"
#row_data <- read.csv(file="~/algo1.csv")
row_data <- read.csv(file="~/algo2.csv")

row_data <- row_data[,c("GENERATION","CAPTURES_BEST_CASE","CAPTURES_TOTAL","CAPTURES_MEAN","TOT_FITNESS_MEAN")]

row_data$CAPTURES_BEST_CASE_PERC <- row_data$CAPTURES_BEST_CASE/16

data <- row_data 



pdf("plots.pdf")

print(
  ggplot(data,aes(x=GENERATION,y=TOT_FITNESS_MEAN))+
  geom_point() +
  ggtitle("Mean Fitness") 
)

print(
  ggplot(data,aes(x=GENERATION,y=CAPTURES_MEAN))+
  geom_point() +
  ggtitle("Mean Captures") 
)


print(

  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE_PERC))+
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

print(
  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
  geom_point()
)


dev.off()



