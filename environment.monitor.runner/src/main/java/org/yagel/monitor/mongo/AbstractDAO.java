package org.yagel.monitor.mongo;

import com.mongodb.client.MongoDatabase;

public abstract class AbstractDAO {

  protected MongoDatabase mongoDatabase;

  public AbstractDAO(MongoConnect connect) {
    this.mongoDatabase = connect.getDatabase();
  }

}
