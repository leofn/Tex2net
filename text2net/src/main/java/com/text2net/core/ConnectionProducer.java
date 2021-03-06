package com.text2net.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.text2net.core.api.Connection;
import com.text2net.core.api.ConnectionElement;
import com.text2net.core.api.Entidade;

import gate.AnnotationSet;
import gate.Document;
import gate.SimpleFeatureMap;
import gate.util.InvalidOffsetException;

public class ConnectionProducer {

	
	public List<Connection> process(Document annotatedText) {
		
		List<Connection> result = new ArrayList<Connection>();
		
		
		// Listas de anotacoes detectadas pelo gate ja na ordem em que aparecem no documento
		AnnotationSet todasAnnots = annotatedText.getAnnotations();
		List<gate.Annotation> inicioList = gate.Utils.inDocumentOrder(todasAnnots.get("Inicio"));
		List<gate.Annotation> entidadeList = gate.Utils.inDocumentOrder(todasAnnots.get("EntidadeIdentificada"));
		
		for (int i = 0; i < inicioList.size(); i++)
		{
			gate.Annotation annIni = inicioList.get(i);
			long inicioPortaria = annIni.getStartNode().getOffset();

			SimpleFeatureMap fMap = annIni.getFeatures();

			long fimPortaria;
			if (i == inicioList.size() - 1) { // se estivermos no ultimo inicio, o fim eh o fim e nao a proximo inicio
				fimPortaria = annotatedText.getContent().size().longValue();
			} else	{
				fimPortaria = inicioList.get(i + 1).getStartNode().getOffset();
			}

			// se chegamos aqui temos uma portaria identificada. vamos
			// anotar as entidades nela presentes.
			//List<gate.Annotation> entidadesEncontradasGate = new ArrayList<gate.Annotation>();

			HashMap<String, ConnectionElement> entidadesEncontradasNome = new HashMap<String, ConnectionElement>();

			


			for (int j = 0; j < entidadeList.size(); j++)
			{
				System.out.print('.');

				gate.Annotation annEnt = entidadeList.get(j);

				long inicioEntidade = annEnt.getStartNode().getOffset();

				if (inicioEntidade >= inicioPortaria)
				{
					if (inicioEntidade < fimPortaria) // Se esta entre o inicio e o final, pertence a portaria
					{
						long fimEntidade = annEnt.getEndNode().getOffset();

						
						String entidadeText;
						try {
								entidadeText = annotatedText.getContent().getContent(inicioEntidade, fimEntidade).toString();
						} catch (InvalidOffsetException e) {
							e.printStackTrace();
							continue;
						}
						
						ConnectionElement entidade = new ConnectionElement(entidadeText, inicioEntidade, fimEntidade);

						//if (entidade.tipoEntidade.equals("Orgao"))
						//	entidadesEncontradasOrgao.put(entidade.entidade, entidade); // entidadesEncontradasOrgao.add(entidade);
					//	else
						entidadesEncontradasNome.put(entidade.getName(), entidade);// .add(entidade);

						// adiciona para remover da lista depois. se achou nessa portaria, nao estara em nenhuma outra
						//entidadesEncontradasGate.add(annEnt);

					}// TODO se falhar aqui ja pode sair fora. a lista esta
						// ordenada.
					else
					{
						// log.warn("saind pois inicioEntidade (" + inicioEntidade + ") > fimportaria (" +
						// fimPortaria
						// + ")");
						break;
					}
				}
				/*
				 * else { log.warn("saind pois inicioEntidade (" + inicioEntidade + ") < inicioportaria (" + fimPortaria + ")"
				 * ); break; }
				 */
			}
			
		    Iterator it = entidadesEncontradasNome.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		       
		        for (Entry<String, ConnectionElement> entry : entidadesEncontradasNome.entrySet())
		        {
		        	ConnectionElement elementA = (ConnectionElement) pair.getValue();
		        	ConnectionElement elementB = (ConnectionElement) entry.getValue();
		        	if (!elementA.getName().equals(elementB.getName()))
		        			result.add(new Connection(elementA, elementB,  inicioPortaria));
		        }
		        
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		}
		System.out.println("####### FIM do connection producer ##########");
		return result;
		
	}
}
