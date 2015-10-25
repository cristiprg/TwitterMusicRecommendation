__author__ = 'Andreas'

import json
import pickle

#load data
data = open('data3.txt','r')
#read data (string)
data = data.readline()
#serialize
json_data = json.loads(data)

#create id list
id = [];
#loop over items in serialized json data (all users)
for item in json_data['users']:
    #append id with from within user tags
    id.append(item['id'])

#remove last 14 bringing us to 360 artists
for i in range(0,14):
    id.pop(-1)

#save list
pickle.dump(id,open('artistIDs.txt', 'w'))
