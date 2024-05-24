package voiceassistant.model;

public class CommandRequest {

    private String message;

    // Default constructor
    public CommandRequest() {}

    // Constructor with parameters
    public CommandRequest(String message) {
        this.message = message;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}