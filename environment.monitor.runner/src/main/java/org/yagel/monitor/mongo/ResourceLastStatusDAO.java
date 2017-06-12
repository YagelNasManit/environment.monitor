package org.yagel.monitor.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.yagel.monitor.ResourceStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ResourceLastStatusDAO {

  private final static String COLLECTION_NAME = "ResourceLastStatus";
  private MongoCollection<Document> thisCollection;


  public ResourceLastStatusDAO(MongoDatabase db) {
    thisCollection = db.getCollection(COLLECTION_NAME);
  }

  /**
   * remove database object from collection
   *
   * @param environmentName parameter for database object deletion
   */
  public void delete(String environmentName) {
    thisCollection.deleteMany(new Document().append("environmentName", environmentName));
  }

  /**
   * insert into ResourceLastStatus collection new information. But firstly delete previous db object
   *
   * @param environmentName parameter for database object insertion / deletion
   * @param resources       parameter for database object insertion
   */
  public synchronized void insert(String environmentName, Collection<ResourceStatus> resources) {
    delete(environmentName);
    List<Document> dbResources = new ArrayList<>();
    for (ResourceStatus resource : resources) {
      Document ls = DocumentMapper.resourceStatusToDocument(environmentName, resource);
      dbResources.add(ls);
    }

    thisCollection.insertMany(dbResources);
  }

  /**
   * remove database object from collection
   *
   * @param environmentName parameter for database object deletion
   */
  public List<ResourceStatus> find(String environmentName) {
    List<ResourceStatus> resources;

    resources = thisCollection.find(new Document().append("environmentName", environmentName))
        .map(DocumentMapper::resourceStatusFromDocument).into(new ArrayList<>());

    return resources;
  }

  public List<ResourceStatus> find(String environmentName, Set<String> resourceIds) {
    List<ResourceStatus> resources;

    BasicDBList resourcesDBList = new BasicDBList();
    resourcesDBList.addAll(resourceIds);

    BasicDBObject envDBNameClause = new BasicDBObject("environmentName", environmentName);
    BasicDBObject inDBClause = new BasicDBObject("$in", resourcesDBList);
    BasicDBObject resourceDBClause = new BasicDBObject("resourceId", inDBClause);

    BasicDBList andDBClause = new BasicDBList();
    andDBClause.add(envDBNameClause);
    andDBClause.add(resourceDBClause);

    BasicDBObject root = new BasicDBObject("$and", andDBClause);

    resources = thisCollection.find(root)
        .map(DocumentMapper::resourceStatusFromDocument).into(new ArrayList<>());


    return resources;
  }

}
