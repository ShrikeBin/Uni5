load("../Data_Assignment2_Ex1_E2025.rdata")
df <- cgm_data
summary(df[, sapply(df, is.numeric)])
table(df$adhesive_type)

library(ggplot2)
numeric_vars <- c("skin_temp", "humidity", "activity_level",
                  "sweat_rate", "calibration_error", "patient_bmi", "experience")

for (var in numeric_vars) {
  # specify file name
  file_name <- paste0("Lifetime_vs_", var, ".png")
  
  # open PNG device
  png(filename = file_name, width = 900, height = 600, res = 150)  # width/height in pixels, res=150dpi
  
  # create plot
  p <- ggplot(df, aes(x = .data[[var]], y = lifetime)) +
    geom_point(size = 2, alpha = 0.7) +
    geom_smooth(method = "lm", se = FALSE, color = "red") +
    theme_minimal() +
    labs(title = paste("Lifetime vs", var),
         x = var,
         y = "Lifetime")
  
  print(p)    # must print inside png device
  
  dev.off()   # close the device to actually write the file
}

df$adhesive_type <- as.factor(df$adhesive_type)

gamma_model_log <- glm(lifetime ~ skin_temp + humidity + activity_level +
                     sweat_rate + calibration_error + patient_bmi +
                     experience + adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))
gamma_model_canon <- glm(lifetime ~ skin_temp + humidity + activity_level +
                         sweat_rate + calibration_error + patient_bmi +
                         experience + adhesive_type,
                       data = df,
                       family = Gamma(link = "inverse"))
inv_gauss_model_log <- glm(lifetime ~ skin_temp + humidity + activity_level +
                         sweat_rate + calibration_error + patient_bmi +
                         experience + adhesive_type,
                       data = df,
                       family = inverse.gaussian(link = "log"))
models <- list(
  Gamma_Log = gamma_model_log,
  Gamma_Canonical= gamma_model_canon,
  Inv_Gaussian_Log = inv_gauss_model_log
)

for (name in names(models)) {
  print(summary(models[[name]]))
}

aic_values <- sapply(models, AIC)
print(sort(aic_values))

resid_dev <- sapply(models, function(m) if("deviance" %in% names(m)) m$deviance else NA)
print(sort(resid_dev))

final_model <- glm(lifetime ~ skin_temp + humidity + activity_level +
                   calibration_error + patient_bmi * adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))
summary_table <- summary(final_model)$coefficients
round(summary_table, 4)

mean_skin_temp <- mean(df$skin_temp)
mean_humidity <- mean(df$humidity)
mean_cal_error <- mean(df$calibration_error)

pred <- coef(final_model)["(Intercept)"] +
       coef(final_model)["skin_temp"] * mean_skin_temp +
       coef(final_model)["humidity"] * mean_humidity +
       coef(final_model)["activity_level"] * 4 +
       coef(final_model)["calibration_error"] * mean_cal_error +
       coef(final_model)["patient_bmi"] * 30
       
print(exp(pred))

fitted_value <- exp(pred)
fitted_value


deviance(final_model)
AIC(final_model)


summary(final_model)$dispersion



library(ggplot2)

# Means for numeric predictors
mean_skin_temp <- mean(df$skin_temp)
mean_humidity <- mean(df$humidity)
mean_activity <- mean(df$activity_level)
mean_cal_error <- mean(df$calibration_error)
mean_patient_bmi <- mean(df$patient_bmi)

# Reference adhesive
ref_adhesive <- levels(df$adhesive_type)[1]

# --- 1. Boxplot: adhesive_type ---
png("Lifetime_vs_adhesive_type.png", width=900, height=600, res=150)
ggplot(df, aes(x = adhesive_type, y = lifetime)) +
  geom_boxplot(fill = "red") +
  theme_minimal() +
  labs(title = "Sensor Lifetime by Adhesive Type")
dev.off()

# --- 2. Predicted Lifetime vs Skin Temp ---
pred_skin <- data.frame(
  skin_temp = seq(min(df$skin_temp), max(df$skin_temp), length.out = 100),
  humidity = mean_humidity,
  activity_level = mean_activity,
  calibration_error = mean_cal_error,
  patient_bmi = mean_patient_bmi,
  adhesive_type = ref_adhesive
)
pred_skin$pred_lifetime <- predict(final_model, newdata = pred_skin, type = "response")

png("Predicted_Lifetime_vs_SkinTemp.png", width=900, height=600, res=150)
ggplot(pred_skin, aes(x = skin_temp, y = pred_lifetime)) +
  geom_line(color="brown") +
  geom_point(data = subset(df, lifetime <= 15), aes(x = skin_temp, y = lifetime), alpha = 0.1) +
  theme_minimal() +
  labs(title="Predicted Lifetime vs Skin Temp", x="Skin Temp", y="Predicted Lifetime")
dev.off()

# --- 3. Predicted Lifetime vs Humidity ---
pred_hum <- data.frame(
  humidity = seq(min(df$humidity), max(df$humidity), length.out = 100),
  skin_temp = mean_skin_temp,
  activity_level = mean_activity,
  calibration_error = mean_cal_error,
  patient_bmi = mean_patient_bmi,
  adhesive_type = ref_adhesive
)
pred_hum$pred_lifetime <- predict(final_model, newdata = pred_hum, type = "response")

png("Predicted_Lifetime_vs_Humidity.png", width=900, height=600, res=150)
ggplot(pred_hum, aes(x = humidity, y = pred_lifetime)) +
  geom_line(color="green") +
  geom_point(data = subset(df, lifetime <= 15), aes(x = humidity, y = lifetime), alpha = 0.1) +
  theme_minimal() +
  labs(title="Predicted Lifetime vs Humidity", x="Humidity", y="Predicted Lifetime")
dev.off()

# --- 4. Predicted Lifetime vs Activity Level ---
pred_act <- data.frame(
  activity_level = seq(min(df$activity_level), max(df$activity_level), length.out = 100),
  skin_temp = mean_skin_temp,
  humidity = mean_humidity,
  calibration_error = mean_cal_error,
  patient_bmi = mean_patient_bmi,
  adhesive_type = ref_adhesive
)
pred_act$pred_lifetime <- predict(final_model, newdata = pred_act, type = "response")

png("Predicted_Lifetime_vs_Activity.png", width=900, height=600, res=150)
ggplot(pred_act, aes(x = activity_level, y = pred_lifetime)) +
  geom_line(color="red") +
  geom_point(data = subset(df, lifetime <= 15), aes(x = activity_level, y = lifetime), alpha = 0.1) +
  theme_minimal() +
  labs(title="Predicted Lifetime vs Activity Level", x="Activity Level", y="Predicted Lifetime")
dev.off()

# --- 5. Predicted Lifetime vs Calibration Error ---
pred_cal <- data.frame(
  calibration_error = seq(min(df$calibration_error), max(df$calibration_error), length.out = 100),
  skin_temp = mean_skin_temp,
  humidity = mean_humidity,
  activity_level = mean_activity,
  patient_bmi = mean_patient_bmi,
  adhesive_type = ref_adhesive
)
pred_cal$pred_lifetime <- predict(final_model, newdata = pred_cal, type = "response")

png("Predicted_Lifetime_vs_CalibrationError.png", width=900, height=600, res=150)
ggplot(pred_cal, aes(x = calibration_error, y = pred_lifetime)) +
  geom_line(color="purple") +
  geom_point(data = subset(df, lifetime <= 15), aes(x = calibration_error, y = lifetime), alpha = 0.1) +
  theme_minimal() +
  labs(title="Predicted Lifetime vs Calibration Error", x="Calibration Error", y="Predicted Lifetime")
dev.off()

# --- Predicted Lifetime vs BMI (3 adhesive curves) ---

pred_bmi <- expand.grid(
  patient_bmi = seq(min(df$patient_bmi),
                    max(df$patient_bmi),
                    length.out = 100),
  adhesive_type = levels(df$adhesive_type)
)

pred_bmi$skin_temp <- mean_skin_temp
pred_bmi$humidity <- mean_humidity
pred_bmi$activity_level <- mean_activity
pred_bmi$calibration_error <- mean_cal_error


pred_bmi$pred_lifetime <- predict(
  final_model,
  newdata = pred_bmi,
  type = "response"
)

png("Predicted_Lifetime_vs_AdhesvieAndBMI.png",
    width = 900, height = 600, res = 150)

ggplot(pred_bmi,
       aes(x = patient_bmi,
           y = pred_lifetime,
           color = adhesive_type)) +
  geom_line(linewidth = 0.7) +
  geom_point(
    data = subset(df, lifetime <= 15),
    aes(x = patient_bmi,
        y = lifetime),
    alpha = 0.15,
    inherit.aes = FALSE
  ) +
  theme_minimal() +
  labs(
    title = "Predicted Lifetime vs Patient BMI by Adhesive Type",
    x = "Patient BMI",
    y = "Lifetime",
    color = "Adhesive type"
  )


dev.off()


png("Deviance_vs_Fitted.png", width=900, height=600, res=150)
res <- residuals(final_model, type = "deviance")
fit <- fitted(final_model)
plot(fit, res,
     xlab = "Fitted values",
     ylab = "Deviance residuals",
     main = "Deviance residuals vs Fitted")
abline(h = 0, col = "red")
dev.off()

aux_model = gamma_model_log 

pred_df <- data.frame(
  skin_temp = mean(df$skin_temp),
  humidity = mean(df$humidity),
  activity_level = mean(df$activity_level),
  sweat_rate = mean(df$sweat_rate),
  calibration_error = mean(df$calibration_error),
  patient_bmi = mean(df$patient_bmi),
  adhesive_type = levels(df$adhesive_type)[1],  # choose reference
  experience = seq(min(df$experience), max(df$experience), length.out = 100)
)

# Predict using aux_model
pred_df$pred_lifetime <- predict(aux_model, newdata = pred_df, type = "response")

# Save plot to PNG
png("AUX_Predicted_Lifetime_vs_Experience.png", width = 900, height = 600, res = 150)

ggplot(pred_df, aes(x = experience, y = pred_lifetime)) +
  geom_line(color = "blue") +
  geom_point(data = subset(df, lifetime <= 15), aes(x = experience, y = lifetime), alpha = 0.1) +
  theme_minimal() +
  labs(
    title = "Predicted Sensor Lifetime vs Experience",
    x = "Experience (years)",
    y = "Predicted Lifetime (days)"
  )

dev.off()

