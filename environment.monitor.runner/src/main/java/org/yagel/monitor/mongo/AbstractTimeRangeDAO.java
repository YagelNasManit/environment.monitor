package org.yagel.monitor.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.yagel.monitor.utils.DataUtils;

import java.util.Date;

public abstract class AbstractTimeRangeDAO extends AbstractDAO {

  protected final static String COLLECTION_NAME = "ResourceMonthDetail%s";
  protected MongoCollection<Document> thisCollection;
  protected int thisDate = -1;

  public AbstractTimeRangeDAO(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
  }

  protected void switchCollection(Date date) {
    int toDate = DataUtils.joinYearMonthValues(date);
    if (toDate == thisDate)
      return;

    String collectionName = String.format(COLLECTION_NAME, toDate);
    thisCollection = mongoDatabase.getCollection(collectionName);
  }
}
