

# Lab: The In-Memory Contact Book

## The Scenario

Your team needs a lightweight script to temporarily store employee contact information in memory. You've decided to build this using Python's Dictionary data structure.

## Deliverables

1. Navigate to the `starter_code` directory and open `contact_book.py`.
2. Create an empty dictionary named `contacts`.
3. Hardcode the addition of three employees to the `contacts` dictionary.
   - The **Key** must be their name (a String).
   - The **Value** must be a Tuple containing two strings: `(Phone Number, Email)`.
4. Create a prompt using `input()` asking the user: "Enter a name to lookup: ".
5. Retrieve the tuple from the dictionary using the provided name.
6. Print the details using an f-string: `Name: {name}, Phone: {phone}, Email: {email}`.
7. *Challenge:* Dictionaries will crash with a `KeyError` if you try to look up a key that doesn't exist. Can you find a safe way (like the `.get()` method) to look up a name?

## Definition of Done

- A dictionary containing at least 3 names maps to Tuples containing contact info.
- The script successfully fetches a user's details based on console input.
- The output is neatly formatted.