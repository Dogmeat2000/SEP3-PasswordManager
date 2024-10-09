package dk.sep3.passwordmanager.client.view;

import dk.sep3.passwordmanager.client.viewmodel.PasswordManager;
import java.util.Scanner;

public class CommandLineInterfaceImpl implements ICommandLineInterface {
  private final PasswordManager viewModel;

  public CommandLineInterfaceImpl(PasswordManager viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void start() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Welcome to Password Manager");

    while (true) {
      System.out.println("Enter command : ");
      String command = scanner.nextLine();
      handleCommand(command);
    }
  }

  @Override
  public void handleCommand(String command) {
    String[] args = command.split(" ");
    switch (args[0]) {
      case "save-password":
        if (args.length == 3) {
          viewModel.savePassword(args[1], args[2]);
        } else {
          System.out.println("Use: save-password <username> <password>");
        }
        break;
      case "get-password":
        if (args.length == 2) {
          viewModel.getPassword(args[1]);
        } else {
          System.out.println("Use: get-password <username>");
        }
        break;
      default:
        System.out.println("Unknown command.");
    }
  }
}