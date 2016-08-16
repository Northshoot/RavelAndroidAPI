package ai.harmony.ravel.defines;

import java.util.HashMap;

/**
 * Created by lauril on 10/27/15.
 */
public class GeneratedGattAtrributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    /**
     * Iterate over the list of gatt attributes and put them into map
     *
     */
    /*
    //each service has it's own chars in a sub-dictionary data structure
    //TODO: generating strings with uuid
    {% for ble_service in data.ble_services %}
        public static String {{ble_service.name}}_SERVICE = {{ble_service.UUID_string}};
        {% for ble_char in ble_service.ble_chars %}
            public static String {{ble_char.name}} = ble_char.UUID_string;
        {% endfor %}
    {% endfor %}

     */
    //TODO: generating UUIDs
     /*
        {% for ble_service in data.ble_services %}
        public final static UUID UUID_{{ble_service.name}}_SERVICE = UUID
            .fromString({{ble_service.UUID_string}});
            {% for ble_char in ble_service.ble_chars %}
        public final static UUID UUID_{{ble_char.name}} = UUID
            .fromString({{ble_char.name}});
            {% endfor %}
        {% endfor %}
        */
    // unfortunately generation need to be repeated here to put
    // the service and chars in the look up table
    //TODO: generating adding to the map
    static {
        /*
        {% for ble_service in data.ble_services %}
        attributes.put({{ble_service.name}}_SERVICE ,{{ble_service.UUID_string}});
            {% for ble_char in ble_service.ble_chars %}
        attributes.put({{ble_char.name}},ble_char.UUID_string);
            {% endfor %}
        {% endfor %}
        */
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

}
