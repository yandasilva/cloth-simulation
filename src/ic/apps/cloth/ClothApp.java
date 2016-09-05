package ic.apps.cloth;

//My imports
import ic.mlibs.util.Application;

public class ClothApp extends ClothAppSettings implements Application{
    
    //Cloth object
    private Cloth cloth;
    //Path of the ClothSettings.settings file
    private final String path;
    
    //Max simulation steps
    private final double  maxSteps = 1000;
    //Integration time step
    private final double  timeStep = 0.001;
    //Steps simulated so far
    private int     steps    = 0;
    //Is the simulation running?
    private boolean play     = false;
   
    //Constructor
    public ClothApp (String confFile){
        
        super(confFile);
        path = confFile;
        cloth = new Cloth(path);
    
    }
    
    //Creates the application
    @Override
    public void create() throws Exception {
        try{
            cloth.readChe(getMfile());
            cloth.initMassesInfo();
        } catch (Exception e){
            //Destroys the application
            destroy();
            //Log
            System.out.println("Error on ExApp::create");
        }
    }

    //Destroys the application
    @Override
    public void destroy() {
        //Destroys the mesh
        cloth  = null;
        //Clears settings
        clearSettings();
    }

    //Run the application
    @Override
    public void run() {
        play = !play;
    }

    //Render the scene
    @Override
    public void render() {
        if(cloth.isInit()){
            if ( play && steps < maxSteps ){
                cloth.updatePositions(steps*timeStep, timeStep);
                steps++;
            }
            cloth.render(getCmap(), getRmode(), getCmapMin(), getCmapMax());            
        }
    }

    @Override
    public void reset() {
        //Reloads settings
        loadSettings();
        //Allocates cloth created with reloaded settings
        cloth = new Cloth(path);
    }

    @Override
    public void reload() {
        //Reloads settings
        loadSettings();
    }
    
}
