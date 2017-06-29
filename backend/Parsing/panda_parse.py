import pandas as pd


categoryName = 'sport?page=2'
url = 'http://www.calorizator.ru/product/' + categoryName

for i, df in enumerate(pd.read_html(url)):
    df.to_csv('sport3_%s.csv' % i, encoding='utf-8')