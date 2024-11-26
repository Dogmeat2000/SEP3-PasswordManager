# SEP3 Password Manager

## Installation Guide

### Certificates for Secure SSL Connections
This system relies on HTTPS encryption to securely transport  
data between the client and server. To enable secure communication,  
the system uses two types of certificates: a self-signed keystore  
for the server and a public certificate for the client.

#### Server-Side Certificate
The server uses a self-signed `keystore.jks` file, which contains  
the `mycert` certificate. This file is located in the  
`src/main/resources` folder of both the **LoadBalancer** and  
**WebAPI** modules.

- The `keystore.jks` file includes both the private and public keys.
- The certificate details (file path, password, and alias) are  
  configured in the `application.properties` file.

#### Client-Side Certificate
The client must trust the server's certificate by using the  
same public key. To achieve this, the public key is extracted  
from the `keystore.jks` file using the **Keytool** utility.  
The exported public key is stored in the `backend-cert.cer` file,  
located in the `src/main/resources` folder of the **LoadBalancer**  
module.

**Steps to Install/Trust the Certificate**  
To enable the client to trust the server, the public key must  
be installed as a trusted certificate on the client machine:

1. Locate the `backend-cert.cer` file in the file explorer and  
   double-click it.
2. In the popup window, click **Install Certificate**.
3. Choose **Local Machine** (administrative rights required).
4. Select **Place all certificates in the following store** and  
   choose:
  - **Trusted Root Certification Authorities** (Rodnøglecentre, der er tillid til).
5. Complete the installation process.

### Starting the System for the First Time (Client Side)
Since the certificates are self-signed, most browsers will not  
initially accept them as trusted. When opening the client side  
in a browser for the first time, the user will need to manually  
proceed past a security warning.

**Steps to Proceed with Self-Signed Certificates:**
1. When the browser shows a security warning, click the  
   **Advanced** link.
2. Click **Proceed to [URL] (unsafe)**.
3. The browser will temporarily accept the certificate, allowing  
   access to the Password Manager.

### Future Improvements
Once the system is hosted in production, a proper certificate  
from a trusted Certificate Authority (e.g., Let's Encrypt) will  
replace the self-signed certificate to avoid these warnings and  
improve security.
