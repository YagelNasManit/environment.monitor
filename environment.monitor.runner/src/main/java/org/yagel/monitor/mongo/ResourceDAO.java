package org.yagel.monitor.mongo;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static org.yagel.monitor.mongo.DocumentMapper.resourceToDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.yagel.monitor.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceDAO extends AbstractDAO {

  private final static String COLLECTION_NAME = "Resources";
  private MongoCollection<Document> thisCollection;

  public ResourceDAO(MongoDatabase db) {
    super(db);
    thisCollection = db.getCollection(COLLECTION_NAME);
  }


  /**
   * Inserts new resource to DB, or else updates existing resource description if existing ID was used
   *
   * @param resource resource to be upserted
   */
  public synchronized void insert(Resource resource) {
    this.thisCollection.replaceOne(
        eq("_id", resource.getId()),
        resourceToDocument(resource),
        new UpdateOptions().upsert(true));
  }


  /**
   * Inserts new resources to DB, or else updates existing ones descriptions if existing ID`s was used
   *
   * @param resource resources to be upserted
   */
  public synchronized void insert(Set<Resource> resource) {
    List<ReplaceOneModel<Document>> upserts =
        resource.stream()
            .map(
                res -> new ReplaceOneModel<Document>(
                    eq("_id", res.getId()),
                    resourceToDocument(res),
                    new UpdateOptions().upsert(true)
                )
            ).collect(Collectors.toList());

    this.thisCollection.bulkWrite(upserts);
  }


  /**
   * Finds single resource by id provided
   *
   * @param resourceId to be fetched
   * @return matched by id resource
   */
  public synchronized Resource find(String resourceId) {
    List<Resource> resources = thisCollection.find(eq("_id", resourceId))
        .limit(1)
        .map(DocumentMapper::resourceFromDocument)
        .into(new ArrayList<>());

    return resources.get(0);
  }


  /**
   * Finds multiple resources by id`s provided
   *
   * @param resourceIds to be fetched
   * @return matched by id`s resources
   */
  public synchronized Set<Resource> find(Set<String> resourceIds) {
    return thisCollection.find(in("_id", resourceIds))
        .map(DocumentMapper::resourceFromDocument)
        .into(new HashSet<>());
  }
}
