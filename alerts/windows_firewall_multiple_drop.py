import datetime,random,time
from ipaddress import IPv4Address
from random import getrandbits
import sys 

protocol = random.choice(['UDP','TCP'])

bits = getrandbits(32) 
addr = IPv4Address(bits)
random_ip = str(addr)
inout =  random.randint(0,1)
src_prt = str(random.randint(50, 60000))
dst_prt = str(random.randint(50, 60000))
if len(sys.argv) > 1:
    ip = sys.argv[1]
else:
    ip = '192.168.1.10'
log = ""

#<date> <type> <protocol> <org_ip> <dst_ip> <org_prt> <dst_prt>
#Fields: date time action protocol src-ip dst-ip src-port dst-port size tcpflags tcpsyn tcpack tcpwin icmptype icmpcode info path
for i in range(random.randint(7,20)):
    date = str(datetime.datetime.now())[:19]
    

    if inout == 0:
        log = log + date + " DROP " + protocol + " " + ip + " " + random_ip + " " + src_prt + " " + dst_prt + " - - - - - - - RECEIVE\n"

    else:
        log = log + date + " DROP " + protocol + " " + random_ip + " " + ip + " " + src_prt + " " + dst_prt + " - - - - - - - RECEIVE\n"

f = open("windowspfirewall.logtest", "a+")
f.write(log)
f.close()
