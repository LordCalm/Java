package lr2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

public class MyBinaryTree<T extends Comparable<T>> implements Iterable<T> {
    // https://www.baeldung.com/java-binary-tree

    public class MyNode {
        private T value;
        MyNode left;
        MyNode right;

        MyNode(T num) {
            this.value = num;
            left = right = null;
        }
    }
    
    private MyNode root;

    public interface NodeAction<T> {
        void toDo(T value);
    }

    // Вставка
    private MyNode addRecursive(MyNode current, T num) {
        if (current == null) {
            return new MyNode(num);
        }
        if (num.compareTo(current.value) < 0) {
            current.left = addRecursive(current.left, num);
        } else if (num.compareTo(current.value) > 0) {
            current.right = addRecursive(current.right, num);
        }
        return current;
    }

    public void add(T num) {
        root = addRecursive(root, num);
    }

    // Поиск элемента
    private boolean containsNodeRecursive(MyNode current, T num) {
        if (current == null) {
            return false;
        }
        if (num.compareTo(current.value) == 0) {
            return true;
        }
        return (num.compareTo(current.value) < 0)
                ? containsNodeRecursive(current.left, num)
                : containsNodeRecursive(current.right, num);
    }

    public boolean containsNode(T num) {
        return containsNodeRecursive(root, num);
    }

    // Удаление элемента
    private MyNode deleteRecursive(MyNode current, T num) {
        if (current == null) {
            return null;
        }
        if (num.compareTo(current.value) == 0) {
            if (current.left == null && current.right == null) {
                return null;
            }
            if (current.right == null) {
                return current.left;
            }
            if (current.left == null) {
                return current.right;
            }
            T smallestValue = findSmallestValue(current.right);
            current.value = smallestValue;
            current.right = deleteRecursive(current.right, smallestValue);
            return current;
        }
        if (num.compareTo(current.value) < 0) {
            current.left = deleteRecursive(current.left, num);
            return current;
        }
        current.right = deleteRecursive(current.right, num);
        return current;
    }

    private T findSmallestValue(MyNode root) {
        return root.left == null ? root.value : findSmallestValue(root.left);
    }

    public void delete(T num) {
        if (num == null)
            return;
        root = deleteRecursive(root, num);
    }

    // Удаление по индексу
    public void deleteByIndex(int index) {
        T bigNum = getByIndex(root, new int[] { index });
        if (bigNum == null) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        delete(bigNum);
    }

    // Поиск в глубину
    public void traverseInOrder() {
        traverseInOrder(root);
    }

    public void traverseInOrder(NodeAction<T> action) {
        traverseInOrder(root, action);
    }

    public void traverseInOrder(MyNode node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.value);
            traverseInOrder(node.right);
        }
    }

    public void traverseInOrder(MyNode node, NodeAction<T> action) {
        if (node != null) {
            traverseInOrder(node.left, action);
            action.toDo(node.value);
            traverseInOrder(node.right, action);
        }
    }

    // Поиск в ширину
    public void traverseLevelOrder() {
        if (root == null) {
            return;
        }
        Queue<MyNode> nodes = new LinkedList<>();
        nodes.add(root);
        while (!nodes.isEmpty()) {
            MyNode node = nodes.remove();
            System.out.print(" " + node.value);
            if (node.left != null) {
                nodes.add(node.left);
            }
            if (node.right != null) {
                nodes.add(node.right);
            }
        }
    }

    // Доступ по индексу
    public T getByIndex(int index) {
        T bigNum = getByIndex(root, new int[] { index });
        if (bigNum == null) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        return bigNum;
    }

    // Массив в Java — это объект, и ссылка на него передаётся по значению
    private T getByIndex(MyNode node, int[] index) {
        if (node == null) {
            return null;
        }
        T left = getByIndex(node.left, index);
        if (left != null) {
            return left;
        }
        if (index[0] == 0) {
            return node.value;
        }
        index[0]--;
        return getByIndex(node.right, index);
    }

    // Итератор
    @Override
    public java.util.Iterator<T> iterator() {
        return new MyBinaryTreeIterator();
    }

    // https://www.geeksforgeeks.org/dsa/implement-binary-search-treebst-iterator/
    class MyBinaryTreeIterator implements Iterator<T> {
        private final Stack<MyNode> stack = new Stack<>();
        private MyBinaryTree<T>.MyNode current;
        
        // инициализация
        public MyBinaryTreeIterator() {
            this.current = root;
        }
        
        // вернуть false, если следующего не существует
        @Override
        public boolean hasNext() {
            return current != null || !stack.isEmpty();
        }
        
        // вернуть текущее значение и обновить итератор
        @Override
        public T next() {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }
            MyBinaryTree<T>.MyNode node = stack.pop();
            T value = node.value;
            current = node.right;
            return value;
        }
    }
}
