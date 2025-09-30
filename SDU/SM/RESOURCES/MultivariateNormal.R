# Load necessary library
library(MASS)
library(mvtnorm)
# Define mean vector and covariance matrix
mu <- c(0, 0)  # Mean
sigma <- matrix(c(1, -0.5, -.5, 1), nrow = 2)  # Covariance matrix

# Create a grid of x and y values
x <- seq(-3, 3, length.out = 100)
y <- seq(-3, 3, length.out = 100)
grid <- expand.grid(x = x, y = y)

# Compute the density of the bivariate normal distribution
z <- mvrnorm(n = 1, mu = mu, Sigma = sigma)
density <- dmvnorm(grid, mean = mu, sigma = sigma)

# Reshape density for contour plotting
z_matrix <- matrix(density, nrow = length(x), ncol = length(y))

# Plot the contour
contour(x, y, z_matrix, main = "Contour Plot of Bivariate Normal Distribution",
        xlab = "X", ylab = "Y")
