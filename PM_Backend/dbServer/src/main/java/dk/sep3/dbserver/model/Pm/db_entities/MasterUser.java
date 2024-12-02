package dk.sep3.dbserver.model.Pm.db_entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**<p>JPA Compatible entity, that includes all data relating to each Master User that needs to be persisted.</p>*/
@Entity
@Table(name="\"master_user\"")
public class MasterUser implements Serializable
{
  @Id  // Tells Spring Boot, that this value is the primary key.
  @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "masteruser_id_generator")
  @SequenceGenerator(name = "masteruser_id_generator", sequenceName = "master_user_id_seq", allocationSize = 1)
  private int id;

  @Column (name = "master_username", nullable=false, unique = true)
  private String masterUsername;

  @Column (name = "master_password", nullable=false)
  private String encryptedPassword;

  // A no-args constructor, as required by the Java Data API (JPA) specifications.
  protected MasterUser() {
  }

  public MasterUser(int id, String masterUsername, String encryptedPassword) {
    setId(id);
    setMasterUsername(masterUsername);
    setEncryptedPassword(encryptedPassword);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getMasterUsername() {
    return masterUsername;
  }

  public void setMasterUsername(String masterUsername) {
    this.masterUsername = masterUsername;
  }

  public String getEncryptedPassword() {
    return encryptedPassword;
  }

  public void setEncryptedPassword(String password) {
    this.encryptedPassword = password;
  }

  // Required by Spring Boot JPA:
  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MasterUser masterUser = (MasterUser) o;
    return getId() == masterUser.getId() && Objects.equals(getMasterUsername(), masterUser.getMasterUsername()) && Objects.equals(getEncryptedPassword(), masterUser.getEncryptedPassword());
  }

  // Required by Spring Boot JPA:
  @Override public int hashCode() {
    return Objects.hash(getId(), getMasterUsername(), getEncryptedPassword());
  }

  @Override public String toString() {
    return "MasterUser{" + "id='" + id + "'" + "masterUsername='" + masterUsername + '\'' + ", password='" + encryptedPassword + '\'' + '}';
  }
}
