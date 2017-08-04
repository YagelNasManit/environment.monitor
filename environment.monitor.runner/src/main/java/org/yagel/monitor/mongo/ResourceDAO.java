package org.yagel.monitor.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.yagel.monitor.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    Document dbResource = DocumentMapper.resourceToDocument(resource);

    this.thisCollection.updateOne(
        new Document("_id", resource.getId()),
        new Document("$set", dbResource), new UpdateOptions().upsert(true));
  }

  public synchronized void insert(Set<Resource> resource) {
    // TODO replace with Bulk write
    resource.forEach(this::insert);

  }

  public synchronized Resource find(String resourceId) {
    Document dbResource = new Document("_id", resourceId);

    List<Resource> resources = thisCollection.find(dbResource)
        .limit(1)
        .map(DocumentMapper::resourceFromDocument)
        .into(new ArrayList<>());

    return resources.get(0);
  }

  public synchronized Set<Resource> find(Set<String> resourceIds) {
    return thisCollection.find(Filters.in("_id", resourceIds))
        .map(DocumentMapper::resourceFromDocument)
        .into(new HashSet<>());
  }
}
