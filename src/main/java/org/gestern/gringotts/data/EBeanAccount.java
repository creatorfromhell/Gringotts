package org.gestern.gringotts.data;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The type E bean account.
 */
@Entity
@Table(name = "gringotts_account")
@UniqueConstraint(columnNames = {"type", "owner"})
public class EBeanAccount {
    /**
     * The Id.
     */
    @Id
    int id;
    /**
     * Type string.
     */
    @NotNull
    String type;
    /**
     * Owner id.
     */
    @NotNull
    String owner;
    /**
     * Virtual balance.
     */
    @NotNull
    long cents;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets cents.
     *
     * @return the cents
     */
    public long getCents() {
        return cents;
    }

    /**
     * Sets cents.
     *
     * @param cents the cents
     */
    public void setCents(long cents) {
        this.cents = cents;
    }

    @Override
    public String toString() {
        return "EBeanAccount(" + owner + "," + type + ": " + cents + ")";
    }

}