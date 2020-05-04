package sudoku;

public class GameObject {
    
    private int x, y, value, location, testValue;
    //tells whether the cell is a pre-given number or not
    private Boolean given = false;
    //holds x, y values of gameObjects 3x3 square
    private int[] quadrant = new int[2];
    
    
    public GameObject(int x, int y, int value, boolean given, int location){
        this.x = x;
        this.y = y;
        this.value = value;
        this.given = given;
        this.location = location;
        this.findQuadrant();
        
    }
    
    //sets subsquare (3x3 square) coordinates
    public void findQuadrant(){
        int tempX, tempY;
        tempX = (x)/3;
        tempY = (y-1)/3; 
        
        quadrant[0] = tempX;
        quadrant[1] = tempY;
    }
    
    public String testValueToString(){
       return "" + testValue;
    }
    
    public void setTestValue(int value){
        this.testValue = value;
    }
    public int getTestValue(){
        return testValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Boolean getGiven() {
        return given;
    }

    public int getQuadrantX() {
        return quadrant[0];
    }
    public int getQuadrantY() {
        return quadrant[1];
    }

    public void setQuadrant(int[] quadrant) {
        this.quadrant = quadrant;
    }

    public int getLocation() {
        return location;
    }
    
    
    public void incrementer(){
        value++;
    }
    
    @Override
    public String toString(){
      return "" + value;  
    }
}
