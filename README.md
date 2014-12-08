# personalize: A Solr ValueSource plugin function to personalize search results based on a user's profile

The task is to build a personalized search routine for a book company like Amazon. (The same routine could be utilized by a video company, like Netflix.) 

This search routine takes as input:

  * a set of search terms
  * the identity of the user issuing the search.

The routine outputs a list of books (videos) ordered by a score computed based on two criteria:

  * C1: how well the book’s (video's) title matches the search terms
  * C2: how relevant the book is to the user’s general interests.

Book documents are stored in a Solr index and have the following three fields:

  * title
  * author
  * category (for example, financial, computer, espionage, gardening)

Note: In the default example in the Solr 4.10.2 release, the documents represented by the *schema.xml* file are books with exactly these fields, so we can use the *schema.xml* file in the default example without any changes.

In addition, there exists a profile for each user, consisting of:

  * name (primary key)
  * a list of categories of books the user is interested in

These profiles are stored outside the Solr index in some appropriate data store/cache.

Suppose we have two users, Frank and Peter. Frank buys only financial books (not that they have helped him to be a good investor), so Frank’s profile looks like:

  * name: Frank
  * list of categories: financial

Peter, on the other hand, loves Pussy Galore and is a big fan of spy novels, so Peter’s profile looks like:

  * name: Peter
  * list of categories: espionage

Now, suppose that Frank issues a search for books by the supplying the search term “bond.” Consideration of criterion C1 (how well the book title matches the search terms) suggests that two books should be returned by this search:

    
        title: The British Bond Market
        author: George Banks
        category: Financial
    
        title: The Name’s Bond
        author: Ian Fleming
        category: espionage
    

But, it is unclear based just on criterion C1 how these results should be ordered when returning them to Frank since both books contain a single term in the title field that matches the search term. On the other hand, consideration of criterion C2 (how relevant the book is to the user’s interests) suggests that the first book is better suited to Frank’s interests and should be returned at the top of the list. If Peter issues the same search, the order should be reversed.

We can accomplish this in Solr by constructing the following query:

    q=title:bond _val_:”personalize($user, category)”&user=<username>

where

  * __``bond``__ is the search term
  * __``<username>``__ is the name of the user issuing the search, Frank or Peter
  * __``user=<username>``__ is an assignment of the user name to the local variable __``user``__
  * __``$user``__ is a dereferencing of that local variable __``user``__, which evaluates to the user name
  * __``category``__ is the category field from the book document
  * __``personalize``__ is a function that we have written and plugged in to Solr by extending the Solr/Lucene [ValueSource](http://lucene.apache.org/core/4_10_2/queries/org/apache/lucene/queries/function/ValueSource.html?is-external=true) and [ValueSourceParser](http://lucene.apache.org/solr/4_10_2/solr-core/org/apache/solr/search/ValueSourceParser.html) classes.

The __``personalize``__ function uses the value stored in the __``user``__ parameter as a key to look up the user’s profile. It then looks to see if the value stored in the __``category``__ parameter is contained in the list of categories of book the user is interested in. If it is, the __``personalize``__ function returns a boost. If not, it returns 0.

The Solr query infrastructure then calculates a final score by combining the boost returned by the __``personalize``__ function with the relevancy score based on how well the book title matched the search term “bond” (this relevancy score having been calculated by Lucene's [Similarity](http://lucene.apache.org/core/4_10_2/core/org/apache/lucene/search/similarities/Similarity.html) class).

The end result is that the scores of books matching the search terms are boosted (or not) by the personal preferences of the user issuing the search.
