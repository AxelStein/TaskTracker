package com.axel_stein.tasktracker.api.graph;

import com.axel_stein.tasktracker.api.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.Objects.requireNonNull;

public class TaskGraph {
    private int vertexCount = 0;
    private int edgeCount = 0;
    private HashMap<Integer, List<Integer>> adjacent;
    private HashMap<Integer, Task> tasks;
    private int taskCounter = 0;

    public TaskGraph() {
        adjacent = new HashMap<>();
        tasks = new HashMap<>();
    }

    public void addEdge(Task from, Task to) {
        int fromIndex;
        if (!tasks.containsValue(from)) {
            fromIndex = addVertex(from);
        } else {
            fromIndex = findTaskIndex(from);
        }

        int toIndex;
        if (!tasks.containsValue(to)) {
            toIndex = addVertex(to);
        } else {
            toIndex = findTaskIndex(to);
        }
        requireNonNull(adjacent.get(fromIndex)).add(toIndex);
        edgeCount++;
    }

    public void removeEdge(Task from, Task to) { // fixme
        String fromId = from.getId();
        String toId = to.getId();
        requireNonNull(adjacent.get(fromId)).remove(toId);

        if (degree(fromId) == 0) { // fixme
            removeVertex(fromId);
        }
    }

    /**
     * Kahn`s algorithm of topological sorting
     * @return sorted task list or null, if there is a cycle in the graph
     */
    public List<Task> sort() {
        List<Task> list = new ArrayList<>();

        // Create a vector to store indegrees of all
        // vertices. Initialize all indegrees as 0.
        int[] inDegree = new int[vertexCount];

        // Traverse adjacency lists to fill indegrees of
        // vertices. This step takes O(V+E) time
        for (Integer i : adjacent.keySet()) {
            for (Integer j : requireNonNull(adjacent.get(i))) {
                inDegree[j] += 1;
            }
        }

        // Create an queue and enqueue all vertices with
        // indegree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertexCount; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        // Initialize count of visited vertices
        int count = 0;

        // One by one dequeue vertices from queue and enqueue
        // adjacents if indegree of adjacent becomes 0
        while (!queue.isEmpty()) {
            // Extract front of queue (or perform dequeue)
            // and add it to topological order
            int v = queue.poll();
            list.add(tasks.get(v));

            // Iterate through all neighbouring nodes
            // of dequeued node u and decrease their in-degree by 1
            for (Integer i : requireNonNull(adjacent.get(v))) {
                inDegree[i] -= 1;
                // If in-degree becomes zero, add it to queue
                if (inDegree[i] == 0) {
                    queue.add(i);
                }
            }
            count++;
        }
        if (count != vertexCount) {
            // There exists a cycle in the graph
            return null;
        }
        return list;
    }

    private int findTaskIndex(Task t) {
        for (Integer i: tasks.keySet()) {
            if (requireNonNull(tasks.get(i)).equals(t)) {
                return i;
            }
        }
        return 0;
    }

    private int addVertex(Task t) {
        int v = taskCounter;
        tasks.put(v, t);
        adjacent.put(v, new ArrayList<>());
        taskCounter++;
        vertexCount++;
        return v;
    }

    private int degree(String v) { // fixme
        if (adjacent.containsKey(v)) {
            return requireNonNull(adjacent.get(v)).size();
        }
        return 0;
    }

    private void removeVertex(String v) { // fixme
        adjacent.remove(v);
        tasks.remove(v);
    }

}
