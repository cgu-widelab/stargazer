import datetime
import logging.handlers
import os
import threading
from pathlib import Path

import pymysql
from data import (
    WeatherData3day,
    WeatherData7day,
    SunsetData,
)

DB_HOST = "db"
DB_USER = "stargazer"
with open("/run/secrets/db-stargazer-password", "r") as fp:
    DB_PASSWORD = fp.read()

# set logger
if not os.path.exists('../log_files'):
    os.mkdir('../log_files')
MyLogger = logging.getLogger('MyLogger')
MyLogger.setLevel(logging.INFO)
handler = logging.handlers.TimedRotatingFileHandler('../log_files/info', when='D', interval=1)
handler.setFormatter(logging.Formatter("%(asctime)s - %(levelname)s - %(message)s"))
handler.setLevel(logging.INFO)
handler.suffix = "%Y%m%d.log"
MyLogger.addHandler(handler)


def update_weather_data_3():
    my_db = pymysql.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASSWORD, database="stargazer")
    wd3 = WeatherData3day()
    wd3.format_weather_data()
    my_cursor = my_db.cursor()
    my_cursor.execute('DELETE FROM `dynamic_data_3` WHERE 1')
    my_db.commit()
    sql = 'INSERT INTO `dynamic_data_3`(`zip_code`, `dd_DateTime`, `T`, `RH`, `PoP6h`, `CI`, `Wx`, `WD`) \
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s)'
    my_cursor.executemany(sql, wd3.all_weather_data)
    my_db.commit()
    my_db.close()
    del wd3


def update_weather_data_7():
    my_db = pymysql.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASSWORD, database="stargazer")

    wd7 = WeatherData7day()
    wd7.format_weather_data()
    my_cursor = my_db.cursor()
    my_cursor.execute('DELETE FROM `dynamic_data_7` WHERE 1')
    my_db.commit()
    sql = 'INSERT INTO `dynamic_data_7`(`zip_code`, `dd_DateTime`, `T`, `RH`, `CI`, `PoP24h`, `Wx`, `WD`) \
            VALUES (%s,%s,%s,%s,%s,%s,%s,%s)'
    my_cursor.executemany(sql, wd7.all_weather_data)
    my_db.commit()
    my_db.close()
    del wd7


def update_sunset_data():
    my_db = pymysql.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASSWORD, database="stargazer")
    sd = SunsetData()
    sd.format_sunset_data()
    my_cursor = my_db.cursor()
    my_cursor.execute('DELETE FROM `sunset_time` WHERE 1')
    my_db.commit()
    sql = 'INSERT INTO `sunset_time`(`area`, `ss_Date`, `sr_Time`, `ss_Time`) \
            VALUES (%s,%s,%s,%s)'
    my_cursor.executemany(sql, sd.all_sunset_data)
    my_db.commit()
    my_db.close()
    del sd


def clean_dir(path: Path = Path('..')):
    for x in path.glob('*'):
        if x.suffix in ['.xml', '.zip']:
            x.unlink()


def update_weather_3hr(next_execute_time=10800):
    try:
        update_weather_data_3()
        update_weather_data_7()
        clean_dir()
        MyLogger.info("Weather data successfully updated.")
    except Exception as e:
        MyLogger.exception(f"Error occurs while updating sunset data. Error: {e}")
    threading.Timer(next_execute_time, update_weather_3hr).start()


def update_sunset_1yr(next_execute_time=315360):
    try:
        update_sunset_data()
        MyLogger.info("Sunset data successfully updated.")
    except Exception as e:
        MyLogger.exception(f"Error occurs while updating sunset data. Error: {e}")
    threading.Timer(next_execute_time, update_sunset_1yr).start()


if __name__ == '__main__':
    # calculate next run time for weather_data
    next_time = datetime.datetime.now().hour
    next_time = next_time - next_time % 3 + 3
    next_time = str(datetime.date.today() + datetime.timedelta(days=(next_time == 24))) + " " + str(next_time % 24) + ":15:0"
    next_time = datetime.datetime.strptime(next_time, "%Y-%m-%d %H:%M:%S")
    next_time = (next_time - datetime.datetime.now()).total_seconds()
    threading.Timer(10, update_weather_3hr, [next_time]).start()
    # calculate next run time for sunset_data
    next_time = datetime.datetime.now().year + 1
    next_time = str(next_time) + "-1-1 1:0:0"
    next_time = datetime.datetime.strptime(next_time, "%Y-%m-%d %H:%M:%S")
    next_time = (next_time - datetime.datetime.now()).total_seconds()
    threading.Timer(10, update_sunset_1yr, [next_time%315360]).start()
