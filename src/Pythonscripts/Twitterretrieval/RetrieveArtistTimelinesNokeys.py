__author__ = 'Andreas'
import twitter
import pickle
import json
import time
#Setting up Twitter API
#inputkeys needed to authorize access to API here
auth = twitter.OAuth('', '', '', '')

twitter_api = twitter.Twitter(auth=auth)

#read IDs
# retrieves tweets from first 360 artists based on their ID which is saved in artistIDs.txt which is retrieved by GetVerifiedMusicians.py
ids = pickle.load(open('artistIDs.txt','r'))
start = 0
end = 179
while end < 360:

    #for first 180 ids
    for i in range(start, end):
    #retrieve 200 tweets from that ids(artist) timeline
        print "twitter ID: %d" % ids[i]
        print "i: %d" % i
        try:
            tosave = twitter_api.statuses.user_timeline(user_id=ids[i], count=200)
        except twitter.api.TwitterHTTPError:
            print "%d is unauthorized" % ids[i]
            continue

        if tosave is None:
            print "%d is empty" % ids[i]
        else:
            with open('C:/Users/Andreas/PycharmProjects/untitled/artisttweets/Tweets_from_%d.json' % ids[i], 'w') as outfile:
                json.dump(tosave, outfile)

    start += 180
    end += 180
    time.sleep(900)
    #use time.sleep for longer inputs.