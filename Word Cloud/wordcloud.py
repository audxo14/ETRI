#-*- coding: utf-8 -*-

from collections import Counter
import urllib
import random
import webbrowser

from lxml import html
import pytagcloud # requires Korean font support
import sys
import csv


r = lambda: random.randint(0,255)
color = lambda: (r(), r(), r())

dic =[]

with open('test.csv', 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter = ',', quotechar = '|')
    for row in reader:
        try:
            dic.append({'color': color(), 'tag': unicode(row[1], 'cp949'), 'size': int(row[2])/6})
        except:
            continue


def draw_cloud(tags, filename, fontname = 'Noto Sans CJK', size = (600, 800)):
    pytagcloud.create_tag_image(tags, filename, fontname = fontname, size = size)

tags = dic

draw_cloud(tags, 'wordcloud.png')
