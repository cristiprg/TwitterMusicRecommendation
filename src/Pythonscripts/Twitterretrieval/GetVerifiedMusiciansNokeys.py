__author__ = 'Andreas'

import twitter
import json
import io

def save_json(filename, data):
    with io.open('Twittermusic/artists/{0}.json'.format(filename),'w', encoding='utf-8') as f:
        f.write(unicode(json.dumps(data,ensure_ascii=False)))

def load_json(filename):
    with io.open('Twittermusic/artists/{0}.json'.format(filename),encoding='utf-8') as f:
        return f.read()


#Setting up Twitter API
auth = twitter.OAuth('', '', '', '')

twitter_api = twitter.Twitter(auth=auth)

#Verfied ID = 63796828
#Music ID = 2129624
#get lists by verified (looking for ID)

#lists = twitter_api.lists.list(_id=63796828)
members = twitter_api.lists.members(list_id=2129624, count=5000)

with open('data3.txt', 'w') as outfile:
    json.dump(members, outfile)


#save_json("test",members)





#print lists
print json.dumps(members, indent=1)