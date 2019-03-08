import datetime, random, time
from ipaddress import IPv4Address
from random import getrandbits
import sys 

######################################################################
###################### DEFINICIONES ##################################
######################################################################
months = {
            "01" : "Jan",
            "02" : "Feb",
            "03" : "Mar",
            "04" : "Apr",
            "05" : "May",
            "06" : "Jun",
            "07" : "Jul",
            "08" : "Aug",
            "09" : "Sep",
            "10" : "Oct",
            "11" : "Nov",
            "12" : "Dec"
         }

ips = [
        '192.168.2.10', '192.168.2.11', '192.168.2.12',
        '192.168.1.10', '192.168.1.11', '192.168.1.12',
        '192.168.1.13', '192.168.1.14', '192.168.1.15',
        '192.168.1.20', '192.168.1.21', '192.168.1.22'
      ]

database_ip = "192.168.0.10"
database_name = "SIEM_DB"

######################################################################
########################### SCRIPT ###################################
######################################################################
# <date_utc> <hostname> postgres[<pid>] : [<group_id>] <sql_error_code> <session_id> <message_type>: <message>
while True:
    date = str(datetime.datetime.now())
    m = date[5:7]
    d = date[8:10]
    date = months[m] + " " + d + " " + date[11:19]

    log = date + " " + database_name + " postgres[" + str(random.randint(2000, 8000)) + "]: [" + str(random.randint(10, 100)) + "-1] " + str(random.randint(10000, 40000)) + " " + str(random.randint(10000000, 80000000))

    e_type = random.randint(1,10)
    if e_type < 8:
        m_type = "STATEMENT"
        m = random.randint(1,10)

        if m < 8:
            message = "SELECT * FROM table_" + str(random.randint(1,5))
        elif m < 9:
            message = "INSERT INTO table_" + str(random.randint(1,5)) + "(col" + str(random.randint(1,10)) + ") VALUES (value)"
        else:
            message = "ALTER TABLE table_" + str(random.randint(1,5)) + " ADD COLUMN new_col type"

    elif e_type == 8:
        m_type = "ERROR"
        if random.randint(1,2) == 1:
            message = "column \"(col" + str(random.randint(1,10)) + ")\" of relation \"table_" + str(random.randint(1,5)) + "\" already exists"
        else:
            message = "column table_" + str(random.randint(1,5)) + ".col" + str(random.randint(1,10)) + " does not exist"
    
    elif e_type == 9:
        m_type = "WARNING"
        message = "skipping \"pg_tablespace\" --- only superuser can vacuum it"

    elif e_type == 10:
        m_type = "LOG"
        message = "unexpected EOF on client connection"


    log = log + " " + str(e_type) + ": " + message
    
    f = open("pg_log.logtest", "a+")
    f.write(log+'\n')
    f.close()
    time.sleep(random.randint(2,5))


