package hse.hw03.MyTreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BinaryTree<E extends Comparable<? super E>> implements MyTreeSet<E> {

    private class TreeNode {

        private E value;
        private TreeNode left = null;
        private TreeNode right = null;
        private int size = 0;

        TreeNode(E value) {
            this.value = value;
        }

        private E valueByPosition(int position) {
            if (left != null) {
                if (position < left.size) {
                    return left.valueByPosition(position);
                }
                position -= left.size;
            }
            if (position == 0) {
                return value;
            }
            if (right != null) {
                return right.valueByPosition(position - 1);
            }
            return null;
        }

        private int lessThan(E value) {
            int leftSize = 0;
            if (left != null) {
                leftSize = left.size;
            }
            if (this.value.equals(value)) {
                return leftSize;
            }
            if (comparator.compare(this.value, value) > 0) {
                if (left == null) {
                    return 0;
                } else {
                    return left.lessThan(value);
                }
            } else {
                if (right == null) {
                    return leftSize + 1;
                } else {
                    return leftSize + 1 + right.lessThan(value);
                }
            }
        }

        private void reverse() {
            var tmp = right;
            right = left;
            left = tmp;
            if (left != null) {
                left.reverse();
            }
            if (right != null) {
                right.reverse();
            }
        }
    }

    private class TreeIterator implements Iterator<E> {

        private int position = 0;
        private int step = 1;

        /**
         * {@link Iterator#hasNext()}
         */
        public boolean hasNext() {
            return (root != null && 0 <= position + step && root.size >= position + step);
        }

        /**
         * {@link Iterator#next()}
         */
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return root.valueByPosition(position += step);
        }
    }

    private TreeNode root = null;
    private Comparator<E> comparator;

    public BinaryTree() {
        comparator = Comparator.naturalOrder();
    }

    public BinaryTree(@Nullable Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * {@link TreeSet#size()}
     */
    public int size() {
        if (root == null) {
            return 0;
        }
        return root.size;
    }

    /**
     * {@link TreeSet#isEmpty()}
     */
    public boolean isEmpty() {
        return (root != null);
    }

    /**
     * {@link TreeSet#contains(Object)}
     */
    public boolean contains(@NotNull Object o) {
        TreeNode curNode = root;
        while (curNode != null) {
            if (curNode.equals(o)) {
                return true;
            }
            if (comparator.compare(curNode.value, (E) o) > 0) {
                curNode = curNode.left;
            } else {
                curNode = curNode.right;
            }
        }
        return false;
    }

    /**
     * {@link TreeSet#iterator()}
     */
    @NotNull
    public Iterator<E> iterator() {
        return new TreeIterator();
    }

    /**
     * {@link TreeSet#toArray()}
     */
    @NotNull
    public Object[] toArray() {
        if (root == null) {
            return new Object[0];
        }
        var arrayOfSet = new Object[root.size];
        var iter = new TreeIterator();
        for (int i = 0; i < root.size; i++) {
            arrayOfSet[i] = iter.next();
        }
        return arrayOfSet;
    }

    /**
     * {@link TreeSet#toArray(Object[])}
     */
    @NotNull
    public <T> T[] toArray(@NotNull T[] a) {
        if (root != null && a.length < root.size) {
            return (T[]) toArray();
        }
        if (root == null) {
            return a;
        }
        var iter = new TreeIterator();
        for (int i = 0; i < root.size; i++) {
            a[i] = (T) iter.next();
        }
        return a;
    }

    /**
     * {@link TreeSet#add(Object)}
     */
    public boolean add(@NotNull E e) {
        var curNode = root;
        TreeNode parent = null;
        while (curNode != null) {
            if (curNode.value.equals(e)) {
                return false;
            }
            parent = curNode;
            if (comparator.compare(curNode.value, e) > 0) {
                curNode = curNode.left;
            } else {
                curNode = curNode.right;
            }
        }
        if (parent == null) {
            root = new TreeNode(e);
        } else {
            if (comparator.compare(parent.value, e) > 0) {
                parent.left = new TreeNode(e);
            } else {
                parent.right = new TreeNode(e);
            }
        }
        return true;
    }

    private TreeNode merge(TreeNode left, TreeNode right) {
        if (left == null) {
            return right;
        }
        left.right = merge(left.right, right);
        return left;
    }

    /**
     * {@link TreeSet#remove(Object)}
     */
    public boolean remove(@NotNull Object o) {
        var curNode = root;
        TreeNode parent = null;
        while (curNode != null) {
            if (curNode.value.equals(o)) {
                if (parent == null) {
                    root = merge(curNode.left, curNode.right);
                } else {
                    if (parent.left == curNode) {
                        parent.left = merge(curNode.left, curNode.right);
                    } else {
                        parent.right = merge(curNode.left, curNode.right);
                    }
                }
                return true;
            }
            parent = curNode;
            if (comparator.compare(curNode.value, (E) o) > 0) {
                curNode = curNode.left;
            } else {
                curNode = curNode.right;
            }
        }
        return false;
    }

    /**
     * {@link TreeSet#containsAll(Collection)}
     */
    public boolean containsAll(@NotNull Collection<?> c) {
        for (var element: c.toArray()) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@link TreeSet#addAll(Collection)}
     */
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean setChanged = false;
        for (var element: c.toArray()) {
            if (add((E) element)) {
                setChanged = true;
            }
        }
        return setChanged;
    }

    /**
     * {@link TreeSet#retainAll(Collection)}
     */
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean setChanged = false;
        var arrayOfElements = toArray();
        var newSet = new BinaryTree<E>();
        for (var element: arrayOfElements) {
            if (c.contains(element)) {
                newSet.add((E) element);
            } else {
                setChanged = true;
            }
        }
        root = newSet.root;
        return setChanged;
    }

    /**
     * {@link TreeSet#removeAll(Collection)}
     */
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean setChanged = false;
        for (var element: c.toArray()) {
            if (remove(element)) {
                setChanged = true;
            }
        }
        return setChanged;
    }

    /**
     * {@link TreeSet#clear()}
     */
    public void clear() {
        root = null;
    }

    /**
     * {@link TreeSet#descendingIterator()}
     **/
    public Iterator<E> descendingIterator() {
        var iterator = new TreeIterator();
        iterator.position = size() - 1;
        iterator.step = -1;
        return iterator;
    }

    /**
     * {@link TreeSet#descendingSet()}
     **/
    public MyTreeSet<E> descendingSet() {
        var rootReversed = root;
        rootReversed.reverse();
        var newSet = new BinaryTree<E>();
        newSet.root = rootReversed;
        return newSet;
    }

    /**
     * {@link TreeSet#first()}
     **/
    public E first() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        return root.valueByPosition(0);
    }

    /**
     * {@link TreeSet#last()}
     **/
    public E last() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        return root.valueByPosition(size() - 1);
    }

    /**
     * {@link TreeSet#lower(Object)}
     **/
    public E lower(E e) {
        if (root == null) {
            return null;
        }
        var less = root.lessThan(e);
        if (less == 0) {
            return null;
        }
        return root.valueByPosition(less - 1);
    }

    /**
     * {@link TreeSet#floor(Object)}
     **/
    public E floor(E e) {
        if (contains(e)) {
            return e;
        }
        return lower(e);
    }

    /**
     * {@link TreeSet#ceiling(Object)}
     **/
    public E ceiling(E e) {
        if (root == null) {
            return null;
        }
        var less = root.lessThan(e);
        return root.valueByPosition(less);
    }

    /**
     * {@link TreeSet#higher(Object)}
     **/
    public E higher(E e) {
        if (root == null) {
            return null;
        }
        var less = root.lessThan(e);
        if (contains(e)) {
            return root.valueByPosition(less + 1);
        } else {
            return root.valueByPosition(less);
        }
    }

}