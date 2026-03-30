# #control flow statements in Python

# #For

# LoopPets = ["Fluffy", "Daisy", "Ash", "Wonton the Gray"]

# #Loop through a collection of items
# #for x in collection: do something
# for pet in LoopPets:
#     print(pet)
#     print("I love ", pet)

# print(pet) #this is outside the loop

# #while

# #good for execution while a condition is true
# count = 0
# while(count < 5):
#     print("From the while loop")
#     count += 1 #this is the same as count = count +1

# #Note is that we can nest loops and mix and match them
# #BUT, it you find yourself using 3 or more loops deep, there is likely a better way

# #if-else

# #we can check conditions and execute a block of code if it is true and another if it is false
# #Be careful with indetation in Python, it is how we define blocks of code
# #ALWAYS CHECK INDENTATIONS

# cheese = 3

# if cheese > 5:
#     print("Cheese is the 'grate-est'!")
# else:
#     print("Cheese is not the 'grate-est' :(")

# #We can also check multiple conditions using elif (else if)
# score = 25

# if score >= 90:
#     print("Grade A")
# elif score >= 80:
#     print("Grade B")
# elif score >= 70:
#     print("Grade C")
# elif score >= 60:
#     print("Grade D")
# else: #else catches anything that is not caught by the above conditions
#     print("Grade F")

# #Shorthand if
# a = 50
# b = 11
# if a > b: print("a is greater than b")

# #shorthand if-else
# print("a is greater than b") if a > b else print("a is not greater than b")

# #multiple conditions using logical operators
# c = 70
# if a > b and c > a:
#     print("a is greater than b and c is greater than a")

# if a > b or c > a:
#     print("a is greater than b or c is greater than a")

# if not a > b:
#     print("a is not greater than b")


# #Match-case (Python 3.10+) and is similar to switch statements in java

# choice = input("select a number between 1 and 3")
# match choice:
#     case "1":
#         print("You selected 1")
#     case "2":
#         print("You selected 2")
#     case "3":
#         print("You selected 3")
#     case _: #the underscore is a wildcard that matches anything that hasn't been matched by the above cases
#         print("You did not follow directions")


#Functions
# functions are how we package useful functionality into reusable blocks to use when needed

#creating a function in python, we use the 'def' keyword followed by the function name and parentheses
#function with 2 parameters
def addition(x:int, y:int): #this is for readability, not for defining functionality, it is called type hinting
    return x + y

#No parameters function
def bark():
    print("woof!")

#calling functions
sum = addition(5, 10)
print(sum)
bark()

#puttng strings instead of integers in the addition function will output strings
print(addition("Pink", "Floyd"))

#Scopes
#a scope is a section/area of code where an object/variable/function can be called upon and used

#Local: variables declared inside a block of code are only available within that block
def local_variable():
    msg = "Hi y'all" #local variable
    print(msg)
    #Enclosed scope: variables declared in an enclosing function are available in nested functions
    def nested_function():
        #print(msg) 
        msg = "Enclosed hi Yall"
        print(msg)
    nested_function()
    print(msg)

local_variable()

#Global scope: item can be accessed anywhere in the file they are delcared in
#As well as in other files if imported

#built-in: default python methods and all keywords live here and can be accessed anywhere