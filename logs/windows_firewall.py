import datetime,random,time
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

protocols = ['UDP','TCP']

types = ['OPEN', 'DROP', 'CLOSE']

bits = getrandbits(32) 
addr = IPv4Address(bits)
random_ip = str(addr)

if len(sys.argv) > 1:
    ip = sys.argv[1]
else:
    ip = '192.168.1.10'

######################################################################
########################### SCRIPT ###################################
######################################################################
#<date> <type> <protocol> <org_ip> <dst_ip> <org_prt> <dst_prt>
#Fields: date time action protocol src-ip dst-ip src-port dst-port size tcpflags tcpsyn tcpack tcpwin icmptype icmpcode info path
while True:
    date = str(datetime.datetime.now())[:19]
    l_type = random.choice(types)

    if random.randint(0,1) == 0:
        log = date + " " + l_type + " " + random.choice(protocols) + " " + ip + " " + random_ip + " " + str(random.randint(50, 60000)) + " " + str(random.randint(50, 60000)) + " - - - - - - - "

    else:
        log = date + " " + l_type + " " + random.choice(protocols) + " " + random_ip + " " + ip + " " + str(random.randint(50, 60000)) + " " + str(random.randint(50, 60000)) + " - - - - - - - "

    if l_type == "DROP":
        log = log + "RECEIVE"


    f = open("windowspfirewall.logtest", "a+")
    f.write(log+'\n')
    f.close()
    time.sleep(random.randint(2,5))