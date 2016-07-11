#-*- coding: utf-8 -*-
from collections import Counter
import os
import re
import chardet
from konlpy.tag import Kkma
from konlpy.tag import Hannanum
from nltk import collocations
import subprocess
import csv

def WriteDictToCSV(csv_file,csv_columns,dict_data, path):
    try:
        with open(path + csv_file, 'wb') as csvfile:
            csvfile.write(u'\ufeff'.encode('utf-8').strip())
            writer = csv.DictWriter(csvfile, fieldnames=csv_columns)
            writer.writeheader()
            for data in dict_data:
                writer.writerow({k:v.encode('utf-8').strip() for k, v in data.items()})
    except IOError as (errno, strerror):
            print("I/O error({0}): {1}".format(errno, strerror))
    return

def GET_DATASET(doc, path):
    dic = []
    tmppos = []
    pos = Hannanum().pos(doc)
    tagged_words = Kkma().pos(doc)
    for word in pos:
        if word[1] == 'N':
            tmppos.append(word)

    tmp_pos = tmppos

    for words in tmp_pos:
        for stop in stopwords:
            if unicode(words[0]) == unicode(stop):
                tmppos = remove_values_from_list(tmppos, words)

    cnt = Counter(tmppos)
    i = 1
    for freq_word in cnt.most_common(50):
        dic.append({'구분': 'unigram', '순위': str(i), '단어': freq_word[0][0], '횟수': str(freq_word[1])})
        i = i + 1

    words = [w for w, t in tagged_words]
    ignored_words = stopwords
    finder = collocations.BigramCollocationFinder.from_words(words)
    finder.apply_word_filter(lambda w: len(w) < 2 or w in ignored_words)
    finder.apply_freq_filter(int(dic[19]['횟수']))  # only bigrams that appear 3+ times

    for bigram in (finder.nbest(measures.pmi, 20)):
        dic.append({'구분': 'bigram', '순위': '1~20', '단어': unicode(bigram[0]) + unicode(bigram[1]),
                    '횟수': str(dic[19]['횟수']) + u"회 이상"})

    finder = collocations.TrigramCollocationFinder.from_words(words)
    finder.apply_word_filter(lambda w: len(w) < 2 or w in ignored_words)
    finder.apply_freq_filter(int(dic[19]['횟수']))  # only trigrams that appear 3+ times

    for trigram in (finder.nbest(measures.pmi, 20)):
        dic.append(
            {'구분': 'trigram', '순위': '1~20', '단어': unicode(trigram[0]) + unicode(trigram[1]) + unicode(trigram[2]),
             '횟수': str(dic[19]['횟수']) + u"회 이상"})

    csv_columns = ['구분', '순위', '단어', '횟수']

    csv_file = filename.split('.')[0] + "freq.csv"

    WriteDictToCSV(csv_file, csv_columns, dic, path)

def remove_values_from_list(the_list, val):
   return [value for value in the_list if value != val]

current_dir = os.path.dirname(os.path.abspath(__file__))    #Get current Path
current_dir = current_dir.replace('\\', '/')
txt_dir = current_dir+"/parse/txt/"
result_dir = current_dir+"/result/"
"""
exe_dir = current_dir + "/parse/hwp2txt.exe"
subprocess.call([exe_dir])
#When the pycharm or cmd tells you that you need Java to run, use the code belows

"""
jar_dir = current_dir + "/parse/hwp2txt.jar"
subprocess.call(['java', '-jar', jar_dir])

stopwords = []

if not os.path.exists(result_dir):          #Check whether there is a folder named result
    os.makedirs(result_dir)                 #If there's not, make a folder

stop_text = open("stopwords.txt", "r")

for sw in stop_text.readlines():
    sw = sw.decode('cp949')
    sw = re.sub('\\n', '', sw)
    stopwords.append(sw)

measures = collocations.BigramAssocMeasures()
measures2 = collocations.TrigramAssocMeasures()

for path, dirs, files in os.walk(txt_dir):
    for file_list in files:
        filename = file_list.decode('cp949')
        if filename.split('.')[1] == 'txt':
            doc = open((txt_dir+filename)).read()
            doc_type = chardet.detect(doc)

            if doc_type['encoding'] == 'utf-8':
                doc = unicode(doc, 'utf-8')
                GET_DATASET(doc, result_dir)
            elif doc_type['encoding'] == 'UTF-16LE':
                doc = doc.decode('utf-16').encode('utf-8')
                doc = unicode(doc, 'utf-8')
                GET_DATASET(doc, result_dir)


stop_text.close()
