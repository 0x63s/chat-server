package chatroom.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Chatroom {
    private String name;
    private String creator;
    private Set<String> members;

    public static final List<Chatroom> chatrooms = new ArrayList<>();

    //private final List<Message> messages = new ArrayList<>();

    //record Message(String username, String message) {
    //}

    public Chatroom(String name, String creator) {
        this.name = name;
        this.creator = creator;
        this.members = new HashSet<>();
        this.members.add(creator);
    }

    //Add methods to manage members, check creator, etc.
    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void addMember(String username) {
        members.add(username);
    }

    public void removeMember(String username) {
        members.remove(username);
    }

    public boolean isCreator(String username) {
        return creator.equals(username);
    }

    public boolean isMember(String username) {
        return members.contains(username);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

//    public boolean isMemberOrCreator(String username) {
//        return isCreator(username) || isMember(username);
//    }

    public static boolean exists(String name) {
        synchronized (chatrooms) {
            for (Chatroom chatroom : chatrooms) {
                if (chatroom.name.equals(name)) return true;
            }
        }
        return false;
    }

    public static List<String> listChatrooms() {
        synchronized (chatrooms) {
            return chatrooms.stream().map( a -> a.name ).collect(java.util.stream.Collectors.toList());
        }
    }

    public static Chatroom get(String name) {
        synchronized (chatrooms) {
            for (Chatroom chatroom : chatrooms) {
                if (chatroom.name.equals(name)) return chatroom;
            }
        }
        return null;
    }

    public static void add(Chatroom chatroom) {
        synchronized (chatrooms) {
            chatrooms.add(chatroom);
        }
    }



}