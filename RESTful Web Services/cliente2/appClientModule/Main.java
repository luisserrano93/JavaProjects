
import upm.datos.*;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class Main {

    public static void main(String[] args) throws Exception{

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(getBaseURI());
        
      
        System.out.println("Leyenda: Las peticiones enumeradas son las que se piden en el enunciado, el resto de peticiones no se piden"
        		+ "pero se han llevado a cabo para intentar aportar mayor riqueza");
        
        
        // 1. Creación de un nuevo usuario:
	
        System.out.println("1.Creamos un nuevo usuario con username 'alf69': ");
        User nuevoUsuario = new User("alf69", "Alfredo", "Cristobal", "Blanco","Agua,30","alfcris@gmail.com","918735050");
       
        Response response = target.path("users").request().post(Entity.xml(nuevoUsuario)); // Establece Content Type
        System.out.println("Estado Respuesta: "+ response.getStatus());
        
        if(response.getHeaders().containsKey("Location")){
       	 Object location = response.getHeaders().get("Location").get(0);
            System.out.println("Location: "+ location.toString());
       }

        
        //  Creación de otro usuario con mismo username:
        
        System.out.println("Creamos otro usuario con username 'alf69': ");
        User mismoUsuario = new User("alf69", "Pepe", "Pepe", "Pepe","Agua,30","Pepe@gmail.com","918735659");
        
        Response response1 = target.path("users").request().post(Entity.xml(mismoUsuario)); // Establece Content Type
        System.out.println("Estado Respuesta: "+ response1.getStatus());
        
        String valor1 = response1.readEntity(String.class);
        System.out.println("Entidad: "+valor1);
        
        
       // 2. Publicacion de varios posts:
       
		// 2.1 Publicacion del primer post:
                
		System.out.println("2. Publicación de varios posts: ");
		
        System.out.println("Publicación del primer post: ");
                
        Post primerPost = new Post();
        primerPost.setTextoPost("Hola, ¡Este es mi primer post en upmsocial!");

        
        Response response2 = target.path("users/alf69/posts").request().post(Entity.xml(primerPost));
        System.out.println("Estado Respuesta: "+ response2.getStatus());
        
        if(response.getHeaders().containsKey("Location")){
          	 Object location = response2.getHeaders().get("Location").get(0);
               System.out.println("Location: "+ location.toString());
          }
        
        
		// 2.2 Publicacion del segundo post:
        
        System.out.println("Publicación del segundo post: ");
        
        Post segundoPost = new Post();
        segundoPost.setTextoPost("¡Este es mi segundo post en upmsocial!");
                
        
        Response response3 = target.path("users/alf69/posts").request().post(Entity.xml(segundoPost));
        System.out.println("Estado Respuesta: "+ response3.getStatus());
        
        if(response.getHeaders().containsKey("Location")){
          	 Object location = response3.getHeaders().get("Location").get(0);
               System.out.println("Location: "+ location.toString());
          }
       
		// 2.3 Publicacion del tercer post:

        
        System.out.println("Publicación del tercer post: ");
                
        Post tercerPost = new Post();
        tercerPost.setTextoPost("Me estoy cansando de esta red social");
        
        Response response4 = target.path("users/alf69/posts").request().post(Entity.xml(tercerPost));
        System.out.println("Estado Respuesta: "+ response4.getStatus());
        
        if(response.getHeaders().containsKey("Location")){
          	 Object location = response4.getHeaders().get("Location").get(0);
               System.out.println("Location: "+ location.toString());
          }
        
        
        // Obtener mis post utilizando los filtros disponibles, primero se pedirá que se obtengan a partir del 2º post y soólo se pide 1 post:
        
        System.out.println("Obtener lista de mis posts (inicio = 2 & cuantos = 1): ");
        
        System.out.println("Posts: "+ target.path("users/alf69/posts").queryParam("inicio", "2").queryParam("cuantos", "1").request().accept(MediaType.APPLICATION_XML).get(String.class));
        
        // 3. Obtener el contenido de uno de los post:
        
        System.out.println("3. Obtener el contenido de los posts publicados anteriormente: ");
        System.out.println("Contenido del post 1: "+ primerPost.getTextoPost());
        System.out.println("Contenido del post 2:" + segundoPost.getTextoPost());
        System.out.println("Contenido del post 3:" + tercerPost.getTextoPost());

        
        
        // 4. Modificar un post:
        
        
        System.out.println("4. Modificación del post: 3");
        Post post = new Post();
        post.setIdPost(3);
        post.setTextoPost("Post 3 modificado");
        
       
        response = target.path("users/alf69/posts/3").request().put(Entity.xml(post));        
        System.out.println("Estado Respuesta: "+ response.getStatus());        
        System.out.println("Contenido del post " + post.getIdPost()+": "+ post.getTextoPost());

        // 5. Borrar un post:
        
        System.out.println("5. Ahora borramos el post 1 : ");
        response = target.path("users/alf69/posts/1").request().delete();
        System.out.println("Estado Respuesta: "+ response.getStatus());

        String valor2 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor2);
        
        //6. Buscar posibles amigos entre los usuarios:
        
        System.out.println("6. Buscar posibles amigos entre los usuarios (Buscaremos usuarios cuyo nombre contenga la palabra 'carlos'):");
        
        System.out.println("Posibles amigos: "+ target.path("users").queryParam("nombre", "carlos").request().accept(MediaType.APPLICATION_XML).get(String.class));
        
        //7. Agregar un amigo:
        
        User u = target.path("users/cejas69").request().accept(MediaType.APPLICATION_XML).get(User.class);
        
        System.out.println("7. Agregamos a amigos al usuario con username: "+ u.getUsername());

        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(u),Response.class);
        System.out.println("Estado respuesta: "+response.getStatus());
        if(response.getHeaders().containsKey("Location")){
         	 Object location = response.getHeaders().get("Location").get(0);
              System.out.println("Location: "+ location.toString());
         }
        
        // Intentamos agregar como amigos a un usuario que ya es nuestro amigo:
        
        System.out.println("Agregamos a amigos a un usuario que ya es nuestro amigo");

        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(u),Response.class);
        System.out.println("Estado respuesta: "+response.getStatus());
        String valor4 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor4);
        
        // Intentamos agregar como amigos a un usuario que no esta registrado en upmsocial:
        
        System.out.println("Intentamos agregar como amigo a un usuario que no existe en upmsocial: ");
        User usuarioInventado = new User();
        usuarioInventado.setUsername("noExisto");
        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(usuarioInventado),Response.class);
        System.out.println("Estado respuesta: "+response.getStatus());
        String valor5 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor5);
        
        
        // 8. Eliminar un amigo:
        
        System.out.println("8. Eliminamos de la lista de amigos al usuario con username: "+u.getUsername());
        response = target.path("users/alf69/amigos/cejas69").request().delete();
        System.out.println("Estado respuesta: "+response.getStatus());
        String valor6 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor6);

   
        // Agreamos 3 amigos
        System.out.println("(Agregaremos a unos cuantos amigos para las proximas peticiones)");
        User u1 = target.path("users/charli55").request().accept(MediaType.APPLICATION_XML).get(User.class);
        User u2 = target.path("users/danivs").request().accept(MediaType.APPLICATION_XML).get(User.class);
        User u3 = target.path("users/vilela4").request().accept(MediaType.APPLICATION_XML).get(User.class);

        
        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(u1),Response.class);
        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(u2),Response.class);
        response = target.path("users/alf69/amigos").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(u3),Response.class);
        
        //9. Obtener la lista de amigos usando los filtros disponibles:
        
        System.out.println("9. Obtener la lista de amigos utilizando filtros(buscaremos aquellos amigos cuyo nombre contenga una 'e'): ");   
        System.out.println("Amigos: "+ target.path("users/alf69/amigos").queryParam("nombre", "e").request().accept(MediaType.APPLICATION_XML).get(String.class));

        //10. Consultar el numero de posts total que he publicado:
        
        System.out.print("10. Numero total de posts publicados: ");
        System.out.println(target.path("users/alf69/posts/numeroPost").request().accept(MediaType.APPLICATION_XML).get(String.class));
 
        // 11. Obtener la lista de usuarios:
        
        System.out.println("11. Obtener la lista de usuarios: ");
        System.out.println("Usuarios: "+ target.path("users").request().accept(MediaType.APPLICATION_XML).get(String.class));

        //12. Modificar los datos de nuestro perfil:
        
        System.out.println("12. Modificación de los datos de nuestro perfil: ");
        
        System.out.println("12.1. Primero intenamos modificar nuestro username: ");
        
        User perfil = new User();
        perfil.setUsername("alffalff");
       
        response = target.path("users/alf69").request().put(Entity.xml(perfil));        
        System.out.println("Estado Respuesta: "+ response.getStatus());        
        

        String valor7 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor7);
        
        System.out.println("12.2 Segundo modificamos ciertos datos, incluyendo el username");
        
        perfil.setUsername("USERNAME_MODIFICADO");
        perfil.setApellido1("APELLIDO_MODIFICADO");
        perfil.setDireccion("DIRECCION_MODIFICADA");
        response = target.path("users/alf69").request().put(Entity.xml(perfil));        
        System.out.println("Estado Respuesta: "+ response.getStatus());        
        

        String valor8 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor8);
        
        System.out.println("Ahora vemos los nuevos valores de nuestro pefil: ");
        
        System.out.println("Usuarios: "+ target.path("users/alf69").request().accept(MediaType.APPLICATION_XML).get(String.class));
        
        System.out.println("Vemos como el valor del username sigue siendo: "+ nuevoUsuario.getUsername());
        
        response.close();
        
        System.out.println("12.3. Ahora modificados ciertos datos, sin incluir el username: ");
        
        perfil.setApellido2("apellido_definitivo");
        perfil.setTelefono("telefono_definitivo");
        perfil.setApellido1("apellido_definitivo");
        
        response = target.path("users/alf69").request().put(Entity.xml(perfil));        
        System.out.println("Estado Respuesta: "+ response.getStatus());        
        
        
        

        
        //13. Darse de baja de la red social:
        
        System.out.println("13. Ahora nos damos de baja de la red social (Suponemos ahora que somos por ejemplo el usuario cejas69): ");
        
        response = target.path("users/cejas69").request().delete();
        System.out.println("Estado respuesta: "+response.getStatus());
        String valor10 = response.readEntity(String.class);
        System.out.println("Entidad: "+valor10);
        
        System.out.println("Y comprobamos que se ha borrado el usuario: ");
        System.out.println("users: "+ target.path("users").request().accept(MediaType.APPLICATION_XML).get(String.class));

        
        //14. Obtener la lista de posts publicados por amigos que contienen un determinado texto
        
        System.out.println("14. Obtener la lista de posts publicados que contienen la palabra 'sabado' ");

        System.out.println("Posts de amigos: "+ target.path("users/alf69/amigos/posts").queryParam("contenido", "sabado").request().accept(MediaType.APPLICATION_XML).get(String.class));

        System.out.println("Obtener la lista de posts publicados entre el 01-01-2015 y 01-01-2016: ");

        System.out.println("Posts de amigos: "+ target.path("users/alf69/amigos/posts").queryParam("fechaInicio", "01-01-2015").queryParam("fechaFin", "01-01-2016").request().accept(MediaType.APPLICATION_XML).get(String.class));


    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/upmsocial/api").build();
    }
}