package orionedutech.in.lmstrainerapp.model;

public class FeedbackModel {
private String question,questionID,response;

    public FeedbackModel(String question, String questionID, String response) {
        this.question = question;
        this.questionID = questionID;
        this.response = response;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
