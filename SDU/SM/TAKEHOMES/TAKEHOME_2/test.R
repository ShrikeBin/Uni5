load("Data_Assignment2_Ex1_E2025.rdata")
df <- cgm_data
df$adhesive_type <- as.factor(df$adhesive_type)

model = glm(lifetime ~ skin_temp + patient_bmi + sweat_rate +
                     humidity + calibration_error + activity_level +
                     experience + adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

summary(model)

final_model <- glm(lifetime ~ skin_temp + humidity + activity_level +
                     calibration_error + adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))
                   
summary(final_model)