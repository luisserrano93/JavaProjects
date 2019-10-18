package upm.datos;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


import java.util.Date;



@XmlRootElement(name = "post")
public class Post {
	private int idPost;
	private String idUsuario_Post;
	private String textoPost;
	private Date fechaPost;

	@XmlAttribute(required = false)
	public int getIdPost() {
		return idPost;
	}

	public void setIdPost(int idPost) {
		this.idPost = idPost;
	}

	public String getIdUsuario_Post() {
		return idUsuario_Post;
	}

	public void setIdUsuario_Post(String idUsuario_Post) {
		this.idUsuario_Post = idUsuario_Post;
	}

	public String getTextoPost() {
		return textoPost;
	}

	public void setTextoPost(String textoPost) {
		this.textoPost = textoPost;
	}

	public Date getFechaPost() {
		return fechaPost;
	}

	public void setFechaPost(Date fechaPost) {
		this.fechaPost = fechaPost;
	}

	public Post() {

	}

	public Post(int idPost, String idUsuario_Post, String textoPost, Date fechaPost) {
		super();
		this.idPost = idPost;
		this.idUsuario_Post = idUsuario_Post;
		this.textoPost = textoPost;
		this.fechaPost = fechaPost;
	}

}
