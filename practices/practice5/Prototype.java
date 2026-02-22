import java.util.ArrayList;
import java.util.List;

public class PrototypeDemo {

    static class Weapon implements Cloneable {
        private String name;
        private int damage;

        public Weapon(String name, int damage) {
            this.name = name;
            this.damage = damage;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getDamage() { return damage; }
        public void setDamage(int damage) { this.damage = damage; }

        @Override
        protected Weapon clone() {
            try {
                return (Weapon) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return "Weapon{name='" + name + "', damage=" + damage + "}";
        }
    }

    static class Armor implements Cloneable {
        private String name;
        private int defense;

        public Armor(String name, int defense) {
            this.name = name;
            this.defense = defense;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getDefense() { return defense; }
        public void setDefense(int defense) { this.defense = defense; }

        @Override
        protected Armor clone() {
            try {
                return (Armor) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return "Armor{name='" + name + "', defense=" + defense + "}";
        }
    }

    static class Skill implements Cloneable {
        private String name;
        private int power;

        public Skill(String name, int power) {
            this.name = name;
            this.power = power;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPower() { return power; }
        public void setPower(int power) { this.power = power; }

        @Override
        protected Skill clone() {
            try {
                return (Skill) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return "Skill{name='" + name + "', power=" + power + "}";
        }
    }

    static class Character implements Cloneable {
        private String name;
        private int health, strength, agility, intelligence;
        private Weapon weapon;
        private Armor armor;
        private List<Skill> skills;

        public Character(String name, int health, int strength, int agility, int intelligence) {
            this.name = name;
            this.health = health;
            this.strength = strength;
            this.agility = agility;
            this.intelligence = intelligence;
            this.skills = new ArrayList<>();
        }

        public void setWeapon(Weapon weapon) { this.weapon = weapon; }
        public void setArmor(Armor armor) { this.armor = armor; }
        public void addSkill(Skill skill) { skills.add(skill); }
        public List<Skill> getSkills() { return skills; }

        @Override
        protected Character clone() {
            try {
                Character cloned = (Character) super.clone();
                if (this.weapon != null) cloned.weapon = this.weapon.clone();
                if (this.armor != null) cloned.armor = this.armor.clone();
                cloned.skills = new ArrayList<>();
                for (Skill s : this.skills) cloned.skills.add(s.clone());
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Cloning failed", e);
            }
        }

        @Override
        public String toString() {
            return String.format("Character{name='%s', health=%d, str=%d, agi=%d, int=%d, weapon=%s, armor=%s, skills=%s}",
                    name, health, strength, agility, intelligence, weapon, armor, skills);
        }
    }

    public static void main(String[] args) {
        Character original = new Character("Warrior", 100, 20, 15, 10);
        original.setWeapon(new Weapon("Sword", 25));
        original.setArmor(new Armor("Plate", 30));
        original.addSkill(new Skill("Slash", 15));
        original.addSkill(new Skill("Block", 5));

        System.out.println("Original: " + original);

        Character clone = original.clone();
        System.out.println("Clone:    " + clone);

        clone.setWeapon(new Weapon("Axe", 30));
        clone.getSkills().get(0).setPower(20);

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + original);
        System.out.println("Clone:    " + clone);
    }
}
