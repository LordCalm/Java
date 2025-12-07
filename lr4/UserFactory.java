package lr4;

import java.util.ArrayList;

import lr1.MyBigInt;
import lr3.MyIntType;
import lr3.UserType;

public class UserFactory {
    public ArrayList<String> getTypeNameList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("MyIntType");
        list.add("MyBigInt");
        return list;
    }

    public UserType getBuilderByName(String name) {
        switch (name) {
            case "MyIntType":
                return new MyIntType();         // прототип
            case "MyBigInt":
                return new MyBigInt();          // аналогично
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + name);
        }
    }
}
