library(ggplot2)

#random
row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_reg_24.csv")
row_data <- cbind(env="random",row_data)
row_data <- cbind(pop="24",row_data)

# data is the final data container
# the first time, we just dump what was read in row_data
# next time around we use rbind to concatenate
data <- row_data 

row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_diff_24.csv")
row_data <- cbind(env="difficult",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_vdiff_24.csv")
row_data <- cbind(env="very difficult",row_data)
row_data <- cbind(pop="24",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_reg_36.csv")
row_data <- cbind(env="random",row_data)
row_data <- cbind(pop="36",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_diff_36.csv")
row_data <- cbind(env="difficult",row_data)
row_data <- cbind(pop="36",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="/Users/sadat/Dropbox/summ_env_vdiff_36.csv")
row_data <- cbind(env="very difficult",row_data)
row_data <- cbind(pop="36",row_data)
data <- rbind(data,row_data)








data$env <- factor(data$env)
data$pop <- factor(data$pop)


pdf("/tmp/mean_caps_comparison.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=CAPTURES_MEAN))+
xlim(0,1000)+
#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=env), size=0.25)+
facet_grid( pop ~ env, scales="free_y") +  
#xlab("Generation")+
#ylab("Resources Capture %") +
#ggtitle("1") +  
theme_bw() +
theme( text = element_text(size = 11)) +
theme(legend.position="bottom",legend.direction="horizontal")
#scale_color_discrete(name = "Resource Distribution")
)


pdf("/tmp/best_caps_comparison.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=CAPTURES_BEST_CASE))+
xlim(0,1000)+
#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=env), size=0.25)+
facet_grid( pop ~ env, scales="free_y") +  
#xlab("Generation")+
#ylab("Resources Capture %") +
#ggtitle("1") +  
theme_bw() +
theme( text = element_text(size = 11)) +
theme(legend.position="bottom",legend.direction="horizontal")
#scale_color_discrete(name = "Resource Distribution")
)



pdf("/tmp/E_comparison.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=GENERATION,y=E))+
xlim(0,1000)+
#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=env), size=0.25)+
facet_grid( pop ~ env, scales="free_y") +  
#xlab("Generation")+
#ylab("Resources Capture %") +
#ggtitle("1") +  
theme_bw() +
theme( text = element_text(size = 11)) +
theme(legend.position="bottom",legend.direction="horizontal")
#scale_color_discrete(name = "Resource Distribution")
)


orderedData <-data[order(data$CAPTURES_MEAN),]

pdf("/tmp/E_comparison2.pdf")
#png("interactions.png", width = 8, height = 4, units = "in", res=180)
print(ggplot(data,aes(x=CAPTURES_MEAN,y=E))+

#scale_y_continuous(labels =  percent_format()) +
#geom_point(aes(shape=interaction), fill="white", color="black", size=1.75,  alpha=1/4)+
geom_line(aes(color=env), size=0.25)+
facet_grid( pop ~ env, scales="free_y") +  
#xlab("Generation")+
#ylab("Resources Capture %") +
#ggtitle("1") +  
theme_bw() +
theme( text = element_text(size = 11)) +
theme(legend.position="bottom",legend.direction="horizontal")
#scale_color_discrete(name = "Resource Distribution")
)


pdf("/tmp/Species_comparison.pdf")
	print(
	  ggplot(data) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_DETECTORS, color="Detectors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_EXTRACTORS, color="Extractors")) +
	  geom_point(alpha=0.3,aes(x=GENERATION,y=NUM_TRANSPORTERS, color="Transporters")) + 
	  facet_grid( pop ~ env, scales="free_y") +  
	  ggtitle("Species Growth vs. Best Captures") + 
	  scale_color_discrete("Species") +
	  ylab("Number of Agents") +
	  xlab("Capture (Best)") + 
	  theme_bw() +
theme( text = element_text(size = 11)) +
theme(legend.position="bottom",legend.direction="horizontal")
	)

dev.off()



