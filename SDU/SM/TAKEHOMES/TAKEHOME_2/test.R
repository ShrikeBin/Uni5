#| warning: false
load("Data_Assignment2_Ex1_E2025.rdata")
df <- cgm_data
df$adhesive_type <- as.factor(df$adhesive_type)

model <- glm(lifetime ~ skin_temp + humidity + activity_level +
                     sweat_rate + calibration_error + patient_bmi +
                     experience + adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

summary(model)

interaction_all <- glm(lifetime ~ (skin_temp + humidity + activity_level + 
                               sweat_rate + calibration_error + patient_bmi + experience) * adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

summary(interaction_all)

interaction <- glm(lifetime ~ skin_temp + humidity + activity_level + 
                               sweat_rate + calibration_error + patient_bmi + experience + patient_bmi * adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

summary(interaction)

tester <- step(glm(lifetime ~ skin_temp + humidity + activity_level +
                     sweat_rate + calibration_error +
                     experience + patient_bmi * adhesive_type,
                   data = df,
                   family = Gamma(link = "log")), trace=0)

final <-  glm(lifetime ~ skin_temp + humidity + activity_level +
                     calibration_error + patient_bmi * adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

alt <- glm(lifetime ~ skin_temp + humidity + activity_level +
                     calibration_error + patient_bmi + adhesive_type,
                   data = df,
                   family = Gamma(link = "log"))

summary(final)

summary(alt)

summary(tester)

anova(final, tester)


model_max <- glm(lifetime ~ (skin_temp + humidity + activity_level +
                     sweat_rate + calibration_error + patient_bmi +
                     experience + adhesive_type)^2,
                   data = df,
                   family = Gamma(link = "log"))

model_max_alt <- step(model_max, trace=0)

summary(model_max)

summary(model_max_alt)

etc <- glm(lifetime ~ skin_temp + humidity + activity_level + 
    sweat_rate + calibration_error + patient_bmi + experience + 
    adhesive_type + 
    humidity:experience +
    sweat_rate:calibration_error +
    patient_bmi:adhesive_type, family = Gamma(link = "log"), 
    data = df)


summary(final)


test <- glm(lifetime ~ skin_temp + humidity + activity_level +
                     sweat_rate + calibration_error + patient_bmi +
                     experience + adhesive_type,
                   data = df,
                   family = gaussian(link = "log"))

summary(test)