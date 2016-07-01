
# coding: utf-8

# In[1]:


import requests
import random
import urllib
from bs4 import BeautifulSoup
from datetime import datetime
import time
import csv
import os
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


# In[2]:

#Crawl HTML of google search results
def get_google_list_html(num, key):
    r = requests.get('https://www.google.co.in/search?hl=ko&output=search&q='+unicode(key,'cp949')+'&tbm=nws&tbs=qdr:d&start='+str(num))
    soup = BeautifulSoup(r.text, "lxml")
    return soup


# In[3]:

#Crawl HTML of KDI
def get_html_KDI(pagenum):
    r = requests.get('http://www.kdi.re.kr/policy/ep_list.jsp?pg='+str(pagenum)+'&&pp=10')
    
    soup = BeautifulSoup(r.text, "lxml")
    return soup


# In[4]:

#Crawl HTML of Korea Government
def get_html_Korea_Cen(pagenum):
    r = requests.get('http://www.korea.go.kr/ntnadmNews?&pageIndex='+str(pagenum))
    
    soup = BeautifulSoup(r.text, "lxml")
    return soup


# In[5]:

#Crawl HTML of Korea Government
def get_html_Korea_Rem(pagenum):
    r = requests.get('http://www.korea.go.kr/locgovNews?&pageIndex='+str(pagenum))
    
    soup = BeautifulSoup(r.text, "lxml")
    return soup


# In[95]:

# To get what we want from the HTML... News Title, Date, Reference, and links for each article.
def get_google_article(html):    
    
    article_list = []
    
    tot_article = html.find_all(class_='g')
    for i in range(0,len(tot_article)):
        flag = 1
        try:
            tmp_title = html.find_all(class_='g')[i]
            tmp_test = tmp_title.find_all('a')
            if (len(tmp_test) < 2):
                flag = 0
            else:
                flag = 1

            tmp_link = tmp_test[0]
            if flag == 1:
                for j in range(0, 10):
                    test_str = tmp_title.find_all('a')[j].get_text()
                    article_title = test_str.encode('utf-8')
                    tmp_link = tmp_test[j]
                    article_link = tmp_link.encode('utf-8').split('\"')[1].split('&amp')[0].split('/url?q=')[1].replace('%3F','?')
                    article_link = article_link.replace('%3D','=')
                    article_link = article_link.replace('%26','&')
                    article_ref = unicode(tmp_title.find_all(class_='slp')[j].get_text().split(' - ')[0])
                    if len(article_title) == 0:
                        break
                    
                    article = {'구분': '뉴스', '발표처' : article_ref, 
                               '웹주소' : '=HYPERLINK(\"'+article_link+"\")", 
                               '제목' : article_title}
                    article_list.append(article)

            else: 
                test_str = tmp_title.find_all('a')[0].get_text()
                #test = unicode(test_str, 'utf-8')
                article_title = test_str.encode('utf-8')
                article_link = tmp_link.encode('utf-8').split('\"')[1].split('&amp')[0].split('/url?q=')[1].replace('%3F','?')
                article_link = article_link.replace('%3D','=')
                article_link = article_link.replace('%26','&')
                article_ref = unicode(tmp_title.find_all(class_='slp')[0].get_text().split(' - ')[0])

                article = {'구분': '뉴스', '발표처' : article_ref, 
                            '웹주소' : '=HYPERLINK(\"'+article_link+"\")",
                           '제목' : article_title}
                article_list.append(article)
                #print test_str
            """
            test_str = tmp_title.find_all('a')[0].get_text()
            #test = unicode(test_str, 'utf-8')
            article_title = test_str.encode('utf-8')
            article_link = tmp_link.encode('utf-8').split('\"')[1].split('&amp')[0].split('/url?q=')[1].replace('%3F','?')
            article_link = article_link.replace('%3D','=')
            article_ref = unicode(tmp_title.find_all(class_='slp')[0].get_text().split(' - ')[0])

            article = {'구분': '뉴스', '발표처' : article_ref, '웹주소' : article_link, '제목' : article_title}
            article_list.append(article)
            #print test_str
            """
        except:
            continue
    return article_list


# In[7]:

def get_data_KDI(html):
    keepgo = 1
    tmp_conts = html.find_all(class_="rpt_conts")[0]
    tmp_date = tmp_conts.find_all(class_="nlt")
    tmp_sup = tmp_conts.find_all(class_="rpt_sup")
    tmp_lst = tmp_conts.find_all(class_="rpt_lst")
    tmp_a = tmp_conts.find_all('a')
    
    for i in range(0, len(tmp_date), 2):
        
        month = str(tmp_date[i]).split('.')[1]
        day = str(tmp_date[i]).split('.')[2]
        day = str(day).split('</')[0]
        
        j = (i/2)
      
        if month == currentmonth:
            if day == currentday:
               
                tmp_a[j] = tmp_a[j].encode('utf-8')
                tmp_str = str(tmp_a[j]).split(">")[1]
                tmp_title = str(tmp_str).split("<")[0]

                tmp_sup[j] = tmp_sup[j].encode('utf-8')
                tmp_str = str(tmp_sup[j]).split("<em>")[1]
                tmp_ori = (tmp_str).split("</em>")[0]

                tmp_str = str(tmp_lst[j]).split(">")[1]
                tmp_str = str(tmp_str).split("\"")[3]
                tmp_str= str(tmp_str).split("./")[1]
                tmp_link = "=HYPERLINK(\"http://www.kdi.re.kr/policy/" + tmp_str+"\")"

                dataset.append({ '구분': '중앙부처', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})

            else:
                keepgo = 0
        else:
            keepgo = 0
            
    return keepgo


# In[8]:

def get_data_Korea_Cen(html):
    keepgo = 1
    tmp_policy = html.find_all(class_='search_policy_list')[0]
    tmp_date = tmp_policy.find_all(class_='sect')
    tmp_dl = tmp_policy.find_all('dl')
    
    for i in range(0, len(tmp_date), 3):
        
        month = str(tmp_date[i]).split('.')[1]
        day = str(tmp_date[i]).split('.')[2]
        day = str(day).split('</')[0]
        
        j = (i/3)
        
        if month == currentmonth:
            if day == currentday:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')
                
                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr"+ tmp_link +"\")"

                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()

                dataset.append({ '구분': '중앙부처', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link })
            else: 
                keepgo = 0
        else:
            keepgo = 0
        
    return keepgo


# In[9]:

def get_data_Korea_Rem(html):
    keepgo = 1
    tmp_policy = html.find_all(class_='search_policy_list')[0]
    tmp_date = tmp_policy.find_all(class_='sect')
    tmp_dl = tmp_policy.find_all('dl')

    for i in range(0, len(tmp_date), 3):
        
        month = str(tmp_date[i]).split('.')[1]
        day = str(tmp_date[i]).split('.')[2]
        day = str(day).split('</')[0]
        
        j = (i/3)
        
        if month == currentmonth:
            if day == currentday:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')
                
                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr"+ tmp_link +"\")"
            
                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()
                
                dataset.append({ '구분': '지자체', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link })
            else: 
                keepgo = 0
        else:
            keepgo = 0
        
    return keepgo


# In[10]:

def WriteDictToCSV(csv_file,csv_columns,dict_data):
    try:
        with open(csv_file, 'wb') as csvfile:
            csvfile.write(u'\ufeff'.encode('utf-8').strip())
            writer = csv.DictWriter(csvfile, fieldnames=csv_columns)
            writer.writeheader()
            for data in dict_data:
                writer.writerow({k:v.encode('utf-8').strip() for k,v in data.items()})
    except IOError as (errno, strerror):
            print("I/O error({0}): {1}".format(errno, strerror)) 
    return   


# In[100]:

final_list = []
google_list = []
tmp_list = []
search_key = raw_input("Please write your search key for Google ")


# In[12]:

dataset = []
date = str(datetime.today()).split(" ")[0]
currentmonth =  date.split('-')[1]
currentday =  date.split('-')[2]


# In[13]:

p = 1
keepgo = 1
while(keepgo == 1):
    
    test_html = get_html_KDI(p)
    keepgo = get_data_KDI(test_html)
    p= p+1


# In[14]:

p = 1
keepgo = 1
while(keepgo == 1):
    Korea_html = get_html_Korea_Cen(p)
    keepgo = get_data_Korea_Cen(Korea_html)
    p = p+1


# In[15]:

p = 1
keepgo = 1
while(keepgo == 1):
    Korea_rem_html = get_html_Korea_Rem(p)
    keepgo = get_data_Korea_Rem(Korea_rem_html)
    p = p+1


# In[101]:

for i in range(0,20):
    delay = random.randrange(10,30)
    html = get_google_list_html(i*10, search_key)
    tmp_list = get_google_article(html)
    if len(tmp_list) == 0:
        break
    google_list = google_list + tmp_list
    time.sleep(delay)


# In[102]:

csv_columns = ['구분', '발표처', '제목', '웹주소']
currentPath = os.getcwd()
csv_file = str(date)+".csv"
final_list = google_list + dataset


# In[103]:

WriteDictToCSV(csv_file,csv_columns,final_list)


# In[ ]:



