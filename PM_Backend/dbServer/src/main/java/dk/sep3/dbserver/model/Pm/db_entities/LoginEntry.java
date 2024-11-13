package dk.sep3.dbserver.model.Pm.db_entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="\"login_entry\"")
public class LoginEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loginentry_id_generator")
    @SequenceGenerator(name = "loginentry_id_generator", sequenceName = "login_entry_id_seq", allocationSize = 1)
    private int id;

    @Column(name = "entry_username", nullable = false)
    private String entryUsername;

    @Column(name = "entry_password", nullable = false)
    private String entryPassword;

    @Column(name = "entry_name", nullable = false)
    private String entryName;

    @Column(name = "entry_address")
    private String entryAddress;

    // Todo: Change the below variable, to point to the Category implementation using JPA - once that gets implemented. (Skrevet af Kristian)
    @Column(name = "entry_category_id", nullable = false)
    private int entryCategoryId;

    @Column(name = "master_user_id", nullable = false)
    private int masterUserId;

    // No-args constructor, required by JPA
    protected LoginEntry() {}

    public LoginEntry(int id, String entryUsername, String entryPassword, String entryName, String entryAddress, int entryCategoryId, int masterUserId) {
        this.id = id;
        this.entryUsername = entryUsername;
        this.entryPassword = entryPassword;
        this.entryName = entryName;
        this.entryAddress = entryAddress;
        this.entryCategoryId = entryCategoryId;
        this.masterUserId = masterUserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntryUsername() {
        return entryUsername;
    }

    public void setEntryUsername(String entryUsername) {
        this.entryUsername = entryUsername;
    }

    public String getEntryPassword() {
        return entryPassword;
    }

    public void setEntryPassword(String entryPassword) {
        this.entryPassword = entryPassword;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getEntryAddress() {
        return entryAddress;
    }

    public void setEntryAddress(String entryAddress) {
        this.entryAddress = entryAddress;
    }

    public int getEntryCategoryId() {
        return entryCategoryId;
    }

    public void setEntryCategoryId(int entryCategoryId) {
        this.entryCategoryId = entryCategoryId;
    }

    public int getMasterUserId() {
        return masterUserId;
    }

    public void setMasterUserId(int masterUserId) {
        this.masterUserId = masterUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginEntry that = (LoginEntry) o;
        return id == that.id &&
                entryCategoryId == that.entryCategoryId &&
                masterUserId == that.masterUserId &&
                Objects.equals(entryUsername, that.entryUsername) &&
                Objects.equals(entryPassword, that.entryPassword) &&
                Objects.equals(entryName, that.entryName) &&
                Objects.equals(entryAddress, that.entryAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entryUsername, entryPassword, entryName, entryAddress, entryCategoryId, masterUserId);
    }

    @Override
    public String toString() {
        return "LoginEntry{" +
                "id=" + id +
                ", entryUsername='" + entryUsername + '\'' +
                ", entryPassword='" + entryPassword + '\'' +
                ", entryName='" + entryName + '\'' +
                ", entryAddress='" + entryAddress + '\'' +
                ", entryCategoryId=" + entryCategoryId +
                ", masterUserId=" + masterUserId +
                '}';
    }
}
