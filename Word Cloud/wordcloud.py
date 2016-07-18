#-*- coding: utf-8 -*-

from collections import Counter
import urllib
import random
import webbrowser

from lxml import html
import pytagcloud
from pytagcloud import LAYOUT_HORIZONTAL
from pytagcloud import make_tags
# requires Korean font support
import sys
import csv


r = lambda: random.randint(0,255)
color = lambda: (r(), r(), r())

dic =[]

with open('test.csv', 'rb') as csvfile:
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


def draw_cloud(tags, filename, fontname, size):
    pytagcloud.create_tag_image(tags, filename, fontname = fontname, size = size, rectangular=True, layout=LAYOUT_HORIZONTAL)

tags = dic
if(dic[0]['size'] > 70):
    size = (600, 600)
elif(dic[0]['size'] > 60):
    size = (550, 550)
elif(dic[0]['size'] > 50):
    size = (450, 450)
elif(dic[0]['size'] > 40):
    size = (400, 400)
elif(dic[0]['size'] > 30):
    size = (350, 350)

draw_cloud(tags, 'wordcloud.png','Noto Sans CJK', size)