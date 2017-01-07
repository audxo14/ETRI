#-*- coding: utf-8 -*-

# use pytagcloud module and make wordcloud
from collections import Counter
import urllib
import random
import webbrowser
import os
import fnmatch

from lxml import html
import pytagcloud
from pytagcloud import LAYOUT_HORIZONTAL
from pytagcloud import make_tags
# requires Korean font support
import sys
import csv

def draw_cloud(tags, filename, fontname, size):
    pytagcloud.create_tag_image(tags, filename, fontname = fontname, size = size, rectangular=True, layout=LAYOUT_HORIZONTAL)


r = lambda: random.randint(0,255)
color = lambda: (r(), r(), r())


current_dir = os.path.dirname(os.path.abspath(__file__))
current_dir = current_dir.replace('\\', '/')
csv_dir = current_dir+"/csv/"

path = csv_dir

file_list = [os.path.join(dirpath, f)
    for dirpath, dirnames, files in os.walk(path)
    for f in fnmatch.filter(files, '*.csv')]

for i in range(0, len(file_list)):
    file_list[i] = file_list[i].replace('\\', '/')
    file_list[i] = file_list[i].decode('cp949').encode('utf-8')
    file_list[i] = unicode(file_list[i], 'utf-8')
    tmp_str = file_list[i].split("/")
    file_len = len(tmp_str)

for fileName in file_list:
    with open(fileName, 'rb') as csvfile:
        dic = []
        count = False
        reader = csv.reader(csvfile, delimiter = ',', quotechar = '|')
        for row in reader:
            try:
                if(int(row[2]) > 500):
                    freq = int(int(row[2]) * 0.4)
                    count = True
                elif(int(row[2]) > 400):
                    freq = int(int(row[2])*0.5)
                    count = True
                elif(int(row[2])> 300):
                    freq = int(int(row[2]) * 0.6)
                    count = False
                elif(int(row[2]) > 200):
                    freq = int(int(row[2])* 0.65)
                    count = False
                elif(int(row[2]) > 100):
                    freq = int(int(row[2]) * 0.7)
                    count = False
                else:
                    freq = int(int(row[2])* 0.75)
                    count = False
                if(count):
                    sizediv = 3
                else:
                    sizediv = 2
                dic.append({'color': color(), 'tag': unicode(row[1], 'cp949'), 'size': freq/sizediv})
            except:
                continue
    tags = dic
    if (dic[0]['size'] > 70):
        size = (600, 600)
    elif (dic[0]['size'] > 60):
        size = (550, 550)
    elif (dic[0]['size'] > 50):
        size = (450, 450)
    elif (dic[0]['size'] > 40):
        size = (400, 400)
    elif (dic[0]['size'] > 30):
        size = (350, 350)
    cloudName =  fileName.split('/csv/')[1]
    cloudName = cloudName.split('.csv')[0]

    dirName = fileName.split('/csv/')[0]
    draw_cloud(tags, dirName + '/word_cloud/'+cloudName+ '.png', 'Noto Sans CJK', size)

