package lr1;
import lr2.MyBinaryTree;
import lr3.*;


public class Main {
    public static void main(String[] args) {
        System.out.println("=== Тест дерева для MyIntType ===");

        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());

        tree.add(new MyIntType(5));
        tree.add(new MyIntType(2));
        tree.add(new MyIntType(8));
        tree.add(new MyIntType(1));
        tree.add(new MyIntType(3));

        System.out.println("=== Итерация по дереву ===");
        for (UserType val : tree) {
            System.out.println(val);
        }
    }
}
