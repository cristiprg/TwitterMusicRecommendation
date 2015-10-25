GetVerifiedMusiciansNokeys.py
retreieves a JSON document with up to 5000 artists (only 374 in the list) with their details such as ID description etc. saves this JSON to a document called data3

GetMusiciansTimelines.py
Saves ID's from data3 in a document called ArtistIDs, uses pickle to save them.

RetrieveArtistTimelinesNokeys.py
Retrieves tweets from artists in the artistIDs.txt folder and saves tweets as JSON in a folder artisttweets for later use (this is the data used in our project)

OBSERVE: To get GetVerifiedMusiciansNokeys.py and RetrieveArtistTimelinesNokeys.py to work you need to input private keys from "your" twitter developer account to get access to the API. this is done in line 17 of GetVerifiedMusiciansNokeys and line 8 of Retrieve ArtistTimelinesNokeys