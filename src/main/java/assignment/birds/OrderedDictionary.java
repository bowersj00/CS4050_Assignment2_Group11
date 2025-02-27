package assignment.birds;

public class OrderedDictionary implements OrderedDictionaryADT {

    Node root;

    OrderedDictionary() {
        root = new Node();
    }

    /**
     * Returns the Record object with key k, or it returns null if such a record
     * is not in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment/birds/DictionaryException.java
     */
    @Override
    public BirdRecord find(DataKey k) throws DictionaryException {
        Node current = root;
        int comparison;
        if (root.isEmpty()) {         
            throw new DictionaryException("There is no record matches the given key");
        }

        while (true) {
            comparison = current.getData().getDataKey().compareTo(k);
            if (comparison == 0) { // key found
                return current.getData();
            }
            if (comparison > 0) {
                if (current.getLeftChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getLeftChild();
            } else if (comparison < 0) {
                if (current.getRightChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getRightChild();
            }
        }

    }

    /**
     * Inserts r into the ordered dictionary. It throws a DictionaryException if
     * a record with the same key as r is already in the dictionary.
     *
     * @param r
     * @throws birds.DictionaryException
     */
    @Override
    public void insert(BirdRecord r) throws DictionaryException {
        // Write this method
        Node current = root;
        int comparison;
        if (root.isEmpty()) {
            root = new Node(r);
        }
        //loop through bst
        while (true) {
            comparison = current.getData().getDataKey().compareTo(r.getDataKey());
            if (comparison == 0) { // record with the same key already exists
                throw new DictionaryException("A record with the given key already exists");
            }
            if (comparison > 0) { // if current node has a greater key than r
                if (current.getLeftChild() == null) {
                    //insert as left child
                    current.setLeftChild(new Node(r));
                    return;
                }
                //go to left subtree
                current = current.getLeftChild();
            } else if (comparison < 0){ // if current node has a smaller key than r
                if (current.getRightChild() == null) {
                    //insert as right child
                    current.setRightChild(new Node(r));
                    return;
                }
                //go to right subtree
                current = current.getRightChild();
            }
        }
    }

    /**
     * Removes the record with Key k from the dictionary. It throws a
     * DictionaryException if the record is not in the dictionary.
     *
     * @param k
     * @throws birds.DictionaryException
     */
    @Override
    public void remove(DataKey k) throws DictionaryException {
        // Write this method
        root = removeNode(root, k);
    }

    //private method to remove a node from a tree
    private Node removeNode(Node node, DataKey k) throws DictionaryException {
        if (node.isEmpty()) {
            throw new DictionaryException("Dictionary is empty");
        }
        int comparison = node.getData().getDataKey().compareTo(k);
        if (comparison > 0) { //if current node has a greater key than k
            //go to the left subtree
            node.setLeftChild(removeNode(node.getLeftChild(), k));
        } else if (comparison < 0) { //if current node has a smaller key than k
            //go to the right subtree
            node.setRightChild(removeNode(node.getRightChild(), k));
        } else { //if node is found
            if (node.getLeftChild() == null && node.getRightChild() == null) {
                //case 1: no left child or right child
                return null;
            } else if (node.getLeftChild() == null) {
                //case 2: no left child
                return node.getRightChild();
            } else if (node.getRightChild() == null) {
                //case 3: no right child
                return node.getLeftChild();
            } else {
                //case 4: left and right child
                //find the successor, copy the successor data to current node, and then delete successor
                Node successorNode = findMin(node.getRightChild());
                node.setData(successorNode.getData());
                node.setRightChild(removeNode(node.getRightChild(), successorNode.getData().getDataKey()));
            }
        }
        return node;
    }

    /**
     * Returns the successor of k (the record from the ordered dictionary with
     * smallest key larger than k); it returns null if the given key has no
     * successor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public BirdRecord successor(DataKey k) throws DictionaryException{
        // Write this method
        Node current = root;
        Node successorNode = null;
        if (root.isEmpty()) {
            throw new DictionaryException("Dictionary is empty");
        }

        while (current != null) {
            int comparison = current.getData().getDataKey().compareTo(k);
            if (comparison > 0) { //if key of current node is greater than k
                //update successor node and search the left subtree
                successorNode = current;
                current = current.getLeftChild();
            } else if (comparison < 0) { //if key of current node is less than k
                //search the right subtree
                current = current.getRightChild();
            } else { //if node with key k is found
                if (current.getRightChild() != null) {
                    successorNode = findMin(current.getRightChild());
                }
                break;
            }
        }
        if (successorNode == null) {
            return null;
        }
        return successorNode.getData();
    }

    //private method to find minimum value in a subtree
    private Node findMin(Node n) {
        while (n.getLeftChild() != null) {
            n = n.getLeftChild();
        }
        return n;
    }

    /**
     * Returns the predecessor of k (the record from the ordered dictionary with
     * largest key smaller than k; it returns null if the given key has no
     * predecessor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public BirdRecord predecessor(DataKey k) throws DictionaryException{
        Node current = root;
        Node predecessorNode = null;
        if (root.isEmpty()) {
            return null;
        }

        while (current != null) {
            int comparison = current.getData().getDataKey().compareTo(k);
            if (comparison > 0) { //if key of current node is greater than k
                //search the left subtree
                current = current.getLeftChild();
            } else if (comparison < 0) { //if key of current node is less than k
                //update predecessor node and search the right subtree
                predecessorNode = current;
                current = current.getRightChild();
            } else {
                if (current.getRightChild() != null) {
                    predecessorNode = findMax(current.getLeftChild());
                }
            }
        }
        return predecessorNode.getData();
    }

    //private method to find maximum value in a subtree
    private Node findMax(Node n) {
        while (n.getRightChild() != null) {
            n = n.getRightChild();
        }
        return n;
    }

    /**
     * Returns the record with smallest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     *
     * @return
     */
    @Override
    public BirdRecord smallest() throws DictionaryException{
        // Write this method
        if (root.isEmpty()) {
            return null;
        }
        Node current = root;
        while (current.getLeftChild() != null) {
            current = current.getLeftChild();
        }
        return current.getData();
    }

    /*
	 * Returns the record with largest key in the ordered dictionary. Returns
	 * null if the dictionary is empty.
     */
    @Override
    public BirdRecord largest() throws DictionaryException{
        // Write this method
        if (root.isEmpty()) {
            return null;
        }
        Node current = root;
        while (current.getRightChild() != null) {
            current = current.getRightChild();
        }
        return current.getData();
    }
      
    /* Returns true if the dictionary is empty, and true otherwise. */
    @Override
    public boolean isEmpty (){
        return root.isEmpty();
    }
}
