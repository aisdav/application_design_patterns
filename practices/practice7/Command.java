import java.util.*;

public class SmartHomeCommandDemo {

    interface ICommand {
        void execute();
        void undo();
        default String name() { return getClass().getSimpleName(); }
    }

    static class Light {
        private final String location;
        private boolean on;
        Light(String location) { this.location = location; }
        void on() { on = true; System.out.println("💡 Light [" + location + "] ON"); }
        void off() { on = false; System.out.println("💡 Light [" + location + "] OFF"); }
        boolean isOn() { return on; }
    }

    static class TV {
        private final String location;
        private boolean on;
        private int channel = 1;
        TV(String location) { this.location = location; }
        void on() { on = true; System.out.println("📺 TV [" + location + "] ON (ch " + channel + ")"); }
        void off() { on = false; System.out.println("📺 TV [" + location + "] OFF"); }
        void setChannel(int channel) {
            int prev = this.channel;
            this.channel = Math.max(1, channel);
            System.out.println("📺 TV [" + location + "] channel " + prev + " -> " + this.channel);
        }
        boolean isOn() { return on; }
        int getChannel() { return channel; }
    }

    static class AirConditioner {
        private final String location;
        private boolean on;
        private int temp = 24;
        AirConditioner(String location) { this.location = location; }
        void on() { on = true; System.out.println("❄️ AC [" + location + "] ON (temp " + temp + "°C)"); }
        void off() { on = false; System.out.println("❄️ AC [" + location + "] OFF"); }
        void setTemp(int temp) {
            int prev = this.temp;
            this.temp = Math.max(16, Math.min(30, temp));
            System.out.println("❄️ AC [" + location + "] temp " + prev + " -> " + this.temp + "°C");
        }
        boolean isOn() { return on; }
        int getTemp() { return temp; }
    }

    static class Curtains {
        private final String location;
        private boolean open;
        Curtains(String location) { this.location = location; }
        void open() { open = true; System.out.println("🪟 Curtains [" + location + "] OPEN"); }
        void close() { open = false; System.out.println("🪟 Curtains [" + location + "] CLOSE"); }
        boolean isOpen() { return open; }
    }

    static class MusicPlayer {
        private final String location;
        private boolean playing;
        private int volume = 5;
        MusicPlayer(String location) { this.location = location; }
        void play() { playing = true; System.out.println("🎵 Music [" + location + "] PLAY (vol " + volume + ")"); }
        void stop() { playing = false; System.out.println("🎵 Music [" + location + "] STOP"); }
        void setVolume(int volume) {
            int prev = this.volume;
            this.volume = Math.max(0, Math.min(10, volume));
            System.out.println("🎵 Music [" + location + "] volume " + prev + " -> " + this.volume);
        }
        boolean isPlaying() { return playing; }
        int getVolume() { return volume; }
    }

    static class NoCommand implements ICommand {
        @Override public void execute() { System.out.println("⚠️ No command assigned to this slot."); }
        @Override public void undo() { System.out.println("⚠️ Nothing to undo."); }
        @Override public String name() { return "NoCommand"; }
    }

    static class LightOnCommand implements ICommand {
        private final Light light;
        private boolean prevOn;
        LightOnCommand(Light light) { this.light = light; }
        @Override public void execute() { prevOn = light.isOn(); light.on(); }
        @Override public void undo() { if (prevOn) light.on(); else light.off(); }
    }

    static class LightOffCommand implements ICommand {
        private final Light light;
        private boolean prevOn;
        LightOffCommand(Light light) { this.light = light; }
        @Override public void execute() { prevOn = light.isOn(); light.off(); }
        @Override public void undo() { if (prevOn) light.on(); else light.off(); }
    }

    static class TVOnCommand implements ICommand {
        private final TV tv;
        private boolean prevOn;
        TVOnCommand(TV tv) { this.tv = tv; }
        @Override public void execute() { prevOn = tv.isOn(); tv.on(); }
        @Override public void undo() { if (prevOn) tv.on(); else tv.off(); }
    }

    static class TVOffCommand implements ICommand {
        private final TV tv;
        private boolean prevOn;
        TVOffCommand(TV tv) { this.tv = tv; }
        @Override public void execute() { prevOn = tv.isOn(); tv.off(); }
        @Override public void undo() { if (prevOn) tv.on(); else tv.off(); }
    }

    static class TVSetChannelCommand implements ICommand {
        private final TV tv;
        private int prevChannel;
        private boolean prevOn;
        private final int newChannel;
        TVSetChannelCommand(TV tv, int newChannel) { this.tv = tv; this.newChannel = newChannel; }
        @Override public void execute() {
            prevOn = tv.isOn();
            prevChannel = tv.getChannel();
            if (!tv.isOn()) tv.on();
            tv.setChannel(newChannel);
        }
        @Override public void undo() {
            if (!prevOn) {
                tv.setChannel(prevChannel);
                tv.off();
            } else {
                tv.setChannel(prevChannel);
                tv.on();
            }
        }
    }

    static class ACOnCommand implements ICommand {
        private final AirConditioner ac;
        private boolean prevOn;
        ACOnCommand(AirConditioner ac) { this.ac = ac; }
        @Override public void execute() { prevOn = ac.isOn(); ac.on(); }
        @Override public void undo() { if (prevOn) ac.on(); else ac.off(); }
    }

    static class ACOffCommand implements ICommand {
        private final AirConditioner ac;
        private boolean prevOn;
        ACOffCommand(AirConditioner ac) { this.ac = ac; }
        @Override public void execute() { prevOn = ac.isOn(); ac.off(); }
        @Override public void undo() { if (prevOn) ac.on(); else ac.off(); }
    }

    static class ACSetTempCommand implements ICommand {
        private final AirConditioner ac;
        private int prevTemp;
        private boolean prevOn;
        private final int newTemp;
        ACSetTempCommand(AirConditioner ac, int newTemp) { this.ac = ac; this.newTemp = newTemp; }
        @Override public void execute() {
            prevOn = ac.isOn();
            prevTemp = ac.getTemp();
            if (!ac.isOn()) ac.on();
            ac.setTemp(newTemp);
        }
        @Override public void undo() {
            if (!prevOn) {
                ac.setTemp(prevTemp);
                ac.off();
            } else {
                ac.setTemp(prevTemp);
                ac.on();
            }
        }
    }

    static class CurtainsOpenCommand implements ICommand {
        private final Curtains curtains;
        private boolean prevOpen;
        CurtainsOpenCommand(Curtains curtains) { this.curtains = curtains; }
        @Override public void execute() { prevOpen = curtains.isOpen(); curtains.open(); }
        @Override public void undo() { if (prevOpen) curtains.open(); else curtains.close(); }
    }

    static class CurtainsCloseCommand implements ICommand {
        private final Curtains curtains;
        private boolean prevOpen;
        CurtainsCloseCommand(Curtains curtains) { this.curtains = curtains; }
        @Override public void execute() { prevOpen = curtains.isOpen(); curtains.close(); }
        @Override public void undo() { if (prevOpen) curtains.open(); else curtains.close(); }
    }

    static class MusicPlayCommand implements ICommand {
        private final MusicPlayer player;
        private boolean prevPlaying;
        MusicPlayCommand(MusicPlayer player) { this.player = player; }
        @Override public void execute() { prevPlaying = player.isPlaying(); player.play(); }
        @Override public void undo() { if (prevPlaying) player.play(); else player.stop(); }
    }

    static class MusicStopCommand implements ICommand {
        private final MusicPlayer player;
        private boolean prevPlaying;
        MusicStopCommand(MusicPlayer player) { this.player = player; }
        @Override public void execute() { prevPlaying = player.isPlaying(); player.stop(); }
        @Override public void undo() { if (prevPlaying) player.play(); else player.stop(); }
    }

    static class MusicSetVolumeCommand implements ICommand {
        private final MusicPlayer player;
        private int prevVolume;
        private boolean prevPlaying;
        private final int newVolume;
        MusicSetVolumeCommand(MusicPlayer player, int newVolume) { this.player = player; this.newVolume = newVolume; }
        @Override public void execute() {
            prevPlaying = player.isPlaying();
            prevVolume = player.getVolume();
            if (!player.isPlaying()) player.play();
            player.setVolume(newVolume);
        }
        @Override public void undo() {
            if (!prevPlaying) {
                player.setVolume(prevVolume);
                player.stop();
            } else {
                player.setVolume(prevVolume);
                player.play();
            }
        }
    }

    static class MacroCommand implements ICommand {
        private final List<ICommand> commands;
        MacroCommand(List<ICommand> commands) { this.commands = new ArrayList<>(commands); }
        @Override public void execute() { for (ICommand c : commands) c.execute(); }
        @Override public void undo() { for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo(); }
        @Override public String name() { return "MacroCommand(" + commands.size() + ")"; }
    }

    static class RemoteControl {
        private final ICommand[] onSlots;
        private final ICommand[] offSlots;
        private final Deque<ICommand> undoStack = new ArrayDeque<>();
        private final Deque<ICommand> redoStack = new ArrayDeque<>();
        private boolean recording;
        private final List<ICommand> recorded = new ArrayList<>();

        RemoteControl(int slots) {
            onSlots = new ICommand[slots];
            offSlots = new ICommand[slots];
            ICommand no = new NoCommand();
            Arrays.fill(onSlots, no);
            Arrays.fill(offSlots, no);
        }

        void setCommands(int slot, ICommand onCommand, ICommand offCommand) {
            checkSlot(slot);
            onSlots[slot] = (onCommand == null) ? new NoCommand() : onCommand;
            offSlots[slot] = (offCommand == null) ? new NoCommand() : offCommand;
        }

        void pressOn(int slot) {
            checkSlot(slot);
            ICommand cmd = onSlots[slot];
            run(cmd);
        }

        void pressOff(int slot) {
            checkSlot(slot);
            ICommand cmd = offSlots[slot];
            run(cmd);
        }

        void pressUndo() {
            if (undoStack.isEmpty()) {
                System.out.println("↩️  Undo: nothing");
                return;
            }
            ICommand cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
            if (recording) recorded.add(new UndoCommand(this));
        }

        void pressRedo() {
            if (redoStack.isEmpty()) {
                System.out.println("↪️  Redo: nothing");
                return;
            }
            ICommand cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
            if (recording) recorded.add(new RedoCommand(this));
        }

        void startRecording() {
            recording = true;
            recorded.clear();
            System.out.println("⏺️  Recording started");
        }

        ICommand stopRecordingAsMacro(String macroName) {
            recording = false;
            ICommand macro = new NamedMacroCommand(macroName, recorded);
            System.out.println("⏹️  Recording stopped -> " + macro.name());
            return macro;
        }

        void printSlots() {
            System.out.println("\n--- Remote Slots ---");
            for (int i = 0; i < onSlots.length; i++) {
                System.out.printf("Slot %d: ON=%s | OFF=%s%n", i, onSlots[i].name(), offSlots[i].name());
            }
            System.out.println("--------------------\n");
        }

        private void run(ICommand cmd) {
            cmd.execute();
            if (!(cmd instanceof NoCommand)) {
                undoStack.push(cmd);
                redoStack.clear();
            }
            if (recording) recorded.add(cmd);
        }

        private void checkSlot(int slot) {
            if (slot < 0 || slot >= onSlots.length) {
                throw new IllegalArgumentException("Invalid slot: " + slot + ", allowed 0.." + (onSlots.length - 1));
            }
        }
    }

    static class UndoCommand implements ICommand {
        private final RemoteControl remote;
        UndoCommand(RemoteControl remote) { this.remote = remote; }
        @Override public void execute() { remote.pressUndo(); }
        @Override public void undo() { }
        @Override public String name() { return "UndoCommand"; }
    }

    static class RedoCommand implements ICommand {
        private final RemoteControl remote;
        RedoCommand(RemoteControl remote) { this.remote = remote; }
        @Override public void execute() { remote.pressRedo(); }
        @Override public void undo() { }
        @Override public String name() { return "RedoCommand"; }
    }

    static class NamedMacroCommand extends MacroCommand {
        private final String macroName;
        NamedMacroCommand(String macroName, List<ICommand> commands) { super(commands); this.macroName = macroName; }
        @Override public String name() { return "Macro[" + macroName + "]"; }
    }

    public static void main(String[] args) {
        Light livingLight = new Light("Living Room");
        TV livingTV = new TV("Living Room");
        AirConditioner ac = new AirConditioner("Bedroom");
        Curtains curtains = new Curtains("Living Room");
        MusicPlayer music = new MusicPlayer("Kitchen");

        ICommand lightOn = new LightOnCommand(livingLight);
        ICommand lightOff = new LightOffCommand(livingLight);

        ICommand tvOn = new TVOnCommand(livingTV);
        ICommand tvOff = new TVOffCommand(livingTV);
        ICommand tvCh7 = new TVSetChannelCommand(livingTV, 7);

        ICommand acOn = new ACOnCommand(ac);
        ICommand acOff = new ACOffCommand(ac);
        ICommand acTemp20 = new ACSetTempCommand(ac, 20);

        ICommand curtainsOpen = new CurtainsOpenCommand(curtains);
        ICommand curtainsClose = new CurtainsCloseCommand(curtains);

        ICommand musicPlay = new MusicPlayCommand(music);
        ICommand musicStop = new MusicStopCommand(music);
        ICommand musicVol8 = new MusicSetVolumeCommand(music, 8);

        RemoteControl remote = new RemoteControl(6);

        remote.setCommands(0, lightOn, lightOff);
        remote.setCommands(1, tvOn, tvOff);
        remote.setCommands(2, acOn, acOff);
        remote.setCommands(3, curtainsOpen, curtainsClose);
        remote.setCommands(4, musicPlay, musicStop);

        remote.printSlots();

        System.out.println("== Single commands ==");
        remote.pressOn(0);
        remote.pressOff(0);
        remote.pressUndo();
        remote.pressRedo();

        remote.pressOn(1);
        tvCh7.execute();
        remote.pressUndo();
        remote.pressRedo();

        System.out.println("\n== Empty slot ==");
        remote.pressOn(5);
        remote.pressUndo();

        System.out.println("\n== Macro command ==");
        ICommand leavingHome = new MacroCommand(List.of(
                lightOff,
                tvOff,
                acOff,
                curtainsClose,
                musicStop
        ));
        leavingHome.execute();
        remote.pressUndo();

        System.out.println("\n== Record macro from remote ==");
        remote.startRecording();
        remote.pressOn(0);
        remote.pressOn(1);
        tvCh7.execute();
        remote.pressOn(2);
        acTemp20.execute();
        remote.pressOn(3);
        remote.pressOn(4);
        musicVol8.execute();
        remote.pressUndo();
        remote.pressRedo();
        ICommand movieNight = remote.stopRecordingAsMacro("MovieNight");

        System.out.println("\n== Assign recorded macro to slot 5 ON ==");
        remote.setCommands(5, movieNight, new NoCommand());
        remote.printSlots();

        remote.pressOn(5);
        remote.pressUndo();
    }
}
