/*
 * 发布订阅模式 · Pub Sub Pattern
 * 描述
 * 发布/订阅模式是系统设计中广泛使用的模式。 在此问题中，您需要实现发布/订阅模式以支持特定频道上的用户订阅，并从订阅的频道获取通知消息。
 * 
 * 您需要实现3种方法： 
 * subscribe(channel, user_id)：将给定用户订阅到给定频道。
 * unsubscribe(channel，user_id)：取消订阅给定用户的给定用户。
 * publish(channel，message)：您需要将消息发布到频道，以便在频道上订阅的每个人都会收到此消息。 调用PushNotification.notify（user_id，message）将消息推送给用户。
 * 
 * Pub/Sub pattern is a wide used pattern in system design. 
 * In this problem, you need to implement a pub/sub pattern to support user subscribes on a specific channel and get notification messages from subscribed channels.

 * There are 3 methods you need to implement:
 * subscribe(channel, user_id): Subscribe the given user to the given channel.
 * unsubscribe(channel, user_id): Unsubscribe the given user from the given channel.
 * publish(channel, message): You need to publish the message to the channel so that everyone subscribed on the channel will receive this message. Call PushNotification.notify(user_id, message) to push the message to the user.
 * 
 * 样例
 * subscribe("group1",  1)
 * publish("group1", "hello")
 * >> user 1 received "Hello"
 * subscribe("group1", 2)
 * publish("group1", "thank you")
 * >> user 1 received "thank you"
 * >> user 2 received "thank you"
 * unsubscribe("group2", 3)
 * >> user 3 is not in group2, do nothing
 * unsubscribe("group1", 1)
 * publish("group1", "thank you very much")
 * >> user 2 received "thank you very much"
 * publish("group2", "are you ok?")
 * >> # you don't need to push this message to anyone
 * 如果在同一频道上订阅的用户超过1个，则用户接收该消息的时间顺序无关紧要。 您可以在将消息推送给用户1之前推送给用户2。
 */

/* Definition of PushNotification
 * class PushNotification {
 *     public static void notify(int user_id, String the_message)
 *  };
 */
public class PubSubPattern {
    private Map<String, Set<Integer>> channels;
    
    public PubSubPattern(){
    	channels = new HashMap<String, HashSet<Integer>>();
    }
    
    /**
     * @param channel: 
     * @param user_id: 
     * @return: nothing
     */
    public void subscribe(String channel, int user_id) {
        if (!channels.containsKey(channel)) {
            channels.put(channel, new HashSet<Integer>());
        }
        HashSet<Integer> user_ids = channels.get(channel);
        user_ids.add(user_id);
    }

    /**
     * @param channel: 
     * @param user_id: 
     * @return: nothing
     */
    public void unsubscribe(String channel, int user_id) {
        if (!channels.containsKey(channel)) {
            return;
        }
        
        HashSet<Integer> user_ids = channels.get(channel);
        user_ids.remove(user_id);
    }

    /**
     * @param channel: 
     * @param message: 
     * @return: nothing
     */
    public void publish(String channel, String message) {
        if (!channels.containsKey(channel)) {
            return;
        }
        for (Integer user_id : channels.get(channel)) {
            PushNotification.notify(user_id, message);
        }
    }
}
