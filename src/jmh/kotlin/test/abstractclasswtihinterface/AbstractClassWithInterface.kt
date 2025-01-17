package test.abstractclasswtihinterface

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

interface Visitor {
     fun visit(node: TreeNode)
     fun visit0(node: Leaf)
     fun visit1(node: OneChildNode)
     fun visit2(node: TwoChildNode)
     fun visit3(node: ThreeChildNode)
     fun visit4(node: FourChildNode)
}

abstract class VisitorBase : Visitor {
    override fun visit(node: TreeNode) = node.acceptChildren(this)
    override fun visit0(node: Leaf) = visit(node)
    override fun visit1(node: OneChildNode) = visit(node)
    override fun visit2(node: TwoChildNode) = visit(node)
    override fun visit3(node: ThreeChildNode) = visit(node)
    override fun visit4(node: FourChildNode) = visit(node)
}

abstract class TreeNode {
    abstract fun accept(visitor: Visitor)
    abstract fun acceptChildren(visitor: Visitor)
}

class Leaf: TreeNode() {
    override fun accept(visitor: Visitor) { visitor.visit0(this) }
    override fun acceptChildren(visitor: Visitor) { }
}
class OneChildNode(val a: TreeNode): TreeNode() {
    override fun accept(visitor: Visitor) { visitor.visit1(this) }
    override fun acceptChildren(visitor: Visitor) { a.accept(visitor) }
}
class TwoChildNode(val a: TreeNode, val b: TreeNode): TreeNode() {
    override fun accept(visitor: Visitor) { visitor.visit2(this) }
    override fun acceptChildren(visitor: Visitor) {
        a.accept(visitor)
        b.accept(visitor)
    }

}
class ThreeChildNode(val a: TreeNode, val b: TreeNode, val c: TreeNode): TreeNode() {
    override fun accept(visitor: Visitor) { visitor.visit3(this) }
    override fun acceptChildren(visitor: Visitor) {
        a.accept(visitor)
        b.accept(visitor)
        c.accept(visitor)
    }
}
class FourChildNode(val a: TreeNode, val b: TreeNode, val c: TreeNode, val d: TreeNode): TreeNode() {
    override fun accept(visitor: Visitor) { visitor.visit4(this) }
    override fun acceptChildren(visitor: Visitor) {
        a.accept(visitor)
        b.accept(visitor)
        c.accept(visitor)
        d.accept(visitor)
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
open class TestAbstractClassWithInterface {
    val randomTree = genRandomTree(1000000)


    @Benchmark
    fun test() {
        var count = 0
        val visitors = listOf(
            object : VisitorBase() {
                override fun visit2(node: TwoChildNode) {
                    count++
                    node.acceptChildren(this)
                }
            },
            object : VisitorBase() {
                override fun visit3(node: ThreeChildNode) {
                    count++
                    node.acceptChildren(this)
                }
            },
            object : VisitorBase() {
                override fun visit4(node: FourChildNode) {
                    count++
                    node.acceptChildren(this)
                }
            }
        )
        for (visitor in visitors) {
            randomTree.accept(visitor)
        }
        count
    }
}