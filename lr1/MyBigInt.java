package lr1;
import lr3.*;
import java.io.*;

public class MyBigInt implements Comparable<MyBigInt>, UserType {
    private long[] value; // каждый блок хранит 63 бита
    private boolean sign = true; // true +, false -

    private static final long BLOCK_MASK = 0x7FFFFFFFFFFFFFFFL; // 63 бита
    private static final int BLOCK_SIZE = 63;

    // Конструкторы
    public MyBigInt() {
        this.value = new long[] { 0 };
    }

    public MyBigInt(int num) {
        if (num == 0) {
            this.value = new long[] { 0 };
        } else {
            this.value = new long[] { Math.abs((long) num) & BLOCK_MASK }; // нужно сделать беззнаковый тип
            this.sign = (num > 0);
        }
    }

    public MyBigInt(long num) {
        if (num == 0) {
            this.value = new long[] { 0 };
        } else if (num == Long.MIN_VALUE) {
            // -2^63 = -(1 << 63)
            this.value = new long[] { 0, 1 }; // второй блок = 1, т.е. 2^63
            this.sign = false;
        } else {
            this.value = new long[] { Math.abs(num) & BLOCK_MASK };
            this.sign = (num > 0);
        }
    }

    public MyBigInt(long[] num, boolean sign) {
        int n = num.length;
        this.value = new long[n];
        for (int i = 0; i < n; i++) {
            this.value[i] = num[i] & BLOCK_MASK; // гарантируем 63 бита
        }
        this.sign = sign;

        // Дополнительно: убрать ведущие нули
        int lastNonZero = n;
        while (lastNonZero > 1 && this.value[lastNonZero - 1] == 0) {
            lastNonZero--;
        }
        if (lastNonZero < n) {
            long[] trimmed = new long[lastNonZero];
            System.arraycopy(this.value, 0, trimmed, 0, lastNonZero);
            this.value = trimmed;
        }
    }

    public MyBigInt(String str) {
        if (str.startsWith("-")) {
            this.sign = false;
            str = str.substring(1);
        } else {
            this.sign = true;
        }
        if (!str.matches("\\d+")) {
            throw new IllegalArgumentException("Not a decimal number: " + str);
        }

        // отдельная обработка нуля
        if (str.equals("0")) {
            this.value = new long[]{0};
            this.sign = true; // по соглашению 0 всегда положительный
            return;
        }

        // Decimal to Binary Conversion Program
        // Given a non negative number n, the task is to convert
        // the given number into an equivalent binary representation.
        // https://www.geeksforgeeks.org/dsa/program-decimal-binary-conversion/?hl=ru-RU

        long currentStr = 0;
        int bitIndex = 0; // счётчик битов внутри числа
        int strCount = (int) ((str.length() * Math.log(10) / Math.log(2)) / BLOCK_SIZE) + 1;
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

            if (bitIndex == BLOCK_SIZE) {
                temp[Pointer++] = currentStr & BLOCK_MASK;
                currentStr = 0;
                bitIndex = 0;
            }
        }

        // Сохраняем последний неполный блок (полные блоки сохранены в цикле)
        if (bitIndex > 0) {
            temp[Pointer++] = currentStr & BLOCK_MASK;
        }

        // Копируем в массив нужного размера
        this.value = new long[Pointer];
        System.arraycopy(temp, 0, value, 0, Pointer);
    }

    // Изменение содержимого отдельных блоков
    public void setBlock(int index, long newValue) { 
        if (index >= 0 && index < value.length) {
            value[index] = newValue & BLOCK_MASK;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public long getBlock(int index) {
        if (index >= 0 && index < value.length) {
            return value[index];
        } else {
            throw new IndexOutOfBoundsException();
        }
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
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    }

    @Override
    public int compareTo(MyBigInt b) {
        if (this.sign == b.sign) {
            int cmp = compareAbs(this, b);
            return this.sign ? cmp : -cmp;
        }
        return this.sign ? 1 : -1;
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
                // перенос — старший (64-й) бит относительно базы 2^63
                carry = sum >>> BLOCK_SIZE; // 0 или 1
                result[i] = sum & BLOCK_MASK; // строго 63 бита
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
        } else {
            // a + (-b) == a - b
            if (compareAbs(a, b) >= 0) {
                MyBigInt r = subtractAbs(a, b);
                r.sign = a.sign;
                return r;
            } else {
                MyBigInt r = subtractAbs(b, a);
                r.sign = b.sign; // знак большего по модулю
                return r;
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
                // заняли 1 "единицу" из следующего блока (база 2^63)
                sub += (1L << BLOCK_SIZE);
                borrow = 1;
            } else {
                borrow = 0;
            }
            result[i] = sub & BLOCK_MASK;
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
                result.sign = false;
                return result;
            }
        } else {
            MyBigInt result = MyBigInt.add(a, b);
            result.sign = a.sign;
            return result;
        }
    }

    // Умножение Алгоритм Карацубы
    // a = aHigh * B + aLow
    // b = bHigh * B + bLow
    // B = 2^(half*BLOCK_SIZE)

    // a*b = z2*B^2 + z1*B + z0
    public static MyBigInt multiplyKaratsuba(MyBigInt a, MyBigInt b) {
        // базовый случай: если числа маленькие, используем школьное умножение
        if (a.value.length == 1 && b.value.length == 1) {
            long x = a.value[0] & BLOCK_MASK;
            long y = b.value[0] & BLOCK_MASK;

            if (x <= 0x7FFFFFFFL && y <= 0x7FFFFFFFL) {
                // оба ≤ 31 бит → безопасно умножаем
                long prod = x * y;
                return new MyBigInt(new long[] { prod & BLOCK_MASK, prod >>> BLOCK_SIZE }, a.sign == b.sign);
            } else {
                // делим каждый блок на "левую" и "правую" половины по 31 бит
                MyBigInt aLow = new MyBigInt(new long[] { x & 0x7FFFFFFFL }, true);
                MyBigInt aHigh = new MyBigInt(new long[] { x >>> 31 }, true);
                MyBigInt bLow = new MyBigInt(new long[] { y & 0x7FFFFFFFL }, true);
                MyBigInt bHigh = new MyBigInt(new long[] { y >>> 31 }, true);

                // Карацуба для этих половинок
                MyBigInt z0 = multiplyKaratsuba(aLow, bLow);
                MyBigInt z2 = multiplyKaratsuba(aHigh, bHigh);
                MyBigInt z1 = multiplyKaratsuba(add(aLow, aHigh), add(bLow, bHigh));
                z1 = subtract(subtract(z1, z0), z2);

                // собираем результат: z0 + (z1 << 31) + (z2 << 62)
                MyBigInt res = add(shiftLeft(z2, 62),
                        add(shiftLeft(z1, 31), z0));
                res.sign = (a.sign == b.sign);
                return res;
            }
        }

        int n = Math.max(a.value.length, b.value.length);
        int half = (n + 1) / 2;

        // разделяем на "левую" и "правую" части
        MyBigInt aLow = a.slice(0, half);
        MyBigInt aHigh = a.slice(half, a.value.length);
        MyBigInt bLow = b.slice(0, half);
        MyBigInt bHigh = b.slice(half, b.value.length);

        // рекурсивные вызовы
        MyBigInt z0 = multiplyKaratsuba(aLow, bLow);
        MyBigInt z2 = multiplyKaratsuba(aHigh, bHigh);
        MyBigInt z1 = multiplyKaratsuba(add(aLow, aHigh), add(bLow, bHigh));
        z1 = subtract(subtract(z1, z0), z2);

        // собираем результат: z0 + (z1 << (half*BLOCK_SIZE)) + (z2 <<
        // (2*half*BLOCK_SIZE))
        MyBigInt res = add(shiftLeft(z2, 2 * half * BLOCK_SIZE),
                add(shiftLeft(z1, half * BLOCK_SIZE), z0));

        res.sign = (a.sign == b.sign);
        return res;
    }

    // Возвращает подмассив блоков [from, to)
    public MyBigInt slice(int from, int to) {
        if (from < 0)
            from = 0;
        if (to > value.length)
            to = value.length;
        if (from >= to) {
            return new MyBigInt(new long[] { 0 }, true);
        }
        int len = to - from;
        long[] part = new long[len];
        System.arraycopy(this.value, from, part, 0, len);
        return new MyBigInt(part, this.sign);
    }

    // Сдвиг влево на shiftBits бит
    public static MyBigInt shiftLeft(MyBigInt a, int shiftBits) {
        if (shiftBits == 0 || (a.value.length == 1 && a.value[0] == 0)) {
            return new MyBigInt(a.value, a.sign);
        }

        int blockShift = shiftBits / BLOCK_SIZE; // сколько целых блоков нужно добавить слева
        int bitShift = shiftBits % BLOCK_SIZE; // остаток битов

        long[] res = new long[a.value.length + blockShift + 1];

        long carry = 0;
        for (int i = 0; i < a.value.length; i++) {
            long cur = a.value[i] & BLOCK_MASK;
            long shifted = (cur << bitShift) & BLOCK_MASK; // сдвиг
            // в итоговом массиве должен быть записан текущий сдвиг и то, что вылезло из
            // предыдущего блока
            res[i + blockShift] |= shifted | carry;
            carry = (bitShift == 0) ? 0 : (cur >>> (BLOCK_SIZE - bitShift));
        }
        // если что-то осталось, записываем в последний блок слева
        if (carry != 0) {
            res[a.value.length + blockShift] = carry;
        }

        // убираем ведущие нули
        int last = res.length;
        while (last > 1 && res[last - 1] == 0)
            last--;
        long[] trimmed = new long[last];
        System.arraycopy(res, 0, trimmed, 0, last);

        return new MyBigInt(trimmed, a.sign);
    }

    // Вывод в строку в дестятичный формат
    @Override
    public String toString() {
        // https://www.geeksforgeeks.org/dsa/program-binary-decimal-conversion/
        // если число = 0
        if (value.length == 1 && value[0] == 0) {
            return "0";
        }

        // создаём копию, чтобы не портить исходный массив
        long[] temp = value.clone();

        StringBuilder sb = new StringBuilder();

        // делим большое число на 10 и собираем цифры
        while (!isZero(temp)) {
            long remainder = divBy10(temp);
            sb.append(remainder);
        }

        if (!sign) {
            sb.append("-");
        }

        return sb.reverse().toString();
    }

    // Проверка, что массив = 0
    private boolean isZero(long[] arr) {
        for (long v : arr) {
            if (v != 0)
                return false;
        }
        return true;
    }

    // Деление на 10 для массива блоков по 63 бита
    private int divBy10(long[] arr) {
        final long BASE_DIV10 = 922337203685477580L; // floor(2^63 / 10)
        final long BASE_MOD10 = 8L; // 2^63 % 10

        long carry = 0; // остаток (0..9)
        for (int i = arr.length - 1; i >= 0; i--) {
            long x = arr[i] & BLOCK_MASK;

            // t = carry*8 + x (может выглядеть отрицательным, но в unsigned это < 2^63 +
            // 72)
            long t = x + carry * BASE_MOD10;

            long qLow = Long.divideUnsigned(t, 10); // floor(t / 10) как unsigned
            long r = Long.remainderUnsigned(t, 10); // t % 10 как unsigned

            long q = carry * BASE_DIV10 + qLow; // итоговый частный для блока
            arr[i] = q & BLOCK_MASK; // строго 63 бита
            carry = r; // остаток идёт на следующий (младший) блок
        }
        return (int) carry; // 0..9
    }
    
    // Запись в десятичном виде в файл
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(this.toString());
        }
    }

    // Чтение в десятичном виде из файла
    public static MyBigInt loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            return new MyBigInt(line);
        }
    }
    
    // Запись в двоичном виде в файл
    public void saveBinary(String filename) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
            out.writeBoolean(sign);
            out.writeInt(value.length);
            for (long v : value) {
                out.writeLong(v);
            }
        }
    }

    // Чтение в двоичном виде из файла
    public static MyBigInt loadBinary(String filename) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename))) {
            boolean sign = in.readBoolean();
            int len = in.readInt();
            long[] value = new long[len];
            for (int i = 0; i < len; i++) {
                value[i] = in.readLong();
            }
            MyBigInt res = new MyBigInt();
            res.sign = sign;
            res.value = value;
            return res;
        }
    }

    // ==================================================================
    // 2. Реализация методов интерфейса UserType
    // ==================================================================

    @Override
    public String typeName() {
        return "MyBigInt";
    }

    @Override
    public Object create() {
        // Создаем "нулевой" объект
        return new MyBigInt();
    }

    @Override
    public Object clone() {
        // Создаем копию текущего объекта
        // Важно использовать `this.value.clone()` для создания нового массива,
        // а не копирования ссылки.
        return new MyBigInt(this.value.clone(), this.sign);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        try (BufferedReader reader = new BufferedReader(in)) {
            // Считываем строку из потока и используем конструктор из строки
            String line = reader.readLine();
            if (line != null) {
                return parseValue(line);
            }
            // Если поток пуст, можно вернуть null или создать объект по умолчанию
            return create(); 
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения из потока", e);
        }
    }

    @Override
    public Object parseValue(String ss) {
        // Используем уже существующий конструктор, который парсит строку
        return new MyBigInt(ss);
    }

    @Override
    public Comparator getTypeComparator() {
        // Возвращаем реализацию компаратора.
        // Так как MyBigInt уже реализует Comparable, мы можем просто
        // использовать его метод compareTo.
        // Лямбда-выражение создает анонимный класс, реализующий Comparator.
        return (o1, o2) -> ((MyBigInt) o1).compareTo((MyBigInt) o2);
    }

    @Override
    public boolean equals(Object o) {
        // 1. Проверка на идентичность ссылок
        if (this == o) return true;
        
        // 2. Проверка на null и совпадение классов
        if (o == null || getClass() != o.getClass()) return false;
        
        // 3. Приведение типа
        MyBigInt other = (MyBigInt) o;
        
        // 4. Сравнение всех значимых полей
        // Сравниваем знак и содержимое массива value
        return this.compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        // Генерируем хэш-код на основе тех же полей, что используются в equals
        int result = Boolean.hashCode(sign);
        result = 31 * result + java.util.Arrays.hashCode(value);
        return result;
    }
}
