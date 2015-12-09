library(ggplot2)

#random
row_data <- read.csv(file="/tmp/sum_me_reg_24_m123.csv")
row_data <- cbind(env="m123",row_data)
row_data <- cbind(pop="24",row_data)

# data is the final data container
# the first time, we just dump what was read in row_data
# next time around we use rbind to concatenate
data <- row_data 

row_data <- read.csv(file="/tmp/sum_me_reg_24_m132.csv")
row_data <- cbind(env="m132",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="/tmp/sum_me_reg_24_m213.csv")
row_data <- cbind(env="m213",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="/tmp/sum_me_reg_24_m231.csv")
row_data <- cbind(env="m231",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="/tmp/sum_me_reg_24_m312.csv")
row_data <- cbind(env="m312",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="/tmp/sum_me_reg_24_m321.csv")
row_data <- cbind(env="m321",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)




data$env <- factor(data$env)
data$pop <- factor(data$pop)




# pdf("/tmp/E_comparison2.pdf")
# png("interactions.png", width = 8, height = 4, units = "in", res=180)
# print(ggplot(data,aes(x=CAPTURES_MEAN,y=E))+

# scale_y_continuous(labels =  percent_format()) +
# geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
# geom_line(aes(color=env), size=0.25)+
# facet_grid( pop ~ env, scales="free_y") +  
# xlab("Generation")+
# ylab("Resources Capture %") +
# ggtitle("1") +  
# theme_bw() +
# theme( text = element_text(size = 11)) +
# theme(legend.position="bottom",legend.direction="horizontal")
# scale_color_discrete(name = "Resource Distribution")
# )


numExperiments <- " "

pdf("/tmp/comparison_plots2.pdf")
	print(
	  ggplot(data) +
	  geom_line(aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN, color="Detectors")) +
	  geom_line(aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN, color="Extractors")) +
	  geom_line(aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN, color="Transporters")) + 
	  #ggtitle(parse("Species Fitness Growth", " () +
	  ggtitle(bquote(atop(.("Species Fitness Growth"), atop(.(numExperiments))))) + 
	  scale_color_discrete(name="Species") +
	  ylab("Fitness") +
	  xlab("Generation") + 
	  facet_grid( pop ~ env, scales="free_y") 
	) 

	print(
	  ggplot(data,aes(x=GENERATION,y=DETECTOR_FITNESS_MEAN))+
	  geom_line(color="red") +
	  ggtitle("Detector Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 
	)

	print(
	  ggplot(data,aes(x=GENERATION,y=EXTRACTOR_FITNESS_MEAN))+
	  geom_line(color="green") +
	  ggtitle("Extractor Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 
	)

	print(
	  ggplot(data,aes(x=GENERATION,y=TRANSPORTER_FITNESS_MEAN))+
	  geom_line(color="blue") +
	  ggtitle("Transporter Fitness") +
	  ylab("Fitness") +
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 
	)


	#print(

	  #ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE_PERC))+
	  #geom_point()

	  #xlim(0,1000)+
	  #geom_line(aes(color=resource_distribution), size=0.25)+
	  #facet_grid( gofp ~ gosc, scales="free_y") +  
	  #xlab("Generation")+ylab("Resources Capture %") +
	  #ggtitle("Capture Trends (Algo-Params (gofp/gosc) vs. Resource Distribution)") +  
	  #theme_bw() +
	  #theme( text = element_text(size = 11)) +
	  #theme(legend.position="bottom",legend.direction="horizontal") +
	  #scale_color_discrete(name = "Resource Distribution")
	#)

	print(
	  ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
	  geom_point(alpha=0.3) +
	  ggtitle("Captures (Best Case)") +
	  ylab("Captures (Best Case)") +
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 
	)


	print(
	  ggplot(data,aes(x=GENERATION,y=CAPTURES_MEAN))+
	  geom_point(alpha=0.3) +
	  ggtitle("Captures (Mean)") +
	  ylab("Captures (Mean)") +
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 
	)



	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Generation") + 
	  scale_color_discrete(name="Species") +
	  ylab("Number of Agents")+
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 	  
	) 

	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Best Captures") + 
	  scale_color_discrete("Species") +
	  ylab("Number of Agents") +
	  xlab("Capture (Best)")
	  +facet_grid( pop ~ env, scales="free_y") 	  
	) 
	
	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  ggtitle("Species Growth vs. Mean Captures") + 
	  scale_color_discrete("Species") +
	  ylab("Number of Agents") +
	  xlab("Capture (Mean)")
	  +facet_grid( pop ~ env, scales="free_y") 	  
	) 
	



	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=E)) +
	  geom_smooth(method=lm, aes(x=GENERATION,y=E)) +
	  ggtitle("Evenness vs. Generation") + 
	  ylab("Evenness (E)") + 
	  xlab("Generation")
	  +facet_grid( pop ~ env, scales="free_y") 	  
	)

	orderedData <-data[order(data$CAPTURES_MEAN),]

	print(
	  ggplot(orderedData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_BEST_CASE,y=E)) +
	  geom_smooth(method=lm, aes(x=CAPTURES_BEST_CASE,y=E)) +

	  ggtitle("Evenness vs. Best Capture") + 
	  ylab("Evenness (E)") +
	  xlab("Capture (Best)")
	  +facet_grid( pop ~ env, scales="free_x") 	  
	  
	)

	print(
	  ggplot(orderedData) +
	  geom_point(alpha=0.3,aes(x=CAPTURES_MEAN,y=E)) +
	  geom_smooth(method=lm, aes(x=CAPTURES_MEAN,y=E)) +

	  ggtitle("Evenness vs. Mean Capture") + 
	  ylab("Evenness (E)") +
	  xlab("Capture (Mean)")
	  +facet_grid( pop ~ env, scales="free_x") 	  
	)












dev.off()



