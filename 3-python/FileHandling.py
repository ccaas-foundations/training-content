#File handling refers to the process of creating, opening, reading, writing, closing, and deleting fles using Python
#Python has built-in functions and methods to make this easy

#Important to know for data persistence and processing
#Automation of many real-world tasks like data analysis, web scraping, and more
#Great for handling large data sets

#open() is the key function that takes 2 parameters; the filename and the mode
#4 main modes:
# 'r' - read mode, it is the default mode for reading and throws an error if the file does not exist
# 'a' - append mode, lets you append data to the end of the file, and creates a new file if it does not exist
# 'w' - write mode, lets you write data to a file, if it already has data, it will be overwritten, and creates a new file if it does not already exist
# 'x' - create mode, it creates a new file and returns an error if the file already exists or encounters a creation issue

#We can also specify if a file should be handled as binary or text using 'b' or 't' respectively
#Note that text is default

myFile = open('./resources/MyNewFile.txt', 'r')

#the read() function will read the entire contents of the file as a string
#print(myFile.read())

#the readline() function will read one line, and calling it again will read the next line
print(myFile.readline())
print(myFile.readline())

#this will read the next 6 characters in the file, including spaces and punctuation
print(myFile.read(6))

#it is a best practice to close any files as soon as you are done with them
#otherwise, the file exists in memory and can cause leaks and other issues
myFile.close()

#this will create a new file and throw an error if the file already exists
# try:
#     myFile2 = open('./resources/MyNewestFile.txt', 'x')

# except FileExistsError:
#     print("File already exists, cannot create a new file with the same name")

# finally:
#     myFile2.close()

#we can use the 'with' statement to automatically close the file after its suite of functions is finished
with open('./resources/MyNewFile.txt', 'r') as myFile:
    print(myFile.read())

pokemonTeam = []

for i in range(6):
    pokemon = input("Enter a pokemon for your team: ")
    pokemonTeam.append(pokemon)

with open('./resources/myPokemonTeam.txt', "w") as TeamFile:
    for pokemon in pokemonTeam:
        TeamFile.write(pokemon + "\n") #the \n is for a new line
    print("Your pokemon team has been saved!")

with open('./resources/myPokemonTeam.txt', 'r') as TeamFile:
    print("Your Pokemon team consists of: ")
    print(TeamFile.read())


#To delete a file, we need to import the os module
#although it is not required, it is a good practice to check if a file exists before trying to delete it

import os

bExists = os.path.exists('./resources/myPokemonTeam.txt')

if bExists:
    os.remove('./resources/myPokemonTeam.txt')
    print("file deleted successfully")
else:
    print("file does not exist, cannot delete")