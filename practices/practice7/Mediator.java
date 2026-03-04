import java.util.*;

public class ChatMediatorDemo {

    // ===== Interfaces =====
    interface IMediator {
        void createChannel(String channel);

        void join(String channel, IUser user);
        void leave(String channel, IUser user);

        void sendToChannel(String channel, IUser from, String text);
        void sendToChannel(String channel, String fromUserName, String text); // cross-channel by name

        void sendPrivate(IUser from, String toUserName, String text);

        void mute(String channel, String adminName, String targetName, int messagesCount);
        void unmute(String channel, String adminName, String targetName);

        boolean isMember(String channel, String userName);
    }

    interface IUser {
        String name();
        void receive(String channel, String from, String text);
        void system(String channel, String text);

        void join(String channel);
        void leave(String channel);

        void say(String channel, String text);
        void sayTo(String channel, String text); 
        void pm(String toUserName, String text);
    }


    static class ChatMediator implements IMediator {
        protected final Map<String, ChannelMediator> channels = new HashMap<>();
        protected final Map<String, IUser> usersByName = new HashMap<>();
        protected final boolean autoCreateChannel;

        ChatMediator(boolean autoCreateChannel) {
            this.autoCreateChannel = autoCreateChannel;
        }

        @Override
        public void createChannel(String channel) {
            channels.computeIfAbsent(channel, ChannelMediator::new);
        }

        protected ChannelMediator requireChannel(String channel) {
            ChannelMediator cm = channels.get(channel);
            if (cm == null) {
                if (autoCreateChannel) {
                    cm = new ChannelMediator(channel);
                    channels.put(channel, cm);
                } else {
                    throw new IllegalArgumentException("Channel does not exist: " + channel);
                }
            }
            return cm;
        }

        protected IUser requireUser(String userName) {
            IUser u = usersByName.get(userName);
            if (u == null) throw new IllegalArgumentException("Unknown user: " + userName);
            return u;
        }

        protected void registerUser(IUser user) {
            usersByName.put(user.name(), user);
        }

        @Override
        public void join(String channel, IUser user) {
            Objects.requireNonNull(user);
            registerUser(user);
            ChannelMediator cm = requireChannel(channel);
            cm.add(user);
        }

        @Override
        public void leave(String channel, IUser user) {
            Objects.requireNonNull(user);
            ChannelMediator cm = requireChannel(channel);
            cm.remove(user);
        }

        @Override
        public void sendToChannel(String channel, IUser from, String text) {
            Objects.requireNonNull(from);
            ChannelMediator cm = requireChannel(channel);
            cm.broadcast(from.name(), text);
        }

        @Override
        public void sendToChannel(String channel, String fromUserName, String text) {
            IUser from = requireUser(fromUserName);
            sendToChannel(channel, from, text);
        }

        @Override
        public void sendPrivate(IUser from, String toUserName, String text) {
            Objects.requireNonNull(from);
            IUser to = requireUser(toUserName);
            to.receive("(PM)", from.name(), text);
        }

        @Override
        public void mute(String channel, String adminName, String targetName, int messagesCount) {
            ChannelMediator cm = requireChannel(channel);
            cm.mute(adminName, targetName, messagesCount);
        }

        @Override
        public void unmute(String channel, String adminName, String targetName) {
            ChannelMediator cm = requireChannel(channel);
            cm.unmute(adminName, targetName);
        }

        @Override
        public boolean isMember(String channel, String userName) {
            ChannelMediator cm = channels.get(channel);
            return cm != null && cm.isMember(userName);
        }
    }

    static class ChannelMediator {
        private final String name;
        private final Map<String, IUser> members = new LinkedHashMap<>();
        private final Set<String> admins = new HashSet<>();
        private final Map<String, Integer> muteRemaining = new HashMap<>(); 

        ChannelMediator(String name) { this.name = name; }

        void add(IUser user) {
            boolean wasEmpty = members.isEmpty();
            members.put(user.name(), user);
            if (wasEmpty) admins.add(user.name()); 
            systemToAll(user.name() + " joined");
        }

        void remove(IUser user) {
            if (members.remove(user.name()) != null) {
                admins.remove(user.name());
                muteRemaining.remove(user.name());
                systemToAll(user.name() + " left");
                if (members.size() == 1) admins.add(members.keySet().iterator().next());
            }
        }

        boolean isMember(String userName) {
            return members.containsKey(userName);
        }

        void broadcast(String from, String text) {
            if (!members.containsKey(from)) {
                throw new IllegalStateException("User '" + from + "' is not in channel '" + name + "'");
            }
            Integer left = muteRemaining.get(from);
            if (left != null && left > 0) {
                muteRemaining.put(from, left - 1);
                members.get(from).system(name, "You are muted (" + left + " messages left blocked).");
                return;
            } else if (left != null && left <= 0) {
                muteRemaining.remove(from);
                members.get(from).system(name, "Mute expired.");
            }

            for (IUser u : members.values()) {
                if (!u.name().equals(from)) u.receive(name, from, text);
            }
        }

        void systemToAll(String text) {
            for (IUser u : members.values()) u.system(name, text);
        }

        void mute(String adminName, String targetName, int messagesCount) {
            if (!admins.contains(adminName)) {
                throw new SecurityException("User '" + adminName + "' is not admin in channel '" + name + "'");
            }
            if (!members.containsKey(targetName)) {
                throw new IllegalArgumentException("Target '" + targetName + "' not in channel '" + name + "'");
            }
            int cnt = Math.max(1, messagesCount);
            muteRemaining.put(targetName, cnt);
            systemToAll("ADMIN " + adminName + " muted " + targetName + " for " + cnt + " messages");
        }

        void unmute(String adminName, String targetName) {
            if (!admins.contains(adminName)) {
                throw new SecurityException("User '" + adminName + "' is not admin in channel '" + name + "'");
            }
            muteRemaining.remove(targetName);
            systemToAll("ADMIN " + adminName + " unmuted " + targetName);
        }
    }


    static class User implements IUser {
        private final String name;
        private final IMediator mediator;

        User(String name, IMediator mediator) {
            this.name = Objects.requireNonNull(name);
            this.mediator = Objects.requireNonNull(mediator);
        }

        @Override public String name() { return name; }

        @Override
        public void receive(String channel, String from, String text) {
            System.out.println("[" + channel + "] " + from + " -> " + name + ": " + text);
        }

        @Override
        public void system(String channel, String text) {
            System.out.println("[" + channel + "] *system* -> " + name + ": " + text);
        }

        @Override public void join(String channel) { mediator.join(channel, this); }
        @Override public void leave(String channel) { mediator.leave(channel, this); }

        @Override public void say(String channel, String text) { mediator.sendToChannel(channel, this, text); }
        @Override public void sayTo(String channel, String text) { mediator.sendToChannel(channel, name, text); }

        @Override public void pm(String toUserName, String text) { mediator.sendPrivate(this, toUserName, text); }
    }

    public static void main(String[] args) {
        IMediator mediator = new ChatMediator(true); 

        IUser alice = new User("Alice", mediator);
        IUser bob = new User("Bob", mediator);
        IUser carol = new User("Carol", mediator);

        alice.join("general");
        bob.join("general");
        carol.join("general");

        alice.join("dev");
        bob.join("dev");

        System.out.println("\n== Messages in channels ==");
        alice.say("general", "Привет всем!");
        bob.say("dev", "Кто сегодня делает билд?");
        carol.say("general", "Я в general, не вижу dev сообщения.");

        System.out.println("\n== Error: user not in channel ==");
        try {
            carol.say("dev", "Пустите в dev!");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        System.out.println("\n== Private message ==");
        alice.pm("Carol", "Хочешь зайти в dev?");

        System.out.println("\n== Cross-channel send (Alice -> dev) ==");
        alice.sayTo("dev", "Я пишу в dev, хотя сейчас могу быть не участником? (проверим)");

        alice.join("dev");
        alice.sayTo("dev", "Теперь я в dev и пишу сюда.");

        System.out.println("\n== Admin mute (first member of channel becomes admin) ==");

        mediator.mute("general", "Alice", "Bob", 2);
        bob.say("general", "1) Это сообщение должно быть заблокировано");
        bob.say("general", "2) И это тоже");
        bob.say("general", "3) А это уже должно пройти");

        System.out.println("\n== Leave notifications ==");
        carol.leave("general");
        alice.say("general", "Carol вышла.");

        System.out.println("\n== Non-admin tries to mute ==");
        try {
            mediator.mute("general", "Bob", "Alice", 1);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
