1.) Build the source code into a jar.

2.) Add the lines in the solrconfig.xml file in this directory to the
solrconfig.xml file in your Solr instance..

3.) Start the Solr instance.

4.) Go to the Solr admin console, select the relevant document collection,
and the Query page.

5.) On the Query page:

  -- add the following string in the "q" field:

    *:* _val_:"personalize($user, type)"

  -- add the following string in the "Raw Query Parameters" field:

    user="frank"

6.) Execute the query. You should be able to set a break point inside of
the PersonalizeFunction.getValues method and step through the code.
