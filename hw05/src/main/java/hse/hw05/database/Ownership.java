package hse.hw05.database;

import org.jetbrains.annotations.NotNull;

/**
 * Stores person and phone in ownership
 */
public class Ownership {

    private final @NotNull String name;
    private final @NotNull String phone;

    public Ownership(@NotNull String name, @NotNull String phone) {
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("Name: %s | Phone: %s", name, phone);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ownership) {
            return toString().equals(obj.toString());
        }
        return false;
    }
}