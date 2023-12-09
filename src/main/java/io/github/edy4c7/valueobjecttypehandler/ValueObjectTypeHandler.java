package io.github.edy4c7.valueobjecttypehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * MyBatis type handler for Value Object
 *
 * @param <E>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ValueObjectTypeHandler<E> extends BaseTypeHandler<E> {
  private final Class<E> type;
  private final String getterName;
  private final String factoryName;

  public ValueObjectTypeHandler(Class<E> type) {
    this(type, "getValue", type.isEnum() ? "of" : null);
  }

  @Override
  public final void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {
    try {
      final var getter = type.getMethod(getterName);
      final var value = getter.invoke(parameter);
      ps.setObject(i, value);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public final E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return getNullableResult(rs.getObject(columnName));
  }

  @Override
  public final E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return getNullableResult(rs.getObject(columnIndex));
  }

  @Override
  public final E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return getNullableResult(cs.getObject(columnIndex));
  }

  private E getNullableResult(Object parameter) {
    try {
      var cls = parameter.getClass();
      Object result;

      if (factoryName != null) {
        final var factory = type.getMethod(factoryName, cls);
        result = factory.invoke(null, parameter);
      } else {
        final var ctor = type.getConstructor(cls);
        result = ctor.newInstance(parameter);
      }

      return type.cast(result);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
