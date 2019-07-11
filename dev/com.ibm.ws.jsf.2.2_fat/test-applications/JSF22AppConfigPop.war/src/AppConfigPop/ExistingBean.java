package AppConfigPop;

/**
 * Simple test bean, this one defined through faces-config.xml
 */
public class ExistingBean {

    private String message = "SuccessfulExistingBeanTest";

    public String getMessage() {

        return message;
    }

    public void setMessageBean(String message) {
        this.message = message;
    }
}
