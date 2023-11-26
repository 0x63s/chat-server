package chatroom.server.handlers;

import chatroom.server.Chatroom;
import chatroom.server.Client;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatroomHandler extends Handler {

    @Override
    protected void handleGet(HttpExchange httpExchange, HandlerResponse response) {
        String mapping = httpExchange.getRequestURI().toString(); // For this handler, will begin with "/chatrooms"
        if (mapping.equals("/chatrooms")) {
            response.jsonOut.put("chatrooms", listChatrooms());
        } else { // Unsupported request type
            response.jsonOut.put("Error", "Invalid request");
        }
    }

    @Override
    protected void handlePost(HttpExchange httpExchange, JSONObject JSONin, HandlerResponse response) {
        String mapping = httpExchange.getRequestURI().toString(); // For this handler, will begin with "/chatroom"

        // Read various strings that may be present (depending on the mapping)
        String chatroomName = readString(JSONin, "chatroom");
        String username = readString(JSONin, "username");
        String token = readString(JSONin, "token");

        // If anything at all goes wrong, we throw an exception and return an error
        try {
            switch (mapping) {
                case "/chatroom/create" -> {
                    if (token == null || chatroomName == null) throw new Exception("Invalid parameters");
                    createChatroom(chatroomName, token, response);
                }
                case "/chatroom/join" -> {
                    if (token == null || chatroomName == null || username == null) throw new Exception("Invalid parameters");
                    joinChatroom(chatroomName, username, token, response);
                }
                case "/chatroom/leave" -> {
                    if (token == null || chatroomName == null || username == null) throw new Exception("Invalid parameters");
                    leaveChatroom(chatroomName, username, token, response);
                }
                default -> {
                    throw new Exception("No such mapping");
                }
            }
        } catch (Exception e) {
            response.jsonOut.put("Error", e.getMessage());
        }
    }

    private JSONArray listChatrooms() {
        JSONArray chatroomArray = new JSONArray();
        for (String chatroomName : Chatroom.listChatrooms()) chatroomArray.put(chatroomName);
        return chatroomArray;
    }

    private void createChatroom(String chatroomName, String token, HandlerResponse response) throws Exception {
        Client creator = Client.findByToken(token);
        if (creator == null) throw new Exception("Invalid token");
        if (Chatroom.exists(chatroomName)) throw new Exception("Chatroom already exists");

        Chatroom chatroom = new Chatroom(chatroomName, creator.getName());
        Chatroom.add(chatroom);

        creator.joinChatroom(chatroomName);
        response.jsonOut.put("chatroom", chatroomName);
    }

    private void joinChatroom(String chatroomName, String username, String token, HandlerResponse response) throws Exception {
        Client client = Client.findByToken(token);
        if (client == null) throw new Exception("Invalid token");
        if (client.isInAnyChatroom()) throw new Exception("User is already in a chatroom");
        Chatroom chatroom = Chatroom.get(chatroomName);
        if (chatroom == null) throw new Exception("Chatroom does not exist");
        if (!chatroom.isMember(username)) throw new Exception("User is not a member of the chatroom");

        client.joinChatroom(chatroomName);
        response.jsonOut.put("chatroom", chatroomName);
    }

    private void leaveChatroom(String chatroomName, String username, String token, HandlerResponse response) throws Exception {
        Client client = Client.findByToken(token);
        if (client == null) throw new Exception("Invalid token");
        if (!client.isInChatroom(chatroomName)) throw new Exception("User is not in the specified chatroom");
        Chatroom chatroom = Chatroom.get(chatroomName);
        if (chatroom == null) throw new Exception("Chatroom does not exist");

        client.leaveChatroom(chatroomName);
        response.jsonOut.put("chatroom", chatroomName);
    }
}
