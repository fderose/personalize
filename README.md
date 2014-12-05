personalize
===========

Solr ValueSource plugin function to personalize search results based on a user's profile

The task is to construct a personalized search routine for a book company like Amazon. This search routine takes as input:

•	a set of search terms
•	the identity of the user issuing the search.

The routine returns a list of books ordered by a single score computed based on two criteria:

•	C1: how well the book’s title matches the search terms
•	C2: how relevant the book is to the user’s interests.

Book documents are stored in a Solr index and have the following three fields:

•	title
•	author
•	type (for example, financial, computer, espionage, gardening)

In addition, there exists a profile for each user, consisting of:

•	name
•	a list of types of books the user is interested in

These profiles are stored outside the Solr index in some appropriate data store/cache.

Suppose we have two users, Frank and Peter. Frank buys only financial books (not that they have helped him to be a good investor), so Frank’s profile looks like:

•	name: Frank
•	list of types: financial

Peter, on the other hand, loves Pussy Galore and is a big fan of spy novels, so Peter’s profile looks like:

•	name: Peter
•	list of types: espionage

Now, suppose that Frank issues a search for books by the supplying the search term “bond.” Consideration of criterion C1 (how well the book title matches the search terms) suggests that two books are candidates:

•	title: The British Bond Market
•	author: George Banks
•	type: Financial

•	title: The Name’s Bond
•	author: Ian Fleming
•	type: espionage

But, consideration of criterion C2 (how relevant the book is to the user’s interests) suggests that the first book would be better suited to Frank’s interests and should be returned at the top of the list. If Peter issues the same search, the order should be reversed.

We can accomplish this in Solr by constructing the following query:

q=title:bond _val_:”personalize($user, type)”&user=<username>

where

•	bond is the search term
•	<username> is the name of the user issuing the search, Frank or Peter
•	user=<username> is an assignment of the user name to the local variable user
•	$user is a dereferencing of that local variable user
•	type is the type field from the book document
•	personalize is a function that we have written and plugged in to Solr by extending the Solr ValueSource and ValueSourceParser classes.

The personalize function uses the user parameter as a key to look up the user’s profile. It then looks to see if the type passed in as the second parameter is contained in the list of types of book the user is interested in. If it is, the personalize function returns a boost. If not, it returns 0.

The Solr query infrastructure takes care of combining the returned boost with the relevancy score based on how well the book title matched the search term “bond.”

The end result is that the score of books matching the search terms is boosted by the personal preferences of the user issuing the search.
