# print("Hello, World!")

# #Datatypes-implicitly defined (typically) but can be explicitly defined as well

# #num
# #Integer - whole number
# x = 4
# explicitInt:int = 6


# #Float - decimal number
# y = 4.5
# explicitFloat:float = 6.2

# #booleans - true/false
# MyBool = True
# explicitBool:bool = False
# #truthy/falsey values - values that evaluate to true or false in a boolean context
# #Falsey - empty strings, zero, empty lists, None, False, Empty collection(dictionaries, lists, sets, tuples)
# #truthy - non-empty strings, non-zero numbers, non-empty lists, everything else

# #Strings
# MyString = "This is my favorite string in the world"
# MySingleQuoteString = 'This is another string'
# explicitString:str = "This is an explicitly defined string"

# #Nonetype - represents the absence of a value
# MyNullValue = None
# #useful for testing purposes, not often used


# #By default, print statements are ended by a new line
# print(x) ; print(y)
# print(MyString)

# #we can do math in our print statements
# print(x + y)

# #we can also mix data types
# print("I have ", x, "Cats")

# #Variables can be changed after they have been set
# x = "My Favorite Color"
# print(x)

# #Casting lets us specify the data type of a variable and perform type conversion
# #be careful when casting because we can have data loss (converting to an int from a float will drop the decimals)
# a = str(9) #a will be '9'
# b = int(9) #b will be 9
# c = float(0) #c will be 9.0
# print(a, b, c)

# #the type() function tells us what datatype a variable is
# print(type(a)) #<class 'str'>
# print(type(b)) #<class 'int'>

# #Variables are case sensitive
# A = "This is my capital A variable"
# print(a, A)

# #we can assign multiple variables in one line
# X, Y, Z = "Pink", "Green", "Blue"
# print(X, Y, Z)

# #we can also assign the same value to multiple variables in one line
# dog = DOG = Dog = "Beagle" #DOG is a constant value by naming convention
# print(dog, DOG, Dog)


# #collections - variables that hold multiple values
# #Lists, tuples, sets, dictionaries

# """
# Lists - collections that are ordered, changeable, and allow duplicate values defined with square brackets []
# Tuples - collections that are ordered, UNchangeable, and allow duplicate values defined with parentheses ()
# Sets - collections that are UNordered, changeable, and do NOT allow duplicate values defined with curly braces {}
# Dictionaries - collections that are ordered, changeable, and do NOT allow duplicate values defined with curly braces {} and use Key:Value pairs
# """

# #lists - python sees lists as objects with the data type 'list'
# #can contain different data types
# Fruits = ["Orange", "Strawberry", "Apples"]
# Or, St, Ap = Fruits
# print(Fruits)
# print(Or, Ap)

# #len() determines the length of a collection
# print(len(Fruits))

# #we can access items in a list using indexing
# print(Fruits[2])
# #we can also use negative indexing to access items from the end of the list
# print(Fruits[-1])
# #a range of indexes
# print(Fruits[0:2])

# #append items to the end of a list
# Fruits.append("bananas")
# print(Fruits)
# #we can also add items at a specific index
# Fruits.insert(1, "grapes")
# print(Fruits)

# #Tuples - similar to lists but are immutable (cannot be changed after they are created)
# MyAnimalTuple = ("Cat", "Dog", "Bird", "Fish")
# print(MyAnimalTuple)

# #the len() on a tuple
# print(len(MyAnimalTuple))

# #Since tuples are immutable(unchangeable) we cannot use the append() or insert() methods on them
# #instead, we can convert it to a list
# AnimalList = list(MyAnimalTuple)
# AnimalList.append("Hamster")
# print(AnimalList)

# #Sets are created using curly braces
# Colors = {"Red", "Pink", "Green"}
# print(Colors)

# #Sets can use the add() or remove() methods to change the set
# Colors.add("Blue")
# Colors.remove("Pink")
# print(Colors)

# #Sets support intersection, union, and difference operations
# SecondSet = {"Burger", "Fries", "Milkshake", "Red", "Green"}

# IntersectionSet = Colors & SecondSet
# print(IntersectionSet) #the intersection of the two sets is empty because there are no common elements

# UnionSet = Colors | SecondSet
# print(UnionSet)

# DifferenceSet = Colors - SecondSet
# print(DifferenceSet)

# #Dictionaries are key:value pairs defined with curly braces
# #pairs are separated by commas, keys and values are separated by colons
# FoodDictionary = {
#     "Sushi": "Fish",
#     "Burger": "Beef",
#     "Pizza": "Pepperoni"
# }
# print(FoodDictionary)

# #values() function return a list of all the values in the dictionary
# print(FoodDictionary.values())

# #We can change values in the dictionary by referring to its key
# FoodDictionary["Sushi"] = "Rice"
# print(FoodDictionary)

# #We can also add items by using a new index key and giving it a value
# FoodDictionary["Taco"] = "Chicken"
# print(FoodDictionary)

# #check to see if a key exists in the dictionary
# if "Burger" in FoodDictionary:
#     print("Yes, there is a burger in the food dictionary")

# NewDictionary = {(1, 2, 3): "This is a list as a key"}
# print(NewDictionary)


# #Operators
# #these are our symbols for performing behaviors between data types
# #+ add together two values
# print(11+3)
# print("Cherry" + "Pie")

#Arithmetic Operators - +, -, *, /, %, **, //(floor division)

#Assignment Operators - =, +=, -=, *=, /=, %=, **=, //=
#assigning values to a variable

#Comparison Operators - ==, !=, >, <, >=, <=

#Logical Operators - and, or, not


#Getting user input using the input() function
print("please enter your name")
name = input()
#print("Hello, ", name)

#We can use f to paramterize a string 
print(f"Hello, {name}. Welcome to Python!")

Color = input(f"Hello, {name}, What is your favorite color?")
print(f"I like {Color} too!")

MyInputInt = int(input("Please enter a Integer: "))
print(f"You entered {MyInputInt} and the data type is {type(MyInputInt)}")