__author__ = 'Andreas'

from collections import Counter, defaultdict
from machine_learning import split_data
import math, random, re, glob

#Tokenize
def tokenize(message):
    message = message.lower()                       # convert to lowercase
    all_words = re.findall("[a-z0-9']+", message)   # extract the words
    return set(all_words)                           # remove duplicates


def count_words(training_set):
    """training set consists of pairs (tweets, is_solo_artist)"""
    #Creates dictionary counts containing words and their frequencies.
    counts = defaultdict(lambda: [0, 0])
    for tweets, is_solo_artist in training_set:
        #print(is_solo_artist)
        for word in tokenize(tweets):
            counts[word][0 if is_solo_artist else 1] += 1

    #removes most uncommon words (without this threshhold our value for probabilities are so low that they are essentially 0 and a division by zero error will occur.
    #Probably this is due to the fact that there are a lot of "unique" words in tweets, usernames, links, slang etc.
    frequency_threshold = 10 #Change this value.
    for key, value in counts.items():
        if value[0] < frequency_threshold and value[1] < frequency_threshold:
            del counts[key]

    return counts

def word_probabilities(counts, total_solo_artists, total_bands, k=0.5):
    """turn the word_counts into a list of triplets
    w, p(w | solo_artist) and p(w | ~solo_artist)"""
    return [(w,
             (solo_artist + k) / (total_solo_artists + 2 * k),
             (band + k) / (total_bands + 2 * k))
             for w, (solo_artist, band) in counts.iteritems()]

def solo_artist_probability(word_probs, message):
    """
    Returns probability that a given set of tweets is a solo artist.
    """

    message_words = tokenize(message)
    log_prob_if_solo_artist = log_prob_if_band = 0.0


    #add up logs instead of actual probabilities and convert later to save resources.
    for word, prob_if_solo_artist, prob_if_band in word_probs:

        # for each word in the message,
        # add the log probability of seeing it
        if word in message_words:
            log_prob_if_solo_artist += math.log(prob_if_solo_artist)
            log_prob_if_band += math.log(prob_if_band)

        # for each word that's not in the message
        # add the log probability of _not_ seeing it
        else:
            log_prob_if_solo_artist += math.log(1.0 - prob_if_solo_artist)
            log_prob_if_band += math.log(1.0 - prob_if_band)

    #convert logs back to actual probabilities
    prob_if_solo_artist = math.exp(log_prob_if_solo_artist)
    prob_if_band = math.exp(log_prob_if_band)
    return prob_if_solo_artist / (prob_if_solo_artist + prob_if_band)


class NaiveBayesClassifier:

    def __init__(self, k=0.5):
        self.k = k
        self.word_probs = []

    def train(self, training_set):

        # count solo_artist and band messages
        num_solo_artists = len([is_solo_artist
                         for message, is_solo_artist in training_set
                         if is_solo_artist])
        num_bands = len(training_set) - num_solo_artists

        # run training data through our "pipeline"
        word_counts = count_words(training_set)
        self.word_probs = word_probabilities(word_counts,
                                             num_solo_artists,
                                             num_bands,
                                             self.k)

    def classify(self, message):
        return solo_artist_probability(self.word_probs, message)

    def classifyanartist(self, message):
        p = solo_artist_probability(self.word_probs, message)
        if p > self.k:
            output = "your text is classified as: solo artist"
        else:
            output = "your text is classified as: band"
        return output, p


def get_subject_data(path):

    data = []

    # glob.glob returns every filename that matches the wildcarded path
    for fn in glob.glob(path):
        #if band not in filename = is_solo_artist true
        is_solo_artist = "bands" not in fn #band not in filename e.g. if not in band folder everything in band is not solo_artist.

        #appends a list to "data" cotaining the tweets from each artist and whether or not this artist is a solo-artist or not.
        with open(fn,'r') as file:
            for line in file:
                data.append((line, is_solo_artist))


    return data

def p_solo_artist_given_word(word_prob):
    word, prob_if_solo_artist, prob_if_band = word_prob
    return prob_if_solo_artist / (prob_if_solo_artist + prob_if_band)

def train_and_test_model(path):

    data = get_subject_data(path)
    random.seed(0)      # just so you get the same answers as me
    train_data, test_data = split_data(data, 0.75) #splits data, 25% training, 75% test (used on both solo_artist and band)
    #print train_data
    #print test_data
    classifier = NaiveBayesClassifier()
    classifier.train(train_data)

    classified = [(tweets, is_solo_artist, classifier.classify(tweets))
              for tweets, is_solo_artist in test_data]

    counts = Counter((is_solo_artist, solo_artist_probability > 0.5) # (actual, predicted)
                     for _, is_solo_artist, solo_artist_probability in classified)
    #Prints stats...
    print counts


    words = sorted(classifier.word_probs, key=p_solo_artist_given_word)

    solo_artist_words = words[-5:]
    band_words = words[:5]

    print "most_probable_solo_artist_words", solo_artist_words
    print "most_probable_band_words", band_words

#Train on preprocessed data
if __name__ == "__main__":
    train_and_test_model(r"C:\Naive Bayes Data\processedtweets\*\*")

"""
#Train on unprocessed data
if __name__ == "__main__":
    train_and_test_model(r"C:\Naive Bayes Data\unprocessedtweets\*\*")
"""