package test.genericinterface
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

interface Visitor<T> {
    fun visit(node: TreeNode, data: T) = node.acceptChildren(this, data)
    fun visit0(node: Leaf, data: T) = visit(node, data)
    fun visit1(node: OneChildNode, data: T) = visit(node, data)
    fun visit2(node: TwoChildNode, data: T) = visit(node, data)
    fun visit3(node: ThreeChildNode, data: T) = visit(node, data)
    fun visit4(node: FourChildNode, data: T) = visit(node, data)
}

interface VisitorVoid: Visitor<Nothing?> {
    fun visit(node: TreeNode) = node.acceptChildren(this, null)
    fun visit0(node: Leaf) = visit(node)
    fun visit1(node: OneChildNode) = visit(node)
    fun visit2(node: TwoChildNode) = visit(node)
    fun visit3(node: ThreeChildNode) = visit(node)
    fun visit4(node: FourChildNode) = visit(node)
    override fun visit(node: TreeNode, data: Nothing?) = visit(node)
    override fun visit0(node: Leaf, data: Nothing?) = visit0(node)
    override fun visit1(node: OneChildNode, data: Nothing?) = visit1(node)
    override fun visit2(node: TwoChildNode, data: Nothing?) = visit2(node)
    override fun visit3(node: ThreeChildNode, data: Nothing?) = visit3(node)
    override fun visit4(node: FourChildNode, data: Nothing?) = visit4(node)
}



abstract class TreeNode {
    abstract fun <T> accept(visitor: Visitor<T>, data: T)
    abstract fun <T> acceptChildren(visitor: Visitor<T>, data:T)
}

class Leaf: TreeNode() {
    override fun <T> accept(visitor: Visitor<T>, data: T) { visitor.visit0(this, data) }
    override fun <T> acceptChildren(visitor: Visitor<T>, data: T) { }
}
class OneChildNode(val a: TreeNode): TreeNode() {
    override fun <T> accept(visitor: Visitor<T>, data: T) { visitor.visit1(this, data) }
    override fun <T> acceptChildren(visitor: Visitor<T>, data: T) { a.accept(visitor, data) }
}
class TwoChildNode(val a: TreeNode, val b: TreeNode): TreeNode() {
    override fun <T> accept(visitor: Visitor<T>, data: T) { visitor.visit2(this, data) }
    override fun <T> acceptChildren(visitor: Visitor<T>, data: T) {
        a.accept(visitor, data)
        b.accept(visitor, data)
    }

}
class ThreeChildNode(val a: TreeNode, val b: TreeNode, val c: TreeNode): TreeNode() {
    override fun <T> accept(visitor: Visitor<T>, data: T) { visitor.visit3(this, data) }
    override fun <T> acceptChildren(visitor: Visitor<T>, data: T) {
        a.accept(visitor, data)
        b.accept(visitor, data)
        c.accept(visitor, data)
    }
}
class FourChildNode(val a: TreeNode, val b: TreeNode, val c: TreeNode, val d: TreeNode): TreeNode() {
    override fun <T> accept(visitor: Visitor<T>, data: T) { visitor.visit4(this, data) }
    override fun <T> acceptChildren(visitor: Visitor<T>, data: T) {
        a.accept(visitor, data)
        b.accept(visitor, data)
        c.accept(visitor, data)
        d.accept(visitor, data)
    }
}

val r = Random(239)

fun genRandomTree(n: Int): TreeNode {
    if (n == 0) return Leaf()
    val size = r.nextInt(1, minOf(4, n) + 1)
    val split = (listOf(0, n - 1) + List(size - 1) { r.nextInt(0, n - 1) }).sorted()
    val subs = split.zipWithNext { a, b -> genRandomTree(b - a) }
    require(subs.size == size)
    return when (size) {
        1 -> OneChildNode(subs[0])
        2 -> TwoChildNode(subs[0], subs[1])
        3 -> ThreeChildNode(subs[0], subs[1], subs[2])
        else -> FourChildNode(subs[0], subs[1], subs[2], subs[3])
    }
}

@Warmup(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
open class TestGenericInterface {
    val randomTree = genRandomTree(1000000)


    @Benchmark
    fun test() {
        var count = 0
        val visitors = listOf(
            object : VisitorVoid {
                override fun visit2(node: TwoChildNode) {
                    count++
                    node.acceptChildren(this, null)
                }
            },
            object : VisitorVoid {
                override fun visit3(node: ThreeChildNode) {
                    count++
                    node.acceptChildren(this, null)
                }
            },
            object : VisitorVoid {
                override fun visit4(node: FourChildNode) {
                    count++
                    node.acceptChildren(this, null)
                }
            }
        )
        for (visitor in visitors) {
            randomTree.accept(visitor, null)
        }
        count
    }
}