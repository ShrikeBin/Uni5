# script.R
data <- read.csv("HearingThresholds.csv", header = TRUE)
print("data type: ")
print(class(data))
data$avg1k = (data$l1k + data$r1k)/2
data$left = (data$l500 + data$l1k + data$l2k + data$l3k + data$l4k + data$l6k + data$l8k)/7
data$right = (data$r500 + data$r1k + data$r2k + data$r3k + data$r4k + data$r6k + data$r8k)/7
data$avg = (data$right + data$left)/2

hist(data$avg, breaks = seq(from = 0, to = 90, by = 5),
xlab = "Hearing threshold (dB)", ylab = "Frequency",
main = "Distribution of hearing levels")

