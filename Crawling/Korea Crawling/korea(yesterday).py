#-*- coding: utf-8 -*-
import requests
from bs4 import BeautifulSoup
import datetime
import csv
import os
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

# Crawl HTML of KDI
def get_html_KDI(pagenum):
    r = requests.get('http://www.kdi.re.kr/policy/ep_list.jsp?pg=' + str(pagenum) + '&&pp=10')

    soup = BeautifulSoup(r.text, "lxml")
    return soup

#Crawl HTML of Korea Government
def get_html_Korea_Cen(pagenum):
    r = requests.get('http://www.korea.go.kr/ntnadmNews?&pageIndex='+str(pagenum))

    soup = BeautifulSoup(r.text, "lxml")
    return soup

# Crawl HTML of Korea Government
def get_html_Korea_Rem(pagenum):
    r = requests.get('http://www.korea.go.kr/locgovNews?&pageIndex=' + str(pagenum))

    soup = BeautifulSoup(r.text, "lxml")
    return soup


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

        j = (i / 2)

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
                tmp_str = str(tmp_str).split("./")[1]
                tmp_link = "=HYPERLINK(\"http://www.kdi.re.kr/policy/" + tmp_str + "\")"

                dataset.append({'구분': '중앙부처', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})

            else:
                keepgo = 0
        else:
            keepgo = 0

        today = str(datetime.datetime.now()).split('-')[2]
        today = today.split(' ')[0]
        if month == str(datetime.datetime.now()).split('-')[1]:
            if day == today:
                keepgo = 1
    return keepgo


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

        if month == currentmonth:
            if day == currentday:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')

                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr" + tmp_link + "\")"

                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()

                dataset.append({'구분': '중앙부처', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})
            else:
                keepgo = 0
        else:
            keepgo = 0
        today = str(datetime.datetime.now()).split('-')[2]
        today = today.split(' ')[0]
        if month == str(datetime.datetime.now()).split('-')[1]:
            if day == today:
                keepgo = 1
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

        if month == currentmonth:
            if day == currentday:
                tmp_dt = tmp_dl[j].find_all('dt')[0]
                tmp_title = tmp_dt.get_text().encode('utf-8')

                tmp_link = str(tmp_dt).split("\"")[1]
                tmp_link = "=HYPERLINK(\"http://www.korea.go.kr" + tmp_link + "\")"

                tmp_dd = tmp_dl[j].find_all('dd')[1].get_text()
                tmp_ori = tmp_dd.encode('utf-8').strip()

                dataset.append({'구분': '지자체', '발표처': tmp_ori, '제목': tmp_title, '웹주소': tmp_link})
            else:
                keepgo = 0
        else:
            keepgo = 0
        today = str(datetime.datetime.now()).split('-')[2]
        today = today.split(' ')[0]
        if month == str(datetime.datetime.now()).split('-')[1]:
            if day == today:
                keepgo = 1

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
date = str(datetime.datetime.now()- datetime.timedelta(days=1)).split(" ")[0]
currentmonth = date.split('-')[1]
currentday = date.split('-')[2]

p = 1
keepgo = 1
while (keepgo == 1):
    test_html = get_html_KDI(p)
    keepgo = get_data_KDI(test_html)
    p = p + 1

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

csv_columns = ['구분', '발표처', '제목', '웹주소']
currentPath = os.getcwd()
csv_file = "Korea-"+str(date)+".csv"
WriteDictToCSV(csv_file, csv_columns, dataset)
