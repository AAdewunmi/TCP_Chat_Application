import java.io.Serializable;

/**
 * @author Adrian Adewunmi
 * @date 25 Sept 2022
 * @description ChatMessage Class
 */
public class ChatMessage implements Serializable {

    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

}
