package io.github.edy4c7.valueobjecttypehandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class ValueObjectTypeHandlerTest {

  private final ValueObjectTypeHandler<TestValueObject> voHandler
      = new ValueObjectTypeHandler<>(TestValueObject.class);
  private final ValueObjectTypeHandler<TestEnum> enumHandler
      = new ValueObjectTypeHandler<>(TestEnum.class);

  public static class EnumValueTypeHandlerForErrorTest<E extends Enum<E>> extends
      ValueObjectTypeHandler<E> {

    public EnumValueTypeHandlerForErrorTest(Class<E> type) {
      super(type, "foo", "var");
    }
  }

  @Test
  public void shouldSetParameter() throws SQLException {
    var ps = mock(PreparedStatement.class);
    var index = 1;
    enumHandler.setNonNullParameter(ps, index, TestEnum.VALUE1, null);
    verify(ps).setObject(index, TestEnum.VALUE1.getValue());
  }

  @Test
  public void shouldSetParameter2() throws SQLException {
    var ps = mock(PreparedStatement.class);
    var index = 1;
    enumHandler.setNonNullParameter(ps, index, TestEnum.VALUE2, null);
    verify(ps).setObject(index, TestEnum.VALUE2.getValue());
  }

  @Test
  public void shouldSetParameter3() throws SQLException {
    var ps = mock(PreparedStatement.class);
    var index = 1;
    var vo = new TestValueObject("value");
    voHandler.setNonNullParameter(ps, index, vo, null);
    verify(ps).setObject(index, vo.value());
  }

  @Test
  public void shouldFailToSetParameter() {
    var errorHandler = new EnumValueTypeHandlerForErrorTest<>(TestEnum.class);
    var ps = mock(PreparedStatement.class);
    var index = 1;

    var e = assertThrows(RuntimeException.class,
        () -> errorHandler.setNonNullParameter(ps, index, TestEnum.VALUE1, null));
    assertNotNull(e.getCause());
  }

  @Test
  public void shouldGetResultFromResultSetByName() throws SQLException {
    var rs = mock(ResultSet.class);
    var name = "test_enum";
    when(rs.getObject(name)).thenReturn("1");

    var result = enumHandler.getNullableResult(rs, name);
    verify(rs).getObject(name);
    assertEquals(TestEnum.VALUE1, result);
  }

  @Test
  public void shouldGetResultFromResultSetByNameToVo() throws SQLException {
    var rs = mock(ResultSet.class);
    var name = "test_enum";
    when(rs.getObject(name)).thenReturn("value");

    var result = voHandler.getNullableResult(rs, name);
    assertEquals("value", result.getValue());
  }

  @Test
  public void shouldGetResultFromResultSetByIndex() throws SQLException {
    var rs = mock(ResultSet.class);
    var index = 1;
    when(rs.getObject(index)).thenReturn("1");

    var result = enumHandler.getNullableResult(rs, index);
    verify(rs).getObject(index);
    assertEquals(TestEnum.VALUE1, result);
  }

  @Test
  public void shouldGetResultFromResultSetByIndex2() throws SQLException {
    var rs = mock(ResultSet.class);
    var index = 1;
    when(rs.getObject(index)).thenReturn("2");

    var result = enumHandler.getNullableResult(rs, index);
    verify(rs).getObject(index);
    assertEquals(TestEnum.VALUE2, result);
  }

  @Test
  public void shouldGetResultFromCallableStatement() throws SQLException {
    var rs = mock(CallableStatement.class);
    var index = 1;
    when(rs.getObject(index)).thenReturn("1");

    var result = enumHandler.getNullableResult(rs, index);
    verify(rs).getObject(index);
    assertEquals(TestEnum.VALUE1, result);
  }

  @Test
  public void shouldFailToGetResult() throws SQLException {
    var errorHandler = new EnumValueTypeHandlerForErrorTest<>(TestEnum.class);
    var rs = mock(ResultSet.class);
    var name = "test_enum";
    when(rs.getObject(anyString())).thenReturn("1");

    var e = assertThrows(RuntimeException.class,
        () -> errorHandler.getNullableResult(rs, name));
    assertNotNull(e.getCause());
  }
}
