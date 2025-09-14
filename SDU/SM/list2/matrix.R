
# 3.1
A <- matrix(c(-1, 4,
               3, 2), 
               nrow = 2, ncol = 2, byrow = FALSE)

B <- matrix(c(4, 1, -2,
              -3, -2, 0), 
              nrow = 3, ncol = 2, byrow = FALSE)

C <- matrix(c(5, -4, 2), 
              nrow = 3, ncol = 1, byrow = FALSE)

cat("TASK 3.1\n")
cat("a):\n")
print(5 * A)
cat("\n")

cat("b):\n")
print(B %*% A)
cat("\n")

cat("c):\n")
print(t(A) %*% t(B))
cat("\n")

cat("d):\n")
print(t(C) %*% B)
cat("\n")

cat("e):\n")
print((solve(t(B) %*% B)) %*% t(B) %*% C)
cat("\n")

# 3.2

Q <- matrix(c(2/3, 1/3, 2/3,   # first column
              -2/3, 2/3, 1/3,  # second column
              1/3, 2/3, -2/3), # third column
            nrow = 3, ncol = 3, byrow = FALSE)

invQ <- solve(Q)
transQ <- t(Q)

cat("TASK 3.2:\n")
cat("invQ:\n")
print(invQ)
cat("\ntransQ:\n")
print(transQ)
cat("\n all.equal(invQ, transQ):\n")
print(all.equal(invQ, transQ))
cat("\n")


# 3.3

cat("TASK 3.3:\n")

# vectors
x1 <- c(4, 0, 0, 1)
x2 <- c(3, 1, 0, -1)

# Gram-Schmidt (zerżnięte z neta)
u1 = x1 * (1/sqrt(sum(x1^2)))
x2_hat = u1 * (sum(x2 * u1))
e2 = x2 - x2_hat
u2 = e2 * (1/sqrt(sum(e2^2)))

# results
cat("\nu1:\n"); print(u1)
cat("u2:\n"); print(u2)

# checks
cat("\nDot product u1 * u2 (should be ~ 0): ", sum(u1 * u2), "\n")
cat("Norm of u1: ", sqrt(sum(u1^2)), "\n")
cat("Norm of u2: ", sqrt(sum(u2^2)), "\n")

library(pracma)
GS <- gramSchmidt(cbind(x1, x2)) # column bind
cat("\npracma gramSchmidt result (columns are orthonormal vectors):\n")
print(GS$Q)


U = cbind(u1, u2)
cat("\nU matrix (columns are orthonormal vectors):\n")
print(U)

y = c(1, 1, 1, 1)
cat("\ny vector:\n")
print(y)
cat("\nProjection of y onto the space spanned by u1 and u2:\n")
proj_y = U %*% (t(U) %*% y) # U^T (U y) - idk why it works
print(proj_y)

# 3.4

cat("\nTASK 3.4:\n")