#Modules
#modules in python are simply files in which python code is written
#To use different modules, we can import them using the import keyword followed by the module name
#Modules also support using aliases using the 'as' keyword

#Arrays in python are not technically built in by default, instead we have lists
#so to use arrays, we need to import the array module
#Arrays contain elements of the same type, stored sequentially in memory, and indexed from 0
#array(typecode, initializer)

#Typecode: is a single character that specifies the type of elements in the array (datatype)
# 'i': signed integer (4 bytes)
# 'f': floating point number (4 bytes)
# 'd': double precision floating point number (8 bytes)
# 'b' : signed char (1 byte)
# 'u' : Unicode character (2 bytes)
# 'l' : signed long (4 bytes) (platform dependent)

#initializer: is an optional parameter that can be used to initialize the array with a list of values,
#it can be a list, a tuple, or any iterable containing the intial values for the array

import array as ar

IntArray = ar.array('i', [1, 2, 3, 45, 12, 8, 68, 99])
print(IntArray[3])
subset = IntArray[3:6]
print(subset)

for i in IntArray:
    print(i)

#find the length
print(len(IntArray))

#sort
print(sorted(IntArray))


#Lambda functions AKA anonymous functions
#small, single-line functions that can have any number of arguments, but only one expression
#defined using lambda keyword followed by the arguments, a colon, and the expression
#lambda args: expression

add = lambda x, y: x + y
print(add(5, 10))

Pokedex = [
    ("Bulbasaur", 1),
    ("Lapras", 131),
    ("Eevee", 133),
    ("Cubone", 104),
    ("Gengar", 94),
    ("Pikachu", 25)
]

#Sort the pokedex by number
Pokedex.sort(key=lambda x: x[1], reverse=False)
print(Pokedex)