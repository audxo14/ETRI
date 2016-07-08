#-*- coding: utf-8 -*-
import requests
import random
import urllib
from bs4 import BeautifulSoup
import datetime
import time
import csv
from operator import itemgetter
import os
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


# Crawl HTML of Korea Government
def get_html_Korea_Cen(pagenum):
    r = requests.get('http://www.korea.go.kr/ntnadmNews?&pageIndex=' + str(pagenum))

    soup = BeautifulSoup(r.text, "lxml")
    return soup

#Crawl HTML of Korea Government
def get_html_Korea_Rem(pagenum):
    r = requests.get('http://www.korea.go.kr/locgovNews?&pageIndex='+str(pagenum))

    soup = BeautifulSoup(r.text, "lxml")
    return soup


def get_data_Korea_Cen(html):
    keepgo = 1
    tmp_policy = html.find_all(class_='search_policy_list')[0]
    tmp_date = tmp_policy.find_all(class_='sect')
    tmp_dl = tmp_policy.find_all('dl')

    for i in range(0, len(tmp_date), 3):

        month = str(tmp_date[i]).split('.')[1]
        day = str(tmp_date[i]).split('.')[2]
        day = str(day).split('</')[0]

        j = (i / 3)

        if month == nowmonth:
            if day == today:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')

                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr" + tmp_link + "\")"

                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()

                kor_gv.append({'구분': '중앙부처', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})
            else:
                keepgo = 0
        else:
            keepgo = 0

    return keepgo


def get_data_Korea_Rem(html):
    keepgo = 1
    tmp_policy = html.find_all(class_='search_policy_list')[0]
    tmp_date = tmp_policy.find_all(class_='sect')
    tmp_dl = tmp_policy.find_all('dl')

    for i in range(0, len(tmp_date), 3):

        month = str(tmp_date[i]).split('.')[1]
        day = str(tmp_date[i]).split('.')[2]
        day = str(day).split('</')[0]

        j = (i / 3)

        if month == nowmonth:
            if day == today:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')

                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr" + tmp_link + "\")"

                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()

                kor_re.append({'구분': '지자체', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})
            else:
                keepgo = 0
        else:
            keepgo = 0

    return keepgo


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

dataset = []
kor_gv = []
kor_re = []
date = str(datetime.datetime.now()- datetime.timedelta(days=1)).split(" ")[0]
currentmonth =  date.split('-')[1]
currentday =  date.split('-')[2]
nowmonth = str(datetime.datetime.now()).split('-')[1]
today = str(datetime.datetime.now()).split('-')[2]
today = today.split(' ')[0]
savedate = nowmonth + '-' + today

p = 1
keepgo = 1
while(keepgo == 1):
    Korea_html = get_html_Korea_Cen(p)
    keepgo = get_data_Korea_Cen(Korea_html)
    p = p+1

p = 1
keepgo = 1
while(keepgo == 1):
    Korea_rem_html = get_html_Korea_Rem(p)
    keepgo = get_data_Korea_Rem(Korea_rem_html)
    p = p+1

kor_gv = sorted(kor_gv, key = itemgetter('발표처'))
kor_re = sorted(kor_re, key = itemgetter('발표처'))

dataset = kor_gv + kor_re
csv_columns = ['구분', '발표처', '제목', '웹주소']
currentPath = os.getcwd()
csv_file = "Korea-"+str(date)+".csv"
WriteDictToCSV(csv_file,csv_columns,dataset)