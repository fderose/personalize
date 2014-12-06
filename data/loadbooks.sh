URL=http://localhost:8983/solr/update/json
curl $URL -H 'Content-type:application/json' -d '
[
  {"id":"1","title":"The British Bond Market","author":"George Banks","category":"financial"},
  {"id":"2","title":"The Name is Bond","author":"Ian Fleming","category":"espionage"}
]'
curl $URL?commit=true

