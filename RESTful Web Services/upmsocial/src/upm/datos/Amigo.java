package upm.datos;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "amigo")
public class Amigo {

	private String idAmigos;
	private String idUsuario_amigos;
	private Date fechaAmistad;

	public String getIdAmigos() {
		return idAmigos;
	}

	public void setIdAmigos(String idAmigos) {
		this.idAmigos = idAmigos;
	}

	public String getIdUsuario_amigos() {
		return idUsuario_amigos;
	}

	public void setIdUsuario_amigos(String idUsuario_amigos) {
		this.idUsuario_amigos = idUsuario_amigos;
	}

	public Date getFechaAmistad() {
		return fechaAmistad;
	}

	public void setFechaAmistad(Date fechaAmistad) {
		this.fechaAmistad = fechaAmistad;
	}

	public Amigo() {

	}

	public Amigo(String idAmigos, String idUsuario_amigos, Date fechaAmistad) {
		super();
		this.idAmigos = idAmigos;
		this.idUsuario_amigos = idUsuario_amigos;
		this.fechaAmistad = fechaAmistad;
	}

}
