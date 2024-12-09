package dk.sep3.dbserver.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**<p>Specialized Utility Class used for encoding primarily the MasterUser password before committing to the Database for storage.</p>
 * <p> The Password encoder utilizes the Spring Frameworks builtin security module, which offers access to a range of encryption algorithms.</p>
 * <p> It specifically uses the Argon2 algorithm, which provides Very High Security at a medium performance hit, with built-in salting.
 * This algorithm features the highest security among the available algorithms in Spring Security, which is essential - since this is a password manager.</p>
 * <p> More documentation can be found here: {@link org.springframework.security.crypto.password.PasswordEncoder}.</p>
 * <p><b>Comparison of Password Encryption Algorithms</b></p>
 * <table border="1">
 *   <caption>Comparison of Password Encryption Algorithms</caption>
 *   <tr>
 *     <th>Algorithm</th>
 *     <th>Security</th>
 *     <th>Performance</th>
 *     <th>Built-In Salting</th>
 *     <th>Adaptive (Future-Proof)</th>
 *     <th>Recommended Use Case</th>
 *   </tr>
 *   <tr>
 *     <td>BCrypt</td>
 *     <td>High</td>
 *     <td>Medium</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>General purpose</td>
 *   </tr>
 *   <tr>
 *     <td>PBKDF2</td>
 *     <td>High</td>
 *     <td>Medium</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Enterprise systems</td>
 *   </tr>
 *   <tr>
 *     <td>Argon2</td>
 *     <td>Very High</td>
 *     <td>Medium</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>High-security systems</td>
 *   </tr>
 *   <tr>
 *     <td>SCrypt</td>
 *     <td>Very High</td>
 *     <td>Medium</td>
 *     <td>Yes</td>
 *     <td>Yes</td>
 *     <td>Systems requiring memory hardness</td>
 *   </tr>
 *   <tr>
 *     <td>SHA-256/512</td>
 *     <td>Medium</td>
 *     <td>High</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Legacy systems</td>
 *   </tr>
 *   <tr>
 *     <td>NoOp</td>
 *     <td>None</td>
 *     <td>High</td>
 *     <td>No</td>
 *     <td>No</td>
 *     <td>Testing purposes only</td>
 *   </tr>
 * </table>
 */
@Configuration
public class SecurityConfig
{
  @Value("${password.encoder.argon2.memory:32768}")
  private int memory;

  @Value("${password.encoder.argon2.saltLength:24}")
  private int saltLength;

  @Value("${password.encoder.argon2.iterations:2}")
  private int iterations;

  @Value("${password.encoder.argon2.parallelism:2}")
  private int parallelism;

  @Value("${password.encoder.argon2.hashLength:48}")
  private int hashLength;


  /**<p> The PasswordEncoder utilizes the Spring Frameworks builtin security module, which offers access to a range of encryption algorithms.</p>
   * <p> It specifically uses the Argon2 algorithm, which provides Very High Security at a medium performance hit, with built-in salting.
   * This algorithm features the highest security among the available algorithms in Spring Security, which is essential - since this is a password manager.</p>*/
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
  }
}
