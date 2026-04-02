class Item:
    # 1. Create the constructor
    def __init__(self, name, quantity, price):
        # TODO: Assign the parameters to the object using `self.name = name`, etc.
        pass

class InventoryManager:
    # 2. Create the Manager
    def __init__(self):
        # TODO: Create an empty List attached to `self.items`
        pass
        
    def add_item(self, target_item):
        # TODO: Append the target_item to `self.items`
        pass
        
    def display_inventory(self):
        # TODO: Use a `for` loop to iterate over `self.items` and print their details.
        pass

def main():
    print("Welcome to the Inventory System!")
    # TODO: Create a new InventoryManager object
    
    # 3. Build the interactive loop
    while True:
        print("\n1. Add Item | 2. View Inventory | 3. Quit")
        choice = input("Enter choice: ")
        
        if choice == '1':
            pass # TODO: Ask for details, create an Item, and add it
        elif choice == '2':
            pass # TODO: Display inventory
        elif choice == '3':
            print("Shutting down...")
            # TODO: Break out of the loop
        else:
            print("Invalid Option!")

if __name__ == "__main__":
    main()