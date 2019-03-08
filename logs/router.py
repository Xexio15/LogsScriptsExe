import datetime, random, time
from ipaddress import IPv4Address
from random import getrandbits
import sys 

######################################################################
###################### DEFINICIONES ##################################
######################################################################
ips = [
        '192.168.2.10', '192.168.2.11', '192.168.2.12',
        '192.168.1.10', '192.168.1.11', '192.168.1.12',
        '192.168.1.13', '192.168.1.14', '192.168.1.15',
        '192.168.1.20', '192.168.1.21', '192.168.1.22'
      ]

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
######################################################################
########################### SCRIPT ###################################
######################################################################
while True:
    date = str(datetime.datetime.now())
    m = date[5:7]
    d = date[8:10]
    date = months[m] + " " + d + " " + date[11:19]

    message_id = random.randint(0,9)

    if message_id < 6:
        log = date+': %SYS-5-CONFIG_I: Configured from console by admin on vty0 (1.1.1.1)'
    elif message_id < 9:
        log = date+': %SEC-6-IPACCESSLOGP: list 120 '+random.choice(['denied','permited'])+' ' + random.choice(['tcp','udp']) + ' 10.0.0.'+str(random.randint(1,200))+'('+str(random.randint(100,2000))+') -> 10.0.0.'+str(random.randint(1,200))+'('+str(random.randint(100,2000))+'), '+str(random.randint(1,50))+' packets'
    elif message_id == 9:
        log = date+': %CLEAR-5-COUNTERS: Clear counter on all interfaces by admin on vty0 (172.25.1.1)'
    

    f = open("router.logtest", "a+")
    f.write(log+'\n')
    f.close()
    time.sleep(random.randint(60,90))
