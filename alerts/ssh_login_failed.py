import datetime, random, time
from ipaddress import IPv4Address
from random import getrandbits

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

bits = getrandbits(32) 
names = ["root","anna", "fred", "alice", "bob", "alex", "root"]
date = str(datetime.datetime.now())
m = date[5:7]
d = date[8:10]
date = months[m] + " " + d + " " + date[11:19]

log = date + " server sshd["+str(random.randint(5000,20000))+"]: "+"Failed password for "+random.choice(names)+" from "+str(IPv4Address(bits))+" port 6647 ssh2"

f = open("sshd.logtest", "a+")
f.write(log+'\n')
f.close()
