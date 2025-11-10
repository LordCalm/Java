package lr3;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Тестирование интерфейса UserType для MyIntType")
public class MyIntTypeTest extends UserTypeMyTest {

    @Override
    protected UserType getUserTypeInstance() {
        return new MyIntType();
    }

    @Override
    protected String getSampleValueString() {
        return "123";
    }

    @Override
    protected String getAnotherValueString() {
        return "456"; // Больше, чем 123
    }

    @Override
    protected String getZeroValueString() {
        return "0";
    }
}