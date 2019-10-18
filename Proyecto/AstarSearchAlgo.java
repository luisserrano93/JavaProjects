package Aestrella;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

class Node{
	public final String nombre;
	public double g_x;
	public double h_x = 0;
	public double f_x = 0;
	public double x;
	public double y;
	public Edge[] adyacencias;
	public Node padre;

	public Node(String val, double x, double y){
		nombre = val;
		this.x = x;
		this.y = y;
	}

	public String toString(){
		return nombre;
	}
}

class Edge{
	public final double coste;
	public final Node objetivo;

	public Edge(Node nodoObjetivo, double costVal){
		objetivo = nodoObjetivo;
		coste = costVal;
	}
}

public class AstarSearchAlgo{
	static Node nodos [];
	//h nos indica la distancia en linea recta del nodo en el que nos encontramos hasta el final del recorrido

	public AstarSearchAlgo(){
		inicializarNodos();
	}

	public static String iniciar(String inicio, String fin){
		inicializarNodos();
		Node nodoinicio = null;
		Node nodofin = null;
		String ret = "Camino no encontrado";

		for(int i =0; i<nodos.length; i++){
			if(nodos[i].nombre.equals(inicio))
				nodoinicio = nodos[i];
			if(nodos[i].nombre.equals(fin))
				nodofin = nodos[i];
		}

		if(nodoinicio != null && nodofin != null){
			calcularH(nodofin);
			astarSearch(nodoinicio,nodofin);
			List<Node> path = printPath(nodofin);
			System.out.println("Camino: " + path);
			ret ="Camino: " + path;
		}
		return ret;
	}

	private static double calcularDistancia(Node a,Node b){
		double x1 = a.x;
		double x2 = b.x;
		double y1 = a.x;
		double y2 = b.y;
		double distancia = (float) Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
		System.out.println(a.nombre + " a " + b.nombre + ": " + distancia);
		return distancia;
	}

	public static void inicializarNodos() {	//definimos las estaciones en el mapa
		
		Node estacion1 = new Node("Schiedam Centrum", 51.921609, 4.409954);
		Node estacion2 = new Node("Marconiplein", 51.913296, 4.432747);
		Node estacion3 = new Node("Delfshaven", 51.910086, 4.445762);
		Node estacion4 = new Node("Coolhaven", 51.909562, 4.458339);
		Node estacion5 = new Node("Dijkzigt", 51.912172, 4.466782);
		Node estacion6 = new Node("Eendrachtsplein", 51.915907, 4.473144);
		Node estacion7 = new Node("Beurs", 51.918288, 4.481292);
		Node estacion8 = new Node("Blaak", 51.920036, 4.489732);
		Node estacion9 = new Node("Oostplein", 51.923289, 4.496433);
		Node estacion10 = new Node("Gerdesiaweg", 51.925973, 4.505870);
		Node estacion11 = new Node("Voorschoterlaan", 51.925143, 4.512617);
		Node estacion12 = new Node("Kralingse Zoom", 51.921505, 4.533970);
		Node estacion13 = new Node("Capelsebrug", 51.920915, 4.556888);
		Node estacion14 = new Node("Schenkel", 51.932604, 4.563324);
		Node estacion15 = new Node("Prinsenlaan", 51.940027, 4.556734);
		Node estacion16 = new Node("Oosterflank", 51.944929, 4.555004);
		Node estacion17 = new Node("Alexander", 51.951962, 4.551764);
		Node estacion18 = new Node("Graskruid", 51.957467, 4.549522);
		Node estacion19 = new Node("Romeynshof", 51.961513, 4.542624);
		Node estacion20 = new Node("Binnenhof", 51.960657, 4.535329);
		Node estacion21 = new Node("Hesseplaats", 51.963154, 4.552463);
		Node estacion22 = new Node("Nieuw Verlaat", 51.964753, 4.561532);
		Node estacion23 = new Node("Ambachtsland",	51.965713, 4.569976);
		Node estacion24 = new Node("De Tochten", 51.968787, 4.578286);
		Node estacion25 = new Node("Nesselande", 51.979018, 4.586358);
		Node estacion26 = new Node("De Akkers",51.832936,4.318342);
		Node estacion27 = new Node("Heemraadlaan", 51.838227, 4.331540);
		Node estacion28 = new Node("Spijkenisse Centrum", 51.846668, 4.334512);
		Node estacion29 = new Node("Zalmplaat", 51.855520, 4.363243);
		Node estacion30 = new Node("Hoogvliet", 51.863152, 4.359405);
		Node estacion31 = new Node("Tussenwater", 51.862683, 4.376109);
		Node estacion32 = new Node("Pernis", 51.884650, 4.382135);
		Node estacion33 = new Node("Vijfsluizen", 51.908288, 4.371889);
		Node estacion34 = new Node("Troelstralaan", 51.915277, 4.383916);
		Node estacion35 = new Node("Parkweg", 51.921609, 4.394162);
		Node estacion36 = new Node("Slotlaan", 51.928493, 4.578380);
		Node estacion37 = new Node("Capelle Centrum", 51.931525, 4.589969);
		Node estacion38 = new Node("De Terp", 51.935766, 4.599849);
		Node estacion39 = new Node("Poortugal", 51.861667, 4.395833);
		Node estacion40 = new Node("Rhoon", 51.859167, 4.419167);
		Node estacion41 = new Node("Slinge", 51.874722, 4.477778);
		Node estacion42 = new Node("Zuidplein", 51.886944, 4.488611);
		Node estacion43 = new Node("Maashaven", 51.897222, 4.494722);
		Node estacion44 = new Node("Rijnhaven", 51.903889, 4.496944);
		Node estacion45 = new Node("Wilhelminaplein", 51.907222, 4.493889);
		Node estacion46 = new Node("Leuvehaven", 51.9125, 4.481944);
		Node estacion47 = new Node("Stadhuis", 51.923333, 4.478333);
		Node estacion48 = new Node("Rotterdam Centraal", 51.925, 4.469444);
		Node estacion49 = new Node("Blijdorp", 51.930833, 4.457778);
		Node estacion50 = new Node("Melanchthonweg", 51.948333, 4.464444);
		Node estacion51 = new Node("Meijersplein", 51.956111, 4.462222);
		Node estacion52 = new Node("Rodenrijs", 51.973333, 4.460556);
		Node estacion53 = new Node("Berkel Westpolder", 51.984722, 4.4575);
		Node estacion54 = new Node("Pijnacker Zuid", 52.004722, 4.445833);
		Node estacion55 = new Node("Pijnacker Centrum", 52.019722, 4.438333);
		Node estacion56 = new Node("Nootdorp", 52.047778, 4.414444);
		Node estacion57 = new Node("Leidschenveen", 52.064444, 4.402778);
		Node estacion58 = new Node("Forepark", 52.070317, 4.392406);
		Node estacion59 = new Node("Leidschendam-Voorburg", 52.077748, 4.381791);
		Node estacion60 = new Node("Voorburg’t Loo", 52.082222, 4.366389);
		Node estacion61 = new Node("Laan Van NOI", 52.079388, 4.343380);
		Node estacion62 = new Node("Den Haag Centraal", 52.081149, 4.324287);

		Node estaciones [] = {estacion1,estacion2,estacion3,estacion4,estacion6,estacion7,estacion8,estacion9,estacion10,
							  estacion11,estacion12,estacion13,estacion14,estacion15,estacion16,estacion17,estacion18,estacion19,estacion20,
							  estacion21,estacion22,estacion23,estacion24,estacion25,estacion26,estacion27,estacion28,estacion29,estacion30,
							  estacion31,estacion32,estacion33,estacion34,estacion35,estacion36,estacion37,estacion38,estacion39,estacion40,
							  estacion41,estacion42,estacion43,estacion44,estacion45,estacion46,estacion47,estacion48,estacion49,estacion50,
							  estacion51,estacion52,estacion44,estacion54,estacion55,estacion56,estacion57,estacion58,estacion59,estacion60,
							  estacion61,estacion62};
		nodos = estaciones;
		
		//Definir adyacencias entre estaciones

		//Schiedam Centrum (trasbordo)
		estacion1.adyacencias = new Edge[]{
				new Edge(estacion2,calcularDistancia(estacion1,estacion2)),
				new Edge(estacion35,calcularDistancia(estacion1,estacion35))
		};

		//Marconiplein
		estacion2.adyacencias = new Edge[]{
				new Edge(estacion1,calcularDistancia(estacion2,estacion1)),
				new Edge(estacion3,calcularDistancia(estacion2,estacion3))
		};

		//Delfshaven
		estacion3.adyacencias = new Edge[]{
				new Edge(estacion2,calcularDistancia(estacion3,estacion2)),
				new Edge(estacion4,calcularDistancia(estacion3,estacion4))
		};

		//Coolhaven
		estacion4.adyacencias = new Edge[]{
				new Edge(estacion3,calcularDistancia(estacion4,estacion3)),
				new Edge(estacion5,calcularDistancia(estacion4,estacion5))
		};

		//Dijkzigt
		estacion5.adyacencias = new Edge[]{
				new Edge(estacion4,calcularDistancia(estacion5,estacion4)),
				new Edge(estacion6,calcularDistancia(estacion5,estacion6))
		};

		//Eendrachtsplein
		estacion6.adyacencias = new Edge[]{
				new Edge(estacion5,calcularDistancia(estacion6,estacion5)),
				new Edge(estacion7,calcularDistancia(estacion6,estacion7))
		};

		//Beurs (trasbordo)
		estacion7.adyacencias = new Edge[]{
				new Edge(estacion6,calcularDistancia(estacion7,estacion6)),
				new Edge(estacion8,calcularDistancia(estacion7,estacion8)),
				new Edge(estacion46,calcularDistancia(estacion7,estacion46)),
				new Edge(estacion47,calcularDistancia(estacion7,estacion47))
		};

		//Blaak
		estacion8.adyacencias = new Edge[]{
				new Edge(estacion7,calcularDistancia(estacion8,estacion7)),
				new Edge(estacion9,calcularDistancia(estacion8,estacion9))
		};

		//Oostplein
		estacion9.adyacencias = new Edge[]{
				new Edge(estacion8,calcularDistancia(estacion9,estacion8)),
				new Edge(estacion10,calcularDistancia(estacion9,estacion10))
		};

		//Gerdesiaweg
		estacion10.adyacencias = new Edge[]{
				new Edge(estacion9,calcularDistancia(estacion10,estacion9)),
				new Edge(estacion11,calcularDistancia(estacion10,estacion11))
		};
		
		//Voorschoterlaan
		estacion11.adyacencias = new Edge[]{
				new Edge(estacion10,calcularDistancia(estacion11,estacion10)),
				new Edge(estacion12,calcularDistancia(estacion11,estacion12))

		};
		
		//Kralingse Zoom
		estacion12.adyacencias = new Edge[]{
				new Edge(estacion11,calcularDistancia(estacion12,estacion11)),
				new Edge(estacion13,calcularDistancia(estacion12,estacion13))
		};
		
		//Capelsebrug (trasbordo)
		estacion13.adyacencias = new Edge[]{
				new Edge(estacion12,calcularDistancia(estacion13,estacion12)),
				new Edge(estacion14,calcularDistancia(estacion13,estacion14)),
				new Edge(estacion36,calcularDistancia(estacion13,estacion36))
		};
		
		//Schenkel
		estacion14.adyacencias = new Edge[]{
				new Edge(estacion13,calcularDistancia(estacion14,estacion13)),
				new Edge(estacion15,calcularDistancia(estacion14,estacion15))
		};
		
		//Prinsenlaan
		estacion15.adyacencias = new Edge[]{
				new Edge(estacion14,calcularDistancia(estacion15,estacion14)),
				new Edge(estacion16,calcularDistancia(estacion15,estacion16))
		};
		
		//Oosterflank
		estacion16.adyacencias = new Edge[]{
				new Edge(estacion15,calcularDistancia(estacion16,estacion15)),
				new Edge(estacion17,calcularDistancia(estacion16,estacion17))
		};
		
		//Alexander
		estacion17.adyacencias = new Edge[]{
				new Edge(estacion16,calcularDistancia(estacion17,estacion16)),
				new Edge(estacion18,calcularDistancia(estacion17,estacion18))
		};
		
		//Graskruid
		estacion18.adyacencias = new Edge[]{
				new Edge(estacion17,calcularDistancia(estacion18,estacion17)),
				new Edge(estacion19,calcularDistancia(estacion18,estacion19)),
				new Edge(estacion21,calcularDistancia(estacion18,estacion21))
		};
		
		//Romeynshof
		estacion19.adyacencias = new Edge[]{
				new Edge(estacion18,calcularDistancia(estacion19,estacion18)),
				new Edge(estacion20,calcularDistancia(estacion19,estacion20))
		};
		
		//Binnenhof
		estacion20.adyacencias = new Edge[]{
				new Edge(estacion19,calcularDistancia(estacion20,estacion19))
		};
		
		//Hesseplaats
		estacion21.adyacencias = new Edge[]{
				new Edge(estacion18,calcularDistancia(estacion21,estacion18)),
				new Edge(estacion22,calcularDistancia(estacion22,estacion23))
		};
		
		//Nieuw Verlaat
		estacion22.adyacencias = new Edge[]{
				new Edge(estacion21,calcularDistancia(estacion22,estacion21)),
				new Edge(estacion23,calcularDistancia(estacion22,estacion23))
		};
		
		//Ambachtsland
		estacion23.adyacencias = new Edge[]{
				new Edge(estacion22,calcularDistancia(estacion23,estacion22)),
				new Edge(estacion24,calcularDistancia(estacion23,estacion24))
		};
		
		//De Tochten
		estacion24.adyacencias = new Edge[]{
				new Edge(estacion23,calcularDistancia(estacion24,estacion23)),
				new Edge(estacion25,calcularDistancia(estacion24,estacion25))
		};
		
		//Nesselande
		estacion25.adyacencias = new Edge[]{
				new Edge(estacion24,calcularDistancia(estacion25,estacion24))
		};
		
		//De Akkers
		estacion26.adyacencias = new Edge[]{
				new Edge(estacion27,calcularDistancia(estacion26,estacion27))
		};
		
		//Heemraadlaan
		estacion27.adyacencias = new Edge[]{
				new Edge(estacion26,calcularDistancia(estacion27,estacion26)),
				new Edge(estacion28,calcularDistancia(estacion27,estacion28))
		};
		
		//Spijkenisse Centrum
		estacion28.adyacencias = new Edge[]{
				new Edge(estacion27,calcularDistancia(estacion28,estacion27)),
				new Edge(estacion29,calcularDistancia(estacion28,estacion29))
		};
		
		//Zalmplaat
		estacion29.adyacencias = new Edge[]{
				new Edge(estacion28,calcularDistancia(estacion29,estacion28)),
				new Edge(estacion30,calcularDistancia(estacion29,estacion30))
		};
		
		//Hoogvliet
		estacion30.adyacencias = new Edge[]{
				new Edge(estacion29,calcularDistancia(estacion30,estacion29)),
				new Edge(estacion31,calcularDistancia(estacion30,estacion31))
		};
		
		//Tussenwater
		estacion31.adyacencias = new Edge[]{
				new Edge(estacion30,calcularDistancia(estacion31,estacion30)),
				new Edge(estacion32,calcularDistancia(estacion31,estacion32)),
				new Edge(estacion39,calcularDistancia(estacion31,estacion39))
		};
		
		//Pernis
		estacion32.adyacencias = new Edge[]{
				new Edge(estacion31,calcularDistancia(estacion32,estacion31)),
				new Edge(estacion33,calcularDistancia(estacion32,estacion33))
		};
		
		//Vijfsluizen
		estacion33.adyacencias = new Edge[]{
				new Edge(estacion32,calcularDistancia(estacion33,estacion32)),
				new Edge(estacion34,calcularDistancia(estacion33,estacion34))
		};
		
		//Troelstralaan
		estacion34.adyacencias = new Edge[]{
				new Edge(estacion33,calcularDistancia(estacion34,estacion33)),
				new Edge(estacion35,calcularDistancia(estacion34,estacion35))
		};
		
		//Parkweg
		estacion35.adyacencias = new Edge[]{
				new Edge(estacion1,calcularDistancia(estacion35,estacion1)),
				new Edge(estacion34,calcularDistancia(estacion35,estacion34))

		};
		
		//Slotlaan
		estacion36.adyacencias = new Edge[]{
				new Edge(estacion13,calcularDistancia(estacion36,estacion13)),
				new Edge(estacion37,calcularDistancia(estacion36,estacion37))
		};
		
		//Capelle Centrum
		estacion37.adyacencias = new Edge[]{
				new Edge(estacion36,calcularDistancia(estacion37,estacion36)),
				new Edge(estacion38,calcularDistancia(estacion37,estacion38))
		};
		
		//De Terp
		estacion38.adyacencias = new Edge[]{
				new Edge(estacion37,calcularDistancia(estacion38,estacion37))

		};
		
		//Poortugal
		estacion39.adyacencias = new Edge[]{
				new Edge(estacion31,calcularDistancia(estacion39,estacion31)),
				new Edge(estacion40,calcularDistancia(estacion39,estacion40))
		};
		
		//Rhoon
		estacion40.adyacencias = new Edge[]{
				new Edge(estacion39,calcularDistancia(estacion40,estacion39)),
				new Edge(estacion41,calcularDistancia(estacion40,estacion41))
		};
		
		//Slinge
		estacion41.adyacencias = new Edge[]{
				new Edge(estacion40,calcularDistancia(estacion41,estacion40)),
				new Edge(estacion42,calcularDistancia(estacion41,estacion42))
		};
		
		//Zuidplein
		estacion42.adyacencias = new Edge[]{
				new Edge(estacion41,calcularDistancia(estacion42,estacion41)),
				new Edge(estacion43,calcularDistancia(estacion42,estacion43))
		};
		
		//Maashaven
		estacion43.adyacencias = new Edge[]{
				new Edge(estacion42,calcularDistancia(estacion43,estacion42)),
				new Edge(estacion44,calcularDistancia(estacion43,estacion44))
		};
		
		//Rijnhaven
		estacion44.adyacencias = new Edge[]{
				new Edge(estacion43,calcularDistancia(estacion44,estacion43)),
				new Edge(estacion45,calcularDistancia(estacion44,estacion45))
		};
		
		//Wilhelminaplein
		estacion45.adyacencias = new Edge[]{
				new Edge(estacion44,calcularDistancia(estacion45,estacion44)),
				new Edge(estacion46,calcularDistancia(estacion45,estacion46))
		};
		
		//Leuvehaven
		estacion46.adyacencias = new Edge[]{
				new Edge(estacion7,calcularDistancia(estacion46,estacion7)),
				new Edge(estacion45,calcularDistancia(estacion46,estacion45))
				
		};
		
		//Stadhuis
		estacion47.adyacencias = new Edge[]{
				new Edge(estacion7,calcularDistancia(estacion47,estacion7)),
				new Edge(estacion48,calcularDistancia(estacion47,estacion48))
		};
		
		//Rotterdam Centraal (trasbordo)
		estacion48.adyacencias = new Edge[]{
				new Edge(estacion47,calcularDistancia(estacion48,estacion47)),
				new Edge(estacion49,calcularDistancia(estacion48,estacion49))
		};
		
		//Blijdorp
		estacion49.adyacencias = new Edge[]{
				new Edge(estacion48,calcularDistancia(estacion49,estacion48)),
				new Edge(estacion50,calcularDistancia(estacion49,estacion50))
		};
		
		//Melanchthonweg
		estacion50.adyacencias = new Edge[]{
				new Edge(estacion49,calcularDistancia(estacion50,estacion49)),
				new Edge(estacion51,calcularDistancia(estacion50,estacion51))
		};
		
		//Meijersplein
		estacion51.adyacencias = new Edge[]{
				new Edge(estacion50,calcularDistancia(estacion51,estacion50)),
				new Edge(estacion52,calcularDistancia(estacion51,estacion52))
		};
		
		//Rodenrijs
		estacion52.adyacencias = new Edge[]{
				new Edge(estacion51,calcularDistancia(estacion52,estacion51)),
				new Edge(estacion53,calcularDistancia(estacion52,estacion53))
		};
		
		//Berkel Westpolder
		estacion53.adyacencias = new Edge[]{
				new Edge(estacion52,calcularDistancia(estacion53,estacion52)),
				new Edge(estacion54,calcularDistancia(estacion53,estacion54))
		};
		
		//Pijnacker Zuid
		estacion54.adyacencias = new Edge[]{
				new Edge(estacion53,calcularDistancia(estacion54,estacion53)),
				new Edge(estacion55,calcularDistancia(estacion54,estacion55))
		};
		
		//Pijnacker Centrum
		estacion55.adyacencias = new Edge[]{
				new Edge(estacion54,calcularDistancia(estacion55,estacion54)),
				new Edge(estacion56,calcularDistancia(estacion55,estacion56))
		};
		
		//Nootdorp
		estacion56.adyacencias = new Edge[]{
				new Edge(estacion55,calcularDistancia(estacion56,estacion55)),
				new Edge(estacion57,calcularDistancia(estacion56,estacion57))
		};
		
		//Leidschenveen
		estacion57.adyacencias = new Edge[]{
				new Edge(estacion56,calcularDistancia(estacion57,estacion56)),
				new Edge(estacion58,calcularDistancia(estacion57,estacion58))
		};
		
		//Forepark
		estacion58.adyacencias = new Edge[]{
				new Edge(estacion57,calcularDistancia(estacion58,estacion57)),
				new Edge(estacion59,calcularDistancia(estacion58,estacion59))
		};
		
		//Leidschendam-Voorburg
		estacion59.adyacencias = new Edge[]{
				new Edge(estacion58,calcularDistancia(estacion59,estacion58)),
				new Edge(estacion60,calcularDistancia(estacion59,estacion60))
		};
		
		//Voorburg’t Loo
		estacion60.adyacencias = new Edge[]{
				new Edge(estacion59,calcularDistancia(estacion60,estacion59)),
				new Edge(estacion61,calcularDistancia(estacion60,estacion61))
		};
		
		//Laan Van NOI
		estacion61.adyacencias = new Edge[]{
				new Edge(estacion60,calcularDistancia(estacion61,estacion60)),
				new Edge(estacion62,calcularDistancia(estacion61,estacion62))
		};
		
		//Den Haag Centraal
		estacion62.adyacencias = new Edge[]{
				new Edge(estacion61,calcularDistancia(estacion62,estacion61))
		};			
	}

	public static void calcularH(Node nodoFinal){
		for (int i = 0;i<nodos.length;i++){
			nodos[i].h_x = calcularDistancia(nodos[i],nodoFinal);
		}
	}

	public static List<Node> printPath(Node destino){
		List<Node> camino = new ArrayList<Node>();

		for(Node node = destino; node!=null; node = node.padre){
			camino.add(node);
		}
		Collections.reverse(camino);
		return camino;
	}

	public static void astarSearch(Node origen, Node destino){

		Set<Node> explorado = new HashSet<Node>();

		PriorityQueue<Node> cola = new PriorityQueue<Node>(61,new Comparator<Node>(){
			public int compare(Node i, Node j){
				if(i.f_x > j.f_x){
					return 1;
				}
				else if (i.f_x < j.f_x){
					return -1;
				}
				else{
					return 0;
				}
			}
		}
				);

		//ponemos coste 0 ya que nos encontramos en el comienzo
		origen.g_x = 0;
		cola.add(origen);
		boolean encontrado = false;

		while((!cola.isEmpty())&&(!encontrado)){

			//ponemos al principio de la cola el nodo que tiene el menor f(x)
			Node actual = cola.poll();
			System.out.println(actual.nombre);
			explorado.add(actual);

			//combrobamos si hemos llegado al destino
			if(actual.nombre.equals(destino.nombre)){
				encontrado = true;
			}

			//comprobamos los hijos del nodo en el que nos encontramos
			for(Edge e : actual.adyacencias){
				Node hijo = e.objetivo;
				double coste = e.coste;
				double temp_g_x = actual.g_x + coste;
				double temp_f_x = temp_g_x + hijo.h_x;

				//si el hijo del nodo ha sido evaluado y f(x) del mismo es mayor, lo saltamos
				if((explorado.contains(hijo)) && 
						(temp_f_x >= hijo.f_x)){
					continue;
				}
				
				//si el nodo hijo no esta en la cola o su f(x) es menor
				else if((!cola.contains(hijo)) || 
						(temp_f_x < hijo.f_x)){

					hijo.padre = actual;
					hijo.g_x = temp_g_x;
					hijo.f_x = temp_f_x;

					if(cola.contains(hijo)){
						cola.remove(hijo);
					}

					cola.add(hijo);
				}
			}
		}
	}
}