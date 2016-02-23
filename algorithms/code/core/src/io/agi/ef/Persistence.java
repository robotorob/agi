package io.agi.ef;

import io.agi.core.data.Data;
import io.agi.ef.serialization.JsonData;
import io.agi.ef.serialization.JsonEntity;
import io.agi.ef.serialization.JsonNode;

import java.util.Collection;

/**
 * Created by dave on 14/02/16.
 */
public interface Persistence {

//    Entity --< Properties
//           --< Data
//    Entity --- name, type, children, node
//               Could be JSON string for entities: { name: xxx, type: yyy } etc


//    Collection<String> getChildren(String key);
//    Collection<String> getNodes();

    // Nodes
    Collection< JsonNode > getNodes(); // list all
    void setNode( JsonNode e );
    JsonNode getNode( String nodeName );
    void removeNode(String nodeName);

    // Entities
    Collection< JsonEntity > getEntities(); // list all
    Collection< String > getChildEntities( String parent );
    void setEntity( JsonEntity e );
    JsonEntity getEntity( String key );
    void removeEntity(String key);

    // Data
//    Collection< String > getDataKeys();
    void setData( JsonData e );
    JsonData getData( String key );
    void removeData(String key);

    // Properties
    Float getPropertyFloat(String key, Float defaultValue);
    void setPropertyFloat(String key, float value);

    Double getPropertyDouble(String key, Double defaultValue);
    void setPropertyDouble(String key, double value);

    Long getPropertyLong(String key, Long defaultValue);
    void setPropertyLong(String key, long value);

    Integer getPropertyInt(String key, Integer defaultValue );
    void setPropertyInt(String key, int value);

    Boolean getPropertyBoolean(String key, Boolean defaultValue);
    void setPropertyBoolean(String key, boolean value);

    String getPropertyString(String key, String defaultValue);
    void setPropertyString(String key, String value);

    // Data
 //   Data getData(String key);
 //   void setData(String key, Data value);

}