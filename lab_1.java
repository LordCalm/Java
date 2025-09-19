import java.io.*;

public class lab_1 {
    public static class MyBigInt {
        private long[] value;
        private boolean sign = true; // true +, false -

        // Конструкторы
        MyBigInt(int num) {
            if (num == 0) {
                this.value = new long[] { 0 };
            } else {
                this.value = new long[] { Math.abs((long) num) & 0xFFFFFFFFL }; // нужно сделать беззнаковый тип
                this.sign = (num > 0);
            }
        }

        MyBigInt(long num) {
            if (num == 0) {
                this.value = new long[] { 0 };
            } else {
                this.value = new long[] { Math.abs((long) num) };
                this.sign = (num > 0);
            }
        }

        MyBigInt(long[] num, boolean sign) {
            int Pointer = num.length;
            this.value = new long[Pointer];
            System.arraycopy(num, 0, this.value, 0, Pointer);
            this.sign = sign;
        }

        MyBigInt(String str) {
            if (str.startsWith("-")) {
                this.sign = false;
                str = str.substring(1);
            } else {
                this.sign = true;
            }
            if (!str.matches("\\d+")) {
                throw new IllegalArgumentException("Not a decimal number: " + str);
            }

            long currentStr = 0;
            int bitIndex = 0; // счётчик битов внутри числа
            int strCount = (int) ((str.length() * Math.log(10) / Math.log(2)) / 64) + 1;
            long[] temp = new long[strCount];
            int Pointer = 0; // счётчик блоков для длинного числа

            while (!str.equals("0")) {
                // Делим строку на 2
                StringBuilder sb = new StringBuilder();
                int prevMod = 0; // остаток от деления предыдущей цифры
                for (int i = 0; i < str.length(); i++) {
                    int digit = str.charAt(i) - '0'; // текущая цифра
                    int cur = prevMod * 10 + digit; // для деления столбиком надо учитывать прошлый остаток
                    sb.append(cur / 2); // постепенно набирается результат деления
                    prevMod = cur % 2; // получаем новый остаток
                }
                // Убираем нули в начале числа
                str = sb.toString().replaceFirst("^0+", "");
                if (str.isEmpty())
                    str = "0";

                // Кладём остаток в текущий блок
                currentStr |= ((long) prevMod << bitIndex); // остаток от деления в конец числа
                bitIndex++;

                if (bitIndex == 64) {
                    temp[Pointer++] = currentStr;
                    currentStr = 0;
                    bitIndex = 0;
                }
            }

            // Сохраняем последний неполный блок (полные блоки сохранены в цикле)
            if (bitIndex > 0) {
                temp[Pointer++] = currentStr;
            }

            // Копируем в массив нужного размера
            this.value = new long[Pointer];
            System.arraycopy(temp, 0, value, 0, Pointer);
        }

        // Сравнение по модулю
        // the value 0 if x == y; 
        // a value less than 0 if x < y as unsigned values; 
        // and a value greater than 0 if x > y as unsigned values
        public static int compareAbs(MyBigInt a, MyBigInt b) {
            int aLenth = a.value.length;
            int bLenth = b.value.length;
            if (aLenth < bLenth) {
                return -1;
            } else if (aLenth > bLenth) {
                return 1;
            } else {
                for (int i = aLenth - 1; i >= 0; i--) {
                    int j = bLenth - (aLenth - i);
                    long bi = (j >= 0) ? b.value[j] : 0;
                    int cmp = Long.compareUnsigned(a.value[i], bi);
                    if (cmp != 0) return cmp;
                }
                return 0;
            }
        }

        // Сложение
        public static MyBigInt add(MyBigInt a, MyBigInt b) {
            if (a.sign == b.sign) {
                int maxLenth = Math.max(a.value.length, b.value.length);
                long[] result = new long[maxLenth + 1]; // +1 на случай переноса
                long carry = 0;

                for (int i = 0; i < maxLenth; i++) {
                    long x = (i < a.value.length) ? a.value[i] : 0;
                    long y = (i < b.value.length) ? b.value[i] : 0;

                    long sum = x + y + carry;

                    // Проверка переполнения (unsigned)
                    carry = Long.compareUnsigned(sum, x) < 0 ? 1 : 0;

                    result[i] = sum;
                }

                if (carry != 0) {
                    result[maxLenth] = 1; // переполнение уходит в новый блок
                    return new MyBigInt(result, a.sign);
                } else {
                    // убираем последний 0-блок
                    long[] trimmed = new long[maxLenth];
                    System.arraycopy(result, 0, trimmed, 0, maxLenth);
                    return new MyBigInt(trimmed, a.sign);
                }
            }
            else {
                // a + (-b) == a - b
                if (compareAbs(a, b) >= 0) {
                    return subtractAbs(a, b);
                } else {
                    return subtractAbs(b, a); // знак большего числа
                }
            }
        }

        // Вычитание по модулю a > b
        public static MyBigInt subtractAbs(MyBigInt a, MyBigInt b) {
            int n = a.value.length;
            long[] result = new long[n];
            long borrow = 0; // перенос в следующий блок

            for (int i = 0; i < n; i++) {
                long x = a.value[i];
                long y = (i < b.value.length) ? b.value[i] : 0;

                long sub = x - y - borrow;

                // Проверка заимствования (borrow)
                if (Long.compareUnsigned(x, y + borrow) < 0) {
                    borrow = 1;
                } else {
                    borrow = 0;
                }

                result[i] = sub;
            }

            // Убираем старшие нулевые блоки
            int lastNonZero = result.length;
            while (lastNonZero > 1 && result[lastNonZero - 1] == 0) {
                lastNonZero--;
            }

            long[] trimmed = new long[lastNonZero];
            System.arraycopy(result, 0, trimmed, 0, lastNonZero);

            return new MyBigInt(trimmed, a.sign);
        }

        // Вычитание
        public static MyBigInt subtract(MyBigInt a, MyBigInt b) {
            if (a.sign == b.sign) {
                // Определяем, кто больше по модулю
                int cmp = compareAbs(a, b);
                if (cmp >= 0) {
                    MyBigInt result = subtractAbs(a, b); // |a| >= |b|
                    result.sign = a.sign;
                    return result;
                } else {
                    MyBigInt result = subtractAbs(b, a); // |b| > |a|
                    result.sign = a.sign;
                    return result;
                }
            } else {
                MyBigInt result = MyBigInt.add(a, b);
                result.sign = a.sign;
                return result;
            }
        }
    }

    // Тест
    public static void main(String[] args) throws IOException {
        MyBigInt v1 = new MyBigInt(13);
        MyBigInt v2 = new MyBigInt("123456789012345678901234567890");
        MyBigInt v3 = new MyBigInt(-5);
        v1 = MyBigInt.add(v1, v3);
        System.out.println((v1.sign ? "+" : "-"));
        System.out.println(Long.toBinaryString(v1.value[0]));
    }
}
