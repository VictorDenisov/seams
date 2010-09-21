package org.creativelabs.seams

import javax.swing.JFrame

import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph

private class MainFrame {
    private val frame = new JFrame

    private val graph = new mxGraph();
    private val parent = graph.getDefaultParent()

    graph.getModel.beginUpdate()
    try {
        val v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30)

        val v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30)

        graph.insertEdge(parent, null, "Edge", v1, v2)
    } finally {
        graph.getModel.endUpdate()
    }

    private val graphComponent = new mxGraphComponent(graph)

    frame.getContentPane().add(graphComponent)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(400, 320)
    frame.setVisible(true)
}

object GuiApp {
    def main(args: Array[String]) {
        val frame = new MainFrame
    }
}

// vim: set ts=4 sw=4 et:
