package com.guidewire.solr;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PersonalizeFunction extends ValueSource {

  protected final String user;
  protected final ValueSource inputValueSource;

  private enum CategoryType {
    financial,
    espionage,
    computer,
    romance
  }

  // The profiles data store is a map from user name to a map from category type to the number of times the user has bought
  // an item in that category. The more items the user has bought in the category, the higher the boost should be.
  private final Map<String, Map<CategoryType,Integer>> profiles = new HashMap<>();

  private void loadProfiles() {
    Map<CategoryType, Integer> categoryMap = new HashMap<>();
    categoryMap.put(CategoryType.espionage, 100);
    categoryMap.put(CategoryType.computer, 20);
    profiles.put("Peter", categoryMap);

    categoryMap = new HashMap<>();
    categoryMap.put(CategoryType.financial, 75);
    categoryMap.put(CategoryType.computer, 10);
    categoryMap.put(CategoryType.romance, 100);
    profiles.put("Frank", categoryMap);
  }

  public PersonalizeFunction(String user, ValueSource inputValueSource) {
    this.user = user;
    this.inputValueSource = inputValueSource;
    loadProfiles();
  }

  @Override
  public FunctionValues getValues(final Map map, final AtomicReaderContext atomicReaderContext) throws IOException {

    final FunctionValues inputValues = inputValueSource.getValues(map, atomicReaderContext);

    return new IntDocValues(this) {
      @Override
      public int intVal(int doc) {
        if (profiles.containsKey(user)) {
          Map<CategoryType, Integer> categoryMap = profiles.get(user);
          String inputValue = inputValues.strVal(doc);
          if (inputValue != null) {
            CategoryType categoryType = CategoryType.valueOf(inputValue);
            if (categoryMap.containsKey(categoryType)) {
              return categoryMap.get(categoryType);
            }
          }
        }
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
    return "personalize";
  }
}
