#-*- coding: utf-8 -*-

import requests
import random
from bs4 import BeautifulSoup
from datetime import datetime
import time
import csv
import os
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

#Crawl HTML of google search results
def get_google_list_html(num, key):
    r = requests.get('https://www.google.co.in/search?hl=ko&output=search&q='+unicode(key,'cp949')+'&tbm=nws&tbs=qdr:d&start='+str(num))
    soup = BeautifulSoup(r.text, "lxml")
    return soup

# To get what we want from the HTML... News Title, Date, Reference, and links for each article.
def get_google_article(html, keyword, index):
    article_list = []
    
    tot_article = html.find_all(class_='g')
    for k in range(0, len(tot_article)):
        flag = 1
        try:
            tmp_title = html.find_all(class_='g')[k]
            tmp_test = tmp_title.find_all('a')
            if len(tmp_test) < 2:
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
                    
                    article = {'순번': str(index), '키워드': unicode(keyword, 'cp949').encode('utf-8'),
                               '발표처' : article_ref,
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

                article = {'순번': str(index), '키워드': unicode(keyword, 'cp949').encode('utf-8'),
                           '발표처' : article_ref,
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

def WriteDictToCSV(csv_file,csv_columns,dict_data):
    try:
        with open(csv_file, 'wb') as csvfile:
            csvfile.write(u'\ufeff'.encode('utf-8').strip())
            writer = csv.DictWriter(csvfile, fieldnames=csv_columns)
            writer.writeheader()
            for data in dict_data:
                writer.writerow({k: v.encode('utf-8').strip() for k, v in data.items()})
    except IOError as (errno, strerror):
            print("I/O error({0}): {1}".format(errno, strerror)) 
    return   

google_list = []
tmp_list = []
search_key = []
f = open("keyword.txt", 'r')
while True:
    line = f.readline()
    if not line: 
        break
    search_key.append(line)
f.close()

for item in range(0, len(search_key)):
    for i in range(0, 20):
        delay = random.randrange(10, 30)
        html = get_google_list_html(i*10, search_key[item])
        tmp_list = get_google_article(html, search_key[item], item+1)
        if len(tmp_list) == 0:
            break
        google_list = google_list + tmp_list
        time.sleep(delay)

date = str(datetime.today()).split(" ")[0]
csv_columns = ['순번', '키워드', '발표처', '제목', '웹주소']
currentPath = os.getcwd()
csv_file = "google-"+str(date)+".csv"

WriteDictToCSV(csv_file, csv_columns, google_list)