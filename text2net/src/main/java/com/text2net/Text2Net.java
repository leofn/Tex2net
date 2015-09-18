package com.text2net;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.text2net.core.ConnectionProducer;
import com.text2net.core.TextAnnotator;
import com.text2net.core.api.AnnotatedText;
import com.text2net.core.api.Connection;

@Path("text2net")
public class Text2Net {
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Connection> getIt() {
       
		try{
			File gappFile =  new File(this.getClass().getClassLoader().getResource("com/text2net/gate/InicioInicio_v5.xapp").getPath());
			File docFile =  new File(this.getClass().getClassLoader().getResource("com/text2net/douSample/Dou-02012013-1.txt").getPath());

			AnnotatedText annotatedText = new TextAnnotator().processFile(gappFile, docFile);
			
			System.out.println("Finished Annotation. Starting connection detection");
			
			List<Connection> connections = new ConnectionProducer().process(annotatedText);
			
			return connections;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
    	return null;
    }

}