package com.hfut.cat_adoption_system.mapper.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis字符串列表类型处理器
 * 
 * 用于实现Java List<String>类型与数据库VARCHAR类型之间的相互转换，
 * 将列表元素以逗号分隔的形式存储到数据库，并从数据库读取时还原为列表。
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    /**
     * 设置非空参数，将List<String>转换为逗号分隔的字符串
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, String.join(",", parameter));
    }

    /**
     * 从ResultSet中按列名获取结果，将逗号分隔字符串转换为List<String>
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    /**
     * 从ResultSet中按列索引获取结果，将逗号分隔字符串转换为List<String>
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    /**
     * 从CallableStatement中按列索引获取结果，将逗号分隔字符串转换为List<String>
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    /**
     * 将逗号分隔的字符串解析为List<String>
     * 
     * @param value 逗号分隔的字符串
     * @return 解析后的字符串列表，空值或空字符串返回空列表
     */
    private List<String> parse(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}