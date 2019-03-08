import random
critical_ips=[
                '49.85.175.46',
                '206.66.14.132',
                '127.56.126.34'
            ]

log = '%ASA-4-106023: Deny ' + random.choice(['tcp','udp']) + ' src outside:' + random.choice(critical_ips) + '/1381 dst dmz:10.0.0.15/445 by access-group "ACL-FROM-OUTSIDE"\n'
log = log*random.randint(6,10)

f = open("Cisco_ASA.logtest", "a+")
f.write(log)
f.close()