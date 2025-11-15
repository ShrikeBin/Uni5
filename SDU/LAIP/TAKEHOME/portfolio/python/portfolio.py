#!/usr/bin/python3
from mip import Model, xsum, CONTINUOUS, MINIMIZE
import matplotlib.pyplot as plt
import sys
import numpy as np

class Data:
    def __init__(self, filename):
        with open(filename, "r") as filehandle:
            lines=filehandle.readlines()
        
        self.N = int(lines[0].strip("\r\n"))
        self.T = int(lines[1].strip("\r\n"))
        self.r = np.zeros([self.N, self.T])

        for line in lines[2:]:
            line=line.strip("\r\n");
            parts=line.split(" ");
            j=int(parts[0])-1
            t=int(parts[1])-1
            val=float(parts[2])
            self.r[j,t]=val

        self.mean_r = np.mean(self.r, axis=1)
        self.c = np.mean(self.r - self.mean_r[:, None], axis=1)
        self.min_r = np.max([0, np.min(self.mean_r)])
        self.max_r = np.max(self.mean_r)
        
        print("min_r:", self.min_r, "max_r:", self.max_r)


def solve(data, epsilon):
    m = Model("portfolio", sense=MINIMIZE)

    # Threshold B
    B = data.min_r + (epsilon/100)*(data.max_r - data.min_r)

    N = data.N
    R_hat = data.mean_r
    c = data.c

    # Variables x_j >= 0
    x = [m.add_var(var_type=CONTINUOUS, lb=0) for j in range(N)]

    # Objective function
    m.objective = xsum(c[j]*x[j] for j in range(N))

    # Constraint 1
    m += xsum(R_hat[j]*x[j] for j in range(N)) >= B

    # Constraint 2 (all money)
    m += xsum(x[j] for j in range(N)) == 1

    status = m.optimize()

    x_values = [x[j].x for j in range(N)]
     
    if status == None:
        return B, 0
    return B, m.objective_value, x_values



def main(argv):
    if len(argv) != 1:
        usage()
    instance = Data(argv[0])
    portfolio_info = []

    epsilons = np.arange(0, 101, 10)
    rewards = np.zeros_like(epsilons, dtype=np.float64)
    risks = np.zeros_like(epsilons, dtype=np.float64)

    for i, epsilon in enumerate(epsilons):
        print(f"=== Solve for epsilon = {epsilon}")
        B_val, obj_val, x_values = solve(instance, epsilon)

        rewards[i] = B_val
        risks[i] = obj_val

        positive_x = [(j+1, x) for j, x in enumerate(x_values) if x > 0]

        portfolio_info.append({
            "epsilon": epsilon,
            "reward": B_val,
            "risk": obj_val,
            "allocations": positive_x
        })

    plt.figure(figsize=(10,6))
    plt.plot(rewards, risks, '-o')
    plt.xlabel("Min Reward")
    plt.ylabel("Associated Min Risk")
    plt.title("Portfolio")
    plt.grid(True)
    
    plt.figure(figsize=(6, 10))
    plt.axis("off")
    text_str = ""
    for info in portfolio_info:
        epsilon = info['epsilon']
        positive_x = info['allocations']
        alloc_str = ", ".join([f"x_{j} = {x:.4f}" for j, x in positive_x])
        text_str += f"\nε = {epsilon}: \n{alloc_str}\n"

    plt.text(0, 1, text_str, fontsize=12, va='top', ha='left', wrap=True)

    plt.tight_layout()
    plt.show()

def usage():
    print("Reads data from datafilename")
    print("Usage: [\"datafilename\"]\n")
    raise SystemExit


if __name__ == "__main__":
    main(sys.argv[1:])