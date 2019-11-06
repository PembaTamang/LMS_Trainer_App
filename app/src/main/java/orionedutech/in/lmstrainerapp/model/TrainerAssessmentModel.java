package orionedutech.in.lmstrainerapp.model;

public class TrainerAssessmentModel {

private String name,type,center,batch;

    public TrainerAssessmentModel(String name, String type, String center, String batch) {
        this.name = name;
        this.type = type;
        this.center = center;
        this.batch = batch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
