package com.example.mud.controller;

import java.util.*;

public class MUDController {
    private final Player player;
    private boolean running;
    private final Scanner scanner;

    public MUDController(Player player) {
        this.player = player;
        this.running = true;
        this.scanner = new Scanner(System.in);
    }

    public void runGameLoop() {
        System.out.println("Welcome to the game! Type 'help' for commands.");
        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            handleInput(input);
        }
    }

    private void handleInput(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "look":
                lookAround();
                break;
            case "move":
                move(argument);
                break;
            case "pick":
                if (argument.startsWith("up ")) {
                    pickUp(argument.substring(3));
                } else {
                    System.out.println("Invalid command! Use 'pick up <item>'.");
                }
                break;
            case "inventory":
                checkInventory();
                break;
            case "help":
                showHelp();
                break;
            case "quit":
            case "exit":
                running = false;
                System.out.println("Goodbye!");
                break;
            default:
                System.out.println("Unknown command! Type 'help' for a list of commands.");
        }
    }

    private void lookAround() {
        Room currentRoom = player.getCurrentRoom();
        System.out.println("Room: " + currentRoom.getName());
        System.out.println(currentRoom.getDescription());
        System.out.println("Items here: " + currentRoom.listItems());
    }

    private void move(String direction) {
        Room newRoom = player.getCurrentRoom().getConnection(direction);
        if (newRoom != null) {
            player.setCurrentRoom(newRoom);
            System.out.println("You moved to: " + newRoom.getName());
            lookAround();
        } else {
            System.out.println("You can't go that way!");
        }
    }

    private void pickUp(String itemName) {
        Room currentRoom = player.getCurrentRoom();
        Item item = currentRoom.getItem(itemName);
        if (item != null) {
            player.addItem(item);
            currentRoom.removeItem(item);
            System.out.println("You picked up " + itemName + ".");
        } else {
            System.out.println("No item named " + itemName + " here!");
        }
    }

    private void checkInventory() {
        List<Item> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("You are carrying: ");
            for (Item item : inventory) {
                System.out.println("- " + item.getName());
            }
        }
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("look - Describe the current room.");
        System.out.println("move <direction> - Move in a direction (forward, back, left, right).");
        System.out.println("pick up <item> - Pick up an item.");
        System.out.println("inventory - Show items in your inventory.");
        System.out.println("help - Show this help menu.");
        System.out.println("quit / exit - End the game.");
    }

    public static void main(String[] args) {
        Room caveEntrance = new Room("Cave Entrance", "A gloomy entrance to an underground cave.");
        Room darkTunnel = new Room("Dark Tunnel", "A narrow tunnel with eerie echoes.");
        caveEntrance.addConnection("forward", darkTunnel);
        darkTunnel.addConnection("back", caveEntrance);

        Item axe = new Item("axe", "A heavy battle axe with a sharp edge.");
        caveEntrance.addItem(axe);

        Item helmet = new Item("helmet", "A reinforced iron helmet, slightly dented.");
        darkTunnel.addItem(helmet);

        Player player = new Player("Warrior", caveEntrance);
        MUDController controller = new MUDController(player);
        controller.runGameLoop();
    }
}

class Player {
    private final String name;
    private Room currentRoom;
    private final List<Item> inventory;

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }
}

class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> connections;
    private final List<Item> items;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.connections = new HashMap<>();
        this.items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addConnection(String direction, Room room) {
        connections.put(direction, room);
    }

    public Room getConnection(String direction) {
        return connections.get(direction);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName)).findFirst().orElse(null);
    }

    public String listItems() {
        return items.isEmpty() ? "No items." : String.join(", ", items.stream().map(Item::getName).toList());
    }
}

class Item {
    private final String name;
    private final String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
