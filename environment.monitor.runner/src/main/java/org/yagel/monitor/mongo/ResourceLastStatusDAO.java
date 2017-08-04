package org.yagel.monitor.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.yagel.monitor.ResourceStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceLastStatusDAO extends AbstractDAO {

  private final static String COLLECTION_NAME = "ResourceLastStatus";
  private MongoCollection<Document> thisCollection;


  public ResourceLastStatusDAO(MongoDatabase db) {
    super(db);
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
  public synchronized void insert(final String environmentName, final Collection<ResourceStatus> resources) {
    delete(environmentName);

    List<Document> dbResources = resources.stream()
        .map(rs -> DocumentMapper.resourceStatusToDocument(environmentName, rs))
        .collect(Collectors.toList());

    thisCollection.insertMany(dbResources);
  }

  /**
   * remove database object from collection
   *
   * @param environmentName parameter for database object deletion
   */
  public List<ResourceStatus> find(String environmentName) {
    List<ResourceStatus> resources = thisCollection.find(new Document().append("environmentName", environmentName))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

    return resources;
  }

  public List<ResourceStatus> find(Collection<String> environmentNames) {
    List<ResourceStatus> resources = thisCollection.find(Filters.in("environmentName", environmentNames))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

    return resources;
  }

  public List<ResourceStatus> find(String environmentName, Set<String> resourceIds) {
    Bson query = Filters.and(
        Filters.eq("environmentName", environmentName),
        Filters.in("resource.resourceId", resourceIds)
    );

    List<ResourceStatus> resources = thisCollection.find(query)
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

    return resources;
  }

}
