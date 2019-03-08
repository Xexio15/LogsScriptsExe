import random

def random_MAC():
    mac = [0x00, 0x16, 0x3e, random.randint(0x00, 0x7f), random.randint(0x00, 0xff), random.randint(0x00, 0xff)] 
    return format(mac[0],'x') + format(mac[1],'x') + '.' + format(mac[2],'x') + format(mac[3],'x') + '.' + format(mac[4],'x') + format(mac[5],'x')

critical_ips=[
                '49.85.175.46',
                '206.66.14.132',
                '127.56.126.34'
            ]


log = '%ASA-4-405001: Received ARP request collision from ' + random.choice(critical_ips) + '/' + random_MAC() + ' on interface inside'

f = open("Cisco_ASA.logtest", "a+")
f.write(log+'\n')
f.close()