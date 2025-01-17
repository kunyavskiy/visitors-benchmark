package test.javaiface;

public interface Visitor {
    default void visit(TreeNode node) { node.acceptChildren(this); }
    default void visit0(Leaf node) { visit(node); }
    default void visit1(OneChildNode node) { visit(node); }
    default void visit2(TwoChildNode node) { visit(node); }
    default void visit3(ThreeChildNode node) { visit(node); }
    default void visit4(FourChildNode node) { visit(node); }
}