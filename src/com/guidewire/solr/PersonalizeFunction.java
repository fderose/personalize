package com.guidewire.solr;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;

import java.io.IOException;
import java.util.Map;

public class PersonalizeFunction extends ValueSource {

  static {
    System.out.println("I can set a breakpoint here to see when the class is loaded");
  }

  protected final String user;
  protected final ValueSource inputValueSource;
  
  public PersonalizeFunction(String user, ValueSource inputValueSource) {
    this.user = user;
    this.inputValueSource = inputValueSource;
  }

  @Override
  public FunctionValues getValues(Map map, AtomicReaderContext atomicReaderContext) throws IOException {
    return new IntDocValues(this) {
      @Override
      public int intVal(int doc) {
        return 0;
      }
    };
  }

  @Override
  public boolean equals(Object o) {
    if (this.getClass() != o.getClass()) {
      return false;
    }
    PersonalizeFunction that = (PersonalizeFunction) o;
    return this.inputValueSource.equals(that.inputValueSource) &&
      this.user.equals(that.user);
  }

  @Override
  public int hashCode() {
    long combinedHashes = this.inputValueSource.hashCode() + user.hashCode();
    return (int) (combinedHashes ^ (combinedHashes >>> 32));
  }

  @Override
  public String description() {
    return "The Guidewire personalization function";
  }
}
