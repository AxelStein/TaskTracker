package com.axel_stein.tasktracker.api.graph;

import com.axel_stein.tasktracker.api.model.Task;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TaskGraphTest {

    @Test
    public void test() {
        Task t0 = createTask(0);
        Task t1 = createTask(1);
        Task t2 = createTask(2);
        Task t3 = createTask(3);
        Task t4 = createTask(4);
        Task t5 = createTask(5);
        Task t6 = createTask(6);
        Task t7 = createTask(7);
        Task t8 = createTask(8);

        TaskGraph graph = new TaskGraph();
        graph.addEdge(t0, t1);
        graph.addEdge(t0, t2);
        graph.addEdge(t0, t3);
        graph.addEdge(t0, t4);
        graph.addEdge(t1, t3);
        graph.addEdge(t2, t3);
        graph.addEdge(t2, t4);
        graph.addEdge(t3, t4);
        graph.addEdge(t4, t5);
        graph.addEdge(t3, t5);
        graph.addEdge(t1, t6);
        graph.addEdge(t6, t3);
        graph.addEdge(t3, t7);
        graph.addEdge(t7, t4);
        graph.addEdge(t7, t5);
        graph.addEdge(t4, t8);
        graph.addEdge(t8, t2);

        assertNull(graph.sort());
    }

    @Test
    public void test_cycle() {
        Task t0 = createTask(0);
        Task t1 = createTask(1);
        Task t2 = createTask(2);
        Task t3 = createTask(3);
        Task t4 = createTask(4);
        Task t5 = createTask(5);
        Task t6 = createTask(6);
        Task t7 = createTask(7);

        TaskGraph graph = new TaskGraph();
        graph.addEdge(t0, t1);
        graph.addEdge(t0, t2);
        graph.addEdge(t0, t3);
        graph.addEdge(t0, t4);
        graph.addEdge(t1, t3);
        graph.addEdge(t2, t3);
        graph.addEdge(t2, t4);
        graph.addEdge(t3, t4);
        graph.addEdge(t4, t5);
        graph.addEdge(t3, t5);
        graph.addEdge(t1, t6);
        graph.addEdge(t6, t3);
        graph.addEdge(t3, t7);
        graph.addEdge(t7, t4);
        graph.addEdge(t7, t5);

        System.out.println(graph.sort());

        List<Task> expected = new ArrayList<>();
        expected.add(t0);
        expected.add(t1);
        expected.add(t2);
        expected.add(t6);
        expected.add(t3);
        expected.add(t7);
        expected.add(t4);
        expected.add(t5);
        assertEquals(expected, graph.sort());
    }

    private Task createTask(Integer id) {
        Task task = new Task();
        task.setId(String.valueOf(id));
        return task;
    }

}