package upm.recursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBElement;

import upm.datos.Amigo;
import upm.datos.Post;
import upm.datos.User;

@Path("/users")
public class UserResource {

	@Context
	private UriInfo uriInfo;

	@Resource(name = "jdbc/users")
	private DataSource ds;

	Statement statement = null;
	ResultSet resultSet = null;

	public static Connection conexionBBDD() throws ClassNotFoundException, SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306" + "/" + "upmSocial";
		Connection conexion = DriverManager.getConnection(url, "root", "yakazymoneling20");
		return conexion;

	}

	// 1. Obtener una lista de todos los usuarios de la red: GET http://localhost:8080/upmsocial/api/users

	// 8. Buscar posibles amigos en la red por nombre (patron): GET http://localhost:8080/upmsocial/api/users?nombre=X

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getUsers(@QueryParam("nombre") @DefaultValue("%") String nombre) throws ClassNotFoundException {
		Connection conn = null;
		try {
			conn = conexionBBDD();
			String sql = "SELECT * FROM Usuario where nombre like ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + nombre + "%");
			ResultSet rs = ps.executeQuery();
			String listado = "<?xml version=\"1.0\"?>" + "<users>";
			rs.beforeFirst();
			while (rs.next()) {
				listado = listado + "<user href = " + uriInfo.getAbsolutePath() + "/" + rs.getString("username")
						+ "><nombre>" + rs.getString("nombre") + "</nombre></user>";
			}
			listado = listado + "</users>";
			return Response.status(Response.Status.OK).entity(listado).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}

	// 1.1 Obtener la representacion completa de un usuario en concreto: GET http://localhost:8080/upmsocial/api/users/[usuario]

	@GET
	@Path("{user_id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getUser(@PathParam("user_id") String user_id) throws ClassNotFoundException {
		Connection conn = null;
		User user = null;
		try {
			conn = conexionBBDD();
			User usuario = null;
			String sql = "SELECT * FROM usuario where username= ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user_id);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				usuario = new User(rs.getString("username"), rs.getString("nombre"), rs.getString("apellido1"),
						rs.getString("apellido2"), rs.getString("direccion"), rs.getString("correo"),
						rs.getString("telefono"));
			}

			user = usuario;
			if (user != null) {
				return Response.status(Response.Status.OK).entity(user).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}

	// 2. Añadir un nuevo usuario: POST http://localhost:8080/upmsocial/api/users/

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response crearUsuario(JAXBElement<User> user, @PathParam("user_id") String user_id) {
		User valor = user.getValue();
		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {
				boolean usuarioRepetido = buscarUsuario(conn, valor.getUsername());
				if (!usuarioRepetido) {

					PreparedStatement query = conn.prepareStatement("INSERT into Usuario values(?,?,?,?,?,?,?)");
					query.setString(1, valor.getUsername());
					query.setString(2, valor.getNombre());
					query.setString(3, valor.getApellido1());
					query.setString(4, valor.getApellido2());
					query.setString(5, valor.getDireccion());
					query.setString(6, valor.getCorreo());
					query.setString(7, valor.getTelefono());

					query.executeUpdate();

					return Response.status(Status.CREATED)
							.header("Location", uriInfo.getAbsolutePath().toString() + "/" + valor.getUsername())
							.build();
				} else {

					return Response.status(Status.UNAUTHORIZED).entity("Username no disponible, pruebe con otro")
							.build();
				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}

	// Funcion para ver si un usuario existe en la base de datos o no

	public static boolean buscarUsuario(Connection conexion, String username) throws SQLException {

		boolean encontrado = false;
		PreparedStatement query = conexion.prepareStatement("SELECT username FROM Usuario where username = ?");
		query.setString(1, username);

		ResultSet res = query.executeQuery();

		while (res.next()) {
			String usuarioBBDD = res.getString("username");

			if (usuarioBBDD.equals(username)) {
				encontrado = true;
			} else

				encontrado = false;

		}

		return encontrado;
	}

	// 3. Publicar un nuevo post: POST http://localhost:8080/upmsocial/api/[usuario]/posts

	@POST
	@Path("{user_id}/posts")
	@Consumes(MediaType.APPLICATION_XML)
	public Response crearPost(JAXBElement<Post> post, @PathParam("user_id") String user_id) {

		Post valor = post.getValue();
		Connection conn = null;
		ArrayList<Integer> lista = null;
		try {
			conn = conexionBBDD();
			try {

				ArrayList<Integer> lista_Identificadores = new ArrayList<Integer>();
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM Post");
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					lista_Identificadores.add(rs.getInt("idPost"));
				}

				lista = lista_Identificadores;

				int idNuevoPost = 1;
				boolean esta = false;

				while (!esta) {

					boolean resultado = lista.contains(idNuevoPost);

					if (resultado) {
						idNuevoPost++;
					} else {
						esta = true;
					}

				}

				DateFormat d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date fecha = new Date();
				String fechaCreada = d.format(fecha);

				PreparedStatement query = conn.prepareStatement("INSERT into Post values(?,?,?,?)");
				query.setInt(1, idNuevoPost);
				query.setString(2, user_id);
				query.setString(3, valor.getTextoPost());
				query.setString(4, fechaCreada);

				query.executeUpdate();

				return Response.status(Status.CREATED)
						.header("Location", uriInfo.getAbsolutePath().toString() + "/" + idNuevoPost).build();
			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}

	}

	// 4. Editar un post: PUT http://upmsocial.es/api/users/[usuario]/posts/[post]

	@PUT
	@Path("{user_id}/posts/{post_id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response editarPost(JAXBElement<Post> post, @PathParam("user_id") String user_id,
			@PathParam("post_id") int post_id) {

		Post valor = post.getValue();
		Connection conn = null;

		try {
			conn = conexionBBDD();
			try {

				DateFormat d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date date = new Date();
				String nuevaFecha = d.format(date);

				PreparedStatement query = conn.prepareStatement(
						"UPDATE post SET textoPost= ? , fechaPost= ? WHERE idPost= ? and idUsuario_Post= ?");

				query.setString(1, valor.getTextoPost());
				query.setString(2, nuevaFecha);
				query.setInt(3, post_id);
				query.setString(4, user_id);

				if (query.executeUpdate() != 0) {

					return Response.status(Status.OK).header("Location", uriInfo.getAbsolutePath().toString()).build();
				} else {
					return Response.status(Status.NOT_FOUND).build();
				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar a la DB.");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexión con la DB.");
			}
		}

	}

	// 5. Eliminar un POST: DELETE http://upmsocial.es/api/[usuario]/posts/[post]

	@DELETE
	@Path("{user_id}/posts/{post_id}")
	public Response deleteVehiculo(@PathParam("user_id") String user_id, @PathParam("post_id") int post_id) {
		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {

				PreparedStatement query = conn
						.prepareStatement("DELETE from Post where idPost = ? and idUsuario_Post= ?");

				query.setInt(1, post_id);
				query.setString(2, user_id);

				if (query.executeUpdate() != 0) {

					return Response.ok().entity("Post " + post_id + " eliminado correctamente").build();
				} else {
					return Response.status(Status.NOT_FOUND).entity("Elemento no encontrado").build();

				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar a la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}

	// 6. Obtener una lista de todos los posts de un usuario

	// - limitar la cantidad de información obtenida por número de posts

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?inicio=X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?cuantos=Y

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?inicio=X&cuantos=Y

	// -filtrar esa lista por fecha:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?fechaInicio=X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?fechaFin=Y

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?fechaInicio=X&fechaFin=Y

	// - Y obviamente filtrando por fecha y limitando la cantidad obtenida:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts?fechaInicio=X&fechaFin=Y&inicio=Z&cuantos=G

	@GET
	@Path("{user_id}/posts")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPosts(@PathParam("user_id") String user_id, @QueryParam("inicio") @DefaultValue("1") int inicio,
			@QueryParam("cuantos") @DefaultValue("3") int cuantos,
			@QueryParam("fechaInicio") @DefaultValue("") String fechaInicio,
			@QueryParam("fechaFin") @DefaultValue("") String fechaFin) {

		Connection conn = null;

		try {
			conn = conexionBBDD();
			try {
				int inicio1 = inicio - 1;
				boolean usuarioRepetido = buscarUsuario(conn, user_id);

				if (usuarioRepetido) {

					if (!fechaInicio.equals("") && fechaFin.equals("")) { // Se muestran los posts a partir de fechaInicio

						Post post;
						ArrayList<Post> lista_Post = new ArrayList<Post>();
						PreparedStatement ps = conn.prepareStatement(
								"SELECT * FROM upmsocial.post where idUsuario_Post = ? and fechaPost >= str_to_date(?, '%d-%m-%Y') order by fechaPost asc limit ?,?");
						ps.setString(1, user_id);
						ps.setString(2, fechaInicio);
						ps.setInt(3, inicio1);
						ps.setInt(4, cuantos);

						ResultSet rs = ps.executeQuery();

						while (rs.next()) {
							post = new Post();
							post.setIdPost(rs.getInt("idPost"));
							post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
							post.setTextoPost(rs.getString("textoPost"));
							post.setFechaPost((Date) rs.getDate("fechaPost"));
							lista_Post.add(post);

						}

						if (!lista_Post.isEmpty()) { // ME DEVUELVES LOS POST

							String listado = "<?xml version=\"1.0\"?>" + "<posts>";

							for (int x = 0; x < lista_Post.size(); x++) {

								listado = listado + "<post href = " + uriInfo.getAbsolutePath() + "/"
										+ lista_Post.get(x).getIdPost() + "><textoPost>"
										+ lista_Post.get(x).getTextoPost() + "</textoPost></post>";
							}

							listado = listado + "</posts>";

							return Response.status(Response.Status.OK).entity(listado).build();
						}

						return Response.status(Response.Status.NOT_FOUND).entity("No hay Post a partir de la fecha " + fechaInicio).build();

					} else if (fechaInicio.equals("") && !fechaFin.equals("")) { // Se muestran los post anteriores a fechaFin

						Post post;
						ArrayList<Post> lista_Post = new ArrayList<Post>();
						PreparedStatement ps = conn.prepareStatement(
								"SELECT * FROM upmsocial.post where idUsuario_Post = ? and fechaPost <= str_to_date(?, '%d-%m-%Y') order by fechaPost asc limit ?,?");
						ps.setString(1, user_id);
						ps.setString(2, fechaFin);
						ps.setInt(3, inicio1);
						ps.setInt(4, cuantos);

						ResultSet rs = ps.executeQuery();

						while (rs.next()) {
							post = new Post();
							post.setIdPost(rs.getInt("idPost"));
							post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
							post.setTextoPost(rs.getString("textoPost"));
							post.setFechaPost((Date) rs.getDate("fechaPost"));
							lista_Post.add(post);

						}

						if (!lista_Post.isEmpty()) { // ME DEVUELVES LOS POST

							String listado = "<?xml version=\"1.0\"?>" + "<posts>";

							for (int x = 0; x < lista_Post.size(); x++) {

								listado = listado + "<post href = " + uriInfo.getAbsolutePath() + "/"
										+ lista_Post.get(x).getIdPost() + "><textoPost>"
										+ lista_Post.get(x).getTextoPost() + "</textoPost></post>";
							}

							listado = listado + "</posts>";

							return Response.status(Response.Status.OK).entity(listado).build();
						}

						return Response.status(Response.Status.NOT_FOUND).entity("No hay Post antes de la fecha " + fechaFin).build();

					} else if (!fechaInicio.equals("") && !fechaFin.equals("")) { // Se muestran los posts entre fechaInicio y fechaFin
						
						Post post;
						ArrayList<Post> lista_Post = new ArrayList<Post>();
						PreparedStatement ps = conn.prepareStatement(
								"SELECT * FROM upmsocial.post where idUsuario_Post = ? and fechaPost between str_to_date(?, '%d-%m-%Y') and str_to_date(?, '%d-%m-%Y') order by fechaPost asc limit ?,?");
						ps.setString(1, user_id);
						ps.setString(2, fechaInicio);
						ps.setString(3, fechaFin);
						ps.setInt(4, inicio1);
						ps.setInt(5, cuantos);

						ResultSet rs = ps.executeQuery();

						while (rs.next()) {
							post = new Post();
							post.setIdPost(rs.getInt("idPost"));
							post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
							post.setTextoPost(rs.getString("textoPost"));
							post.setFechaPost((Date) rs.getDate("fechaPost"));
							lista_Post.add(post);

						}

						if (!lista_Post.isEmpty()) { // ME DEVUELVES LOS POST

							String listado = "<?xml version=\"1.0\"?>" + "<posts>";

							for (int x = 0; x < lista_Post.size(); x++) {

								listado = listado + "<post href = " + uriInfo.getAbsolutePath() + "/"
										+ lista_Post.get(x).getIdPost() + "><textoPost>"
										+ lista_Post.get(x).getTextoPost() + "</textoPost></post>";
							}

							listado = listado + "</posts>";

							return Response.status(Response.Status.OK).entity(listado).build();
						}

						return Response.status(Response.Status.NOT_FOUND).entity("No hay Post entre las fechas "+ fechaInicio+" y " +fechaFin).build();

					} else { // Se muestran todos los posts sin tener en cuenta las fechas ya que no se pasa ninguna fecha

						Post post;
						ArrayList<Post> lista_Post = new ArrayList<Post>();
						PreparedStatement ps = conn.prepareStatement(
								"SELECT * FROM upmsocial.post where idUsuario_Post = ? order by fechaPost asc limit ?,?");
						ps.setString(1, user_id);
						ps.setInt(2, inicio1);
						ps.setInt(3, cuantos);

						ResultSet rs = ps.executeQuery();

						while (rs.next()) {
							post = new Post();
							post.setIdPost(rs.getInt("idPost"));
							post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
							post.setTextoPost(rs.getString("textoPost"));
							post.setFechaPost((Date) rs.getDate("fechaPost"));
							lista_Post.add(post);

						}

						if (!lista_Post.isEmpty()) { // ME DEVUELVES LOS POST

							String listado = "<?xml version=\"1.0\"?>" + "<posts>";

							for (int x = 0; x < lista_Post.size(); x++) {

								listado = listado + "<post href = " + uriInfo.getAbsolutePath() + "/"
										+ lista_Post.get(x).getIdPost() + "><textoPost>"
										+ lista_Post.get(x).getTextoPost() + "</textoPost></post>";
							}

							listado = listado + "</posts>";

							return Response.status(Response.Status.OK).entity(listado).build();
						}

						return Response.status(Response.Status.NOT_FOUND).entity("No hay Post").build();

					}

				} else {

					return Response.status(Status.UNAUTHORIZED).entity("Usuario no existe, pruebe con otro").build();
				}

			} catch (SQLException e) {

				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {

			System.out.println("Fallo al conectar a la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}
	}

	
	// 6.1. Obtener la representación completa de un post mio concreto: GET http://localhost:8080/upmsocial/api/users/[usuario]/posts/[post]
	
		@GET
		@Path("{user_id}/posts/{post_id}")
		@Produces(MediaType.APPLICATION_XML)
		public Response getPost(@PathParam("user_id") String user_id,
								@PathParam("post_id") int post_id) throws ClassNotFoundException {
			Connection conn = null;
			Post post = null;
			try {
				conn = conexionBBDD();
				Post publicacion = null;
				String sql = "SELECT * FROM post where idUsuario_Post = ? and idPost = ? ";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user_id);
				ps.setInt(2, post_id);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					post = new Post(rs.getInt("idPost"),rs.getString("idUsuario_Post"), rs.getString("textoPost"),rs.getDate("fechaPost"));
				
				}

				publicacion = post;
				if (publicacion != null) {
					return Response.status(Response.Status.OK).entity(publicacion).build();
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
			} catch (NumberFormatException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					System.err.println("Fallo al cerrar la conexion con la DB");
				}
			}
		}
		
	
	// 7. Obtener el número de posts publicados por mí en la red social en un determinado periodo (fecha inicio y fin)

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts/numeroPost?fechaInicio=X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts/numeroPost?fechaFin=Y

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/posts/numeroPost?fechaInicio=X&fechaFin=Y

	@GET
	@Path("{user_id}/posts/numeroPost")
	@Produces(MediaType.APPLICATION_XML)
	public Response getNumeroPost(@PathParam("user_id") String user_id,
			@QueryParam("fechaInicio") @DefaultValue("") String fechaInicio,
			@QueryParam("fechaFin") @DefaultValue("") String fechaFin) throws ClassNotFoundException {

		Connection conn = null;
		ArrayList<Post> posts = null;
		int numeroPost = 0;
		try {
			conn = conexionBBDD();
			try {
				Post post;
				ArrayList<Post> lista_Post = new ArrayList<Post>();

				if (fechaInicio.equals("") && !fechaFin.equals("")) { // Se cuentan antes de la fechaFIN dada

					PreparedStatement ps = conn.prepareStatement(
							"SELECT * FROM post where idUsuario_Post = ? and fechaPost <= str_to_date(?, '%d-%m-%Y')");
					ps.setString(1, user_id);
					ps.setString(2, fechaFin);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						post = new Post();
						post.setIdPost(rs.getInt("idPost"));
						post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
						post.setTextoPost(rs.getString("textoPost"));
						post.setFechaPost((Date) rs.getDate("fechaPost"));
						lista_Post.add(post);
					}

					posts = lista_Post;
					numeroPost = posts.size();
					if (numeroPost > 0) {
						return Response.status(Response.Status.OK).entity("El usuario " + user_id + " ha publicado "
								+ numeroPost + " post antes de la fecha " + fechaFin).build();
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity(
								"El usuario " + user_id + " no ha publicado ningun post antes de la fecha " + fechaFin)
								.build();
					}

				} else if (fechaFin.equals("") && !fechaInicio.equals("")) { // Se cuentan a partir de la fecha de inicio dada

					PreparedStatement ps = conn.prepareStatement(
							"SELECT * FROM post where idUsuario_Post = ? and fechaPost >= str_to_date(?, '%d-%m-%Y')");
					ps.setString(1, user_id);
					ps.setString(2, fechaInicio);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						post = new Post();
						post.setIdPost(rs.getInt("idPost"));
						post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
						post.setTextoPost(rs.getString("textoPost"));
						post.setFechaPost((Date) rs.getDate("fechaPost"));
						lista_Post.add(post);
					}

					posts = lista_Post;
					numeroPost = posts.size();
					if (numeroPost > 0) {
						return Response.status(Response.Status.OK).entity("El usuario " + user_id + " ha publicado "
								+ numeroPost + " post a partir de la fecha " + fechaInicio).build();
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("El usuario " + user_id
								+ " no ha publicado ningun post a partir de la fecha " + fechaFin).build();

					}

				} else if (!fechaInicio.equals("") && !fechaFin.equals("")) { // Se cuetan entre las 2 fechas

					PreparedStatement ps = conn.prepareStatement(
							"SELECT * FROM post where idUsuario_Post = ? and fechaPost between str_to_date(?, '%d-%m-%Y') and str_to_date(?, '%d-%m-%Y')");
					ps.setString(1, user_id);
					ps.setString(2, fechaInicio);
					ps.setString(3, fechaFin);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						post = new Post();
						post.setIdPost(rs.getInt("idPost"));
						post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
						post.setTextoPost(rs.getString("textoPost"));
						post.setFechaPost((Date) rs.getDate("fechaPost"));
						lista_Post.add(post);
					}

					posts = lista_Post;

					numeroPost = posts.size();
					if (numeroPost > 0) {
						return Response
								.status(Response.Status.OK).entity("El usuario " + user_id + " ha publicado "
										+ numeroPost + " post entre las fechas " + fechaInicio + " y " + fechaFin)
								.build();
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("El usuario " + user_id
								+ " no ha publicado ningun post entre las fechas " + fechaInicio + " y " + fechaFin)
								.build();

					}

				}

				else { // Se cuentan todos

					PreparedStatement ps = conn.prepareStatement("SELECT * FROM post where idUsuario_Post = ?");
					ps.setString(1, user_id);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						post = new Post();
						post.setIdPost(rs.getInt("idPost"));
						post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
						post.setTextoPost(rs.getString("textoPost"));
						post.setFechaPost((Date) rs.getDate("fechaPost"));
						lista_Post.add(post);
					}

					posts = lista_Post;
					numeroPost = posts.size();
					if (numeroPost > 0) {
						return Response.status(Response.Status.OK).entity("El usuario " + user_id + " ha publicado "
								+ numeroPost+" posts").build();
					} else {
						return Response.status(Response.Status.NOT_FOUND)
								.entity("El usuario " + user_id + " aun no ha publicado ningun post").build();

					}

				}

			} catch (SQLException e) {

				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {

			System.out.println("Fallo al conectar a la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}
	}

	// 9. Añadir un nuevo amigo: POST http://localhost:8080/upmsocial/api/users/[usuario]/amigos

	@POST
	@Path("{user_id}/amigos")
	@Consumes(MediaType.APPLICATION_XML)
	public Response agregarAmigo(JAXBElement<User> user, @PathParam("user_id") String user_id) {

		User valor = user.getValue();
		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {
				boolean usuarioExiste = buscarUsuario(conn, valor.getUsername());

				if (usuarioExiste) {

					boolean amigos = false;
					Amigo amigo;

					ArrayList<Amigo> lista_Amigo = new ArrayList<Amigo>();

					PreparedStatement ps = conn
							.prepareStatement("SELECT * FROM Amigos where idAmigos = ? and idUsuario_amigos = ?");
					ps.setString(1, valor.getUsername());
					ps.setString(2, user_id);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						amigo = new Amigo();
						amigo.setIdAmigos(rs.getString("idAmigos"));
						amigo.setIdUsuario_amigos(rs.getString("idUsuario_amigos"));
						amigo.setFechaAmistad(rs.getDate("fechaAmistad"));
						lista_Amigo.add(amigo);
					}

					if (!lista_Amigo.isEmpty()) {
						amigos = true;
					}

					if (!amigos) {

						DateFormat d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						Date fecha = new Date();
						String fechaCreada = d.format(fecha);

						PreparedStatement query = conn.prepareStatement("INSERT into Amigos values(?,?,?)");
						query.setString(1, valor.getUsername());
						query.setString(2, user_id);
						query.setString(3, fechaCreada);
						int primero = query.executeUpdate();

						PreparedStatement query1 = conn.prepareStatement("INSERT into Amigos values(?,?,?)");
						query1.setString(1, user_id);
						query1.setString(2, valor.getUsername());
						query1.setString(3, fechaCreada);

						int segundo = query1.executeUpdate();

						if (primero != 0 && segundo != 0) {

							return Response.status(Status.CREATED)
									.header("Location", uriInfo.getAbsolutePath().toString() + "/" + valor.getUsername())
									.build();
						}

						else {
							return Response.status(Status.NOT_FOUND).build();
						}

					} else {

						return Response.status(Status.BAD_REQUEST)
								.entity("El usuario con username " + valor.getUsername() + " ya es tu amigo").build();
					}

				} else {

					return Response.status(Status.NOT_FOUND).entity("El usuario escogido no existe").build();

				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}

	}

	// 10. Eliminar un amigo: DELETE http://localhost:8080/upmsocial/api/users/[usuario]/amigos/[amigo]

	@DELETE
	@Path("{user_id}/amigos/{amigo_id}")
	public Response eliminarAmigo(@PathParam("user_id") String user_id, @PathParam("amigo_id") String amigo_id) {

		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {
				boolean usuarioExiste = buscarUsuario(conn, amigo_id);

				if (usuarioExiste) {

					boolean amigos = false;
					Amigo amigo;

					ArrayList<Amigo> lista_Amigo = new ArrayList<Amigo>();

					PreparedStatement ps = conn
							.prepareStatement("SELECT * FROM Amigos where idAmigos = ? and idUsuario_amigos = ?");
					ps.setString(1, amigo_id);
					ps.setString(2, user_id);

					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						amigo = new Amigo();
						amigo.setIdAmigos(rs.getString("idAmigos"));
						amigo.setIdUsuario_amigos(rs.getString("idUsuario_amigos"));
						amigo.setFechaAmistad(rs.getDate("fechaAmistad"));
						lista_Amigo.add(amigo);
					}

					if (!lista_Amigo.isEmpty()) {
						amigos = true;
					}

					if (amigos) {

						PreparedStatement query = conn
								.prepareStatement("DELETE FROM Amigos where idAmigos =? and idUsuario_amigos=?");
						query.setString(1, amigo_id);
						query.setString(2, user_id);
						int primero = query.executeUpdate();

						PreparedStatement query1 = conn
								.prepareStatement("DELETE FROM Amigos where idAmigos =? and idUsuario_amigos=?");
						query1.setString(1, user_id);
						query1.setString(2, amigo_id);

						int segundo = query1.executeUpdate();

						if (primero != 0 && segundo != 0) {

							return Response.ok().entity("El usuario " + amigo_id + " ya no es tu amigo").build();
						}

						else {
							return Response.status(Status.NOT_FOUND).build();
						}

					} else {

						return Response.status(Status.BAD_REQUEST)
								.entity("El usuario con username " + amigo_id + " no es tu amigo").build();
					}

				} else {

					return Response.status(Status.NOT_FOUND).entity("El usuario escogido no existe").build();

				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}

	}

	// 11. Obtener una lista de todos nuestros amigos:

	// - filtrar esa lista por nombre:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos?nombre=X

	// - limitar la cantidad de información obtenida por nº de amigos:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos?inicio=X
	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos?cuantos=Y
	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos?inicio=X&cuantos=Y

	// - Una lista de todos los amigos filtrando por nombre y limitando la cantidad:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos?nombre=X&inicio=Y&cuantos=Z

	@GET
	@Path("{user_id}/amigos")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAmigos(@PathParam("user_id") String user_id,
			@QueryParam("nombre") @DefaultValue("%") String nombre, @QueryParam("inicio") @DefaultValue("1") int inicio,
			@QueryParam("cuantos") @DefaultValue("3") int cuantos) throws ClassNotFoundException {

		Connection conn = null;
		try {
			conn = conexionBBDD();
			ArrayList<User> usuarios = null;
			try {

				int inicio1 = inicio - 1;
				User user;
				ArrayList<User> lista_username = new ArrayList<User>();
				String sql = "SELECT * FROM Usuario join Amigos on Usuario.username = Amigos.idAmigos where Amigos.idUsuario_amigos = ? and Usuario.nombre like ? limit ?,?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, user_id);
				ps.setString(2, "%" + nombre + "%");
				ps.setInt(3, inicio1);
				ps.setInt(4, cuantos);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					user = new User();
					user.setUsername(rs.getString("username"));
					user.setNombre(rs.getString("nombre"));
					user.setApellido1(rs.getString("apellido1"));
					user.setApellido2(rs.getString("apellido2"));
					user.setDireccion(rs.getString("direccion"));
					user.setCorreo(rs.getString("correo"));
					user.setTelefono(rs.getString("telefono"));
					lista_username.add(user);

				}
				usuarios = lista_username;

				if (!usuarios.isEmpty()) {

					int size = usuarios.size();

					String listado = "<?xml version=\"1.0\"?>" + "<amigos>";

					for (int x = 0; x < size; x++) {

						listado = listado + "<amigo href = " + uriInfo.getAbsolutePath() + "/"
								+ usuarios.get(x).getUsername() + "><nombre>" + usuarios.get(x).getNombre()
								+ "</nombre></amigo>";
					}

					listado = listado + "</amigos>";

					return Response.status(Response.Status.OK).entity(listado).build();

				}

				return Response.status(Response.Status.NOT_FOUND).entity("No hay amigos").build();

			} catch (SQLException e) {

				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {

			System.out.println("Fallo al conectar a la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}

	}

	// 12. Cambiar datos de nuestro perfil (excepto el nombre del usuario): PUT http://upmsocial.es/api/users/[usuario]

	@PUT
	@Path("{user_id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response eitarPerfil(JAXBElement<User> user, @PathParam("user_id") String user_id) {
		User valor = user.getValue();
		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {
				if (valor.getUsername() != null && valor.getNombre() == null && valor.getApellido1() == null
						&& valor.getApellido2() == null && valor.getCorreo() == null && valor.getDireccion() == null
						&& valor.getTelefono() == null) {

					return Response.status(Status.BAD_REQUEST).entity("El username no puede modificarse").build();

				}

				String sql = "UPDATE Usuario SET";

				if (valor.getNombre() != null) {
					sql = sql + " nombre = ?, ";
				}
				if (valor.getApellido1() != null) {
					sql = sql + " apellido1 = ?, ";
				}
				if (valor.getApellido2() != null) {
					sql = sql + " apellido2 = ?, ";
				}
				if (valor.getDireccion() != null) {
					sql = sql + " direccion = ?, ";
				}
				if (valor.getCorreo() != null) {
					sql = sql + " correo = ?, ";
				}
				if (valor.getTelefono() != null) {
					sql = sql + " telefono = ?, ";
				}
				sql = sql.substring(0, sql.length() - 2) + " ";
				sql = sql + " where username = ?";

				PreparedStatement query = conn.prepareStatement(sql);

				int contador = 1;

				if (valor.getNombre() != null) {
					query.setString(contador, valor.getNombre());
					contador++;
				}
				if (valor.getApellido1() != null) {
					query.setString(contador, valor.getApellido1());
					contador++;
				}
				if (valor.getApellido2() != null) {
					query.setString(contador, valor.getApellido2());
					contador++;
				}
				if (valor.getDireccion() != null) {
					query.setString(contador, valor.getDireccion());
					contador++;
				}
				if (valor.getCorreo() != null) {
					query.setString(contador, valor.getCorreo());
					contador++;
				}
				if (valor.getTelefono() != null) {
					query.setString(contador, valor.getTelefono());
					contador++;
				}
				query.setString(contador, user_id);

				if (query.executeUpdate() != 0) {

					if (valor.getUsername() == null) {
						return Response.status(Status.OK).header("Location", uriInfo.getAbsolutePath().toString())
								.build();
					}

					return Response.status(Status.OK).header("Location", uriInfo.getAbsolutePath().toString())
							.entity("El username no se puede modificar").build();

				} else {
					return Response.status(Status.NOT_FOUND).build();
				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query.");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar a la DB.");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexión con la DB.");
			}
		}

	}

	// 13. Borrar nuestro perfil de la red social DELETE http://upmsocial.es/api/users/[usuario]

	@DELETE
	@Path("{user_id}")
	public Response borrarPerfil(@PathParam("user_id") String user_id) {
		Connection conn = null;
		try {
			conn = conexionBBDD();
			try {

				PreparedStatement query = conn.prepareStatement("DELETE FROM Usuario WHERE username = ?");

				query.setString(1, user_id);

				
				if (query.executeUpdate() != 0) {

					return Response.ok().entity("Usuario " + user_id + " eliminado correctamente").build();
				} else {
					return Response.status(Status.NOT_FOUND).entity("Usuario no existe").build();

				}

			} catch (SQLException e) {
				System.err.println("Fallo al ejecutar la query");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Fallo al conectar a la BD");
			return Response.status(Response.Status.NOT_FOUND).build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}

	// 14. Consultar los post de nuestros amigos:

	// - Esos posts se pueden filtrar por fecha

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?fechaInicio=X : publicados a partir de la fecha X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?fechaFin=X : publicados antes de la fecha X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?fechaInicio=X&fechaFin=Y : publicados entre las fechas X e Y

	// - Esos posts se pueden filrar por contenido (Posts que contengan un determinado texto)

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?contenido=X : Contienen un texto X

	// - Esos posts se pueden limitar dando desde que posts y cuantos posts obtener:

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?inicio=X : Obtener desde el post numero X

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?cuantos=X : Obtener X pòsts

	// GET http://localhost:8080/upmsocial/api/users/[usuario]/amigos/posts?inicio=X&cuantos=Y : Obtener Y post empezando por el numero X

	@GET
	@Path("{user_id}/amigos/posts")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPostAmigos(@PathParam("user_id") String user_id,
			@QueryParam("fechaInicio") @DefaultValue("") String fechaInicio,
			@QueryParam("fechaFin") @DefaultValue("") String fechaFin,
			@QueryParam("contenido") @DefaultValue("%") String contenido,
			@QueryParam("inicio") @DefaultValue("1") int inicio, @QueryParam("cuantos") @DefaultValue("3") int cuantos)
			throws ClassNotFoundException {
		Connection conn = null;
		try {
			conn = conexionBBDD();

			if (!fechaFin.equals("") && !fechaInicio.equals("")) { // DESDE LA FECHA DE INICIO A LA FECHA FINAL
				int inicio1 = inicio - 1;
				Post post;
				ArrayList<Post> lista_posts = new ArrayList<Post>();
				String sql = "SELECT * FROM post join amigos on post.idUsuario_Post = amigos.idAmigos where amigos.idUsuario_amigos = ? and post.fechaPost between str_to_date(?, '%d-%m-%Y') and str_to_date(?, '%d-%m-%Y') and post.textoPost like ? order by post.fechaPost asc limit ?,?";

				PreparedStatement ps = conn.prepareStatement(sql);

				ps.setString(1, user_id);
				ps.setString(2, fechaInicio);
				ps.setString(3, fechaFin);
				ps.setString(4, "%" + contenido + "%");
				ps.setInt(5, inicio1);
				ps.setInt(6, cuantos);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					post = new Post();
					post.setIdPost(rs.getInt("idPost"));
					post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
					post.setTextoPost(rs.getString("textoPost"));
					post.setFechaPost((Date) rs.getDate("fechaPost"));
					lista_posts.add(post);
				}

				if (!lista_posts.isEmpty()) {

					int size = lista_posts.size();

					String listado = "<?xml version=\"1.0\"?>" + "<posts>";

					for (int x = 0; x < size; x++) {

						listado = listado + "<post><texto>" + lista_posts.get(x).getTextoPost() + "</texto><autorPost>"
								+ lista_posts.get(x).getIdUsuario_Post() + "</autorPost><fechaPost>"
								+ lista_posts.get(x).getFechaPost() + "</fechaPost></post>";
					}

					listado = listado + "</posts>";

					return Response.status(Response.Status.OK).entity(listado).build();

				}

				return Response.status(Response.Status.NOT_FOUND).entity("No hay posts de amigos").build();
			} // SI NO DA FECHAS

			else if (fechaInicio.equals("") && !fechaFin.equals("")) { // DAR TODOS LOS POSTS HASTA LA FECHA FINAL ESTABLECIDA

				int inicio1 = inicio - 1;
				Post post;
				ArrayList<Post> lista_posts = new ArrayList<Post>();
				String sql = "SELECT * FROM post join amigos on post.idUsuario_Post = amigos.idAmigos where amigos.idUsuario_amigos = ? and fechaPost <= str_to_date(?, '%d-%m-%Y') and post.textoPost like ? order by post.fechaPost asc limit ?,?;";

				PreparedStatement ps = conn.prepareStatement(sql);

				ps.setString(1, user_id);
				ps.setString(2, fechaFin);
				ps.setString(3, "%" + contenido + "%");
				ps.setInt(4, inicio1);
				ps.setInt(5, cuantos);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					post = new Post();
					post.setIdPost(rs.getInt("idPost"));
					post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
					post.setTextoPost(rs.getString("textoPost"));
					post.setFechaPost((Date) rs.getDate("fechaPost"));
					lista_posts.add(post);
				}

				if (!lista_posts.isEmpty()) {

					int size = lista_posts.size();

					String listado = "<?xml version=\"1.0\"?>" + "<posts>";

					for (int x = 0; x < size; x++) {

						listado = listado + "<post><texto>" + lista_posts.get(x).getTextoPost() + "</texto><autorPost>"
								+ lista_posts.get(x).getIdUsuario_Post() + "</autorPost><fechaPost>"
								+ lista_posts.get(x).getFechaPost() + "</fechaPost></post>";
					}

					listado = listado + "</posts>";

					return Response.status(Response.Status.OK).entity(listado).build();

				}

				return Response.status(Response.Status.NOT_FOUND).entity("No hay posts de amigos").build();
			}

			else if (fechaFin.equals("") && !fechaInicio.equals("")) { // DAR TODOS LOS POSTS DESDE LA FECHA DE INICIO ESTABLECIDA

				int inicio1 = inicio - 1;
				Post post;
				ArrayList<Post> lista_posts = new ArrayList<Post>();
				String sql = "SELECT * FROM post join amigos on post.idUsuario_Post = amigos.idAmigos where amigos.idUsuario_amigos = ? and fechaPost >= str_to_date(?, '%d-%m-%Y') and post.textoPost like ? order by post.fechaPost asc limit ?,?;";

				PreparedStatement ps = conn.prepareStatement(sql);

				ps.setString(1, user_id);
				ps.setString(2, fechaInicio);
				ps.setString(3, "%" + contenido + "%");
				ps.setInt(4, inicio1);
				ps.setInt(5, cuantos);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					post = new Post();
					post.setIdPost(rs.getInt("idPost"));
					post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
					post.setTextoPost(rs.getString("textoPost"));
					post.setFechaPost((Date) rs.getDate("fechaPost"));
					lista_posts.add(post);
				}

				if (!lista_posts.isEmpty()) {

					int size = lista_posts.size();

					String listado = "<?xml version=\"1.0\"?>" + "<posts>";

					for (int x = 0; x < size; x++) {

						listado = listado + "<post><texto>" + lista_posts.get(x).getTextoPost() + "</texto><autorPost>"
								+ lista_posts.get(x).getIdUsuario_Post() + "</autorPost><fechaPost>"
								+ lista_posts.get(x).getFechaPost() + "</fechaPost></post>";
					}

					listado = listado + "</posts>";

					return Response.status(Response.Status.OK).entity(listado).build();

				}

				return Response.status(Response.Status.NOT_FOUND).entity("No hay posts de amigos").build();
			}

			else { // DAR TODOS LOS POSTS, me da igual desde que fecha hasta que fecha

				int inicio1 = inicio - 1;
				Post post;
				ArrayList<Post> lista_posts = new ArrayList<Post>();
				String sql = "SELECT * FROM post join amigos on post.idUsuario_Post = amigos.idAmigos where amigos.idUsuario_amigos = ? and post.textoPost like ? order by post.fechaPost asc limit ?,?";

				PreparedStatement ps = conn.prepareStatement(sql);

				ps.setString(1, user_id);
				ps.setString(2, "%" + contenido + "%");
				ps.setInt(3, inicio1);
				ps.setInt(4, cuantos);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					post = new Post();
					post.setIdPost(rs.getInt("idPost"));
					post.setIdUsuario_Post(rs.getString("idUsuario_Post"));
					post.setTextoPost(rs.getString("textoPost"));
					post.setFechaPost((Date) rs.getDate("fechaPost"));
					lista_posts.add(post);
				}

				if (!lista_posts.isEmpty()) {

					int size = lista_posts.size();

					String listado = "<?xml version=\"1.0\"?>" + "<posts>";

					for (int x = 0; x < size; x++) {

						listado = listado + "<post><texto>" + lista_posts.get(x).getTextoPost() + "</texto><autorPost>"
								+ lista_posts.get(x).getIdUsuario_Post() + "</autorPost><fechaPost>"
								+ lista_posts.get(x).getFechaPost() + "</fechaPost></post>";
					}

					listado = listado + "</posts>";

					return Response.status(Response.Status.OK).entity(listado).build();

				}

				return Response.status(Response.Status.NOT_FOUND).entity("No hay posts de amigos").build();

			}

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				System.err.println("Fallo al cerrar la conexion con la DB");
			}
		}
	}
}
