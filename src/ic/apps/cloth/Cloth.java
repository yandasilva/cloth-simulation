package ic.apps.cloth;

//My imports
import ic.mlibs.gl.GLMesh;
import ic.mlibs.linalg.DVector;
import ic.mlibs.structures.Point3D;

//Java
import java.util.List;

//Static LWJGL
import static org.lwjgl.opengl.GL11.*;

//Flanagan (math library)
import flanagan.integration.RungeKutta;

public class Cloth extends GLMesh{

    //Gravity constant
    final static double GRAV_CONSTANT = 9.789;
    //Damping constant
    final static double DAMPING_FACTOR = 0.2;
    
    //Auxiliary constants for indexing
    final static int X = 0;
    final static int Y = 1;
    final static int Z = 2;
   
    //Has the cloth started updating?
    private boolean init = false;
    
    //The last registered time
    private double lastTime;
    
    //Cloth settings object
    private final ClothSettings settings;
    
    //Constructor
    public Cloth(String path) {
        
        //Creates a path to the cloth's settings file
        String newPath = path;
        int slashIndex = newPath.lastIndexOf("/");
        newPath = newPath.substring(0, slashIndex + 1);
        newPath = newPath.concat("ClothSettings.settings");
        
        //Creates and sets the settings object
        this.settings = new ClothSettings(newPath);
        
        //Converts time in nanoseconds to seconds
        this.lastTime = (double) System.nanoTime() / 1000000000.0f;
        
        //Creates forces vector from gravity constant
        settings.getForces().add(new DVector(0.0f, GRAV_CONSTANT*settings.getM(), 0.0f));

    }

    //Getters & setters
    public boolean isInit() {
        return init;
    }
    
    //Calculates the distance between two masses
    public DVector calculateDistanceVector(int from, int to) {
        Point3D a = getG(from);
        Point3D b = getG(to);
        if ((from < 0) || (to < 0)){
            return null;
        } else {
            return new DVector(a.getPosX() - b.getPosX(), a.getPosY() - b.getPosY(), a.getPosZ() - b.getPosZ());
        }
        
    }

    //Calculates the total forces acting on a mass 
    public DVector calculateExternalForce() {

        DVector result = new DVector(0.0f, 0.0f, 0.0f);

        for (int i = 0; i < settings.getForces().size(); i++) {
            
            result = result.opAdd(settings.getForces().get(i));

        }

        return result;

    }
    
    //Calculates the damping force on a mass
    public DVector calculateDampingForce(int vertex){
        
        MassInfo vertexMassInfo = (MassInfo) getG(vertex).getObj();
        
        DVector normal = vertexMassInfo.getNormal();
        DVector vertexVelocity = new DVector(0.0f, 0.0f, 0.0f);
        DVector result = new DVector(0.0f, 0.0f, 0.0f);
        
        double velocity = vertexMassInfo.getVelocity();
        
        vertexVelocity.setVecData(normal.getVecData(X) * velocity, X);
        vertexVelocity.setVecData(normal.getVecData(Y) * velocity, Y);
        vertexVelocity.setVecData(normal.getVecData(Z) * velocity, Z);
        
        result.setVecData(normal.getVecData(X) * DAMPING_FACTOR * (-1.0f), X);
        result.setVecData(normal.getVecData(Y) * DAMPING_FACTOR * (-1.0f), Y);
        result.setVecData(normal.getVecData(Z) * DAMPING_FACTOR * (-1.0f), Z);
        
        return result;
        
    }
    
    //Calculates the elastic acting on a spring connecting two masses
    public DVector calculateElasticForce(int from, int to){
        
        DVector distanceVector = calculateDistanceVector(from, to);
        DVector result = new DVector(0.0f, 0.0f, 0.0f);
        
        result.setVecData(distanceVector.getVecData(X) * (-1.0) * settings.getK(), X);
        result.setVecData(distanceVector.getVecData(Y) * (-1.0) * settings.getK(), Y);
        result.setVecData(distanceVector.getVecData(Z) * (-1.0) * settings.getK(), Z);
                
        return result;
        
    }

    //Calculates the total force acting on a mass
    public DVector calculateResultantForce(int vertex) {

        MassInfo massInfo = (MassInfo) getG(vertex).getObj();
        
        DVector result = calculateDampingForce(vertex);
        DVector elasticForce;
        
        int neighbour;
        SpringInfo info;

        for (int i = 0; i < massInfo.getDirectNeighbours().size(); i++) {

            neighbour = massInfo.getDirectNeighbours().get(i);
            info = (SpringInfo) getEH(neighbour);
            
            if (info.isMustUpdate()){
                elasticForce = calculateElasticForce(getV(neighbour), vertex);
                info.setMustUpdate(false);
                info.setForce(elasticForce);
            } else {
                elasticForce = info.getForce().opScale(-1.0f);
                info.setMustUpdate(true);
            }
            
            result = result.opAdd(elasticForce);
            
        }
        
        for (int i = 0; i < massInfo.getTransversalNeighbours().size(); i++) {

            neighbour = massInfo.getTransversalNeighbours().get(i);
            info = (SpringInfo) getEH(neighbour);
            
            if (info.getTransversal() != null) {
                if (info.getTransversal().isMustUpdate()) {
                    elasticForce = calculateDistanceVector(getV(prev(neighbour)), getV(prev(getO(neighbour))));
                    info.getTransversal().setMustUpdate(false);
                    info.getTransversal().setForce(elasticForce);
                } else {
                    elasticForce = info.getTransversal().getForce().opScale(-1.0f);
                    info.getTransversal().setMustUpdate(true);
                }

                result = result.opAdd(elasticForce);
            }
            
        }

        return result;

    }

    //Calculate a mass' acceleration
    public DVector calculateAcceleration(DVector force) {

        DVector result = new DVector(0.0f, 0.0f, 0.0f);
        
        result.setVecData((double) force.getVecData(X) / settings.getM(), X);
        result.setVecData((double) force.getVecData(Y) / settings.getM(), Y);
        result.setVecData((double) force.getVecData(Z) / settings.getM(), Z);
        
        return result;

    }

    //Initializes the masses and springs
    public void initMassesInfo() {

        DVector force = calculateExternalForce();
        
        force.normalize();

        MassInfo massInfo;
        
        for (int i = 0; i < getNvert(); i++) {
            
            massInfo = new MassInfo();

            List<Integer> edges;

            massInfo.setVelocity(0.0);
            massInfo.setNormal(force);

            edges = R_01(i);
            
            massInfo.getDirectNeighbours().addAll(edges);
            
            SpringInfo edge;
            SpringInfo transEdge;
            
            int navEdge = i;
            int next;
            int prev;
            
            int edgeA;
            int edgeB;
            
            for (int k = 0; k < edges.size(); k++){
                
                if (getEH(edges.get(k)).getClass().equals(SpringInfo.class)){
                    edge = (SpringInfo) getEH(edges.get(k));
                } else {
                    edge = new SpringInfo(calculateDistanceVector(i, getV(edges.get(k))));
                }
                        
                setEH(edges.get(k), edge);
                
            }
            
            for (int j = 0; j < R_02(i).size(); j++){
                
                next = next(navEdge);
                prev = prev(navEdge);
                
                if (getEH(next).getClass().equals(SpringInfo.class)){
                    edge = (SpringInfo) getEH(next);
                } else {
                    edge = new SpringInfo(calculateDistanceVector(getV(next), getV(prev)));
                }
                
                edgeA = prev(next);
                edgeB = prev(getO(next));
                
                if ((edgeA >= 0) && (edgeB >= 0)){
                    transEdge = new SpringInfo(calculateDistanceVector(getV(prev(next)), getV(prev(getO(next)))));
                } else {
                    transEdge = null;
                }
                
                edge.setTransversal(transEdge);
                setEH(next, edge);
                
                massInfo.getTransversalNeighbours().add(next);
                                
            }
            
            getG(i).setObj(massInfo);

        }
        init = true;
    }

    //Updates a mass position after a given time delta
    public void updatePositions(double currentTime, double stepSize) {
        
        double nextX;
        double nextY;
        double nextZ;
        double step;
        double module;

        DVector resultant;
        DVector velocity;
        DVector acceleration;
        
        DVector delta = new DVector(0.0f, 0.0f, 0.0f);

        Point3D next;
        Point3D current;
        
        MassInfo massInfo;

        for (int i = 0; i < getNvert(); i++) {

            if (!settings.getAnchors().contains(i)) {

                massInfo = (MassInfo) getG(i).getObj();
                
                resultant = calculateResultantForce(i);
                acceleration = calculateAcceleration(resultant);

                step = (double) stepSize / 10.0f;
                current = getG(i);
                System.out.println ("POSITION = "+current.getPosX() +","+current.getPosY() +","+current.getPosZ());
                velocity = massInfo.getNormal();
                
                velocity.setVecData(velocity.getVecData(X) * massInfo.getVelocity(), X);
                velocity.setVecData(velocity.getVecData(Y) * massInfo.getVelocity(), Y);
                velocity.setVecData(velocity.getVecData(Z) * massInfo.getVelocity(), Z);

                VelocityFunction function = new VelocityFunction();
                
                function.setParameters(lastTime, velocity.getVecData(X), acceleration.getVecData(X));
                nextX = RungeKutta.fourthOrder(function, lastTime, current.getPosX(), currentTime, step);
                
                function.setParameters(lastTime, velocity.getVecData(Y), acceleration.getVecData(Y));
                nextY = RungeKutta.fourthOrder(function, lastTime, current.getPosY(), currentTime, step);
                
                function.setParameters(lastTime, velocity.getVecData(Z), acceleration.getVecData(Z));
                nextZ = RungeKutta.fourthOrder(function, lastTime, current.getPosZ(), currentTime, step);

                delta.setVecData(acceleration.getVecData(X) * stepSize, X);
                delta.setVecData(acceleration.getVecData(Y) * stepSize, Y);
                delta.setVecData(acceleration.getVecData(Z) * stepSize, Z);
                
                velocity = velocity.opAdd(delta);
                module = velocity.normalize();
                
                massInfo.setNormal(velocity);
                massInfo.setVelocity(module);

                next = new Point3D(nextX, nextY, nextZ);
                next.setNrm(velocity);
                next.setObj(massInfo);
                
                setG(next, i);

            }
        }

        lastTime = currentTime;

    }
    
    //Draws the cloth as smoothly colored polygons
    @Override
    public void drawSmooth() {
        
        glBegin(GL_TRIANGLES);
        {
            for (int i = 0; i < getNtrig(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he++));
                MassInfo info = (MassInfo) v0.getObj();

                //Vertex color
                map.setGLColor(info.getVelocity(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                //Vertex position
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he++));
                info = (MassInfo) v1.getObj();

                //Vertex color
                map.setGLColor(info.getVelocity(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                //Vertex position
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he++));
                info = (MassInfo) v2.getObj();

                //Vertex color
                map.setGLColor(info.getVelocity(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                //Vertex position
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
            }
        }
        glEnd();
        
        glBegin(GL_QUADS);
        {
            for (int i = getNtrig(); i < getNface(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he++));

                //Vertex color
                map.setGLColor((Double) v0.getObj(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                //Vertex position
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he++));

                //Vertex color
                map.setGLColor((Double) v1.getObj(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                //Vertex position
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he++));

                //Vertex color
                map.setGLColor((Double) v2.getObj(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                //Vertex position
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
                
                Point3D v3 = getG(getV(he++));

                //Vertex color
                map.setGLColor((Double) v3.getObj(), (byte) 255, true);
                //Vertex normal
                glNormal3d(v3.getNrmX(), v3.getNrmY(), v3.getNrmZ());
                //Vertex position
                glVertex3d(v3.getPosX(), v3.getPosY(), v3.getPosZ());
            }
        }
        glEnd();
    }
    
}