# -*- coding: utf-8 -*-
from collections import Counter

from konlpy.corpus import kolaw
from konlpy.tag import Hannanum
from konlpy.utils import concordance, pprint
from matplotlib import pyplot
from nltk.corpus import stopwords

tmppos=[]
stopwords = [u"위원님", u"위원", u"것", u"말씀", u"우리", u"그것", u"이것", u"존경", u"이", u"저", u"저희", u"때문", u"문제", u"생각", u"미래부", u"장관님", u"거", u"때",
             u"다음", u"일", u"수", u"정부", u"정책"]
doc = unicode(open(u'회의록.txt').read(), 'utf-8')
pos = Hannanum().pos(doc)
for word in pos:
    if word[1] == 'N':
        tmppos.append(word)
for word in tmppos:
    for stopword in stopwords:
        if word[0] == stopword:
            tmppos.remove(word)

cnt = Counter(tmppos)

f = open("freq.txt", "wb")
for freq_word in cnt.most_common(50):
    f.write(freq_word[0][0].encode('utf-8')+"\n")
f.close()
print('\nTop 20 frequent morphemes:'); pprint(cnt.most_common(50))