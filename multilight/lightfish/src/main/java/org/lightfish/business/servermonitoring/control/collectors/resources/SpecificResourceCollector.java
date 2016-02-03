package org.lightfish.business.servermonitoring.control.collectors.resources;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import org.lightfish.business.servermonitoring.control.collectors.AbstractRestDataCollector;
import org.lightfish.business.servermonitoring.control.collectors.DataPoint;
import org.lightfish.business.servermonitoring.entity.ConnectionPool;

/**
 *
 * @author Rob Veldpaus
 */
@ResourceDataCollector
public class SpecificResourceCollector extends AbstractRestDataCollector<ConnectionPool> {

    private static final String RESOURCES = "resources";

    private String resourceName;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public DataPoint<ConnectionPool> collect() {
        Response clientResponse = getResponse(constructResourceString());
        JsonObject response = clientResponse.readEntity(JsonObject.class);
        JsonObject entity = response.getJsonObject("extraProperties").
                getJsonObject("entity");

        int numconnfree = getIntVal(entity, "numconnfree", "current");
        int numconnused = getIntVal(entity, "numconnused", "current");
        int waitqueuelength = getIntVal(entity, "waitqueuelength", "count");
        int numpotentialconnleak = getIntVal(entity, "numpotentialconnleak", "count");

        return new DataPoint<>(resourceName, new ConnectionPool(resourceName, numconnfree, numconnused, waitqueuelength, numpotentialconnleak));
    }

    private int getIntVal(JsonObject entity, String name, String key) {
        return entity.getJsonObject(name).getInt(key);
    }

    private String constructResourceString() {
        return RESOURCES + "/" + resourceName;
    }
}
