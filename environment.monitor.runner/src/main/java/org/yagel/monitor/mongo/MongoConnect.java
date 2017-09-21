package org.yagel.monitor.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.yagel.monitor.exception.DBConnectionException;

import javax.annotation.PreDestroy;

public class MongoConnect {

  private static final Logger log = Logger.getLogger(MongoConnect.class);
  private static final String MONITOR_DB = "monitor_tmp_newDomain";

  MongoDatabase database;
  MongoClient client;

  public MongoConnect() {

    String mongoConnectURI = System.getProperty("mongo.connect.uri", null);

    try {
      if (mongoConnectURI == null) {
        log.info("No connect URI provided using default mongo connection properties");
        init();
      }
      else {
        init(mongoConnectURI);
      }
    }
    catch (Exception e){
      String message = "Exception on mongoDB connection creation. ";
      log.error(message, e);
      throw new DBConnectionException(message,e);
    }
  }

  private void init(String connectURIStr){
    MongoClientURI clientURI = new MongoClientURI(connectURIStr);
    String dbName = clientURI.getDatabase() == null ? MONITOR_DB : clientURI.getDatabase();

    this.client = new MongoClient(clientURI);
    this.database = client.getDatabase(dbName);
  }

   private void init(){
    this.client = new MongoClient();
    this.database = client.getDatabase(MONITOR_DB);
  }

  public MongoDatabase getDatabase(){
     return database;
  }

  public  void close(){
    client.close();
  }


  @PreDestroy
  public void onDestroy(){
    log.info("Closing database connections ");
   close();
    log.info("Database connections closed");
  }
}
