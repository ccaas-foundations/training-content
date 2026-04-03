#The Math module contains many useful mathematical functions and constants
import math

#min() and max() functions find the smallest and largest values in an iterable or among multiple arguments
x = [2, 5, 15, 3, 77]
print(min(x))
print(max(x))

#abs() function returns the absolute value of a number
y = abs(-7.25)
print("absolute value :", y)

#pow() function returns the value of a number raised to the power of another number
a = pow(3, 4) #3 to the power of 4
print("3 to the power of 4 is :", a)

#sqrt() function returns the square root of a number
b = math.sqrt(64) #returns a float
print("the square root of 64 is :", b)

#ceil() function rounds a number up to the nearest integer
c = math.ceil(4.2)
print("the ceiling of 4.2 is :", c)

#floor() function rounds a number down to the nearest integer
d = math.floor(4.7)
print("the floor of 4.7 is :", d)

#pi constant represents the mathematical constant pi, which is approximately equal to 3.14159
e = math.pi
print("the value of pi is: ", e)


#the JSON module can be used to work with JSON data
#JSON stands for JavaScript Object Notation

import json

#We can parse JSON objects and convert them into Python dictionaries
JsonData = '{"Name": "Alice", "Age" : 30, "City" : "New York"}'
#data variable holds our JSON data as a python dictionary
data = json.loads(JsonData)
print("Name :", data["Name"])
print("Age: ", data["Age"])

#we can also convert Python dictionaries into JSON objects
#dict, list, tuple, string, int, float, True/Fale, None can all be converted into JSON using the dumps() function
PythonDict = {"fruit" : "Apple", "color" : "Red", "quantity" : 5}
JSONObject = json.dumps(PythonDict)
print("JSON Object: \n", JSONObject)

#the dumps() can take additional parameters for formatting the json output
FormattedJSON = json.dumps(PythonDict, indent = 4)
print("Formatted JSON object: \n", FormattedJSON)


#RegEx stands for Regular Expression, which is essentially just a sequence of characters that forms a search pattern
#useful for finding substrings, data validation, text manipulation, and more
#the re module provides support for regular expressions in Python
import re

#We can use the search() function to search for a pattern within a string
Pattern = "cat"
Text = "The cat is on the roof."
match = re.search(Pattern, Text)
if match:
    print("Pattern found: ", match.group())

#we have the findall() function to find all occurrences of a pattern in a string
SecondText = "The cat sat on the mat with another cat eating a rat with another cat." 
matches = re.findall(Pattern, SecondText)
print("All occurrences of 'cat':", matches)

# matchesIndex = re.finditer(SecondText, Pattern)
# print("All occurences at index: ", matchesIndex)

#regex metacharacters - these can help us perform more complex searches
# [ ] - A set of characters
print(re.findall(r"[crsm]at", SecondText)) #finds 'cat' or 'rat' or 'sat' or 'mat'

#\d - Any digit 0-9
print(re.findall(r"\d", "There are 3 cats and 2 dogs."))

# ^ - Starts with
print(re.findall(r"^The", SecondText)) #checks if the text starts with 'The'

# $ - Ends with
print(re.findall(r"at.$", SecondText)) #Checks if the text ends with 'at'

#split() function splits a string at each match of a pattern
SplitText = re.split("\s","Split this, text into, words") #\s represents spaces
print("Split text: ", SplitText)

#the sub() function replaces matches with a specified string
ReplacedText = re.sub("cat", "dog", SecondText)
print("Replaced text: ", ReplacedText)