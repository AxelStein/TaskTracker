package com.axel_stein.tasktracker.utils;

import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavType;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;
import java.util.Objects;

public class FragmentDestinationBuilder {

    public static FragmentDestinationBuilder from(NavController controller) {
        return new FragmentDestinationBuilder().setController(controller);
    }

    private NavController controller;
    private int id;
    private Class class_;
    private String label;
    private HashMap<String, NavArgument> arguments = new HashMap<>();

    private FragmentDestinationBuilder() {}

    private FragmentDestinationBuilder setController(NavController controller) {
        this.controller = controller;
        return this;
    }

    public FragmentDestinationBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public FragmentDestinationBuilder setClass(Class c) {
        this.class_ = c;
        return this;
    }

    public FragmentDestinationBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public FragmentDestinationBuilder addArgument(String argumentName, String value) {
        arguments.put(argumentName, getStringArgument(value));
        return this;
    }

    public FragmentDestinationBuilder addArgument(String argumentName, int value) {
        arguments.put(argumentName, getIntArgument(value));
        return this;
    }

    public FragmentNavigator.Destination build() {
        FragmentNavigator navigator = controller.getNavigatorProvider().getNavigator(FragmentNavigator.class);
        FragmentNavigator.Destination destination = new FragmentNavigator.Destination(navigator);
        destination.setClassName(class_.getName());
        destination.setId(id);
        destination.setLabel(label);

        for (String name : arguments.keySet()) {
            destination.addArgument(name, Objects.requireNonNull(arguments.get(name)));
        }

        return destination;
    }

    public void add(NavGraph graph) {
        graph.addDestination(build());
    }

    private static NavArgument getStringArgument(String value) {
        return getArgument(NavType.StringType, value);
    }

    private static NavArgument getIntArgument(int value) {
        return getArgument(NavType.IntType, value);
    }

    private static NavArgument getArgument(NavType type, Object value) {
        NavArgument.Builder builder = new NavArgument.Builder();
        builder.setType(type);
        builder.setDefaultValue(value);
        return builder.build();
    }

}
