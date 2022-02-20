import numpy as np
import pandas as pd
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import LinearSVC
import re
import nltk
from nltk import word_tokenize
from nltk.stem.snowball import SnowballStemmer

#Define stemming (not included by default in the scikit-learn codebase)
class LemmaTokenizer:
     def __init__(self):
            self.stm = SnowballStemmer("english")
     def __call__(self, doc):
        return [self.stm.stem(t) for t in word_tokenize(doc)]

encoder = LabelEncoder()

def textCleaning(text):
    text = re.sub("\'", "", text)
    text = re.sub("[^a-zA-Z]", " ", text)
    text = ' '.join(text.split())
    text = text.lower()
    return text

def loadDataset(path):
    moviesDataset = pd.read_csv(path, encoding="ISO-8859-1", on_bad_lines='skip', low_memory=False)
    moviesDataset.drop_duplicates(subset=['tmdb_id'], keep="first", inplace=True)
    reducedDataset = moviesDataset[['genre_ids', 'overview']]
    reducedDataset = reducedDataset[~reducedDataset.genre_ids.str.len().eq(0)]
    reducedDataset = reducedDataset.replace(r'^\s*$', np.nan, regex=True)
    reducedDataset = reducedDataset.dropna(axis=0, how='any')
    reducedDataset = reducedDataset[~(reducedDataset.overview.str.len() <= 30)]
    reducedDataset = reducedDataset.reset_index(drop=True)

    dropGenres = ['Adventure', 'Mystery', 'Western', 'Science Fiction', 'Romance', 'Fantasy', 'Thriller', 'Family',
                  'War', 'History', 'Music', 'TV Movie']
    reducedDataset = reducedDataset[~reducedDataset['genre_ids'].isin(dropGenres)]

    #Text cleaning from bracket, point, to lowercase...
    reducedDataset['overview'] = reducedDataset['overview'].apply(lambda x: textCleaning(x))
    reducedDataset['overview'] = reducedDataset['overview'].apply(lambda x: removeStopWords(x))
    #Map genres with id
    reducedDataset['genre_ids'] = encoder.fit_transform(reducedDataset.genre_ids)

    return reducedDataset

def removeStopWords(text):
    #nltk.download('stopwords') #Download the first time
    stopwords = nltk.corpus.stopwords.words('english')
    stopwords += ['one','life', 'love', 'two', 'young', 'man', 'new', 'womand', 'get', 'family', 'film', 'also', 'set', 'become',
                'comes', 'comes', 'best', 'even', 'movie', 'de', 'need', 'needs', 'needed', 'might', 'although', 'along', 'afterward', 'afterwards', 'already',
                 'always', 'another', 'anothers', 'anytime', 'anything', 'anywhere', 'anyway', 'becoming', 'becomes', 'else',
                'another', 'elsewhere', 'everyone', 'everything', 'every', 'many', 'meanwhile', 'moreover', 'nothing', 'nothings',
                'nowhere', 'otherwise', 'perhaps', 'please', 'since', 'someone', 'something', 'sometime', 'sometimes', 'somewhere',
                'thereby', 'therefore', 'whatever', 'whenever', 'whereabouts', 'whereby']
    no_stopword_text = [w for w in text.split() if not w in stopwords]
    return ' '.join(no_stopword_text)

def low_stopwords(x):
    return x.lower()

def exportClassifier():
    dataset = loadDataset("./data/dataset.csv")

    tfidf_vectorizer = TfidfVectorizer(min_df=2, ngram_range=(1, 2), tokenizer=LemmaTokenizer())
    X_train_tfidf = tfidf_vectorizer.fit_transform(dataset['overview'])

    select_k = SelectKBest(chi2, k=20000)
    selected_features = select_k.fit_transform(X_train_tfidf, dataset['genre_ids'])

    clf = LinearSVC().fit(selected_features, dataset['genre_ids'])
    joblib.dump(encoder, './classifier/encoder.pkl')
    joblib.dump(tfidf_vectorizer, './classifier/tfidf_vectorizer.pkl')
    joblib.dump(select_k, './classifier/select_k.pkl')
    joblib.dump(clf, './classifier/linearsvc.pkl')
    print("Models exported!")

if __name__ == "__main__":
    exportClassifier()
