package lr3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyIntType implements UserType {
    private Integer value;

    public MyIntType() {
        this.value = 0;
    }

    public MyIntType(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String typeName() {
        return "Integer";
    }

    @Override
    public Object create() {
        return new MyIntType();
    }

    @Override
    public Object clone() {
        return new MyIntType(value);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        BufferedReader br = new BufferedReader(in);
        try {
            System.out.print("Введите значение int: ");
            int v = Integer.parseInt(br.readLine());
            return new MyIntType(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object parseValue(String ss) {
        return new MyIntType(Integer.parseInt(ss.trim()));
    }

    @Override
    public Comparator getTypeComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                int v1 = ((MyIntType) o1).value;
                int v2 = ((MyIntType) o2).value;
                return Integer.compare(v1, v2);
            }
        };
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyIntType other)) return false;
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
