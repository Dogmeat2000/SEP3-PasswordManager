package dk.sep3.passwordmanager.client.viewmodel;

import dk.sep3.passwordmanager.client.service.PasswordService;

public class PasswordManager {
  private final PasswordService passwordService;

  public PasswordManager(PasswordService passwordService) {
    this.passwordService = passwordService;
  }

  public void savePassword(String username, String password) {
    passwordService.savePassword(username, password);
  }

  public void getPassword(String username) {
    String password = passwordService.getPassword(username);
    if (password != null) {
      System.out.println("Password for " + username + ": " + password);
    } else {
      System.out.println("No password found for " + username);
    }
  }
}
