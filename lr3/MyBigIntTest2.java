package lr3;

import lr1.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lr1.MyBigInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестирование интерфейса UserType для MyBigInt")
public class MyBigIntTest2 extends UserTypeMyTest {

    @Override
    protected UserType getUserTypeInstance() {
        return new MyBigInt();
    }

    @Override
    protected String getSampleValueString() {
        return "12345678901234567890";
    }

    @Override
    protected String getAnotherValueString() {
        // Это число гарантированно больше sample
        return "98765432109876543210";
    }

    @Override
    protected String getZeroValueString() {
        return "0";
    }
    
    // Дополнительный тест, специфичный для MyBigInt
    @Test
    @DisplayName("MyBigInt должен корректно парсить отрицательные числа")
    void testParseNegative() {
        String negativeValue = "-1234567890";
        Object parsedObject = userType.parseValue(negativeValue);
        assertEquals(negativeValue, parsedObject.toString(), "Отрицательное число должно корректно парситься и преобразовываться в строку");
    }
}
