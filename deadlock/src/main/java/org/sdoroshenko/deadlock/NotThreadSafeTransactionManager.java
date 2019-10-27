package org.sdoroshenko.deadlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Bank account operations.
 */
public class NotThreadSafeTransactionManager {

    private static final Logger log = LoggerFactory.getLogger(NotThreadSafeTransactionManager.class);
    private final int operationalTimeout;

    public NotThreadSafeTransactionManager(int operationalTimeout) {
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

    private void operationTimeout() {
        try {
            Thread.sleep(operationalTimeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
