# Lab: Object-Oriented Inventory Management

## The Scenario

Your retail store is dropping its legacy tracking software. You are tasked with creating a simplified, modern Object-Oriented terminal script to track a list of physical items in a warehouse.

## Deliverables

Navigate to the `starter_code` directory and open `inventory.py`. You will build two classes and a main menu loop.

1. **Build the `Item` class:**
    * Create an `__init__(self, name, quantity, price)` constructor.
    * Attach those three parameters to the object using `self.`.
2. **Build the `InventoryManager` class:**
    * Create an `__init__(self)` constructor that initializes a blank list called `self.items = []`.
    * Create a method `add_item(self, target_item)` that appends the incoming object to the list.
    * Create a method `display_inventory(self)` that loops over the `self.items` list utilizing a `for` loop, printing each item's details.
3. **Build the Interative `while` Loop:**
    * In the `main()` function, write an infinite `while True:` loop.
    * Ask the user to either: press '1' to add an item, '2' to view items, or '3' to quit.
    * If '1': Ask for name, quantity, and price. Create an `Item` object, and add it to the manager.
    * If '2': Call the manager's `display_inventory()` method.
    * If '3': Use the `break` keyword to shatter the infinite loop and gracefully exit the script.

## Definition of Done

- Two discrete Object-Oriented classes are functioning correctly using `self`.
* A continuous `while` loop successfully blocks script exit, awaiting user keyboard inputs.
* The user can add multiple unique items and then successfully read them back out to the console.