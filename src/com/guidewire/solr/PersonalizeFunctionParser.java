package com.guidewire.solr;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class PersonalizeFunctionParser extends ValueSourceParser {
  @Override
  public ValueSource parse(FunctionQParser functionQParser) throws SyntaxError {
    String user = functionQParser.parseArg();
    ValueSource inputValueSource = functionQParser.parseValueSource();
    return new PersonalizeFunction(user, inputValueSource);
  }
}
