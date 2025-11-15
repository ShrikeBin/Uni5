#!/usr/bin/python3
from mip import Model, xsum, BINARY, CONTINUOUS, maximize
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

        self.av_r = np.mean(self.r, axis=1)
        self.min_r = np.max([0,np.min(self.av_r)])
        self.max_r = np.max(self.av_r)
        print(self.min_r,self.max_r)


def solve(data, epsilon):
    m = Model(sense=maximize)

    # Threshold B
    B = data.min_r + (epsilon/100)*(data.max_r - data.min_r)

    ######### BEGIN: Write your model here

    ######### END

    status = m.optimize()

    # Return B and objective value
    if status == None:  # sometimes MIP returns None if infeasible
        return B, 0
    return B, m.objective_value



def main(argv):
    if len(argv) != 1:
        usage()
    instance = Data(argv[0])

    epsilons = np.arange(0,101,10) #np.array([100]) #
    rewards = np.zeros_like(epsilons,dtype=np.float64) 
    risks = np.zeros_like(epsilons,dtype=np.float64)   
    i=0
    for epsilon in epsilons:
        print(f"=== Solve for epsilon = {epsilon}")
        rewards[i], risks[i] = solve(instance, epsilon)
        i+=1
    print(rewards,risks)
    plt.plot(rewards, risks, '-o')
    plt.ylabel('risks')
    plt.xlabel('rewards')
    plt.show()

def usage():
    print("Reads data from datafilename")
    print("Usage: [\"datafilename\"]\n")
    raise SystemExit


if __name__ == "__main__":
    main(sys.argv[1:])