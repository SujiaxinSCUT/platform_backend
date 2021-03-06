package com.trace.platform.service.dto;

import java.util.List;

public class TraceResult {

    List<Edge> edges;
    List<Node> nodes;

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
