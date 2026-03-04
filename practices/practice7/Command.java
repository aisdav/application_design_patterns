import java.util.*;

public class SmartHomeCommand250 {

    interface ICommand {
        void execute();
        void undo();
        default String name(){ return getClass().getSimpleName(); }
    }

    static class NoCommand implements ICommand {
        public void execute(){ System.out.println("⚠️ Slot is empty"); }
        public void undo(){ System.out.println("⚠️ Nothing to undo"); }
        public String name(){ return "NoCommand"; }
    }


    static class Light {
        private final String where; private boolean on;
        Light(String where){ this.where=where; }
        void on(){ on=true; System.out.println("💡 "+where+": ON"); }
        void off(){ on=false; System.out.println("💡 "+where+": OFF"); }
        boolean isOn(){ return on; }
    }
    static class TV {
        private final String where; private boolean on; private int ch=1;
        TV(String where){ this.where=where; }
        void on(){ on=true; System.out.println("📺 "+where+": ON (ch "+ch+")"); }
        void off(){ on=false; System.out.println("📺 "+where+": OFF"); }
        void setChannel(int c){ int p=ch; ch=Math.max(1,c); System.out.println("📺 "+where+": ch "+p+" -> "+ch); }
        boolean isOn(){ return on; } int getChannel(){ return ch; }
    }
    static class AirConditioner {
        private final String where; private boolean on; private int temp=24;
        AirConditioner(String where){ this.where=where; }
        void on(){ on=true; System.out.println("❄️ "+where+": ON (t "+temp+"°C)"); }
        void off(){ on=false; System.out.println("❄️ "+where+": OFF"); }
        void setTemp(int t){ int p=temp; temp=Math.max(16,Math.min(30,t)); System.out.println("❄️ "+where+": t "+p+" -> "+temp+"°C"); }
        boolean isOn(){ return on; } int getTemp(){ return temp; }
    }


    static class LightOn implements ICommand {
        private final Light l; private boolean prev;
        LightOn(Light l){ this.l=l; }
        public void execute(){ prev=l.isOn(); l.on(); }
        public void undo(){ if(prev) l.on(); else l.off(); }
    }
    static class LightOff implements ICommand {
        private final Light l; private boolean prev;
        LightOff(Light l){ this.l=l; }
        public void execute(){ prev=l.isOn(); l.off(); }
        public void undo(){ if(prev) l.on(); else l.off(); }
    }

    static class TVOn implements ICommand {
        private final TV tv; private boolean prev;
        TVOn(TV tv){ this.tv=tv; }
        public void execute(){ prev=tv.isOn(); tv.on(); }
        public void undo(){ if(prev) tv.on(); else tv.off(); }
    }
    static class TVOff implements ICommand {
        private final TV tv; private boolean prev;
        TVOff(TV tv){ this.tv=tv; }
        public void execute(){ prev=tv.isOn(); tv.off(); }
        public void undo(){ if(prev) tv.on(); else tv.off(); }
    }
    static class TVSetChannel implements ICommand {
        private final TV tv; private final int nextCh;
        private boolean prevOn; private int prevCh;
        TVSetChannel(TV tv,int nextCh){ this.tv=tv; this.nextCh=nextCh; }
        public void execute(){
            prevOn=tv.isOn(); prevCh=tv.getChannel();
            if(!tv.isOn()) tv.on();
            tv.setChannel(nextCh);
        }
        public void undo(){
            tv.setChannel(prevCh);
            if(!prevOn) tv.off(); else tv.on();
        }
        public String name(){ return "TVSetChannel("+nextCh+")"; }
    }

    static class ACOn implements ICommand {
        private final AirConditioner ac; private boolean prev;
        ACOn(AirConditioner ac){ this.ac=ac; }
        public void execute(){ prev=ac.isOn(); ac.on(); }
        public void undo(){ if(prev) ac.on(); else ac.off(); }
    }
    static class ACOff implements ICommand {
        private final AirConditioner ac; private boolean prev;
        ACOff(AirConditioner ac){ this.ac=ac; }
        public void execute(){ prev=ac.isOn(); ac.off(); }
        public void undo(){ if(prev) ac.on(); else ac.off(); }
    }
    static class ACSetTemp implements ICommand {
        private final AirConditioner ac; private final int nextT;
        private boolean prevOn; private int prevT;
        ACSetTemp(AirConditioner ac,int nextT){ this.ac=ac; this.nextT=nextT; }
        public void execute(){
            prevOn=ac.isOn(); prevT=ac.getTemp();
            if(!ac.isOn()) ac.on();
            ac.setTemp(nextT);
        }
        public void undo(){
            ac.setTemp(prevT);
            if(!prevOn) ac.off(); else ac.on();
        }
        public String name(){ return "ACSetTemp("+nextT+")"; }
    }

    static class MacroCommand implements ICommand {
        private final List<ICommand> cmds;
        MacroCommand(List<ICommand> cmds){ this.cmds=new ArrayList<>(cmds); }
        public void execute(){ for(ICommand c:cmds) c.execute(); }
        public void undo(){ for(int i=cmds.size()-1;i>=0;i--) cmds.get(i).undo(); }
        public String name(){ return "Macro("+cmds.size()+")"; }
    }


    static class RemoteControl {
        private final ICommand[] on, off;
        private final Deque<ICommand> undo = new ArrayDeque<>();
        private final Deque<ICommand> redo = new ArrayDeque<>();
        private boolean rec=false; private final List<ICommand> tape = new ArrayList<>();

        RemoteControl(int slots){
            on=new ICommand[slots]; off=new ICommand[slots];
            ICommand no=new NoCommand();
            Arrays.fill(on,no); Arrays.fill(off,no);
        }

        void set(int slot, ICommand onCmd, ICommand offCmd){
            check(slot);
            on[slot]= (onCmd==null)? new NoCommand(): onCmd;
            off[slot]=(offCmd==null)? new NoCommand(): offCmd;
        }

        void pressOn(int slot){ run(get(on,slot)); }
        void pressOff(int slot){ run(get(off,slot)); }

        void pressUndo(){
            if(undo.isEmpty()){ System.out.println("↩️ Undo: nothing"); return; }
            ICommand c=undo.pop(); c.undo(); redo.push(c);
        }
        void pressRedo(){
            if(redo.isEmpty()){ System.out.println("↪️ Redo: nothing"); return; }
            ICommand c=redo.pop(); c.execute(); undo.push(c);
        }

        void startRecording(){ rec=true; tape.clear(); System.out.println("⏺️ Recording start"); }
        ICommand stopRecording(String name){
            rec=false;
            ICommand m=new NamedMacro(name, tape);
            System.out.println("⏹️ Recording stop -> "+m.name());
            return m;
        }

        void printSlots(){
            System.out.println("\n--- Slots ---");
            for(int i=0;i<on.length;i++)
                System.out.printf("S%d: ON=%s | OFF=%s%n", i, on[i].name(), off[i].name());
            System.out.println("------------\n");
        }

        private void run(ICommand c){
            c.execute();
            if(!(c instanceof NoCommand)){ undo.push(c); redo.clear(); }
            if(rec && !(c instanceof NoCommand)) tape.add(c);
        }
        private ICommand get(ICommand[] arr,int slot){ check(slot); return arr[slot]; }
        private void check(int slot){
            if(slot<0||slot>=on.length) throw new IllegalArgumentException("Bad slot "+slot);
        }

        static class NamedMacro extends MacroCommand {
            private final String n;
            NamedMacro(String n, List<ICommand> cmds){ super(cmds); this.n=n; }
            public String name(){ return "Macro["+n+"]"; }
        }
    }


    public static void main(String[] args) {
        Light light = new Light("Living");
        TV tv = new TV("Living");
        AirConditioner ac = new AirConditioner("Bedroom");

        RemoteControl rc = new RemoteControl(4);

        rc.set(0, new LightOn(light), new LightOff(light));
        rc.set(1, new TVOn(tv), new TVOff(tv));
        rc.set(2, new ACOn(ac), new ACOff(ac));
        rc.printSlots();

        System.out.println("== Single + undo/redo ==");
        rc.pressOn(0);
        rc.pressOff(0);
        rc.pressUndo();
        rc.pressRedo();

        System.out.println("\n== Empty slot ==");
        rc.pressOn(3);

        System.out.println("\n== Macro ==");
        ICommand leaving = new MacroCommand(List.of(
                new LightOff(light),
                new TVOff(tv),
                new ACOff(ac)
        ));
        leaving.execute();
        leaving.undo();

        System.out.println("\n== Record macro ==");
        rc.startRecording();
        rc.pressOn(0);
        rc.pressOn(1);
        new TVSetChannel(tv, 7).execute();
        rc.pressOn(2);
        new ACSetTemp(ac, 20).execute();
        ICommand movieNight = rc.stopRecording("MovieNight");

        rc.set(3, movieNight, null);
        rc.printSlots();
        rc.pressOn(3);
        rc.pressUndo();
    }
}
