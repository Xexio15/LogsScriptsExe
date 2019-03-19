import random
critical_ips=[
                '49.85.175.46',
                '206.66.14.132',
                '127.56.126.34'
            ]

log = ''
protocol = random.choice(['tcp','udp'])
ip = random.choice(critical_ips)

for i in range(random.randint(27,59)):
	log = log+'%ASA-4-106023: Deny ' + protocol + ' src outside:' + ip + '/1381 dst dmz:10.0.0.15/'+ str(random.randint(1,65535)) +' by access-group "ACL-FROM-OUTSIDE"\n'

f = open("Cisco_ASA.logtest", "a+")
f.write(log)
f.close()