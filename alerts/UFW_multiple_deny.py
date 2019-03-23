import datetime,random,time,sys
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
IN = ["","eth0"]
OUT = ["eth0","lo"]
PROTO = ["TCP","UDP"]
myMAC = "98:54:1b:f9:5a:ab"
ip_MAC = {
      "192.168.2.10" : "eb:dd:48:f3:8c:b3", "192.168.2.11" : "06:53:26:e6:da:92", "192.168.2.12" : "73:7d:d4:5d:fb:82",
      "192.168.1.10" : "ab:5a:4a:13:1a:4e", "192.168.1.11" : "b3:ea:97:6f:34:bc", "192.168.1.12" : "3d:37:22:a9:25:56",
      "192.168.1.13" : "06:64:03:fd:02:b4", "192.168.1.14" : "08:32:80:a1:eb:5b", "192.168.1.15" : "df:d0:cd:06:43:44",
      "192.168.1.20" : "53:b5:c2:7b:56:95", "192.168.1.21" : "3c:1a:b4:24:3c:12", "192.168.1.22" : "46:c0:38:60:76:9a"
    }

ip_names = {
            '192.168.2.10': 'anna', '192.168.2.11': 'fred', '192.168.2.12': 'alice',
            '192.168.1.10': 'bob', '192.168.1.11': 'alex', '192.168.1.12': 'michael',
            '192.168.1.13': 'joel', '192.168.1.14': 'jim', '192.168.1.15': 'shaun',
            '192.168.1.20': 'sarah', '192.168.1.21': 'monique', '192.168.1.22': 'elliot'
            }

if len(sys.argv) > 1:
    ip = sys.argv[1]
else:
    ip = '192.168.1.10'
date = str(datetime.datetime.now())
m = date[5:7]
d = date[8:10]
date = months[m] + " " + d + " " + date[11:19]
timeUp =  random.uniform(1000, 7000)

#S'escull si es una connexio entrant o sortint
if random.choice(IN) == "eth0":
    inout = "IN=eth0 OUT="
else:
    inout = "IN= OUT="+random.choice(OUT)

#Generem una IP aleatoria
bits = getrandbits(32) 
addr = IPv4Address(bits)
random_ip = str(addr)
if random.randint(0,1) == 1:
    srcdst = "SRC="+ip+" DST="+random_ip
    MAC = ("52:54:00:" + str(random.randint(0, 99)) + ":" + str(random.randint(0, 99)) + ":" + str(random.randint(0, 99))) + ":" + ip_MAC[ip]
else:
    srcdst = "SRC="+random_ip+" DST="+ip
    MAC = ip_MAC[ip] + ":" + ("52:54:00:" + str(random.randint(0, 99)) + ":" + str(random.randint(0, 99)) + ":" + str(random.randint(0, 99)))

protocol = random.choice(PROTO)
spt = str(random.randint(10,10000))
dpt = str(random.randint(10,10000))


for i in range(random.randint(7,20)):
    #Formem la linia de log
    log = date + " "+ip_names[ip]+" " + "kernel: [ " + str(timeUp) + "] [UFW BLOCK] " + inout + " MAC=" + MAC + " " + srcdst + " LEN=" + str(random.randint(10,5000)) + " TOS=0x00 PREC=0x00 TTL=" + str(random.randint(20,100)) + " ID=" + str(random.randint(10000, 99999)) + " DF PROTO=" + protocol + " SPT=" + spt+ " DPT=" + dpt + " LEN=" + str(random.randint(3,300))

    f = open("ufw.logtest", "a+")
    f.write(log+'\n')
f.close()
