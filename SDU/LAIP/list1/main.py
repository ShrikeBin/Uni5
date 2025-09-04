import numpy as np

# TASK 1
# A total of $6,300 was invested in two accounts. Part was invested in obligations at 0.045 annual interest
# rate and part was invested in a money market fund at 0.0375 annual interest rate. If the total simple
# interest for one year was $267.75, then how much was invested in each account?

def task1():
    # x + y = 6300
    # 0.045*x + 0.0375*y = 267.75
    # interest = 267.75

    A = np.array([[1,1], 
                  [0.045, 0.0375]])
    B = np.array([6300, 267.75])
    
    X = np.linalg.solve(A, B)
    print(f"Task 1: {X[0]:.4f} in obligations and {X[1]:.4f} in money market fund")
    
    
# TASK 2
# A construction company produces five different products: p1, p2, p3, p4, p5, using five resources: metal,
# concrete, plastic, water, electricity.
# The amount of resources consumed for producing one unit of products is described by the following
# python dictionaries:

p_1={"metal":0, "concrete":1.3, "plastic":0.2, "water":.8, "electricity":.4}
p_2={"metal":0, "concrete":0, "plastic":1.5, "water":.4, "electricity":.3}
p_3={"metal":.25, "concrete":0, "plastic":0, "water":.2, "electricity":.7}
p_4={"metal":0, "concrete":0, "plastic":.3, "water":.7, "electricity":.5}
p_5={"metal":1.5, "concrete":0, "plastic":.5, "water":.4, "electricity":.8}

# How much metal is consumed if the company decides to produce the following quantities for each
# product: 10, 9, 12, 13, 11?
# Express the calculation asked by the exercise as a linear algebra operation involving matrices and
# vectors. Then calculate the numerical answer using Python and numpy. (For example, you can transform
# the dictionary above into a numpy array as follows: a=np.array(list(p_1.values())).)

def task2():
    # matrix of resources per product
    A = np.array([list(p_1.values()),
                list(p_2.values()),
                list(p_3.values()),
                list(p_4.values()),
                list(p_5.values())])
    print(A)
    # vector of quantities produced
    X = np.array([10,9,12,13,11])
    # total resources consumed by each resource
    B = X.T @ A 
    print(f"Task 2: Total metal consumed:\n {B[0]:.4f} units")


# TASK 3

# Consider again our construction company that produces five different products: p1, p2, p3, p4, p5, using
# five resources: metal, concrete, plastic, water, electricity.
# The amount of resources consumed for producing one unit of products is described by the python
# dictionaries defined in the previous exercise.
# Suppose now that you do not know how much of each product is produced but that you know how
# much of each material is used. Could you determine from this information the amount produced of each
# product? Will a solution exist? Will it be unique?

def task3():
    A = np.array([list(p_1.values()),
            list(p_2.values()),
            list(p_3.values()),
            list(p_4.values()),
            list(p_5.values())])
    
    print("Task 3: Supposing now that I do not know how much of each product is produced but I know how much of each material is used: \n \
          1. Could you determine from this information the amount produced of each product? \n \
          2. Will a solution exist? \n \
          3. Will it be unique?")
    print("If det(A) given A = matrix of p_i i = 1..n is not 0, then a unique solution exists.")
    print("If det(A) = 0, then: \n \
          1. no solution - after reduction of lineary dependent rows, \n \
             the solution vector B isnt to be found in the new space spanned by the rows of A. \n \
          2. infinite solutions - after reduction of lineary dependent rows,\n \
             the solution vector B is to be found in the new space spanned by the rows of A. \n \
             Depending on the number k of lineary dependent rows, there will an k dimensional space of solutions.")
    print(f"det(A) = {np.linalg.det(A):.4f}")

task3()