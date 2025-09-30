x=-500:500/100

plot(x,dnorm(x), type="l", lwd=4)
lines(x,dt(x, df=2), lwd=2, col=2)
lines(x,dt(x, df=5), lwd=2, col=3)
lines(x,dt(x, df=10), lwd=2, col=4)
lines(x,dt(x, df=60), lwd=2, col=5)