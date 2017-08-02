package org.yagel.monitor.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.yagel.monitor.exception.DiagnosticException;

public class MongoConnector {

  private final static String MONITOR_DB = "monitor_tmp_newDomain";
  private final static Logger log = Logger.getLogger(MongoConnector.class);
  private static MongoConnector connector;

  private MongoDatabase db;
  private MongoClient client;


  private MongoConnector() throws DiagnosticException {
    client = new MongoClient();
    db = client.getDatabase(MONITOR_DB);

  }

  private MongoConnector(String connectURIStr) throws DiagnosticException {
    MongoClientURI clientURI = new MongoClientURI(connectURIStr);
    client = new MongoClient(clientURI);
    String dbName = clientURI.getDatabase() == null ? MONITOR_DB : clientURI.getDatabase();
    db = client.getDatabase(dbName);

  }

  public static MongoConnector getInstance() {
    if (connector == null) {
      try {
        String mongoConnectURI = System.getProperty("mongo.connect.uri", null);
        if (mongoConnectURI == null)
          connector = new MongoConnector();
        else
          connector = new MongoConnector(mongoConnectURI);
      } catch (Exception e) {
        log.error("Exception on mongoDB connection creation. ", e);
        throw new RuntimeException(e);
      }
    }
    return connector;
  }


  public ResourceLastStatusDAO getLastStatusDAO() {
    return new ResourceLastStatusDAO(db);
  }

  public ResourceMonthDetailDAO getMonthDetailDAO() {
    return new ResourceMonthDetailDAO(db);
  }

  public ResourceDAO getResourceDAO() {
    return new ResourceDAO(db);
  }


  public void close() {
    client.close();
    connector = null;
  }


}

