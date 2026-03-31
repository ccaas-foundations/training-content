#Errors vs Exceptions
#all exceptions are errors, but not all errors are exceptions

#Errors - problems that prevent the program from running correctly
#This is a syntax error, therefore there is no 'handling' this, instead we just have to fix it
#print 'this is an error'

#Exceptions - problems that are raised during runtime that can be detected and handled using 'try-except' blocks
# try:
#     x = int("Spaghetti")
# except ValueError:
#     print("Conversion has failed")

#Try-except-else-finally blocks
try:
    number = int(input("Please enter a number: "))
    result = 10 / number

except ValueError as ve:
    print("Invalid input, please enter a valid integer")

except ZeroDivisionError as zde:
    print("Cannot divide by zero")

#it is a good idea to have a generic catch-all exception block to handle anything we have not thought of
except Exception as e:
    print("I have no idea how you got here, you messed up big time:", e)

else:
    #runs only if NO exceptions occur
    print(f"Result: {result}")

finally:
    #runs no matter what, doesn't matter if an exception was raised or not
    print("Execution complete")


#we can use the raise keyword to manually throw(raise) an exception
# Y= -5
# if Y < 0:
#     raise Exception("Sorry, no negative numbers allowed")

#We can also create our own custom exceptions by creating a new custom exception class that inherits from the parent Exception class
class MyCustomException(Exception):
    def __init__(self, message = "This is not the exception you are looking for"):
        super().__init__(message)

user_number = input("Please enter an integer")
if not type(user_number) == int:
    raise MyCustomException("You did not enter an integer")