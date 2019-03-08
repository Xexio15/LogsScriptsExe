import random
ips = [
        '192.168.2.10', '192.168.2.11', '192.168.2.12',
        '192.168.1.10', '192.168.1.11', '192.168.1.12',
        '192.168.1.13', '192.168.1.14', '192.168.1.15',
        '192.168.1.20', '192.168.1.21', '192.168.1.22'
      ]

urls = {
        'https://www.mega.nz':'89.44.169.135','https://www.uploaded.net':'81.171.123.200',
        'https://www.zippyshare.com':'145.239.9.15','http://www.mat.ub.edu':'161.116.37.92'
       }

url = random.choice(list(urls.keys()))
log = '%ASA-5-304001: ' + random.choice(ips) + ' Accessed URL ' + urls[url] + ':' + url

f = open("Cisco_ASA.logtest", "a+")
f.write(log+'\n')
f.close()