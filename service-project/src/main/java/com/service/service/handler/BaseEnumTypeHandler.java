package com.service.service.handler;

import com.service.service.enumeration.BaseEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseEnumTypeHandler<E extends BaseEnum> extends BaseTypeHandler<E> {

    private Class<E> type;

    public BaseEnumTypeHandler() {}

    public BaseEnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {

            ps.setInt(i,(Integer)parameter.getCode());
        } else {
            ps.setObject(i, parameter.getCode(), jdbcType.TYPE_CODE);

        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return get(rs.getString(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return get(rs.getString(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return get(cs.getString(columnIndex));
    }

    private <E extends BaseEnum> E get(String v) {
        if (v == null) return null;
        if (StringUtils.isNumeric(v)) {
            return (E)get(type,Integer.parseInt(v));
        } else {
            return  null;//(E)Enum.valueOf(type,v);
        }
    }

    private <E extends BaseEnum> E get(Class<E> type, int v) {
        Method method = null;
        E result = null;
        try {
            method = type.getMethod("get", int.class);
            result = (E)method.invoke(type, v);
        } catch (NoSuchMethodException e) {
            //result = Enum.valueOf(type, String.valueOf(v));
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
