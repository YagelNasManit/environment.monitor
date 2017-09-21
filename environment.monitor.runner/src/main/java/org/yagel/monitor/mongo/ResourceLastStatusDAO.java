package org.yagel.monitor.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.yagel.monitor.ResourceStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceLastStatusDAO extends AbstractDAO {

  private static final String COLLECTION_NAME = "ResourceLastStatus";
  private MongoCollection<Document> thisCollection;


  public ResourceLastStatusDAO(MongoConnect connect) {
    super(connect);
    thisCollection = mongoDatabase.getCollection(COLLECTION_NAME);
  }


  /**
   * Remove all last statuses for particular environment
   *
   * @param environmentName name of environment to remove statuses for
   */
  public void delete(String environmentName) {
    thisCollection.deleteMany(eq("environmentName", environmentName));
  }


  /**
   * Replace existing last statuses for environment by new ones
   *
   * @param environmentName environment to update statuses
   * @param resources       new statuses to be set up
   */
  public synchronized void insert(final String environmentName, final Collection<ResourceStatus> resources) {
    delete(environmentName);

    List<Document> dbResources = resources.stream()
        .map(rs -> DocumentMapper.resourceStatusToDocument(environmentName, rs))
        .collect(Collectors.toList());

    thisCollection.insertMany(dbResources);
  }


  /**
   * Get last statuses for environment resources
   *
   * @param environmentName environment to fetch statuses for
   */
  public List<ResourceStatus> find(String environmentName) {
     return thisCollection.find(new Document().append("environmentName", environmentName))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());
  }


  /**
   * Get last statuses for multiple environments
   *
   * @param environmentNames environments to fetch statuses for
   * @return
   */
  public List<ResourceStatus> find(Collection<String> environmentNames) {
    return thisCollection.find(in("environmentName", environmentNames))
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());
  }


  /**
   * Get last statuses for particular environment and defined resources
   *
   * @param environmentName environment to fetch statuses for
   * @param resourceIds     resources to fetch statuses for
   * @return
   */
  public List<ResourceStatus> find(String environmentName, Set<String> resourceIds) {
    Bson query = and(
        eq("environmentName", environmentName),
        in("resource.resourceId", resourceIds)
    );

    return thisCollection.find(query)
        .map(DocumentMapper::resourceStatusFromDocument)
        .into(new ArrayList<>());

  }

}
