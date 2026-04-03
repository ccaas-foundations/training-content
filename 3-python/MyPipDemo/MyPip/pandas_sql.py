import pandas as pd
import psycopg2
from sqlalchemy import create_engine, text # this creates our database engine with our connection details
from dotenv import load_dotenv #this lets us read from our .env file

engine = create_engine(
    "postgresql+psycopg2://postgres@localhost:5432/postgres" #connection string
)

#We can create and execute raw sql queries using the execution methods from our engine
query = "SELECT student_id, first_name, last_name, major FROM student;"
students_df = pd.read_sql(query, engine)

print(students_df)

#creating a new student record in our database
new_student = pd.DataFrame(
    {
        "first_name": ["Thor"],
        "last_name": ["Odinson"],
        "email": ["ThunderGod@email.com"],
        "major": ["BA"],
        "phone" : ['12345678']
    }
)

#the to_sql() method allows us to write a dataframe to a sql table
new_student.to_sql(name = "student", con=engine, if_exists="append", index=False)

#We can update existing records
update_sql = text("UPDATE student SET major = 'EE' WHERE student_id = 20;")

#update statements need to be executed using 'with'
with engine.connect() as connection:
    connection.execute(update_sql)
    connection.commit()

#Delete records
delete_sql = text("DELETE FROM student WHERE email = 'ThunderGod@email.com';")

#delete statements also need to be executed using 'with'
with engine.connect() as con:
    con.execute(delete_sql)
    con.commit()