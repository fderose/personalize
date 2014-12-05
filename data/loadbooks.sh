URL=http://localhost:8983/solr/update/json
curl $URL -H 'Content-type:application/json' -d '
[
  {"id":"1","title":"The British Bond Market","author":"George Banks"},
  {"id":"2","title":"The Name is Bond","author":"Ian Fleming"}
]'
curl $URL?commit=true

