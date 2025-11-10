package lr3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public abstract class UserTypeMyTest {
    
    protected UserType userType; // Экземпляр тестируемого типа

    // Абстрактные методы, которые должны реализовать дочерние классы
    protected abstract UserType getUserTypeInstance();
    protected abstract String getSampleValueString();
    protected abstract String getAnotherValueString(); // Должно быть > SampleValue
    protected abstract String getZeroValueString();

    @BeforeEach
    void setUp() {
        // Перед каждым тестом получаем новый экземпляр
        userType = getUserTypeInstance();
    }

    @Test
    @DisplayName("typeName() должен возвращать непустое имя типа")
    void testTypeName() {
        String typeName = userType.typeName();
        assertNotNull(typeName, "Имя типа не должно быть null");
        assertFalse(typeName.isEmpty(), "Имя типа не должно быть пустым");
    }

    @Test
    @DisplayName("create() должен создавать объект, равный 'нулевому' значению")
    void testCreate() {
        Object createdObject = userType.create();
        Object zeroObject = userType.parseValue(getZeroValueString());

        assertNotNull(createdObject, "Созданный объект не должен быть null");
        assertEquals(userType.getClass(), createdObject.getClass(), "Созданный объект должен иметь правильный класс");
        assertEquals(zeroObject, createdObject, "Созданный объект должен быть равен 'нулевому' значению");
    }

    @Test
    @DisplayName("parseValue() должен корректно создавать объект из строки")
    void testParseValue() {
        String sample = getSampleValueString();
        Object parsedObject = userType.parseValue(sample);

        assertNotNull(parsedObject, "Распарсенный объект не должен быть null");
        assertEquals(userType.getClass(), parsedObject.getClass(), "Распарсенный объект должен иметь правильный класс");
        // Проверяем, что объект, преобразованный обратно в строку, совпадает с исходной
        assertEquals(sample, parsedObject.toString(), "toString() от распарсенного объекта должен совпадать с исходной строкой");
    }

    @Test
    @DisplayName("clone() должен создавать полную независимую копию объекта")
    void testClone() {
        // 1. Создаем оригинальный объект
        UserType original = (UserType) userType.parseValue(getSampleValueString());

        // 2. Клонируем его
        UserType clone = (UserType) original.clone();

        // 3. Проверки
        assertNotNull(clone, "Клон не должен быть null");
        assertNotSame(original, clone, "Клон и оригинал должны быть разными объектами в памяти");
        assertEquals(original, clone, "Клон и оригинал должны быть равны по значению (assertEquals)");
        assertEquals(original.getClass(), clone.getClass(), "Классы оригинала и клона должны совпадать");
    }
    
    @Test
    @DisplayName("readValue() должен создавать объект из потока ввода")
    void testReadValue() {
        String sample = getSampleValueString();
        // Симулируем ввод пользователя из строки
        String input = sample + System.lineSeparator();
        InputStreamReader reader = new InputStreamReader(
            new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
        );

        Object readObject = userType.readValue(reader);

        assertNotNull(readObject, "Объект из потока не должен быть null");
        assertEquals(userType.parseValue(sample), readObject, "Объект из потока должен быть равен распарсенному из строки");
    }

    @Test
    @DisplayName("getTypeComparator() должен возвращать корректный компаратор")
    void testGetTypeComparator() {
        Comparator comparator = userType.getTypeComparator();
        assertNotNull(comparator, "Компаратор не должен быть null");

        Object value1 = userType.parseValue(getSampleValueString());
        Object value2 = userType.parseValue(getAnotherValueString());
        Object value1_clone = userType.parseValue(getSampleValueString());

        // value1 < value2
        assertTrue(comparator.compare(value1, value2) < 0, "Компаратор должен возвращать < 0 для (меньший, больший)");
        // value2 > value1
        assertTrue(comparator.compare(value2, value1) > 0, "Компаратор должен возвращать > 0 для (больший, меньший)");
        // value1 == value1_clone
        assertEquals(0, comparator.compare(value1, value1_clone), "Компаратор должен возвращать 0 для равных объектов");
    }
}

