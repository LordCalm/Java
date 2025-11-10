package lr3;

import java.io.InputStreamReader;


public interface UserType {
    String typeName();                      // имя типа
    Object create();                        // создать новый объект
    Object clone();                         // клонировать текущий
    Object readValue(InputStreamReader in); // создать и прочитать из потока
    Object parseValue(String ss);           // создать и распарсить из строки
    Comparator getTypeComparator();         // вернуть компаратор
}
