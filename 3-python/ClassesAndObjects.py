#Python is an oop language, which means it can create custom classes and objects based on those classes
#Classes are blueprints for creating objects, and they define the properties and behaviors of those objects

class Dog:
    #In Python, we override the __init__() method to initialize the properties of the class (constructor)
    #notice that it uses DOUBLE underscores
    #Double trailing and leading underscores are used for special methods in Python, also known as 'dunder' methods
    #We pass in 'self' which references the instance of the class we just created
    #we take in arguments that are our classes attributes
    def __init__(self, name, age, breed, color):
        self.name = name
        self.age = age
        self.breed = breed
        self.color = color

    def bark(self): #pass in the self object to access self attributes (remember our scopes!)
        return f"{self.name} barked at a car!"
    
    #__str__() is a dunder method that allows us to define how our object is represented as a string(our toString method)
    def __str__(self):
        return f"{self.name} is a {self.age} year old {self.color} {self.breed}"
    
Major = Dog("Major", 7, "German Shepherd", "Gray")
print(Major)
print(Major.bark())


#Pillars of oop: Abstraction, inheritance, polymorphism, encapsulation
#Abstraction: hiding the complex implementation details and showing only the necessary features of an object
#Inheritance: the ability of a new class to inherit properties and functionality from their parent classes
#Polymorphism: "many forms" meaning one interface but multiple implementations
#Encapsulation: the practice of bundling data/attributes and methods into a single unit and restricting access to some of the object's components

#Inheritance: lets us define classes that inherit from another class
#Parent class being what is inherited FROM
#child class being what is inheriting from another(aka derived class)

class Animal:
    def __init__(self, name, age):
        self.name = name

        #Encapsulation is about protecting data inside of a class
        #we can make variables private by using a __ prefix
        #note that we can make protected attributes using _ prefix, but python does not enforce any rules around this
        #protected attributes are just for devs to understand(a convention)
        self.__age = age

    #to modify a private property, we need getter/setter methods
    def get_age(self): return self.__age
    def set_age(self, age):
        if age > 0:
            self.__age = age
        else:
            print("Age must be positive")
    
    def speak(self):
        return f"{self.name} has made a sound"
        

class Cat(Animal): #Cat is inheriting from Animal, so cat is a child class
    def __init__(self, name, age, breed, color, hairLength):
        #call the parent class constructor using the super() method
        super().__init__(name, age)
        self.breed = breed
        self.color = color
        self.hairLength = hairLength

    def scratch(self):
        return f"{self.name} has scratched the chair!"
    
    #polymorphism example - we are overriding the definition of the speak() function
    def speak(self):
        return f"{self.name} meowed at the door!"
    
Ash = Cat("Ash", 2, "Maine Coone", "Brown", "medium-hair")
print(Ash.scratch())
print(Ash.speak())
#print(Ash.__age) #this will throw an error because __age is a private variable and cannot be accessed directly
print(Ash.get_age())
Ash.set_age(3)
print(Ash.get_age())
Wontina = Animal("Wontina", 7)
print(Wontina.speak())
print(Wontina.get_age()) #we can access the private variable using the getter method