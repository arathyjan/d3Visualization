package com.barclays.ivr.viz.domain;

public class Node {

    public final String label;

    public Node(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return label.equals(node.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    public static Node from(String input) {
        return new Node(input);
    }
}
