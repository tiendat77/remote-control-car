import javax.swing.*;

public class Main {

  public static void main(String[] args) {

    Object[] selectioValues = {"Server", "Client"};
    String initialSection = "Server";

    Object selection = JOptionPane.showInputDialog(null, "Login as: ", "Client Server Communication", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);

    if (selection.equals("Server")) {
      String[] arguments = new String[]{};
      new Server().main(arguments);
      return;
    }

    if (selection.equals("Client")) {
      String IPServer = JOptionPane.showInputDialog("Enter the Server ip adress: ");
      String[] arguments = new String[]{IPServer};
      new Client().main(arguments);
      return;
    }
  }
}
