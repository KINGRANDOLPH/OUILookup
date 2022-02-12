import csv
import datetime
from pathlib import Path
from sqlite3 import Connection, Cursor
import sqlite3
import requests

OUI_DB = "oui.db"
OUI_MILLIS = "oui_date_millis.txt"

def get_assets_path() -> Path:
    curdir = Path(__file__).parents[0]
    dir = curdir.joinpath("app", "src", "main", "assets")
    return dir

def get_raw_path() -> Path:
    curdir = Path(__file__).parents[0]
    dir = curdir.joinpath("app", "src", "main", "res", "raw")
    return dir

def delete_db():
    dir = get_assets_path()
    path = dir.joinpath(OUI_DB)
    path.unlink(missing_ok=True)

def get_connection() -> Connection:
    dir = get_assets_path()
    file = dir.joinpath(OUI_DB)
    return sqlite3.connect(file, detect_types=sqlite3.PARSE_DECLTYPES)

def create_table(cursor: Cursor):
    cursor.execute('CREATE TABLE IF NOT EXISTS `oui` (`oui` TEXT NOT NULL, `orgName` TEXT NOT NULL, `orgAddress` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT)')

def get_csv() -> tuple[str, str]:
    output = requests.get("http://standards-oui.ieee.org/oui/oui.csv")
    last_modified = output.headers["Last-Modified"]
    return (output.text, last_modified)

def add_row(cursor: Cursor, assignment: str, orgName: str, orgAddr: str):
    data = (assignment, orgName, orgAddr)
    cursor.execute('INSERT INTO oui (oui, orgName, orgAddress) VALUES (?,?,?)', data)

def fill_db(cursor: Cursor, data: str):
    csv_reader = csv.DictReader(data.splitlines())
    for row in csv_reader:
        add_row(cursor, row["Assignment"], row["Organization Name"].strip(), row["Organization Address"])

def count_records(cursor: Cursor):
    out = cursor.execute('SELECT COUNT(*) FROM oui').fetchone()
    print(f"Records: {out[0]}")

def close_db(connection: Connection, cursor: Cursor):
    cursor.close()
    connection.commit()
    connection.close()

def update_timestamp(str_date: str):
    path = get_raw_path()
    file = path.joinpath(OUI_MILLIS)

    print(f"Last-Modified: {str_date}")
    temp_time = datetime.datetime.strptime(str_date, "%a, %d %b %Y %H:%M:%S %Z").replace(tzinfo=datetime.timezone.utc)
    timestamp = temp_time.timestamp()
    timestamp_millis = str(round(timestamp * 1000))
    print(f"Timestamp: {timestamp_millis}")

    with open(file, 'w') as outfile:
        outfile.write(timestamp_millis)

def main():
    delete_db()

    # Setup
    connection = get_connection()
    cursor = connection.cursor()
    create_table(cursor)

    # Fetch and update
    output = get_csv()
    fill_db(cursor, output[0])
    count_records(cursor)
    update_timestamp(output[1])

    # Close db connection
    close_db(connection, cursor)

if __name__ == '__main__':
    main()