package lr2;
import lr3.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MyBinaryTreeMyTest {
    @Test
    void testAddAndContains() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(7));

        assertTrue(tree.containsNode(new MyIntType(5)));
        assertTrue(tree.containsNode(new MyIntType(3)));
        assertTrue(tree.containsNode(new MyIntType(7)));
        assertFalse(tree.containsNode(new MyIntType(10)));
    }

    @Test
    void testDeleteLeafNode() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(7));

        tree.delete(new MyIntType(3));
        assertFalse(tree.containsNode(new MyIntType(3)));
        assertTrue(tree.containsNode(new MyIntType(5)));
        assertTrue(tree.containsNode(new MyIntType(7)));
    }

    @Test
    void testDeleteNodeWithOneChild() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(4)); // 3 имеет одного потомка 4

        tree.delete(new MyIntType(3));
        assertFalse(tree.containsNode(new MyIntType(3)));
        assertTrue(tree.containsNode(new MyIntType(4)));
        assertTrue(tree.containsNode(new MyIntType(5)));
    }

    @Test
    void testDeleteNodeWithTwoChildren() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(7));
        tree.add(new MyIntType(6));
        tree.add(new MyIntType(8));

        tree.delete(new MyIntType(7));
        assertFalse(tree.containsNode(new MyIntType(7)));
        assertTrue(tree.containsNode(new MyIntType(6)));
        assertTrue(tree.containsNode(new MyIntType(8)));
    }

    @Test
    void testTraverseInOrder() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(7));
        tree.add(new MyIntType(4));

        List<MyIntType> result = new ArrayList<>();
        tree.traverseInOrder(result::add);

        List<MyIntType> expected = List.of(
                new MyIntType(3),
                new MyIntType(4),
                new MyIntType(5),
                new MyIntType(7)
        );
        assertEquals(expected, result);
    }

    @Test
    void testGetByIndex() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(5));
        tree.add(new MyIntType(3));
        tree.add(new MyIntType(7));
        tree.add(new MyIntType(4));

        assertEquals(new MyIntType(3), tree.getByIndex(0));
        assertEquals(new MyIntType(4), tree.getByIndex(1));
        assertEquals(new MyIntType(5), tree.getByIndex(2));
        assertEquals(new MyIntType(7), tree.getByIndex(3));

        assertThrows(IndexOutOfBoundsException.class, () -> tree.getByIndex(4));
    }

    // Вспомогательный метод для вычисления высоты
    private <T extends UserType> int getHeight(MyBinaryTree<T>.MyNode node) {
        if (node == null) return 0;
        try {
            var leftField = node.getClass().getDeclaredField("left");
            var rightField = node.getClass().getDeclaredField("right");
            leftField.setAccessible(true);
            rightField.setAccessible(true);

            @SuppressWarnings("unchecked")
            MyBinaryTree<T>.MyNode left = (MyBinaryTree<T>.MyNode) leftField.get(node);
            @SuppressWarnings("unchecked")
            MyBinaryTree<T>.MyNode right = (MyBinaryTree<T>.MyNode) rightField.get(node);

            return 1 + Math.max(getHeight(left), getHeight(right));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBalancePreservesOrder() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        for (int i = 1; i <= 7; i++) {
            tree.add(new MyIntType(i));
        }

        List<MyIntType> before = tree.toList();
        tree.balance();
        List<MyIntType> after = tree.toList();

        assertEquals(before, after, "Порядок элементов после балансировки должен сохраняться");
    }

    @Test
    void testBalanceReducesHeight() throws Exception {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        for (int i = 1; i <= 15; i++) {
            tree.add(new MyIntType(i));
        }

        var rootField = tree.getClass().getDeclaredField("root");
        rootField.setAccessible(true);
        var rootBefore = (MyBinaryTree<UserType>.MyNode) rootField.get(tree);
        int heightBefore = getHeight(rootBefore);

        tree.balance();

        var rootAfter = (MyBinaryTree<UserType>.MyNode) rootField.get(tree);
        int heightAfter = getHeight(rootAfter);

        assertTrue(heightAfter < heightBefore, "Высота дерева должна уменьшиться после балансировки");
    }

    @Test
    void testBalanceOnEmptyTree() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        assertDoesNotThrow(tree::balance);
        assertTrue(tree.toList().isEmpty());
    }

    @Test
    void testBalanceSingleNodeTree() {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        tree.add(new MyIntType(42));
        tree.balance();
        List<MyIntType> list = tree.toList();
        assertEquals(1, list.size());
        assertEquals(new MyIntType(42), list.get(0));
    }

    @Test
    void testBalancedTreeRemainsBalanced() throws Exception {
        MyBinaryTree<MyIntType> tree = new MyBinaryTree<>(new MyIntType());
        int[] values = {4, 2, 6, 1, 3, 5, 7};
        for (int v : values) tree.add(new MyIntType(v));

        var rootField = tree.getClass().getDeclaredField("root");
        rootField.setAccessible(true);
        var rootBefore = (MyBinaryTree<MyIntType>.MyNode) rootField.get(tree);
        int heightBefore = getHeight(rootBefore);

        tree.balance();

        var rootAfter = (MyBinaryTree<MyIntType>.MyNode) rootField.get(tree);
        int heightAfter = getHeight(rootAfter);

        assertTrue(Math.abs(heightBefore - heightAfter) <= 1);
    }
}