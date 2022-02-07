package com.knoldus;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelWriterImpl;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

public class OptimizerTest {
    @Test
    public void test_tpch_q6() throws Exception {
        SimpleTable lineitem = SimpleTable.newBuilder("lineitem")
            .addField("l_quantity", SqlTypeName.DECIMAL)
            .addField("l_extendedprice", SqlTypeName.DECIMAL)
            .addField("l_discount", SqlTypeName.DECIMAL)
            .addField("l_shipdate", SqlTypeName.DATE)
            .withRowCount(60_000L)
            .build();

        SimpleSchema schema = SimpleSchema.newBuilder("tpch").addTable(lineitem).build();

        Optimizer optimizer = Optimizer.create(schema);

        String sql ="select\n" +
                "    sum(l.l_extendedprice * l.l_discount) as revenue\n" +
                "from\n" +
                "    lineitem l\n" +
                "where\n" +
                "    l.l_shipdate >= ?\n" +
                "    and l.l_shipdate < ?\n" +
                "    and l.l_discount between (? - 0.01) AND (? + 0.01)\n" +
                "    and l.l_quantity < ?";

        SqlNode sqlTree = optimizer.parse(sql);
        SqlNode validatedSqlTree = optimizer.validate(sqlTree);

    }
}
