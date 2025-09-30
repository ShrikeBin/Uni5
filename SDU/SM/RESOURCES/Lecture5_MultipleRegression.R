setwd("C:/Users/bdebrabant/OneDrive - Syddansk Universitet/ARBEIT/IMADA/LEHRE/ST813_StatistiskModellering/E2025/Scripts")
data=(read.csv("rent99.raw", sep=" "))
attach(data)
head(data)
data$location=as.factor(data$location)
levels(data$location)=c("avg","good","top")

## basic description and scatter plot

library(dplyr)
library(tableone)
CreateTableOne(factorVars=c("location", "bath", "kitchen", "cheating"),
               data=data%>%select(-district))

plot(data[,1:4])

## multiple regression of rent onto area and yearc

fit=lm(rent~area+I(yearc-1956),data=data)
summary(fit)

## polynomial regression / quadratic effects

fit.2=lm(rent~area+I(yearc-1956)+I((yearc-1956)^2),data=data)
summary(fit.2)

# plotting model estimates with basic plotting tools
summary(data$area)

with(data,plot(rent~yearc))

H=predict(fit.2, newdata= 
            data.frame(expand.grid(yearc=1911:2000, area=c(20,51,65,81,160))))
for (i in 0:4){
lines(1911:2000,H[i*90+1:90], lwd=3, col=i+2)
}
legend(1930,2000, legend=c("20m2","51m2","65m2","81m2","160m2"), fill=c(2:6))


## R2 depends on range of X

fit=lm(rent~area+I(yearc-1956),data=data)
S=summary(fit)

# regression on subset with medium areas only
fit.3=lm(rent~area+I(yearc-1956) ,data=data%>% filter( area>51, area<81))
S.3=summary(fit.3)

with(data,plot(area, rent))
abline(v=c(51,81), lwd=2, col=1)
abline(c(97.42, 5.36), lwd=2, col=2)
abline(c(146.56, 4.63), lwd=2, col=4)

# residual SE = sigma
round(S$sigma,2)
round(S.3$sigma,2)

# R2
round(S$r.squared,2)
round(S.3$r.squared,2)

# estimated beta's
round(coef(fit),2)
round(coef(fit.3),2)

