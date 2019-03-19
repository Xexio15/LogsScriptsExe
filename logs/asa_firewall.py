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

database_ip = "192.168.0.10"
gateway = "192.168.0.1"

random_ips = [
                '206.83.102.31',
                '114.102.189.166',
                '25.246.208.127',
                '77.243.32.196',
                '15.90.164.227',
                '234.176.187.187',
                '50.7.89.153',
                '254.188.153.181',
                '184.115.115.105',
                '119.61.184.91',
                '145.198.38.81',
                '162.31.8.67',
                '164.144.24.50',
                '240.115.12.116',
                '242.207.144.21'
            ]

urls = {'google.com':'216.58.201.174','reddit.com':'151.101.193.140',
        'amazon.com':'176.32.103.205','ebay.com':'66.135.216.190',
        'facebook.com':'31.13.83.36',
        'stackoverflow.com':'151.101.129.69','twitter.com':'104.244.42.1'
        }

def random_MAC():
    mac = [0x00, 0x16, 0x3e, random.randint(0x00, 0x7f), random.randint(0x00, 0xff), random.randint(0x00, 0xff)] 
    return format(mac[0],'x') + format(mac[1],'x') + '.' + format(mac[2],'x') + format(mac[3],'x') + '.' + format(mac[4],'x') + format(mac[5],'x')

######################################################################
########################### SCRIPT ###################################
######################################################################
while True:
    message_id = random.randint(0,7)

    if message_id == 0:
        log = '%ASA-6-605004: Login denied from' + random.choice(ips) + '/13269 to outside:' + random.choice(ips) + '/ssh for user "root"'
    #elif message_id == 1:
    #    log = '%ASA-4-405001: Received ARP request collision from ' + random.choice(random_ips) + '/' + random_MAC() + ' on interface inside'
    elif message_id == 2:
        log = '%ASA-6-604101: DHCP client interface outside: Allocated ip = ' + random.choice(ips) + ', mask = 255.255.255.0, gw = ' + gateway
    elif message_id == 3:
        url = random.choice(list(urls.keys()))
        log = '%ASA-5-304001: ' + random.choice(ips) + ' Accessed URL ' + urls[url] + ':https://www.' + url
    elif message_id == 4:
        log = '%ASA-4-106023: Deny ' + random.choice(['tcp','udp']) + ' src outside:' + random.choice(random_ips) + '/1381 dst inside:10.107.8.6/445 by access-group "ACL-FROM-OUTSIDE"'
    elif message_id == 5:
        log = '%ASA-4-106023: Deny ' + random.choice(['tcp','udp']) + ' src outside:' + random.choice(random_ips) + '/1381 dst dmz:10.0.0.15/445 by access-group "ACL-FROM-OUTSIDE"'
    elif message_id == 6:
        log = '%ASA-3-305006: portmap translation creation failed for ' + random.choice(['tcp','udp']) + ' src inside:' + random.choice(ips) + '/2893 dst outside:' + random.choice(ips) + '/3128'
    elif message_id == 7:
        log = '%ASA-3-106011: Deny inbound (No xlate) udp src outside:' + random.choice(ips) + '/137 dst outside:' + random.choice(ips) + '/137'


    f = open("Cisco_ASA.logtest", "a+")
    f.write(log+'\n')
    f.close()
    time.sleep(random.randint(3,6))