# -*- coding: utf-8 -*-
from collections import Counter
from os.path import basename
import os
import csv
import re
import glob
from konlpy.tag import Kkma
from konlpy.corpus import kolaw
from konlpy.tag import Hannanum
from konlpy.utils import concordance, pprint
from matplotlib import pyplot
from nltk.corpus import stopwords
from nltk import collocations

pwd = os.getcwd()+'\\txt'
print pwd

for path, dirs, files in os.walk(pwd):
    for file in files:
        print os.path.join(path, (file).decode('cp949'))

