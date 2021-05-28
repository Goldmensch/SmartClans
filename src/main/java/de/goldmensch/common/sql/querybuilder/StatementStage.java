// proudly stolen from: https://github.com/eldoriarpg/eldo-util/tree/feature/threading

package de.goldmensch.common.sql.querybuilder;

import de.goldmensch.common.consumer.ThrowingConsumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStage<T> {
    /**
     * Set the statements for the query.
     *
     * @param stmt statement
     * @return ResultStage
     */
    ResultStage<T> setStatements(ThrowingConsumer<PreparedStatement, SQLException> stmt);

    /**
     * Set the statements for the query.
     *
     * @return ResultStage
     */
    default ResultStage<T> emptyStatements() {
        return setStatements(s -> {
        });
    }


}
