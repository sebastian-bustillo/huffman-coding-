package com.company;

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Solution to the Huffman Coding assignment.
 * Data Structures 2103
 * 11-12-2019
 * Ouachita Baptist University
 * Sebastian Bustillo
 */

public class HuffmanCode {
    private HuffmanNode root; //Ref to the root.
    private PriorityQueue<HuffmanNode> heap; //Ref to the array with all the nodes.

    /**
     * Constructor to be used with the HuffmanCompressor class.
     *
     * @param frequencies a list of all the frequencies.
     */
    public HuffmanCode(int[] frequencies) {
        this.heap = new PriorityQueue<>(); // Instantiate a PriorityQueue.
        buildHeap(frequencies); //Create the heap.
    }

    /**
     * Second constructor that uses a scanner to read data from a file.
     *
     * @param input scanner object.
     */
    public HuffmanCode(Scanner input) {
        this.heap = new PriorityQueue<>();
        buildHuffmanTree(input);
    }

    /**
     * Inner method for Scanner constructor.
     * @param input
     */
    private void buildHuffmanTree(Scanner input) {
        HuffmanNode huffman_tree_ref = new HuffmanNode();
        this.root = huffman_tree_ref;
        // As long as there's a next line, keep constructing the Huffman Tree.
        while (input.hasNextLine()) {
            huffman_tree_ref = this.root;
            int charASCIIVal = Integer.parseInt(input.nextLine());
            String huffCode = input.nextLine();
            for (int i = 0; i < huffCode.length(); i++) {
                // Left subtree.
                if (huffCode.charAt(i) == '0') {
                    if (huffman_tree_ref.getLeft() != null)
                        huffman_tree_ref = huffman_tree_ref.getLeft();
                    else {
                        huffman_tree_ref.setLeft(new HuffmanNode(null, null));
                        huffman_tree_ref = huffman_tree_ref.getLeft();
                    }
                    // Right subtree.
                } else {
                    if (huffman_tree_ref.getRight() != null)
                        huffman_tree_ref = huffman_tree_ref.getRight();
                    else {
                        huffman_tree_ref.setRight(new HuffmanNode(null, null));
                        huffman_tree_ref = huffman_tree_ref.getRight();
                    }

                }
            }
            // Once you finish the huffman code, then assign the character to that node.
            huffman_tree_ref.setCharacter((char) charASCIIVal);
        }
        this.heap.add(this.root);

    }

    /***
     * buildHeap creates the initial heap (priority queue). Notice that
     * this is a helper method, it is possible that all the code might be
     * put under a single method.
     * @param frequencies is an array of all the characters with their respective frequencies.
     */
    private void buildHeap(int[] frequencies) {

        // Create Huffman nodes and put them into an ArrayList. Notice that those are currently just nodes, we
        // we don't have a complete tree just yet.
        for (int i = 0; i < frequencies.length; i++) { //TODO: why am I able to access the field directly?
            if (frequencies[i] != 0)
                heap.add(new HuffmanNode((char) i, frequencies[i], null, null));
        }

        while (heap.size() > 1) {
            HuffmanNode left = heap.remove();
            HuffmanNode right = heap.remove();
            heap.add(new HuffmanNode(' ', left.getFrequency() + right.getFrequency(), left, right));


        }
    }

    /**
     * save, saves the current huffman tree to a file. It calls a recursive method.
     *
     * @param output is what's being printed to the file.
     */
    public void save(PrintStream output) {
        saveKernel(heap.peek(), "", output);
    }

    /**
     * saveKernel, it's a recursive method to
     * print out  Huffman Tree.
     *
     * @param huffman_Tree_Ref the current node.
     * @param str              the path followed, i.e. the huffman code.
     * @param out              file where to print the corresponding code.
     */
    private void saveKernel(HuffmanNode huffman_Tree_Ref, String str, PrintStream out) {

        if (huffman_Tree_Ref.isLeaf()) {
            out.println((int) huffman_Tree_Ref.getCharacter());
            out.println(str);
            return;
        }

        saveKernel(huffman_Tree_Ref.getLeft(), str + "0", out);
        saveKernel(huffman_Tree_Ref.getRight(), str + "1", out);

    }


    /**
     * translate, converts or decompresses the compressed file.
     *
     * @param input  compressed file.
     * @param output original file.
     */
    public void translate(BitInputStream input, PrintStream output) {

        HuffmanNode huffman_tree_ref = root;

        while (input.hasNextBit()) {
            int inputRef = input.nextBit();
            if (inputRef == 1)
                huffman_tree_ref = huffman_tree_ref.getRight();
            else
                huffman_tree_ref = huffman_tree_ref.getLeft();

            if (huffman_tree_ref.isLeaf()) {
                output.write(huffman_tree_ref.getCharacter());
                huffman_tree_ref = root;
            }

        }


    }

}

/**
 * Inner class, HuffmanNode, this is the implementation
 * of a huffman tree node.
 */
class HuffmanNode implements Comparable<HuffmanNode> {

    private HuffmanNode left; //Ref to the left subtree.
    private HuffmanNode right; //Ref to the right subtree.
    private char character; //Ref to the letter represented by the node.
    private int frequency; //Ref to the frequency (priority) of the node.

    /**
     * Constructors
     *
     * @param letter    the specific character
     * @param frequency the frequency or "weight"
     * @param left      ref to the left node
     * @param right     ref to the right node
     */
    public HuffmanNode(char letter, int frequency, HuffmanNode left, HuffmanNode right) {
        this.character = letter;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.frequency = -1;
        this.character = '@';
        this.left = left;
        this.right = right;
    }

    public HuffmanNode() {
        this.character = '@';
        this.frequency = -1;
        this.left = null;
        this.right = null;
    }

    /**
     * isLeaf method returns whether or not the left
     * and right child are null.
     */
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    /**
     * this is just the implementation of the compareTo method.
     * It's going to compare the frequencies of two HuffmanNodes.
     *
     * @param e curr compare to the key (curr > key?)
     * @return 1, 0, or, -1 if curr it's greater, equal, or less than key, respectively.
     */
    @Override
    public int compareTo(HuffmanNode e) {
        if (e == null)
            throw new IllegalStateException();
        return this.getFrequency() - e.getFrequency();
    }

    /**
     * Getter for the frequency of a Huffman node.
     *
     * @return the frequency field.
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * getter for the character of the current HuffmanNode.
     *
     * @return the character stored on the node.
     */
    public char getCharacter() {
        return this.character;
    }

    /**
     * Gets the left subtree
     *
     * @return a ref to the left subtree.
     */
    public HuffmanNode getLeft() {
        return this.left;
    }

    /**
     * Gets the right subtree.
     *
     * @return a ref to the right subtree.
     */
    public HuffmanNode getRight() {
        return this.right;
    }

    /**
     * set the left subtree.
     *
     * @param l a ref to the new subtree.
     */
    public void setLeft(HuffmanNode l) {
        this.left = l;
    }

    /**
     * set the right subtree.
     *
     * @param r a ref to the new subtree.
     */
    public void setRight(HuffmanNode r) {
        this.right = r;
    }

    /**
     * sets the character of the current node
     *
     * @param character the new letter.
     */
    public void setCharacter(char character) {
        this.character = character;
    }
}
