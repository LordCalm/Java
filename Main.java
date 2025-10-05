package lr1;
import lr2.MyBinaryTree;


public class Main {
    public static void main(String[] args) {
        try {
            MyBigInt v1 = new MyBigInt("0");
            MyBigInt v2 = new MyBigInt("10000000000000000000000000");
            MyBigInt v3 = new MyBigInt(Long.MIN_VALUE);
            MyBinaryTree<MyBigInt> bt = new MyBinaryTree<>();
            bt.add(new MyBigInt(7));
            bt.add(new MyBigInt(11));
            bt.add(new MyBigInt(Long.MIN_VALUE));
            bt.add(new MyBigInt("10000000000000000000000000"));
            bt.add(v1);
            bt.add(v2);
            bt.add(v3);
            bt.traverseInOrder();
            System.out.println("\nПечать через итератор");
            for (MyBigInt v : bt) {
                System.out.println(v.toString()); // in-order
            }

        } catch (Exception e) {
            System.err.println("Произошла ошибка");
            e.printStackTrace();
        }
    }
}
