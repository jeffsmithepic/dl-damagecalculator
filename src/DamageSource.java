public class DamageSource {

    private int str = 0;
    private int coabStr = 0;
    private int buffStr = 0; //note buff strength capped at 200
    private int passiveStr = 0;
    private int ampStr = 0;

    private int coabSd = 0;
    private int buffSd = 0;
    private int passiveSd = 0; //note energize is 50 passive sd

    private int skillMod = 0;
    private boolean isSkillShare = false;
    private boolean isXanderArchetype = false;
    private boolean isCrisis = false;
    private double crisisModifier = 1.0;
    private int buffCount = 0;

    private boolean isDragonSkill = false;
    private static final int BASE_DRAGON_DAMAGE = 70;
    private int dragonDamage = 0;

    private static final int BASE_CRIT_DAMAGE = 70;
    private int coabCd = 0;
    private int buffCd = 0; //note crit dmg capped at 400
    private int passiveCd = 0;
    private int critRate = 2;

    private int punisherSum = 0;

    private int def = 10;
    private int defDown = 0;

    private static final int ON_ELEMENT_BONUS = 50;
    private int elementDamage = 0;

    private double miscModifier = 1.0;

    private int strDoublebuff = 0;
    private int critDmgDoublebuff = 0;
    private boolean regenDoublebuff = false;
    private boolean energyDoublebuff = false;

    String title;

    int energyStacks = 0;

    private enum Buff{PATIA, YUYA, SCLEO_SELF, SCLEO_ALLY, DEF, DYXAIN};

    public DamageSource(){}

    public DamageSource applyTitle(String title){
        this.title = title;
        return this;
    }

    public DamageSource applyStr(int base, int coab, int buff, int passive){
        this.str = base;
        this.coabStr = coab;
        this.buffStr = buff;
        this.passiveStr = passive;
        return this;
    }

    public DamageSource applySkd(int coab, int buff, int passive){
        this.coabSd = coab;
        this.buffSd = buff;
        this.passiveSd = passive;
        return this;
    }

    public DamageSource applySkill(int skillMod, boolean isSS, boolean isXander, boolean isCrisis, double crisisMod, int buffCount){
        this.skillMod = skillMod;
        this.isSkillShare = isSS;
        this.isXanderArchetype = isXander;
        this.isCrisis = isCrisis;
        this.crisisModifier = crisisMod;
        this.buffCount = buffCount;
        return this;
    }

    public DamageSource applyDragonDmg(boolean isDragonSkill, int dragonDamage){
        this.isDragonSkill = isDragonSkill;
        this.dragonDamage = dragonDamage;
        return this;
    }

    public DamageSource applyCritDmg(int coab, int buff, int passive) {
        this.coabCd = coab;
        this.buffCd = buff;
        this.passiveCd = passive;
        return this;
    }

    public DamageSource applyCritRate(int passive){
        this.critRate += passive;
        return this;
    }

    public DamageSource applyPunisher(int value){
        this.punisherSum = value;
        return this;
    }

    public DamageSource applyDefDown(int value){
        this.defDown = value;
        return this;
    }

    public DamageSource applyElementDmg(int value){
        this.elementDamage = value;
        return this;
    }

    public DamageSource applyDoublebuffs(int str, int cd, boolean regen, boolean energy){
        this.strDoublebuff = str;
        this.critDmgDoublebuff = cd;
        this.regenDoublebuff = regen;
        this.energyDoublebuff = energy;
        return this;
    }

    public DamageSource applyBuff(Buff buff){
        return applyBuff(buff, 1);
    }

    public DamageSource applyBuff(Buff buff, int count){
        boolean outDef = false;
        switch (buff) {
            case PATIA -> {
                buffStr += 15 * count;
                outDef = true;
                buffCount += 2 * count;
            }
            case YUYA -> {
                buffStr += 15 * count;
                outDef = true;
                buffCount += 2 * count + 1;
            }
            case SCLEO_SELF -> {
                buffCd += 10 * count;
                outDef = true;
                applyBuff(Buff.SCLEO_ALLY, count);
                buffCount += 2 * count;
            }
            case SCLEO_ALLY -> {
                buffStr += 5 * count;
                buffSd += 10 * count;
                critRate += 3 * count;
                buffCount += 4 * count;
            }
            case DEF -> {
                outDef = true;
                buffCount += count;
            }
            case DYXAIN -> {
                buffStr += 5 * count;
                buffCount += 2 * count;
            }
        }
        if(outDef){
            if(strDoublebuff > 0){
                int doublebuffCount = 1;
                if(strDoublebuff > 18){
                    doublebuffCount = 2;
                }
                buffStr += strDoublebuff * count;
                buffCount += count * doublebuffCount;
            }
            if(critDmgDoublebuff > 0){
                buffCd += critDmgDoublebuff * count;
                buffCount += count;
            }
            if(regenDoublebuff){
                buffCount += count;
            }
            if(energyDoublebuff){
                if(energyStacks == 0){
                    buffCount++;
                }
                energyStacks += count;
                energyStacks = Math.min(energyStacks, 5);
            }
        }
        return this;
    }

    public DamageSource applyAmpStr(int value){
        this.ampStr = value;
        return this;
    }

    private static String intToString(int in){
        String inputString = Integer.toString(in);
        StringBuilder sb = new StringBuilder();
        int length = inputString.length();
        int count = 0;
        while(count < length){
            sb.insert(0, inputString.charAt(length - 1 - count));
            if(count % 3 == 2 && count+1 < length){
                sb.insert(0, ',');
            }
            count++;
        }
        return sb.toString();

    }

    private static double round(double value){
        return (int)(value * 100.0) / 100.0;
    }

    public void print(){
        int effectiveBuffStr = Math.min(buffStr, 200) + ampStr;
        int effectivePassiveSd = passiveSd + (energyStacks == 5 ? 50 : 0);
        double finalStr = str * (1.0 + 0.01*coabStr) * (1.0 + 0.01*effectiveBuffStr) * (1.0 + 0.01*passiveStr);
        double finalSD = (1.0 + 0.01*coabSd) * (1.0 + 0.01*buffSd) * (1.0 + 0.01*effectivePassiveSd);
        double finalCd = 1.0 + (0.01*BASE_CRIT_DAMAGE) + (0.01*coabCd) + (0.01*buffCd) + (0.01*passiveCd);
        double finalPunisher = 1.0 + (0.01*punisherSum);
        double finalDef = def * (1.0 - 0.01*defDown);
        double finalSDMod = 0.01 * (skillMod * (isSkillShare ? 0.7 : 1.0) * (isXanderArchetype ? (1.0 + 0.05*buffCount) : 1.0) * (isDragonSkill ? (1.0 + 0.01*(BASE_DRAGON_DAMAGE + dragonDamage)) : 1.0) * (isCrisis ? crisisModifier : 1.0));
        double finalElementDamage = 1.0 + ((0.01) * (ON_ELEMENT_BONUS + elementDamage));

        if(title != null){
            System.out.println(title);
        }
        System.out.println("Final Strength: " + round(finalStr) + " (Base: " + str + ", Coab: " + coabStr + ", Buff: " + effectiveBuffStr + ", Passive: " + passiveStr + ")");
        System.out.println("Final Skill Damage: " + round(finalSD) + " (Coab: " + coabSd + ", Buff: " + buffSd + ", Passive: " + effectivePassiveSd + ")");
        System.out.println("Final Crit Damage: " + round(finalCd) + " (Base: 70, Coab: " + coabCd + ", Buff: " + buffCd + ", Passive: " + passiveCd + ", Crit Rate: " + critRate + "%)");
        System.out.println("Final Punisher: " + finalPunisher);
        System.out.println("Final Def: " + finalDef);
        System.out.print("Final Skill Modifier: " + round(finalSDMod));
        if(isSkillShare){
            System.out.print(" [Skill Share Modifier: 0.7]");
        }
        if(isXanderArchetype){
            System.out.print(" [Base Modifier: " + skillMod + "%] [Buff Count: " + buffCount + "]");
        }
        if(isCrisis){
            System.out.print(" [Crisis Modifier: " + crisisModifier + "]");
        }
        System.out.println();
        System.out.println("Final Element Damage: " + finalElementDamage);

        double result = (miscModifier * (5.0/3.0) * (1.0 * finalStr * finalSDMod * finalSD * finalCd * finalPunisher * finalElementDamage) / finalDef);
        System.out.println("Expected Damage: " + intToString((int)result) + ", No Crit: " + intToString((int)(result / finalCd)));
        System.out.println("Damage Range: " + intToString((int)(result*0.95)) + " - " + intToString((int)(result*1.05)));
        System.out.println();
    }

    public static void main(String[] args){
        new DamageSource()
                .applyTitle("Hawk")
                .applyStr(5325, 10, 0, 70)
                .applySkd(15, 0, 40)
                .applySkill(1667, true, true, false, 1.0, 3)
                .applyCritRate(14)
                .applyElementDmg(30+20)
                .applyDoublebuffs(13, 15, false, true)
                .applyBuff(Buff.PATIA, 3 + 2)
                .applyBuff(Buff.SCLEO_ALLY, 1 + 1)
                .applyBuff(Buff.DYXAIN, 1)
                .applyAmpStr(20)
                .print();
    }























}
