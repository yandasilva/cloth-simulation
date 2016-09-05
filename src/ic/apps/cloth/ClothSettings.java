package ic.apps.cloth;

//Imports
import ic.mlibs.linalg.DVector;
import ic.mlibs.util.Settings;

//Java
import java.util.ArrayList;
import java.util.List;

public class ClothSettings extends Settings{
    
    //Auxiliary constants for indexing
    final static int X = 0;
    final static int Y = 1;
    final static int Z = 2;
    
    //Elastic constant
    private double k;
    //Mass
    private double m;
    //Damping constant
    private double d;
    //Gravity acceleration
    private double g;
    
    //List of fixed vertices
    private List<Integer> anchors;
    //List of external forces
    private List<DVector> forces;
    
    //Constructior
    public ClothSettings(String arq){
        super(arq);
    }

    //Getters & setters
    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public List<Integer> getAnchors() {
        return anchors;
    }

    public void setAnchors(List<Integer> anchors) {
        this.anchors = anchors;
    }

    public List<DVector> getForces() {
        return forces;
    }

    public void setForces(List<DVector> forces) {
        this.forces = forces;
    }

    //Creates anchors list from a string read from the .settings file
    public List<Integer> createAnchorsList(String p){
        String property = p;
        List<Integer> result = new ArrayList<Integer>();
        if (!property.equalsIgnoreCase("NONE")){
            property = property.replace(" ", "");
            String[] anchorsArray = property.split(",");
            for (String anchorsArray1 : anchorsArray) {
                result.add(Integer.parseInt(anchorsArray1.trim()));
            }
        }
        return result;
    }
    
    //Creates forces list from a string read from the .settings file
    public List<DVector> createForcesList(String p){
        String property = p;
        List<DVector> result = new ArrayList<DVector>();
        if (!property.equalsIgnoreCase("NONE")){
            DVector force = new DVector(0.0, 0.0, 0.0);
            property = property.replace(" ", "");
            property = property.replace("(", "");
            property = property.replace(")", "");
            String[] forcesArray = property.split(",");
            if (forcesArray.length % 3 != 0){
                throw new IllegalArgumentException("One of the vectors is missing components");
            } else {
                for (int i = 0; i < forcesArray.length; i++){
                    switch (i%3){
                        case X:
                            force.setVecData(Double.parseDouble(forcesArray[i]), X);
                            break;
                        case Y:
                            force.setVecData(Double.parseDouble(forcesArray[i]), Y);
                            break;
                        case Z:
                            force.setVecData(Double.parseDouble(forcesArray[i]), Z);
                            result.add(force);
                            break;
                    }
                }
            }
        }
        return result;
    }
    
    //Creates a string from a generic list
    public String createStringFromList (List<?> list){
        String result = "";
        if (list.isEmpty()){
            result = "NONE";
        } else {
            for (int i = 0; i < list.size(); i++){
                result = result.concat(list.get(i).toString());
                if (i != list.size() - 1){
                    result = result.concat(", ");
                }
            }
        }
        return result;
    }
    
    //Overrides
    //Loads settings from file
    @Override
    protected void loadStrings() {

        k       = Double.parseDouble(settings.getProperty( "k" ));
        m       = Double.parseDouble(settings.getProperty( "m" ));
        d       = Double.parseDouble(settings.getProperty( "d" ));
        g       = Double.parseDouble(settings.getProperty( "g" ));
        anchors = createAnchorsList( settings.getProperty( "anchors" ));
        forces  = createForcesList( settings.getProperty( "forces"   ));
        
    }

    //Saves settings to file
    @Override
    protected void saveStrings() {
        
        settings.setProperty( "k", Double.toString( k ));
        settings.setProperty( "m", Double.toString( m ));
        settings.setProperty( "d", Double.toString( d ));
        settings.setProperty( "d", Double.toString( d ));
        settings.setProperty( "anchors", createStringFromList( anchors ));
        settings.setProperty( "forces",  createStringFromList( forces  ));
    }
    
}
