package org.sdoroshenko.deadlock;

import java.math.BigDecimal;

/**
 * Represents bank account.
 */
public class Account {
    private final long id;
    private final String currency;
    private BigDecimal amount;

    public Account(long id, String currency) {
        this.id = id;
        this.currency = currency;
        this.amount = new BigDecimal(0);
    }

    public long getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void add(long value) {
        setAmount(this.amount.add(new BigDecimal(value)));
    }

    public void subtract(long value) {
        setAmount(this.amount.subtract(new BigDecimal(value)));
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
