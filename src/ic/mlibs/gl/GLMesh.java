/**
 * Processamento de malhas poligonais
 * 
 * Pós Graduação IC-UFF 2012.1
 * Classe GLMesh
 * 
 * @author Prof. Marcos Lage <mlage@ic.uff.br>
 */
package ic.mlibs.gl;

//Meus imports
import ic.mlibs.structures.Che;
import ic.mlibs.structures.Point3D;
import ic.mlibs.util.ColorMap;

//LWJGL estático
import static org.lwjgl.opengl.GL11.*;

//Java
import java.util.Iterator;

public class GLMesh extends Che implements GLObject {

    protected ColorMap map = new ColorMap();
    
    @Override
    public void render(String cmap, String rmode, double pmin, double pmax) {
        if(getNvert() == 0) return;
        
        // escolhe o mapa de cores
        map.setColorMap(cmap);
        // seta os valores mínimo e máximo para o mapa
        map.setMapExtrems(pmin, pmax);

        // escolhe o que exibir
        if (rmode.contains( "Points"  )) {
            drawVerts();

        } 
        if (rmode.contains( "Smooth"  )) {
            drawSmooth();

        } 
        if (rmode.contains("Wireframe")) {
            drawWire();
        }
        
        if (rmode.contains("Components")) {
            drawComps();
        }
        
        if( rmode.contains("ColorMap") ){
            // altera o max e min
            map.setMapExtrems(0, 255);
            // desenha a palheta de cores
            map.drawMap(-1.25f,-1.25f,0.1f,2.5f);
        }
    }

    //---
    
    protected void drawVerts() {

        glBegin(GL_POINTS);
        {
            for (int i = 0; i < getNvert(); i++) {
                Point3D v = getG(i);

                // cor do vértice
                map.setGLColor((Double) v.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v.getNrmX(), v.getNrmY(), v.getNrmZ());
                // posição do vértice
                glVertex3d(v.getPosX(), v.getPosY(), v.getPosZ());
            }
        }
        glEnd();
    }

    protected void drawSmooth() {
        
        glBegin(GL_TRIANGLES);
        {
            for (int i = 0; i < getNtrig(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v0.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                // posição do vértice
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v1.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                // posição do vértice
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v2.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                // posição do vértice
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
            }
        }
        glEnd();
        
        glBegin(GL_QUADS);
        {
            for (int i = getNtrig(); i < getNface(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v0.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                // posição do vértice
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v1.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                // posição do vértice
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v2.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                // posição do vértice
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
                
                Point3D v3 = getG(getV(he++));

                // cor do vértice
                map.setGLColor((Double) v3.getObj(), (byte) 255, true);
                // normal do vértice
                glNormal3d(v3.getNrmX(), v3.getNrmY(), v3.getNrmZ());
                // posição do vértice
                glVertex3d(v3.getPosX(), v3.getPosY(), v3.getPosZ());
            }
        }
        glEnd();
    }
    
    protected void drawWire() {

        glLineWidth(1.0f);
        glBegin(GL_LINES);
        {        
            Iterator<Integer> i = EH.keySet().iterator();
            
            while(i.hasNext())
            {
              int he = i.next();
                           
              Point3D v1 = getG( getV(    he    ));
              Point3D v2 = getG( getV( next(he) ));

              // cor do vértice
              map.setGLColor((Double) v1.getObj(), (byte) 255, (Integer)EH.get(he) != -1 );
              glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
              glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ() );

              // cor do vértice
              map.setGLColor((Double) v2.getObj(), (byte) 255, (Integer)EH.get(he) != -1);
              glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
              glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ() );
            }
        }
        glEnd();       
    }

    protected void drawComps() {
        
        glBegin(GL_TRIANGLES);
        {
            for (int i = 0; i < getNtrig(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                // posição do vértice
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                // posição do vértice
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                // posição do vértice
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
            }
        }
        glEnd();
        
        glBegin(GL_QUADS);
        {
            for (int i = getNtrig(); i < getNface(); i++) {
                int he = base(i);
                Point3D v0 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v0.getNrmX(), v0.getNrmY(), v0.getNrmZ());
                // posição do vértice
                glVertex3d(v0.getPosX(), v0.getPosY(), v0.getPosZ());

                Point3D v1 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v1.getNrmX(), v1.getNrmY(), v1.getNrmZ());
                // posição do vértice
                glVertex3d(v1.getPosX(), v1.getPosY(), v1.getPosZ());
                
                Point3D v2 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v2.getNrmX(), v2.getNrmY(), v2.getNrmZ());
                // posição do vértice
                glVertex3d(v2.getPosX(), v2.getPosY(), v2.getPosZ());
                
                Point3D v3 = getG(getV(he));

                // cor do vértice
                map.setGLColor(getC(getV(he++)), (byte) 255, true);
                // normal do vértice
                glNormal3d(v3.getNrmX(), v3.getNrmY(), v3.getNrmZ());
                // posição do vértice
                glVertex3d(v3.getPosX(), v3.getPosY(), v3.getPosZ());
            }
        }
        glEnd();
      }
}
