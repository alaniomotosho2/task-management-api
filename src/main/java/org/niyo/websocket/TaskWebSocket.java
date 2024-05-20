package org.niyo.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;
import org.niyo.task.TaskService;
import org.niyo.task.dto.TaskResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/tasks/{username}", configurator = JwtConfigurator.class)
@ApplicationScoped
public class TaskWebSocket {

    private static final Logger LOG = Logger.getLogger(TaskWebSocket.class);

    @Inject
    TaskService taskService;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        LOG.info("WebSocket connection opened: " + username);
        sessions.put(username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        LOG.info("WebSocket connection closed: " + username);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        LOG.error("WebSocket error occurred: " + username, throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        LOG.info("Message from " + username + ": " + message);
        if (message.equals("get-tasks")) {
            broadcastTasks(session);
        } else {
            broadcast(">> " + username + ": " + message);
        }
    }

    private void broadcastTasks(Session session) {
        List<TaskResponse> tasks = taskService.streamTasks(null);
        try {
            String taskJson = convertTasksToJson(tasks);
            session.getAsyncRemote().sendText(taskJson);
        } catch (Exception e) {
            LOG.error("Error sending tasks to client: " + session.getId(), e);
        }
    }

    private String convertTasksToJson(List<TaskResponse> tasks) {
        // Convert tasks to JSON (using a library like Jackson or manually)
        StringBuilder json = new StringBuilder("[");
        for (TaskResponse task : tasks) {
            json.append(task.toString()).append(",");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 1); // Remove last comma
        }
        json.append("]");
        return json.toString();
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    LOG.error("Unable to send message: " + result.getException());
                }
            });
        });
    }
}
