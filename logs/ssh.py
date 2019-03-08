import datetime, random, time
from ipaddress import IPv4Address
from random import getrandbits
import sys 

######################################################################
###################### DEFINICIONES ##################################
######################################################################
months = {"01" : "Jan",
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

names = ["anna", "fred", "alice", "bob", "alex", "michael", "joel", "jim","shaun","sarah","monique","elliot"]
names_ip = {
            "anna" : "192.168.2.10", "fred" : "192.168.2.11", "alice" : "192.168.2.12",
            "bob" : "192.168.1.10", "alex" : "192.168.1.11", "michael" : "192.168.1.12",
            "joel": "192.168.1.13", "jim" : "192.168.1.14", "shaun" : "192.168.1.15",
            "sarah" : "192.168.1.20", "monique" : "192.168.1.21", "elliot" : "192.168.1.22"
            }
ip_names = {
            '192.168.2.10': 'anna', '192.168.2.11': 'fred', '192.168.2.12': 'alice',
            '192.168.1.10': 'bob', '192.168.1.11': 'alex', '192.168.1.12': 'michael',
            '192.168.1.13': 'joel', '192.168.1.14': 'jim', '192.168.1.15': 'shaun',
            '192.168.1.20': 'sarah', '192.168.1.21': 'monique', '192.168.1.22': 'elliot'
            }
database_ip = "192.168.0.10"
if len(sys.argv) > 1:
    ip = sys.argv[1]
else:
    ip = '192.168.1.10'

######################################################################
########################### SCRIPT ###################################
######################################################################
while True:
    date = str(datetime.datetime.now())
    m = date[5:7]
    d = date[8:10]
    date = months[m] + " " + d + " " + date[11:19]

    log = date + " server sshd["+str(random.randint(5000,20000))+"]: "+"Accepted password for " + ip_names[ip] + " from " + ip + " port 6647 ssh2"

    f = open("sshd.logtest", "a+")
    f.write(log+'\n')
    f.close()
    time.sleep(random.randint(20,40))