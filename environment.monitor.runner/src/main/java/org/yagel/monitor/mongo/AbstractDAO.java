package org.yagel.monitor.mongo;

import com.mongodb.client.MongoDatabase;

public abstract class AbstractDAO {

  protected MongoDatabase mongoDatabase;

  public AbstractDAO(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

}
