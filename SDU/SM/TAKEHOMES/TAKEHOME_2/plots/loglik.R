g = function(y, alpha)
{
  out <- numeric(length(y))

  # case 1: y >= 0, alpha != 0
  in_1 = (y >= 0 & alpha != 0)
  out[in_1] = (((y[in_1] + 1)^(alpha - 1)) / alpha)

  # case 2: y >= 0, alpha == 0
  in_2 = (y >= 0 & alpha == 0)
  out[in_2] = log(y[in_2] + 1)

  # case 3: y < 0, alpha != 2
  in_3 = (y < 0 & alpha != 2)
  out[in_3] = ( - ((((-y[in_3] + 1)^(2 - alpha)) - 1) / (2 - alpha)))

  # case 4: y < 0, alpha == 2
  in_4 = (y < 0 & alpha == 2)
  out[in_4] = ( - log(-y[in_4] + 1))

  return(out)
}

g_prim = function(y, alpha)
{
  out <- numeric(length(y))

  # case 1: y >= 0
  in_1 = (y >= 0)
  out[in_1] = (y[in_1] + 1)^(alpha - 1)

  # case 2: y < 0
  in_2 = (y < 0)
  out[in_2] = (1 - y[in_2])^(1 - alpha)

  return(out)
}

profile_log_likelihood = function(Y, X, alpha)
{
  n = length(Y)

  # transformed Y
  g_Y <- g(Y, alpha)

  # B_hat estimate
  B_hat = solve(t(X) %*% X) %*% t(X) %*% g_Y

  # Residual Sum of Squares
  RSS = sum((g_Y - X %*% B_hat)^2)

  # Constants
  constants = (-n/2)*log(2*pi) - (n/2)

  # profile log likehood = -n/2 log(sigma_hat^2) + sum of log(g_prim(y)) + constants
  out = (-(n/2))*log((1/n) * RSS) + sum(log(g_prim(Y, alpha))) + constants

  return(out)
}

load("../Data_Assignment2_Ex2_E2025.rdata")
df_x <- cbind(1, x)
df_y <- Y


out <- optim(
  par = 0,
  fn = function(a) {-profile_log_likelihood(df_y, df_x, a)}
)

lvec <- out$par + seq(-0.1, 0.1, 0.001)
likelam <- numeric(length(lvec))

for (i in seq_along(lvec)) 
{
  likelam[i] <- profile_log_likelihood(df_y, df_x, lvec[i])
}

library(ggplot2)

df_plot <- data.frame(
  alpha = lvec,
  logLik = likelam
)

png("Loglik.png", width=900, height=600, res=150)
ggplot(df_plot, aes(x = alpha, y = logLik)) +
  geom_point(color = "red", size = 0.7) +
  geom_vline(
    xintercept = out$par,
    linetype = "dashed",
    linewidth = 0.3
  ) +
  labs(
    x = expression(alpha),
    y = "Profile log-likelihood"
  ) +
  theme_minimal(base_size = 10)
dev.off()
