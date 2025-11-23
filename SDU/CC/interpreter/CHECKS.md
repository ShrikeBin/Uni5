# ==== TODO - check these ====

    Scoping:
  • check that variables are declared only once in a scope and initialized
  • the global scope allows for out-of-order definitions of functions and their calls
  • check that variables are declared only once in a scope and initialized

    Variables:
  • cannot be accessed from scopes outside where they have been defined.
  • cannot be redefined in a nested scope; i.e., a variable cannot shadow
    a variable from an outer scope unless defined for the first time in a
    function or as a parameter.
  • can only be re-defined inside a function
  • ensure that a declared variable is also initialized with a value (no default
    values)

    Functions:
  • the name of a function must be unique (no method overloading, no name-
    clash with a variable are allowed)
  • check that the number of actual function parameters matches the number
    of declared parameters
  • **functions can only access local variables and the parameter values (call**
    **by value, not by reference); they cannot access any variable outside the**
    **function.**
  • ensure that only known function names are called, e.g., true() is wrong
    even though it is syntactically correct
  • **check that function statements are only introduced globally (not in Blocks)**
  • check that a return statement occurs only inside a function statement
  • check that the return statement is the last executed statement in branches
    of function bodies; i.e., check that there are no other statements after the
    return in a branch.
  • check that a function with a non-void return type returns the correct type
  • check that the type of the actual function parameters match the declared
    types

    Types:
  • check that a declared variable is initialized with a value of the correct type
  • check that each new assignment to a variable matches the type of the
    variable
  • the conditions of if- and while-statements must be of type Boolean
  • **the write to console-statement expects to be followed by a String**
  • Boolean operations can only be performed on Boolean operands.
  • check that arithmetic operations are performed on Numbers only



### Other:

*Please note, I made two small changes to the assignment description and the test-files:*

    *the print-statement “write_to_console” may print any type (it was stated before differently in the assignment3 description)
    in the previous test-files was a bug in Fibonacci-proc.in (one of the functions  accessed a global variable which is forbidden and should result in a scope error)*

*Please continue working with this latest state.*


*The “template” assingment3_tests.zip contains the test-directory that you can use to replace your current test-directory in the Maven project. Watch out to merge it with the test classes that you have written already on your own.*

### Questions:

- **functions cannot use counters defined outside? (global var's)**
- **functions only global?**
- **do we print ONLY strings?**
- *A variable cannot be redefined in a nested scope; i.e., a variable cannot shadow*
  *a variable from an outer scope unless defined for the first time in a*
  *function or as a parameter.* -> what do we mean by that?

# this fine
var a = 10
{
  a = 11
}

# this no fine
var a = 10
{
  var a = 11
}
