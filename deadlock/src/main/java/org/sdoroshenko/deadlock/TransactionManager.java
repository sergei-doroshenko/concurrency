package org.sdoroshenko.deadlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Bank account operations.
 */
public class TransactionManager {

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);
    private final int operationalTimeout;

    public TransactionManager(int operationalTimeout) {
        this.operationalTimeout = operationalTimeout;
    }

    public void exec(Account from, Account to, long sum) {
        if (from.getAmount().compareTo(new BigDecimal(sum)) <= 0) {
            throw new IllegalArgumentException(
                    String.format("From amount [%s] lq sum[%d]", from.getAmount(), sum)
            );
        }
        log.info("Executing transaction, from: {}, to: {}, sum: {}", new Object[]{from, to, sum});
        from.subtract(sum);
        operationTimeout();
        to.add(sum);
        log.info("Completed transaction, from: {}, to: {}, sum: {}", new Object[]{from, to, sum});
    }

    public void execSync(Account from, Account to, long sum) {
        if (from.getAmount().compareTo(new BigDecimal(sum)) <= 0) {
            throw new IllegalArgumentException(
                    String.format("From amount [%s] lq sum[%d]", from.getAmount(), sum)
            );
        }

        log.info("Executing transaction, from: {}, to: {}, sum: {}", new Object[]{from, to, sum});
        // use a rule to synchronize in a predefined order: first on account with least ID
        if (from.getId() > to.getId()) {
            synchronized (from) {
                synchronized (to) {
                    from.subtract(sum);
                    operationTimeout();
                    to.add(sum);
                }
            }
        } else {
            synchronized (to) {
                synchronized (from) {
                    from.subtract(sum);
                    operationTimeout();
                    to.add(sum);
                }
            }
        }

        log.info("Completed transaction, from: {}, to: {}, sum: {}", new Object[]{from, to, sum});
    }

    private void operationTimeout() {
        try {
            Thread.sleep(operationalTimeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
