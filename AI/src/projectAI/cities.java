package projectAI;
/*
this class include (id , name ,x, y , cost )
//id is consecutive number
 */

public class cities implements Comparable<cities> {
    private  int id;
    private double X_POINT;
    private double Y_POINT;
    private double cost;
    private double R_COST;
    private double Hofn;
    private double final_c;
    cities predecessor;

    private String Name;
    private String Name_A;

    public cities(int id, String Name, double X_POINT, double Y_POINT, String Name_A) {
        this.id=id;
        this.Name = Name;
        this.X_POINT = X_POINT;
        this.Y_POINT = Y_POINT;
        this.Name_A = Name_A;
        this.cost=Double.POSITIVE_INFINITY;
        predecessor=null;
    }
    public cities() {

    }


    public String getName_A() {
        return Name_A;
    }
    public String getName() {
        return this.Name;
    }
    public double getX_POINT() {
        return this.X_POINT;
    }
    public double getY_POINT() {
        return this.Y_POINT;
    }
    public int getId() {
        return id;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getR_COST() {
        return R_COST;
    }

    public void setR_COST(double r_COST) {
        this.R_COST = r_COST;
    }

    public double getHofn() {
        return Hofn;
    }

    public void setHofn(double hofn) {
        this.Hofn = hofn;
    }

    public double getFinal_c() {
        return final_c;
    }

    public void setFinal_c(double final_c) {
        this.final_c = final_c;
    }

    @Override
    public int compareTo(cities cities) {

        return Double.compare(this.final_c, cities.final_c);

    }

}