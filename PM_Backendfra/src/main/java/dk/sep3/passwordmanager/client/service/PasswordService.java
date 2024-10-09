package dk.sep3.passwordmanager.client.service;

import dk.sep3.passwordmanager.loadBalancer.client.ClientRequestHandler;

public class PasswordService {
  private final ClientRequestHandler clientRequestHandler;

  public PasswordService(ClientRequestHandler clientRequestHandler) {
    this.clientRequestHandler = clientRequestHandler;
  }

  public void savePassword(String username, String password) {
    clientRequestHandler.sendSavePasswordRequest(username, password);
  }

  public String getPassword(String username) {
    return clientRequestHandler.sendGetPasswordRequest(username);
  }
}
