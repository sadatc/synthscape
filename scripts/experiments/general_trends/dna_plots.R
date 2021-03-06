# island model
library(ggplot2)


row_data <- read.csv(file="isl_het_none.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="isl_het_trail.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="isl_het_broad.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="isl_het_uni.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, unicast",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="isl_hom_none.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="isl_hom_trail.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="isl_hom_broad.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="isl_hom_uni.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)

# pdf("isl_dna.pdf")
png("isl_dna.png",  
  width     = 6,
  height    = 6,
  units     = "in",
  res=360)

print(ggplot(data,aes(x=GENERATION,y=TRANSPORTER_ALPHA_DIST))+
geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
facet_wrap( ~ type, scale="free", ncol=2) + 
ggtitle("Island Model Alpha Genotype Diff %") +  
theme_bw() +
theme(text = element_text(size = 9)))

dev.off()



row_data <- read.csv(file="emb_het_none.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="emb_het_trail.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="emb_het_broad.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="emb_het_uni.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, unicast",row_data)
data <- rbind(data,row_data)



row_data <- read.csv(file="emb_hom_none.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="emb_hom_trail.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="emb_hom_broad.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="emb_hom_uni.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)


png("emb_dna.png",  
  width     = 6,
  height    = 6,
  units     = "in",
  res=360)

print(ggplot(data,aes(x=GENERATION,y=TRANSPORTER_ALPHA_DIST))+
geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
facet_wrap( ~ type, scale="free", ncol=2) + 
ggtitle("Embodied Model Alpha Genotype Diff %") +  
theme_bw() +
theme(text = element_text(size = 9)))

dev.off()


row_data <- read.csv(file="ali_het_none.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, no interaction",row_data)
data <- row_data

row_data <- read.csv(file="ali_het_trail.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="ali_het_broad.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ali_het_uni.csv")
row_data <- row_data[,c("GENERATION","TRANSPORTER_ALPHA_DIST")]
row_data <- cbind(type="heterogenous, unicast",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="ali_hom_none.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, no interaction",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ali_hom_trail.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, trail",row_data)
data <- rbind(data,row_data)


row_data <- read.csv(file="ali_hom_broad.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, broadcast",row_data)
data <- rbind(data,row_data)

row_data <- read.csv(file="ali_hom_uni.csv")
row_data <- row_data[,c("GENERATION","HOMOGENOUS_ALPHA_DIST")]
names(row_data)[names(row_data)=="HOMOGENOUS_ALPHA_DIST"] <- "TRANSPORTER_ALPHA_DIST"
row_data <- cbind(type="homogenous, unicast",row_data)
data <- rbind(data,row_data)

data$type <- factor(data$type)


png("ali_dna.png",  
  width     = 6,
  height    = 6,
  units     = "in",
  res=360)

print(ggplot(data,aes(x=GENERATION,y=TRANSPORTER_ALPHA_DIST))+
geom_point(fill="white", color="black", size=0.75, shape=1, alpha=1/3)+
facet_wrap( ~ type, scale="free", ncol=2) + 
ggtitle("ALife Model Alpha Genotype Diff %") +  
theme_bw() +
theme(text = element_text(size = 9)))

dev.off()
