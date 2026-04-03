import requests
import logging

#logging configuration
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s",
    handlers=[
        logging.FileHandler("pokemon.log"),
        logging.StreamHandler()
    ]
)
#end logging configuration

logger = logging.getLogger(__name__)

# print("Please enter a pokemon name or pokedex # to fetch its data:")
# query = input()

# #we will use the pokemon api to fetch pokemon data
# url = f"https://pokeapi.co/api/v2/pokemon/{query.lower()}" #.lower() makes our string all lowercase

# response = requests.get(url)
# #print(response) #this will just print the response code (very messy!)
# #print(response.json()) #this will print the entire JSON response

# #to make it more readable, we can format the JSON response
# Name = response.json()['name']
# DexNumber = response.json()['id']
# Height = response.json()['height']
# Weight = response.json()['weight']

# #pokemon types are stored in a list of dictionaries
# types = [t['type']['name'] for t in response.json()['types']]

# #we can print the formatted data
# print(f"Name: {Name.capitalize()}")
# print(f"Pokedex number: {DexNumber}")
# print(f"Height: {Height / 10}m") #height is stored in decimeters
# print(f"Weight: {Weight / 10}kg") #weight is stored in hectograms
# print(f"Types: {', '.join(types)}") #this is taking our types list and joining the items using a comma and a space


#simplify into a function
def get_pokemon(pokemonName):
    url = f"https://pokeapi.co/api/v2/pokemon/{pokemonName.lower()}"
    logger.info(f"Fetching data for Pokemon: {pokemonName}")

    try:
        response = requests.get(url)

        #handle HTTP errors (ex: 404)
        response.raise_for_status()

        data=response.json()

        logger.info(f"Successfully retrieved data for {pokemonName}")

        #drill down into our JSON response
        pokemonInfo = {
            "name": data["name"].capitalize(),
            "pokedex_number": data["id"],
            "height": data["height"],
            "weight": data["weight"],
            "types": [t['type']['name'] for t in data['types']]
        }

        return pokemonInfo

    except requests.exceptions.HTTPError:
        print(f"Pokemon not found: {pokemonName}")

        logger.warning(f"Pokemon not found: {pokemonName}")

    except requests.exceptions.ConnectionError:
        logger.error("Network connection error")

    except requests.exceptions.RequestException as e:
        print(f"Unexpected error: {e}")

    return None


pokemonName = input("Please enter a pokemon name: ")
pokemon = get_pokemon(pokemonName)

if pokemon:
    print("\nPokemon Info:")
    print(f"name: {pokemon['name']}")
    print(f"Dex number: {pokemon['pokedex_number']}")
    print(f"Height: {pokemon['height']}")
    print(f"Weight: {pokemon['weight']}")
    print(f"Types: {', '.join(pokemon['types'])}")
