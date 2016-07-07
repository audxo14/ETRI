# -*- coding: utf-8 -*-
from collections import Counter

from konlpy.corpus import kolaw
from konlpy.tag import Hannanum
from konlpy.utils import concordance, pprint
from matplotlib import pyplot


doc = unicode(open('test.txt').read(), 'utf-8')
pos = Hannanum().pos(doc)
cnt = Counter(pos)

print('\nTop 20 frequent morphemes:'); pprint(cnt.most_common(20))