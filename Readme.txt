This project is an engine for retrieving documents. It was built with the MVVM model and using BM25 formula for ranking.

The model layer has several important parts:

ReadFile class - receives a path of the folder with documents and reads it. the folder should be unzip. 
An example of a corpus that you can use attached as a zip to this project.

Parse Class - In this part we made dataprepartion and “cleaned” the corpus.
Some of the actions that we did are: removing stop words according to a stop-words file that is attached here, separate numbers and units of measurement, change expresion like “6 percents” into “6%”, convert date into uniform format, etc. 
In addition we saved a dictionary of words and the number of its appearance (for later calculation). 
We also allow the user to make stemming to the dictionary using Porter’s stemmer.

Indexer Class - This class uses the parser to make inverted index. The inverted index includes the dictionary and posting files. 
The dictionary is the same as described in the parse class, and the posting files are text files which are saved in the hard disk and used for collecting information for each term (including term, tf, df, list of docs it appeared+number of appearance in each document) and information for each document (most frequent word, number of unique words).

Ranker - receives query and makes preprocessing of it (depends on the user - may use stemming and search synonym words) and then calculate for each document score of relevancy by using BM25 formula. The most relevant documents are shown

Searcher - activate the ranker.

UML link - https://lucid.app/lucidchart/invitations/accept/d451307e-1fb3-43e5-8699-734d42124721
 
