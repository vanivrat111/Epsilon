package epsilon.game;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 *
 * @author Marius
 */
public class NetworkPage extends MenuPage {

    // only instance of this class
    private static NetworkPage page = new NetworkPage();

    // local variables used for hanlding this specific page

    // Indicates wether the user is currently entering text into a field
    private boolean typingString;
    
    // Field used for saving what is currently entered into each line of the lineitems
    private String[] currentString;

    // This should be the same string array that is sent to the superclass. It's used for setting the right text on the page items
    private String[] originalStrings;

    /**
     * Private constructor used for initialising variables
     */
    private NetworkPage() {
        super(new String[]{"IP address: ", "Name: ", " Connect", "Back"}, "Network setup");
        originalStrings = new String[]{"IP address: ", "Name: ", "Connect", "Back"};
        typingString = false;
        currentString = new String[]{"", "" , "", ""};
    }
    
    @Override
    public void useSelected() {
        if (selected == 0 || selected == 1) {
            typingString = true;
            Input.get().requestString(currentString[selected]);
            errorMessage = "";
        } else if (selected == 2) {
            // TODO: connect to server here.
            try {
                InetAddress conn = InetAddress.getByName(currentString[0].trim());
                errorMessage = "Connected to: " + conn.getHostAddress();
            } catch (UnknownHostException e) {
                errorMessage = "Invalid IP address.";
            }
            //errorMessage = "Not yet implemented";
        } else if (selected == 3) {
            errorMessage = "";
            Menu.get().goToPrevious();
        }
    }

    /**
     * Method that gives access to the single object of this class
     *
     * @return the only instance of this class
     */
    public static NetworkPage get() {
         return page;
    }

    @Override
    public void update() {

        // check if input on any field should be handeled
        if (!typingString) {
            // no inputing, use superclass update
            super.update();
        } else {

            // check if the person has finished typing
            if (!Input.get().isTyping()) {
                // if the typing is finished, indicate this to the class and save the variable
                typingString = false;

                currentString[selected] = Input.get().getFinalText();

                items[selected] = originalStrings[selected] + Input.get().getFinalText();
            } else {
                items[selected] = originalStrings[selected] + Input.get().getCurrentText() + "_";
            }
        }
    }

}
