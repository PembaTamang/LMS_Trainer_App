package orionedutech.in.lmstrainerapp.model;

public class TrainerAssignmentModel {
private String name,batch,course;

    public TrainerAssignmentModel(String name, String batch, String course) {
        this.name = name;
        this.batch = batch;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
