package ai.harmony.ravel.model;

/**
 * Created by lauril on 1/6/16.
 */
public class Registration {

    public final String dev_id;
    public final String reg_id;
    public final String name;
    public final String type;

    public Registration(String dev_id, String reg_id,  String name, String type) {
        this.dev_id = dev_id;
        this.reg_id = reg_id;
        this.name = name;
        this.type = type;
    }

}
