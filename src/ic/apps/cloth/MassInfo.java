package ic.apps.cloth;

//Import
import ic.mlibs.linalg.DVector;

//Java
import java.util.ArrayList;
import java.util.List;

public class MassInfo {
    
    //Mass velocity
    private double velocity;
    //Mass normal vector
    private DVector normal;
    //Mass' direct neighbors
    private List<Integer> directNeighbours;
    //Mass' transversal neighbors
    private List<Integer> transversalNeighbours;
    
    //Constructor
    public MassInfo(){
        
        this.velocity = 0.0d;
        this.normal = null;
        this.directNeighbours = new ArrayList<Integer>();
        this.transversalNeighbours = new ArrayList<Integer>();
        
    }

    //Getters & setters
    public List<Integer> getTransversalNeighbours() {
        return transversalNeighbours;
    }

    public void setTransversalNeighbours(List<Integer> transversalNeighbours) {
        this.transversalNeighbours = transversalNeighbours;
    }

    public List<Integer> getDirectNeighbours() {
        return directNeighbours;
    }

    public void setDirectNeighbours(List<Integer> neighbours) {
        this.directNeighbours = neighbours;
    }
    
    public double getVelocity(){
        return this.velocity;
    }
    
    public DVector getNormal(){
        return this.normal;
    }
    
    public void setVelocity(double v){
        this.velocity = v;
    }
    
    public void setNormal(DVector n){
        this.normal = n;
    }
    
}
