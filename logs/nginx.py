 #%{IPORHOST:remote_ip} - %{DATA:user_name} \[%{HTTPDATE:time}\] "%{WORD:request_action} 
 #%{DATA:request} HTTP/%{NUMBER:http_version}" %{NUMBER:response} %{NUMBER:bytes} 
 #"%{DATA:referrer}" "%{DATA:agent}"
import random,datetime,time,os,sys

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
def random_line(afile):
    line = next(afile)
    for num, aline in enumerate(afile, 2):
        if random.randrange(num): continue
        line = aline
    return line


######################################################################
########################### SCRIPT ###################################
######################################################################
while True:
    path =  os.path.abspath(sys.argv[1]+"/nginx_logs.log")
    fl  = open(path, "r")
    line = random_line(fl)
    fl.close()
    first_split = line.split('[')
    first_part = first_split[0]
    d = first_split[1]
    second_split = d.split(']')
    d = second_split[0]
    second_part = second_split[1]

    #04/Jun/2015:06:06:27 +0000
    date = str(datetime.datetime.now())
    a = date[:4]
    m = date[5:7]
    d = date[8:10]
    date = d + "/" + months[m] + "/" + a + ":" + date[11:19] + " +0000"

    log = first_part+'['+date+']'+second_part

    f = open("nginx.logtest", "a+")
    f.write(log)
    f.close()
    time.sleep(random.randint(10,12))



