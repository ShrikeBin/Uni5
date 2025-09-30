### Load Dataset
setwd("C:/Users/bdebrabant/OneDrive - Syddansk Universitet/ARBEIT/IMADA/LEHRE/ST813_StatistiskModellering/E2025/Scripts")
data=(read.csv("rent99.raw", sep=" "))
attach(data)
head(data)
data$location=as.factor(data$location)
levels(data$location)=c("avg","good","top")

## multiple regression of rent onto area and yearc

fit=lm(rent~area+I(yearc-1956),data=data)
S=summary(fit)
S

## Confidence interval for effect of area onto rent

# calculation of SE:
X=model.matrix(fit)
n=nrow(X)
p=ncol(X)
sigma2=sum(fit$residuals^2)/(n-p)
sqrt(sigma2)

beta=coef(fit)[2]  # area-effect  (corresponds to c=(0,1,0)')
SE=sqrt(vcov(fit)[2,2])  # SE of area-effect
# SE=sqrt(solve(t(X)%*%X)[2,2]*sigma2)  # alternatively

# 95% CI (here: assume sigma2 is known)
qnorm(0.975)
qnorm(0.025)

beta+qnorm(0.025)*SE
beta+qnorm(0.975)*SE

# comparison with built-in method
confint(fit)




######
## pointwise confidence bands

set.seed(123)
data.0=data%>%sample_n(20) # smaller dataset for illustration
fit.0=lm(rent~area,data=data.0)

ci <- predict(fit.0, interval = "confidence",
              level = 0.95)

library(ggplot2)
ggplot(data = data.0, aes(x = area, y = rent)) +
  geom_point() +
  geom_smooth(method = "lm", se = FALSE) +
  geom_ribbon(aes(ymin = ci[, 2], ymax = ci[, 3]),
              alpha = 0.2)