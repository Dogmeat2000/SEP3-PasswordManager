package dk.sep3.dbserver.model.discoveryService.db_entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "discoveryservice")
public class DatabaseServer implements Serializable
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
  @SequenceGenerator(name = "id_generator", sequenceName = "discoveryservice_id_seq", allocationSize = 1)
  private long id;

  @Column(name = "host")
  private String host;

  @Column(name = "port")
  private int port;

  @Column(name = "congestion_percentage")
  private int congestionPercentage;

  @Basic(optional = false)
  @Column(name = "last_ping", insertable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Instant lastPing;


  // A no-args constructor, as required by the Java Data API (JPA) specifications.
  protected DatabaseServer() {
  }


  public DatabaseServer(String host, int port, int congestionPercentage) {
    setHost(host);
    setPort(port);
    setCongestionPercentage(congestionPercentage);
    setLastPing(Instant.now());
  }



  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getCongestionPercentage() {
    return congestionPercentage;
  }

  public void setCongestionPercentage(int congestion_percentage) {
    this.congestionPercentage = congestion_percentage;
  }

  public Instant getLastPing() {
    return lastPing;
  }

  public void setLastPing(Instant lastPing) {
    this.lastPing = lastPing;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DatabaseServer that = (DatabaseServer) o;
    return getId() == that.getId() && getPort() == that.getPort() && getCongestionPercentage() == that.getCongestionPercentage() && Objects.equals(getHost(), that.getHost()) && Objects.equals(
        lastPing, that.lastPing);
  }

  @Override public int hashCode() {
    return Objects.hash(getId(), getHost(), getPort(), getCongestionPercentage(), lastPing);
  }
}
