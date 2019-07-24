package org.gestern.gringotts.data;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The type E bean account chest.
 */
@Entity
@Table(name = "gringotts_accountchest")
@UniqueConstraint(columnNames = {"world", "x", "y", "z"})
public class EBeanAccountChest {
    /**
     * The Id.
     */
    @Id
    int id;
    /**
     * The World.
     */
    @NotNull
    String world;
    /**
     * The X.
     */
    @NotNull
    int x;
    /**
     * The Y.
     */
    @NotNull
    int y;
    /**
     * The Z.
     */
    @NotNull
    int z;
    /**
     * The Account.
     */
    @NotNull
    int account;

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
     * Gets world.
     *
     * @return the world
     */
    public String getWorld() {
        return world;
    }

    /**
     * Sets world.
     *
     * @param world the world
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets z.
     *
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets z.
     *
     * @param z the z
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public int getAccount() {
        return account;
    }

    /**
     * Sets account.
     *
     * @param account the account
     */
    public void setAccount(int account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "EBeanAccountChest(" + account + "," + world + ": " + x + "," + y + "," + z + ")";
    }

}