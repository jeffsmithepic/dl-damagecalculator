public class BuffCount {

    String name;
    int count;
    int sum;

    public BuffCount(String name, int count, int value){
        this.name = name;
        this.count = count;
        this.sum = value;
    }

    public String getName(){ return name; }
    public int getCount(){ return count; }
    public int getSum(){ return sum; }

    public void append(String name, int count, int value){
        int max = 0;
        switch(name){
            case "def":
                max = 10;
            case "str":
            case "regen":
                max = 20;
        }
        appendWithMax(count, value, max);
    }

    public void appendWithMax(int count, int value, int max){
        if(count >= this.count){
            return;
        }
        if(count + this.count > max){
            System.out.println("reaching buff count");
            int diff = max - this.count;
            this.count += diff;
            this.sum += value * diff;
            return;
        }
        this.count += count;
        this.sum += count * value;
    }

}
